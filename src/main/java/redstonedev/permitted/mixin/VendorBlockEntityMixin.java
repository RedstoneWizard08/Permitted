package redstonedev.permitted.mixin;

import dev.ithundxr.createnumismatics.content.vendor.VendorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redstonedev.permitted.util.IHasPermitContainer;

@Mixin(value = VendorBlockEntity.class, remap = false)
public class VendorBlockEntityMixin implements IHasPermitContainer {
    @Unique
    public final Container permitted$permitContainer = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            ((VendorBlockEntity) (Object) VendorBlockEntityMixin.this).setChanged();
        }
    };

    @Inject(method = "write", at = @At("RETURN"))
    public void write(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        if (!this.permitted$permitContainer.getItem(0).isEmpty()) {
            tag.put("Permit", this.permitted$permitContainer.getItem(0).save(new CompoundTag()));
        }
    }

    @Inject(method = "read", at = @At("RETURN"))
    public void read(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        ItemStack stack;

        if (tag.contains("Permit", 10)) {
            stack = ItemStack.of(tag.getCompound("Permit"));
            this.permitted$permitContainer.setItem(0, stack);
        } else {
            this.permitted$permitContainer.setItem(0, ItemStack.EMPTY);
        }
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public Container getPermitContainer() {
        return permitted$permitContainer;
    }
}
