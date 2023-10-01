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
  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void renderInject(DrawContext context, CallbackInfo ci)
  {
    this.client.getProfiler().push("debug");
    Entity entity = this.client.getCameraEntity();
    this.blockHit = entity.raycast(20.0, 0.0F, false);
    this.fluidHit = entity.raycast(20.0, 0.0F, true);
    context.draw(() -> {
      this.drawLeftText(context);
      this.drawRightText(context);
      if (this.client.options.debugTpsEnabled) {
        int i = context.getScaledWindowWidth();
        this.drawMetricsData(context, this.client.getMetricsData(), 0, i / 2, true);
        IntegratedServer integratedServer = this.client.getServer();
        if (integratedServer != null) {
          this.drawMetricsData(context, integratedServer.getMetricsData(), i - Math.min(i / 2, 240), i / 2, false);
        }
      }

    });
    this.client.getProfiler().pop();
  }

  /*
  @ModifyVariable(method = "renderLeftText", at = @At("STORE"), ordinal = 1)
  private int renderLeftTextInject(final int j)
  {
    return 19;
  }

  @ModifyVariable(method = "renderRightText", at = @At("STORE"), ordinal = 1)
  private int renderightTextInject(final int j)
  {
    return 19;
  }
*/
}
