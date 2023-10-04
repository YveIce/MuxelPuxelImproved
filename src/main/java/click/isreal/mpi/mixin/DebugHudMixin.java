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

import click.isreal.mpi.Mpi;
import click.isreal.mpi.client.mpiClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.MetricsData;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public class DebugHudMixin
{
  @Shadow
  private MinecraftClient client;
  @Shadow
  private HitResult blockHit;
  @Shadow
  private HitResult fluidHit;
  @Shadow
  private void drawLeftText(DrawContext context) {}
  @Shadow
  private void drawRightText(DrawContext context) {}

  @Shadow
  private void drawMetricsData(DrawContext context, MetricsData metricsData, int x, int width, boolean showFps) {}

  @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
  public void getLeftTextInject(CallbackInfoReturnable<List<String>> cir)
  {
    if (mpiClient.getInstance().isMixelPixel())
    {
      List<String> leftText = cir.getReturnValue();
      // add empty lines on top, so we got space for the topbar
      leftText.add(0, "");
      leftText.add(0, "");
      cir.setReturnValue(leftText);
    }
  }
  @Inject(method = "getRightText", at = @At("RETURN"), cancellable = true)
  public void getRightTextInject(CallbackInfoReturnable<List<String>> cir)
  {
    if (mpiClient.getInstance().isMixelPixel())
    {
      List<String> rightText = cir.getReturnValue();
      // add empty lines on top, so we got space for the topbar
      rightText.add(0, "");
      rightText.add(0, "");
      cir.setReturnValue(rightText);
    }
  }
}
