package click.isreal.topbar.mixin;

import click.isreal.topbar.Topbar;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SystemToast.class)
public class UnsecureServerToastMixin {
    @Shadow
    private SystemToast.Type type;

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void draw(MatrixStack matrices,
                      ToastManager manager,
                      long startTime,
                      CallbackInfoReturnable<Toast.Visibility> info) {
        if(!Topbar.getInstance().unsecureServerWarning() && type == SystemToast.Type.UNSECURE_SERVER_WARNING)
            info.setReturnValue(Toast.Visibility.HIDE);
    }

}
