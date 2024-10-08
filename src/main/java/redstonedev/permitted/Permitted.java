package redstonedev.permitted;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import redstonedev.permitted.commands.PermitCommand;
import redstonedev.permitted.data.PermitData;
import redstonedev.permitted.items.Permit;

import java.util.Optional;

@Mod(Permitted.MOD_ID)
public class Permitted {
    public static final String MOD_ID = "permitted";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final ItemEntry<Permit> PERMIT = REGISTRATE.item("permit", Permit::new)
            .model((ctx, prov) -> {
            })
            .register();

    public static final ItemEntry<Item> DIAMOND_PERMIT = REGISTRATE.item("diamond_permit", Item::new)
            .model((ctx, prov) -> {
            })
            .register();

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("permitted", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MOD_ID))
            .icon(() -> new ItemStack(DIAMOND_PERMIT.get()))
            .displayItems((params, output) -> {
                output.accept(PERMIT.get());
            })
            .build()
    );

    public Permitted() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        PermittedLang.init();
        MinecraftForge.EVENT_BUS.register(this);
        TABS.register(bus);

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
    public void leftClick(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getItemStack().getItem() instanceof Permit && event.getEntity().isCrouching()) {
            PermitData permitData = PermitData.getOrCreate(event.getItemStack());

            if (permitData.owner.isPresent() && permitData.owner.get().equals(event.getEntity().getUUID())) {
                permitData.owner = Optional.empty();
                permitData.ownerName = Optional.empty();
                PermitData.set(event.getItemStack(), permitData);
            }
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        PermitCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
