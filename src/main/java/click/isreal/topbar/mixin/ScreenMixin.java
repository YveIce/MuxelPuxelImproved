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
import click.isreal.topbar.client.TopbarClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment( EnvType.CLIENT )
@Mixin( Screen.class )
public class ScreenMixin
{
    @Shadow
    protected MinecraftClient client;

    @Inject( method = "sendMessage", at = {@At( "HEAD" )}, cancellable = true )
    public void sendMessage( String message, final CallbackInfo ci )
    {
        Topbar.LOGGER.log(Level.WARN, "SendMessage: " + message);
        if ( TopbarClient.getInstance().isMixelPixel() && Topbar.getInstance().isPreventFalseCommands() && message.matches("^(7|t\\/).*") )
        {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_DONKEY_ANGRY, 1.0F, 1.0F));
            this.client.inGameHud.getChatHud().addMessage(new LiteralText("" + Formatting.LIGHT_PURPLE + Formatting.BOLD + "Yve™: " + Formatting.YELLOW + Formatting.ITALIC + "Die Nachricht wurde zu deinem Schutz nicht gesendet, \nda du vermutlich einen Command mit " + Formatting.RED + Formatting.BOLD + Formatting.ITALIC + "/" + Formatting.YELLOW + Formatting.ITALIC + " verschicken wolltest."));
            ci.cancel();
        }
    }

    @Inject( method = "renderOrderedTooltip", at = {@At( "HEAD" )}, cancellable = true )
    public void renderOrderedTooltip( MatrixStack matrices, List<? extends OrderedText> lines, int x, int y, CallbackInfo callbackInfo )
    {
        if ( !lines.isEmpty() )
        {
            boolean isEmpty = true;
            for ( int i = 0; i < lines.stream().count(); i++ )
            {
                if ( lines.get(i).toString().trim().length() > 0 ) isEmpty = false;
            }
            if ( isEmpty ) lines.clear();
        }
    }

    @Inject( method = "renderTooltip", at = {@At( "HEAD" )}, cancellable = true )
    public void renderTooltip( MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo callbackInfo )
    {
        if ( stack.getName().getString().trim().length() == 0 )
        {
            callbackInfo.cancel();
        }
    }
}
