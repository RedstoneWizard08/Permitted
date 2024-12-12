package redstonedev.permitted.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import redstonedev.permitted.Permitted;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Permitted.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final DeferredSupplier<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(
        "permitted",
        () -> CreativeTabRegistry.create(
            Component.translatable("itemGroup." + Permitted.MOD_ID),
            () -> new ItemStack(ModItems.DIAMOND_PERMIT.get())
        )
    );

    public static void init() {
        CREATIVE_MODE_TABS.register();
    }
}
