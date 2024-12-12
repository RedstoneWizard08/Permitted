// I definitely did not copy this build script from another project... no way

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.configurationcache.extensions.capitalized
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.Deflater

plugins {
    id("java")
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("io.github.pacifistmc.forgix") version "1.2.9"
}

println("Permitted v${"mod_version"()}")
apply(plugin = "architectury-plugin")

configure<ArchitectPluginExtension> {
    minecraft = "minecraft_version"()
}

val isRelease = System.getenv("RELEASE_BUILD")?.toBoolean() ?: false
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toInt()
val gitHash = "\"${calculateGitHash()}\""

tasks.jar {
    enabled = false
}

forgix {
    val build = buildNumber?.let { "-build.${it}" } ?: "-local"
    val jarVersion = "${"mod_version"()}-mc${"minecraft_version"() + if (isRelease) "" else build}"

    group = "mod_group_id"()
    mergedJarName = "permitted-${jarVersion}.jar"
    outputDir = "build/libs/merged"
    removeDuplicate("redstonedev.permitted") // the platform-specific stuff is in subpackages anyway
}

tasks.build.get().finalizedBy(tasks.mergeJars.get())
tasks.assemble.get().finalizedBy(tasks.mergeJars.get())

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    base.archivesName.set("mod_id"())
    group = "mod_group_id"()

    val build = buildNumber?.let { "-build.${it}" } ?: "-local"

    version = "${"mod_version"()}+${project.name}-mc${"minecraft_version"() + if (isRelease) "" else build}"

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    java {
        withSourcesJar()

        toolchain {
            if("minecraft_version"() >= "1.20.5")
                languageVersion.set(JavaLanguageVersion.of(21))
            else
                languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    setupRepositories()

    val capitalizedName = project.name.capitalized()

    val loom = the<LoomGradleExtensionAPI>()
    loom.apply {
        silentMojangMappingsLicense()
        runs.configureEach {
            vmArg("-XX:+AllowEnhancedClassRedefinition")
            vmArg("-XX:+IgnoreUnrecognizedVMOptions")
            vmArg("-Dmixin.debug.export=true")
            vmArg("-Dmixin.env.remapRefMap=true")
            vmArg("-Dmixin.env.refMapRemappingFile=${projectDir}/build/createSrgToMcp/output.srg")
        }
    }

    configurations.configureEach {
        resolutionStrategy {
            force("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
        }
    }

    @Suppress("UnstableApiUsage")
    dependencies {
        "minecraft"("com.mojang:minecraft:${"minecraft_version"()}")
        // layered mappings - Mojmap names, parchment and QM docs and parameters
        "mappings"(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${"parchment_version"()}@zip")
        })

        // Used to decompile mixin dumps, needs to be on the classpath
        // Uncomment if you want it to decompile mixin exports, beware it has very verbose logging.
        //implementation("org.vineflower:vineflower:1.10.0")
    }

    // from here down is platform configuration
    if(project.path == ":common") {
        return@subprojects
    }

    apply(plugin = "com.github.johnrengelman.shadow")

    configure<ArchitectPluginExtension> {
        platformSetupLoomIde()
    }

    tasks.named<RemapJarTask>("remapJar") {
        from("${rootProject.projectDir}/LICENSE")
        val shadowJar = project.tasks.named<ShadowJar>("shadowJar").get()
        inputFile.set(shadowJar.archiveFile)
        injectAccessWidener = true
        dependsOn(shadowJar)
        archiveClassifier = null
        doLast {
            transformJar(outputs.files.singleFile)
        }
    }

    val common: Configuration by configurations.creating
    val shadowCommon: Configuration by configurations.creating
    val development = configurations.maybeCreate("development${capitalizedName}")

    configurations {
        compileOnly.get().extendsFrom(common)
        runtimeOnly.get().extendsFrom(common)
        development.extendsFrom(common)
    }

    dependencies {
        common(project(":common", "namedElements")) { isTransitive = false }
        shadowCommon(project(":common", "transformProduction${capitalizedName}")) { isTransitive = false }
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveClassifier = "dev-shadow"
        configurations = listOf(shadowCommon)
        exclude("architectury.common.json")
        destinationDirectory = layout.buildDirectory.dir("devlibs").get()
    }

    tasks.processResources {
        // include packs
        from(project(":common").file("src/main/resources")) {
            include("resourcepacks/")
        }

        val createFabricVersion: String = "create_fabric_version"().replace("(\\d+\\.\\d+\\.\\d+-\\w)", "$1")

        // set up properties for filling into metadata
        val properties = mapOf(
            "mod_id" to "mod_id"(),
            "mod_version" to version,
            "mod_name" to "mod_name"(),
            "mod_authors" to "mod_authors"(),
            "mod_description" to "mod_description"(),
            "mod_license" to "mod_license"(),
            "minecraft_version" to "minecraft_version"(),
            "fabric_api_version" to "fabric_api_version"(),
            "fabric_loader_version" to "fabric_loader_version"(),
            "forge_version" to "forge_version"().split(".")[0], // only specify major version of forge
            "full_forge_version" to "forge_version"(),
            "numismatics_version" to "numismatics_version"(),
            "create_forge_version" to "create_forge_version"().split("-")[0], // cut off build number
            "create_fabric_version" to createFabricVersion // Trim -build.X+mcX.XX.X from version string
        )

        inputs.properties(properties)

        filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml")) {
            expand(properties)
        }
    }

    tasks.jar {
        archiveClassifier = "dev"

        manifest {
            attributes(mapOf("Git-Hash" to gitHash))
        }
    }

    tasks.named<Jar>("sourcesJar") {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })

        manifest {
            attributes(mapOf("Git-Hash" to gitHash))
        }
    }

    components.getByName<AdhocComponentWithVariants>("java") {
        withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
            skip()
        }
    }
}

fun transformJar(jar: File) {
    val contents = linkedMapOf<String, ByteArray>()
    JarFile(jar).use {
        it.entries().asIterator().forEach { entry ->
            if (!entry.isDirectory) {
                contents[entry.name] = it.getInputStream(entry).readAllBytes()
            }
        }
    }

    jar.delete()

    JarOutputStream(jar.outputStream()).use { out ->
        out.setLevel(Deflater.BEST_COMPRESSION)
        contents.forEach { var (name, data) = it
            if(name.startsWith("architectury_inject_${project.name}_common"))
                return@forEach

            if (name.endsWith(".json") || name.endsWith(".mcmeta")) {
                data = (JsonOutput.toJson(JsonSlurper().parse(data)).toByteArray())
            } else if (name.endsWith(".class")) {
                data = transformClass(data)
            }

            out.putNextEntry(JarEntry(name))
            out.write(data)
            out.closeEntry()
        }
        out.finish()
        out.close()
    }
}

fun transformClass(bytes: ByteArray): ByteArray {
    val node = ClassNode()
    ClassReader(bytes).accept(node, 0)

    // Remove Methods & Field Annotated with @DevEnvMixin
    node.methods.removeIf { methodNode: MethodNode -> removeIfDevMixin(node.name, methodNode.visibleAnnotations) }
    // Disabled as I don't feel ok with people being able to remove these
    //node.fields.removeIf { fieldNode: FieldNode -> removeIfDevMixin(fieldNode.visibleAnnotations) }

    return ClassWriter(0).also { node.accept(it) }.toByteArray()
}

fun removeIfDevMixin(nodeName: String, visibleAnnotations: List<AnnotationNode>?): Boolean {
    // Don't remove methods if it's not a GHA build/Release build
    if (buildNumber == null && !nodeName.lowercase(Locale.ROOT).matches(Regex(".*\\/mixin\\/.*Mixin")))
        return false

    if (visibleAnnotations != null) {
        for (annotationNode in visibleAnnotations) {
            if (annotationNode.desc == "Lcom/railwayteam/railways/annotation/mixin/DevEnvMixin;")
                return true
        }
    }

    return false
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}

fun Project.setupRepositories() {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.shedaniel.me/") // Cloth Config, REI
        maven("https://maven.blamejared.com/") // JEI, Hex Casting
        exclusiveMaven("https://maven.parchmentmc.org", "org.parchmentmc.data") // Parchment mappings
        exclusiveMaven("https://maven.quiltmc.org/repository/release", "org.quiltmc") // Quilt Mappings
        exclusiveMaven("https://api.modrinth.com/maven", "maven.modrinth") // LazyDFU, JourneyMap
        exclusiveMaven("https://cursemaven.com", "curse.maven")
        maven("https://maven.theillusivec4.top/") // Curios
        maven("https://maven.tterrag.com/") { // Flywheel, Registrate, Create
            content {
                includeGroup("com.simibubi.create")
                includeGroup("com.tterrag.registrate")
                includeGroup("com.jozufozu.flywheel")
            }
        }
        exclusiveMaven("https://maven.jamieswhiteshirt.com/libs-release", "com.jameswhiteshirt.reach-entity-attributes")// Reach Entity Attributes
        maven("https://maven.terraformersmc.com/releases/") // Mod Menu, EMI
        maven("https://mvn.devos.one/snapshots/") // Create Fabric, Porting Lib, Forge Tags, Milk Lib, Registrate Fabric
        maven("https://maven.cafeteria.dev/releases") // Fake Player API
        maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
        maven("https://jitpack.io/") // Mixin Extras, Fabric ASM
        maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // forge config api port
        maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") // devauth
        maven("https://maven.ithundxr.dev/releases") // Numismatics
    }
}

fun calculateGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

fun RepositoryHandler.exclusiveMaven(url: String, vararg groups: String) {
    exclusiveContent {
        forRepository { maven(url) }
        filter {
            groups.forEach {
                includeGroup(it)
            }
        }
    }
}

val Project.sourceSets
    get() = extensions.findByType(SourceSetContainer::class.java)!!
