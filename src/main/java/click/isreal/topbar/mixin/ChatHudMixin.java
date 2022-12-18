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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment( EnvType.CLIENT )
@Mixin( ChatHud.class )
public abstract class ChatHudMixin
{
    @Shadow
    private final long lastMessageAddedTime = 0L;

    @Shadow
    public abstract boolean isChatFocused();

    @Shadow
    public abstract boolean isChatHidden();

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract double getChatScale();

    @Shadow
    public abstract void addMessage( Text message );

    @Shadow
    public abstract Style getText( double x, double y );
/*
  @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
  public void mouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> callbackInfoReturnable)
  {
    if ( this.isChatFocused() &&
         !this.client.options.hudHidden &&
         !this.isChatHidden() &&
         !this.messageQueue.isEmpty() )
    {
      Topbar.LOGGER.info("Msg clicked");
      double relX = mouseX - 2.0D;
      double relY = this.client.getWindow().getScaledHeight() - mouseY - 40.0D;
      if ( relX <= ( double ) MathHelper.floor(( double ) this.getWidth() / this.getChatScale()) &&
           relY < 0.0D &&
           relY > ( double ) MathHelper.floor(-9.0D * this.getChatScale()) )
      {
        Style style = this.getText(mouseX, mouseY);
        if ( style != null ) Topbar.LOGGER.info("Msg clicked:\n" + style);
      }
    }
  }
*/
}

