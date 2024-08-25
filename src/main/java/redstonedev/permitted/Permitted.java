package redstonedev.permitted;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import redstonedev.permitted.commands.PermitCommand;
import redstonedev.permitted.data.PermitData;
import redstonedev.permitted.items.Permit;

@Mod(Permitted.MOD_ID)
public class Permitted {
    public static final String MOD_ID = "permitted";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);

    public static final ItemEntry<Permit> PERMIT = REGISTRATE.item("permit", Permit::new)
            .model((ctx, prov) -> {
            })
            .register();

    public Permitted() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        PermittedLang.init();
        MinecraftForge.EVENT_BUS.register(this);

        if (FMLEnvironment.dist.isClient())
            bus.addListener(this::clientSetup);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                PERMIT.get(),
                id("rarity"),
                (stack, level, entity, id) -> PermitData.getOrCreate(stack).rarity.ordinal() / 10f
        ));
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        PermitCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
