package redstonedev.permitted;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import redstonedev.permitted.commands.PermitCommand;
import redstonedev.permitted.init.ModCreativeTabs;
import redstonedev.permitted.init.ModItems;
import redstonedev.permitted.init.ModLang;

public class Permitted {
    public static final String MOD_ID = "permitted";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .setCreativeTab(ModCreativeTabs.TAB.getKey());

    public static void init() {
        ModCreativeTabs.init();
        ModItems.init();
        ModLang.init();

        CommandRegistrationEvent.EVENT.register(Permitted::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry,
            Commands.CommandSelection selection) {
        PermitCommand.register(dispatcher, registry);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
