package redstonedev.permitted.gui;

import com.mojang.datafixers.util.Pair;
import dev.ithundxr.createnumismatics.content.vendor.VendorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redstonedev.permitted.Permitted;
import redstonedev.permitted.data.PermitData;
import redstonedev.permitted.items.Permit;
import redstonedev.permitted.mixin.VendorBlockEntityAccessor;

public class PermitSlot extends Slot {
    private final VendorBlockEntityAccessor vendor;

    public PermitSlot(VendorBlockEntityAccessor vendor, Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);

        this.vendor = vendor;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.getItem() instanceof Permit) {
            PermitData permitData = PermitData.get(stack);

            return permitData.owner.isPresent() && permitData.owner.get().equals(this.vendor.getOwner());
        }

        return false;
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, Permitted.id("item/permit_slot"));
    }
}
