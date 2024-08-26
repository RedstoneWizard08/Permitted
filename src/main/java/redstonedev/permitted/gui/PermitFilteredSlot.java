package redstonedev.permitted.gui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import redstonedev.permitted.data.PermitData;
import redstonedev.permitted.items.Permit;
import redstonedev.permitted.mixin.VendorBlockEntityAccessor;

public class PermitFilteredSlot extends Slot {
    private final Container permitContainer;
    private final VendorBlockEntityAccessor vendor;

    public PermitFilteredSlot(VendorBlockEntityAccessor vendor, Container container, Container permitContainer, int slot, int x, int y) {
        super(container, slot, x, y);

        this.vendor = vendor;
        this.permitContainer = permitContainer;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (!permitContainer.isEmpty() && permitContainer.getItem(0).getItem() instanceof Permit) {
            ItemStack permit = permitContainer.getItem(0);
            PermitData data = PermitData.getOrCreate(permit);

            if (data.owner.isPresent() && data.owner.get().equals(this.vendor.getOwner()) && data.items.contains(stack.getItem())) {
                return super.mayPlace(stack);
            }
        }

        return false;
    }
}
