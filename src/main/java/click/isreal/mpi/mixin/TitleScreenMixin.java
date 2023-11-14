/*
 * MIT License
 *
 * Copyright (c) 2022-2023 YveIce, Enrico Messall
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
 */

package click.isreal.mpi.mixin;

import click.isreal.mpi.client.MPUpdatesScrapper;
import click.isreal.mpi.client.MpiScrollableTextWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen
{
  protected TitleScreenMixin(Text title)
  {
    super(title);
  }

  @Shadow
  public abstract void tick();

  @Shadow
  @Nullable
  private SplashTextRenderer splashText;

  private void initUpdatebox()
  {
    if (null != MPUpdatesScrapper.getInstance().getUpdates())
    {
      int top = 2;
      int left = 2;
      int bottom = client.getWindow().getScaledHeight() - 2 - textRenderer.fontHeight;
      int height = bottom - top;
      int width = Math.min((this.width / 2) - 137, 200);
      Text message = Texts.join(MPUpdatesScrapper.getInstance().getUpdates(), Text.literal("\n"));
      if (120 < width)
      {
        this.addDrawableChild(new MpiScrollableTextWidget(left, top, width, height, message, textRenderer));
      }
    }
  }

  @Inject(method = "initWidgetsNormal", at = @At(value = "RETURN"))
  public void initWidgetsNormalInject(int y, int spacingY, CallbackInfo ci)
  {
    initUpdatebox();
  }

  @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LogoDrawer;draw(Lnet/minecraft/client/gui/DrawContext;IF)V"))
  public void renderInject(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci)
  {
    if (null == MPUpdatesScrapper.getInstance().getUpdates())
    {
      initUpdatebox();
    }
    // disable ugly splashtext for now
    this.splashText = null;

  }
}
