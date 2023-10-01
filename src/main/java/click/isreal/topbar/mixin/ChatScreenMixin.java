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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment( EnvType.CLIENT )
@Mixin( ChatScreen.class )
public class ChatScreenMixin
{
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Inject( method = "mouseClicked", at = @At( "HEAD" ), cancellable = true )
    public void mouseClicked( double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callbackInfoReturnable )
    {
        Topbar.LOGGER.info(String.format("ChatScreen->mouseClicked(X: %s, Y: %s, Button: %s)", mouseX, mouseY, button));
        //Topbar.LOGGER.info( getMessage(mouseX,mouseY) );
    }

    /*public ChatHudLine getMessage( double x, double y )
    {
        ChatHud chatHud = this.client.inGameHud.getChatHud();
        if ( ((ChatHudInvoker) chatHud).isChatFocusedInvoker() && !this.client.options.hudHidden && !((ChatHudInvoker) chatHud).isChatHiddenInvoker() )
        {
            double relX = x - 2.0D;
            double relY = (double) this.client.getWindow().getScaledHeight() - y - 40.0D;
            relX = MathHelper.floor(relX / chatHud.getChatScale());
            relY = MathHelper.floor(relY / (chatHud.getChatScale() *
                    (this.client.options.getChatLineSpacing().getValue() + 1.0D)));
            if ( !(relX < 0.0D) && !(relY < 0.0D) )
            {
                List<Text> visibleMessages = (List<Text>) ((ChatHudAccessor) chatHud).getMessageQueue();

                int i = Math.min(chatHud.getVisibleLineCount(), visibleMessages.size());
                if ( relX <= (double) MathHelper.floor((double) chatHud.getWidth() / chatHud.getChatScale()) )
                {
                    this.client.textRenderer.getClass();
                    if ( relY < (double) (9 * i + i) )
                    {
                        this.client.textRenderer.getClass();
                        int msgIndex = (int) (relY / 9.0D + (double) ((ChatHudAccessor) chatHud).getScrolledLines());
                        if ( msgIndex >= 0 && msgIndex < visibleMessages.size() )
                        {
                            Text chatHudLine = (visibleMessages.get(msgIndex);
                            return chatHudLine;
                        }
                    }
                }

                return null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }*/

}
