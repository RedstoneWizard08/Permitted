package redstonedev.permitted.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import redstonedev.permitted.data.PermitData;

import java.util.Optional;

public class Permit extends Item {
    public Permit(Properties props) {
        super(props);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (stack.hasTag() && !PermitData.get(stack).name.isEmpty()) {
            return Component.translatable("permit.title", PermitData.get(stack).getName());
        }

        return Component.translatable("permit.title.none");
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
