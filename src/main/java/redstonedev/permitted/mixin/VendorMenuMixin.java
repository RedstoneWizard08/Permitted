package redstonedev.permitted.mixin;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.ithundxr.createnumismatics.content.vendor.VendorBlockEntity;
import dev.ithundxr.createnumismatics.content.vendor.VendorMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redstonedev.permitted.gui.PermitSlot;
import redstonedev.permitted.util.IHasPermitContainer;

@Mixin(value = VendorMenu.class, remap = false)
public abstract class VendorMenuMixin extends MenuBase<VendorBlockEntity> {
    protected VendorMenuMixin(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    @Inject(method = "addSlots", at = @At("RETURN"))
    protected void addSlots(CallbackInfo ci) {
        addSlot(new PermitSlot((VendorBlockEntityAccessor) this.contentHolder, ((IHasPermitContainer) this.contentHolder).getPermitContainer(), 0, 8, 148));
    }
}
