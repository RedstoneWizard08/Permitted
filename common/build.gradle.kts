import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.build.nesting.NestableJarGenerationTask

tasks.register<RemapJarTask>("remapJar") {
    nestedJars.setFrom()
}

tasks.register<NestableJarGenerationTask>("processIncludeJars")

architectury {
    common((rootProject.property("enabled_platforms") as String).split(","))
}

loom {
    accessWidenerPath = file("src/main/resources/permitted.accesswidener")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    modCompileOnly("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"()}")
    modCompileOnly("dev.ithundxr.createnumismatics:CreateNumismatics-common-${"minecraft_version"()}:${"numismatics_version"()}+common-mc${"minecraft_version"()}")

    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}")
    modApi("dev.architectury:architectury:${"architectury_version"()}")

    annotationProcessor(implementation("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")!!)
}

tasks.processResources {
    // must be part of primary mod to be findable
    exclude("resourcepacks/")

    // don't add development or to-do files into built jar
    exclude("**/*.bbmodel", "**/*.lnk", "**/*.xcf", "**/*.md", "**/*.blend", "**/*.blend1")
}

sourceSets.main {
    resources { // include generated resources in resources
        srcDir("src/generated/resources")
        exclude(".cache/**")
        exclude("assets/create/**")
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}