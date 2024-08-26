package redstonedev.permitted.mixin;

import dev.ithundxr.createnumismatics.content.vendor.VendorBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(value = VendorBlockEntity.class, remap = false)
public interface VendorBlockEntityAccessor {
    @Accessor
    UUID getOwner();
}
