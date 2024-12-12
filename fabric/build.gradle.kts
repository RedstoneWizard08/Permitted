architectury.fabric()

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
        exclude(".cache")
    }
}

loom {
    val common = project(":common")
    accessWidenerPath = common.loom.accessWidenerPath

    runs {
        create("datagen") {
            client()

            name = "Minecraft Data"
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${common.file("src/generated/resources")}")
            vmArg("-Dfabric-api.datagen.modid=permitted")
            vmArg("-Dporting_lib.datagen.existing_resources=${common.file("src/main/resources")}")

            environmentVariable("DATAGEN", "TRUE")
        }
    }
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}")
    modImplementation("com.simibubi.create:create-fabric-${"minecraft_version"()}:${"create_fabric_version"()}")
    modImplementation("dev.ithundxr.createnumismatics:CreateNumismatics-fabric-${"minecraft_version"()}:${"numismatics_version"()}+fabric-mc${"minecraft_version"()}")
    modImplementation(include("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${"forge_config_api_port_version"()}")!!)

    modApi("dev.architectury:architectury-fabric:${"architectury_version"()}")

    modLocalRuntime("maven.modrinth:lazydfu:${"lazydfu_version"()}")
    modLocalRuntime("com.terraformersmc:modmenu:${"modmenu_version"()}")
    modLocalRuntime("me.djtheredstoner:DevAuth-fabric:1.2.1")
    modLocalRuntime("maven.modrinth:jade:${"jade_version"()}+fabric")
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}
