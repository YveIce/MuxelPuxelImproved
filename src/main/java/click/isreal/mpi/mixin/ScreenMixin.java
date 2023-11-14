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

import click.isreal.mpi.Utils;
import click.isreal.mpi.config.Config;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Screen.class)
public class ScreenMixin
{
  private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
  @Shadow
  int width;
  @Shadow
  int height;

  @Intrinsic // YVE: Methode auch für Abgeleitete Klassen verwenden.
  @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
  public void renderBackgroundTextureInject(DrawContext context, CallbackInfo ci)
  {
    if (Config.getInstance().getSolidBackgroundEnabled())
    {
      ci.cancel();
      int color = Config.getInstance().getLoadscreenColor();
      Utils.drawColorRect(context.getMatrices(), 0, 0, width, height, color);

      RenderSystem.enableBlend();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
      context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

      context.drawTexture(VIGNETTE_TEXTURE, 0, 0, 0, 0.0f, 0.0f,  context.getScaledWindowWidth(),  context.getScaledWindowHeight(),  context.getScaledWindowWidth(),  context.getScaledWindowHeight());
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      //context.fill(0, 0, this.width, this.height, Config.getInstance().getLoadscreenColor());
    }
  }
}
