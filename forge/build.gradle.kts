architectury.forge()

loom {
    val common = project(":common")
    accessWidenerPath = common.loom.accessWidenerPath

    forge {
        mixinConfig("permitted-common.mixins.json")
        mixinConfig("permitted.mixins.json")

        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

dependencies {
    forge("net.minecraftforge:forge:${"minecraft_version"()}-${"forge_version"()}")

    modImplementation("com.simibubi.create:create-${"minecraft_version"()}:${"create_forge_version"()}:slim") { isTransitive = false }
    modImplementation("com.tterrag.registrate:Registrate:${"registrate_forge_version"()}")
    modImplementation("com.jozufozu.flywheel:flywheel-forge-${"minecraft_version"()}:${"flywheel_forge_version"()}")
    modImplementation("dev.ithundxr.createnumismatics:CreateNumismatics-forge-${"minecraft_version"()}:${"numismatics_version"()}+forge-mc${"minecraft_version"()}")

    modApi("dev.architectury:architectury-forge:${"architectury_version"()}") { isTransitive = false }

    modLocalRuntime("me.djtheredstoner:DevAuth-forge-latest:1.2.1")

    compileOnly("io.github.llamalad7:mixinextras-common:${"mixin_extras_version"()}")
    annotationProcessor(implementation(include("io.github.llamalad7:mixinextras-forge:${"mixin_extras_version"()}")!!)!!)
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}