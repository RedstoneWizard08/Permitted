package redstonedev.permitted.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import redstonedev.permitted.Permitted;
import redstonedev.permitted.data.PermitData;

@Mod.EventBusSubscriber(modid = Permitted.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Permit extends Item {
    public Permit(Properties props) {
        super(props);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (stack.hasTag() && !PermitData.get(stack).items.isEmpty()) {
            return Component.translatable("permit.title", PermitData.get(stack).getName());
        }

        return super.getName(stack);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof Permit) {
            PermitData permitData = PermitData.get(stack);

            event.getToolTip().add(permitData.rarity.getTooltip());
        }
    }
}
