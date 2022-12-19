package click.isreal.topbar.mixin;

import click.isreal.topbar.Topbar;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class HornDisableMixin {
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfo ci){
        if((Topbar.getInstance().isStreamerMode() || !Topbar.getInstance().hornAudio()) &&
                sound != null && sound.getId().toString().startsWith("minecraft:item.goat_horn")) {
            Topbar.LOGGER.info("Blocking Horn sound");
            ci.cancel();
        }
    }
}
