package redstonedev.permitted.mixin;

import dev.ithundxr.createnumismatics.registry.NumismaticsGuiTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NumismaticsGuiTextures.class, remap = false)
public abstract class NumismaticsGuiTexturesMixin {
    @Shadow
    public int height;

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("HEAD"))
    public void injectHeightChange(CallbackInfo ci) {
        if (((NumismaticsGuiTextures) (Object) this).name().equalsIgnoreCase("vendor")) {
            this.height += 26;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("RETURN"))
    public void injectHeightChangePost(CallbackInfo ci) {
        if (((NumismaticsGuiTextures) (Object) this).name().equalsIgnoreCase("vendor")) {
            this.height -= 26;
        }
    }
}
