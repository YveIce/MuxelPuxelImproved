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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(OrderedTextTooltipComponent.class)
public class OrderedTextTooltipComponentMixin
{
  @Shadow
  private OrderedText text;

  public OrderedTextTooltipComponentMixin(OrderedText text)
  {
    this.text = text;
  }

  // prevent to draw empty tooltips TEXT (i know, didn't sounds like it makes any sense ;-P
  @Inject(method = "drawText", at = {@At("HEAD")}, cancellable = true)
  public void drawTextInject(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers, CallbackInfo ci)
  {
    StringBuilder stringBuilder = new StringBuilder();
    this.text.accept((index, style, codePoint) -> {
      stringBuilder.append((char) codePoint);
      return true;
    });
    if (stringBuilder.toString().strip().trim().length() <= 1)
    {
      ci.cancel();
    }
  }

  // set width to 0 on empty strings, so we can prevent tooltip background draw in TooltipBackgroundRendererMixin
  @Inject(method = "getWidth", at = {@At("HEAD")}, cancellable = true)
  public void getWidthInject(TextRenderer textRenderer, CallbackInfoReturnable<Integer> cir)
  {
    StringBuilder stringBuilder = new StringBuilder();
    this.text.accept((index, style, codePoint) -> {
      stringBuilder.append((char) codePoint);
      return true;
    });
    if (stringBuilder.toString().strip().trim().length() <= 1)
    {
      cir.setReturnValue(0);
    }
  }
}
