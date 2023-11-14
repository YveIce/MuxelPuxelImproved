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

import click.isreal.mpi.client.BarHud;
import click.isreal.mpi.client.mpiClient;
import click.isreal.mpi.config.Config;
import click.isreal.mpi.domain.MixelWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
  final Config config = Config.getInstance();
  @Shadow
  @Final
  private MinecraftClient client;
  private BarHud barHud;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void initInject(MinecraftClient client, ItemRenderer itemRenderer, CallbackInfo ci)
  {
    this.barHud = new BarHud(client);
  }

  @Inject(method = "renderHotbar", at = @At(value = "HEAD"))
  public void renderHotbarInject(float tickDelta, DrawContext context, CallbackInfo ci)
  {
    if (mpiClient.getInstance().isMixelPixel())
    {
      this.barHud.render(context);
    }
  }

  /*
  Shifts down the Status-Effect Symbols, if TopBar is enabled
   */
  @ModifyConstant(method = "renderStatusEffectOverlay", constant = @Constant(intValue = 1))
  private int renderStatusEffectOverlayModL(int l)
  {
    if (mpiClient.getInstance().isMixelPixel())
    {
      return config.getTopShift() + 1;
    }
    else
    {return l;}
  }

  @Inject(method = {"renderScoreboardSidebar"}, at = {@At("HEAD")}, cancellable = true)
  private void renderScoreboardSidebarInject(DrawContext context, ScoreboardObjective objective, final CallbackInfo callbackInfo)
  {
    if ( mpiClient.getInstance().isMixelPixel() && mpiClient.getInstance().getWorld() != MixelWorld.OTHER)
    {
      if (callbackInfo.isCancellable() && !config.getRenderScoreboard() ) callbackInfo.cancel();
    }
  }
}



