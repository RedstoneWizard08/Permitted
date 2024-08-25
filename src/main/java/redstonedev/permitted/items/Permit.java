package redstonedev.permitted.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import redstonedev.permitted.Permitted;
import redstonedev.permitted.data.PermitData;

import java.util.Optional;

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
            event.getToolTip().add(Component.empty());
            event.getToolTip().add(Component.translatable("permit.items.list"));

            for (Item item : permitData.items) {
                event.getToolTip().add(Component.translatable("permit.items.item", item.getName(item.getDefaultInstance())));
            }

            event.getToolTip().add(Component.empty());
            event.getToolTip().add(Component.translatable("permit.owner", permitData.getOwner()));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!pLevel.isClientSide && pPlayer.isCrouching()) {
            PermitData permitData = PermitData.getOrCreate(stack);

            if (permitData.owner.isEmpty() && permitData.ownerName.isEmpty()) {
                permitData.owner = Optional.of(pPlayer.getUUID());
                permitData.ownerName = Optional.of(pPlayer.getName().getString());

                PermitData.set(stack, permitData);
            }
        }

        return InteractionResultHolder.success(stack);
    }
}
