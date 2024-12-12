package redstonedev.permitted.client;

import java.util.List;
import java.util.Optional;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import redstonedev.permitted.Permitted;
import redstonedev.permitted.data.PermitData;
import redstonedev.permitted.init.ModItems;
import redstonedev.permitted.items.Permit;

@Environment(EnvType.CLIENT)
public class PermittedClient {
    public static void init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(PermittedClient::clientSetup);
        InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(PermittedClient::leftClick);
        ClientTooltipEvent.ITEM.register(PermittedClient::onTooltip);
    }

    private static void clientSetup(Minecraft client) {
        ItemProperties.register(
                ModItems.PERMIT.get(),
                Permitted.id("rarity"),
                (stack, level, entity, id) -> PermitData.getOrCreate(stack).rarity.ordinal() / 10f);
    }

    private static void leftClick(Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        
        if (item.getItem() instanceof Permit && player.isCrouching()) {
            PermitData permitData = PermitData.getOrCreate(item);

            if (permitData.owner.isPresent() && permitData.owner.get().equals(player.getUUID())) {
                permitData.owner = Optional.empty();
                permitData.ownerName = Optional.empty();
                
                PermitData.set(item, permitData);
            }
        }
    }

    private static void onTooltip(ItemStack stack, List<Component> lines, TooltipFlag flag) {
        if (stack.getItem() instanceof Permit) {
            PermitData permitData = PermitData.get(stack);

            lines.add(permitData.rarity.getTooltip());
            lines.add(Component.empty());
            lines.add(Component.translatable("permit.items.list"));

            for (Item item : permitData.items) {
                lines.add(Component.translatable("permit.items.item", item.getName(item.getDefaultInstance())));
            }

            lines.add(Component.empty());
            lines.add(Component.translatable("permit.owner", permitData.getOwner()));

            lines.add(Component.empty());
            lines.add(Component.translatable("permit.instructions.right_click"));
            lines.add(Component.translatable("permit.instructions.left_click"));
        }
    }
}
