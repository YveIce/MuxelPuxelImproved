package click.isreal.topbar.mixin;

/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2022 YveIce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

import click.isreal.topbar.Topbar;
import click.isreal.topbar.Utils;
import click.isreal.topbar.client.TopbarClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.*;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.tools.Tool;

@Environment( EnvType.CLIENT )
@Mixin( MinecraftClient.class )
public abstract class MinecraftClientMixin
{
    private static final String[] messagesTools = new String[]{
            "Gleich machst du es kaputt!",
            "Wenn du so weiter machst, hast du bald kein Werkzeug mehr.",
            "Haltbarkeit auf Wish.com bestellt?",
            "Schon mal was von reparieren gehört?",
            "Dein Werkzeug sieht bissel stark gebraucht aus.",
            "Achtung! Gleich is es weg!",
            "Ob kaputtes Werkzeug in den Werkzeug-Himmel kommt?",
            "Mit einem neuem Werkzeug gehts bestimmt besser!",
            "Jetzt wäre ein guter Zeitpunkt für /repair",
            "Die Gewährleistung auf dein Werkzeug ist grade abgelaufen.",
            "Wirf es doch gleich in Lava! Dann ist es auch weg.",
            "Mit /kit bekommst du neue Sachen zum kaputt machen.",
            "Ich hoffe es ist dein Werkzeug was du grade zerstörrst.",
            "Wenn das dein Werkenlehrer sehen würde!",
            "Bei dem Werkzeug hilft auch kein WD40 mehr.",
            "Mit bissel Glitzer sieht es bestimmt aus wie Neu.",
            "Hast du nur ein Werkzeug, das du dieses kaputt machen willst?",
            "Hey du da am Bildschirm! Schau mal auf die Haltbarkeit!",
            "Ich kenn nen coolen Trick wie man Werkzeug verschwinden lassen kann.",
            "Könnte dein Werkzeug reden, würde es 'Aua!' sagen."
    };

    private static final String[] messagesCombat = new String[]{
            "Gleich machst du es kaputt!",
            "Wenn du so weiter machst, hast du bald keine Waffen mehr.",
            "Haltbarkeit auf Wish.com bestellt?",
            "Schon mal was von reparieren gehört?",
            "Deine Waffe sieht aus wie auf Flora gekauft.",
            "Achtung! Gleich is es weg!",
            "Ob kaputte Waffen in die Waffenhölle kommen?",
            "Mit dieser Waffe beeindruckst du nur noch Noobs.",
            "Jetzt wäre ein guter Zeitpunkt für /repair",
            "Die Gewährleistung auf deine Waffe ist grade abgelaufen.",
            "Ene-mene-meg, gleich ist es weg!",
            "Aufwachen! Träumst du oder ist dir deine Waffe egal?",
            "Nervt dich der Sound? Soll er auch!",
            "Würdest du besser aufpassen, würde hier keine Warnung kommen.",
            "Deine Waffe leidet grade Schmerzen."
    };

    @Shadow
    public ClientPlayerEntity player;
    @Shadow
    private boolean integratedServerRunning;
    @Shadow
    private IntegratedServer server;

    @Shadow
    public abstract boolean isInSingleplayer();

    @Shadow
    public abstract ClientPlayNetworkHandler getNetworkHandler();

    private boolean warnBreak( ItemStack itemStack )
    {
        if (itemStack.isEmpty() || !itemStack.isDamageable() || 0 == itemStack.getDamage() || 10 < (itemStack.getMaxDamage() - itemStack.getDamage()) )
        {
            return false;
        }

        String warnText = "Achtung! Weiterer Gebrauch könnte das Item zerstören!";

        if (itemStack.getItem() instanceof MiningToolItem) warnText = Utils.randomString(messagesTools);
        else if(itemStack.getItem() instanceof ToolItem || itemStack.getItem() instanceof RangedWeaponItem)
            warnText = Utils.randomString(messagesCombat);

        player.sendMessage(Text.literal("§e§l" + warnText), true);
        player.playSound(SoundEvents.ENTITY_POLAR_BEAR_WARNING, SoundCategory.MASTER, 1.0f, 1.0f);

        return true;
    }

    @Inject( method = "doItemUse", at = @At( "HEAD" ), cancellable = true )
    public void onDoItemUse(final CallbackInfo callbackInfo)
    {
        warnBreak(player.getInventory().getMainHandStack());
    }

    @Inject( method = "doAttack", at = @At( "HEAD" ), cancellable = true )
    public void onDoAttack(final CallbackInfoReturnable<Boolean> cir)
    {
        warnBreak(player.getInventory().getMainHandStack());
    }

    @Inject( method = "handleBlockBreaking", at = @At( "HEAD" ), cancellable = true )
    public void onHandleBlockBreaking( boolean breaking, final CallbackInfo callbackInfo )
    {
        if ( breaking )
        {
            warnBreak(player.getInventory().getMainHandStack());
        }
    }


    @Inject( method = "joinWorld", at = @At( "RETURN" ) )
    public void joinWorld( ClientWorld world, final CallbackInfo callbackInfo )
    {
        Topbar.LOGGER.warn("WORLD CHANGED TO: " + world.getDimension().toString());
        TopbarClient.getInstance().updateTopBar();
    }
}
