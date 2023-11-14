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

import click.isreal.mpi.client.SplashLogoTexture;
import click.isreal.mpi.config.Config;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

@Environment(EnvType.CLIENT)
@Mixin(SplashOverlay.class)
public class SplashOverlayMixin
{
  @Shadow
  public static final IntSupplier BRAND_ARGB = () -> Config.getInstance().getLoadscreenColor();
  private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
  //private static final Identifier BG = SplashBgTexture.SPLASHBG;
  //@Shadow
  //private static Identifier LOGO = SplashLogoTexture.SPLASHLOGO;
  @Shadow
  @Final
  private MinecraftClient client;
  @Shadow
  @Final
  private boolean reloading;
  @Shadow
  @Final
  private ResourceReload reload;
  @Shadow
  private float progress;
  @Shadow
  private long reloadCompleteTime;
  @Shadow
  private long reloadStartTime;
  @Shadow
  @Final
  private Consumer<Optional<Throwable>> exceptionHandler;

  @Inject(method = "init", at = @At("HEAD"))
  private static void initInject(MinecraftClient client, CallbackInfo ci)
  {
    client.getTextureManager().registerTexture(SplashLogoTexture.SPLASHLOGO, new SplashLogoTexture());
  }

  private static int withAlpha(int color, int alpha)
  {
    return color & 0xFFFFFF | alpha << 24;
  }

  private void renderProgressBar(DrawContext drawContext, int minX, int minY, int maxX, int maxY, float opacity)
  {
    int i = MathHelper.ceil((float) (maxX - minX - 2) * this.progress);
    int j = Math.round(opacity * 255.0f);

    int k = ColorHelper.Argb.getArgb(j, 255, 255, 255);
    drawContext.fill(minX + 2, minY + 2, minX + i, maxY - 2, k);
    drawContext.fill(minX + 1, minY, maxX - 1, minY + 1, k);
    drawContext.fill(minX + 1, maxY, maxX - 1, maxY - 1, k);
    drawContext.fill(minX, minY, minX + 1, maxY, k);
    drawContext.fill(maxX, minY, maxX - 1, maxY, k);
  }

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci)
  {
    ci.cancel();
    context.getMatrices().push();
    float opacity;
    int bgColor = 0;
    int scaledWindowWidth = context.getScaledWindowWidth();
    int scaledWindowHeight = context.getScaledWindowHeight();
    long measuringTimeMs = Util.getMeasuringTimeMs();
    if (this.reloading && this.reloadStartTime == -1L)
    {
      this.reloadStartTime = measuringTimeMs;
    }
    float f = this.reloadCompleteTime > -1L ? (float) (measuringTimeMs - this.reloadCompleteTime) / 1000.0f : -1.0f;
    float progress = this.reloadStartTime > -1L ? (float) (measuringTimeMs - this.reloadStartTime) / 500.0f : -1.0f;
    if (f >= 1.0f)
    {
      if (this.client.currentScreen != null)
      {
        this.client.currentScreen.render(context, 0, 0, delta);
      }
      bgColor = withAlpha(BRAND_ARGB.getAsInt(), MathHelper.ceil((1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f)) * 255.0f));
      context.fill(RenderLayer.getGuiOverlay(), 0, 0, scaledWindowWidth, scaledWindowHeight, bgColor);
      opacity = 1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f);
    }
    else if (this.reloading)
    {
      if (this.client.currentScreen != null && progress < 1.0f)
      {
        this.client.currentScreen.render(context, mouseX, mouseY, delta);
      }
      bgColor = withAlpha(BRAND_ARGB.getAsInt(), MathHelper.ceil(MathHelper.clamp(progress, 0.15, 1.0) * 255.0));
      context.fill(RenderLayer.getGuiOverlay(), 0, 0, scaledWindowWidth, scaledWindowHeight, bgColor);
      opacity = MathHelper.clamp(progress, 0.0f, 1.0f);
    }
    else
    {
      bgColor = BRAND_ARGB.getAsInt();
      float bgColorR = (float) (bgColor >> 16 & 0xFF) / 255.0f;
      float bgColorG = (float) (bgColor >> 8 & 0xFF) / 255.0f;
      float bgColorB = (float) (bgColor & 0xFF) / 255.0f;
      GlStateManager._clearColor(bgColorR, bgColorG, bgColorB, 1.0f);
      GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
      opacity = 1.0f;
    }
    float t = this.reload.getProgress();
    this.progress = MathHelper.clamp(this.progress * 0.95f + t * 0.050000012f, 0.0f, 1.0f);
    int xCenter = (int) ((double) context.getScaledWindowWidth() * 0.5);
    int yCenter = (int) ((double) context.getScaledWindowHeight() * 0.5);
    int y = (int) (yCenter *(1.0-this.progress) + 30);
    double d = Math.min((double) context.getScaledWindowWidth() * 0.75, context.getScaledWindowHeight()) * 0.25;
    int q = (int) (d * 0.5);
    int r = (int) (128 + (d * (1.0-this.progress)));
    //RenderSystem.disableDepthTest();
    //RenderSystem.depthMask(false);
    //RenderSystem.setShaderTexture(0, SplashLogoTexture.SPLASHLOGO);
    RenderSystem.enableBlend();
    //RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);

    float bgColorR = (float) (bgColor >> 16 & 0xFF) / 255.0f;
    float bgColorG = (float) (bgColor >> 8 & 0xFF) / 255.0f;
    float bgColorB = (float) (bgColor & 0xFF) / 255.0f;
    boolean invert = 0.75<(bgColorR+bgColorG+bgColorB)/3;

    //context.setShaderColor(1.0f, 1.0f, 1.0f, opacity);

    if(invert)
    {
      //RenderSystem.enableColorLogicOp();
      context.setShaderColor(0.0f, 0.0f, 0.0f, 1.f);
      //RenderSystem.logicOp(GlStateManager.LogicOp.INVERT);
      //RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
    }

//    context.drawTexture(SplashLogoTexture.SPLASHLOGO, xCenter - r, yCenter - q, r, (int) d, 0.f, 0.0f, 1024, 512, 1024, 1024);
//    context.drawTexture(SplashLogoTexture.SPLASHLOGO, xCenter, yCenter - q, r, (int) d, 0.f, 512.f, 1024, 512, 1024, 1024);
    context.drawTexture(SplashLogoTexture.SPLASHLOGO, xCenter - r, y, r, (int) (r * 0.5f), 0.f, 0.0f, 1024, 512, 1024, 1024);
    context.drawTexture(SplashLogoTexture.SPLASHLOGO, xCenter, y, r, (int) (r * 0.5f), 0.f, 512.f, 1024, 512, 1024, 1024);

    context.setShaderColor(1.0f, 1.0f, 1.0f, opacity);

    context.getMatrices().pop();

    RenderSystem.disableDepthTest();
    RenderSystem.depthMask(false);
    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

    //RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
    context.drawTexture(VIGNETTE_TEXTURE, 0, 0, 0, 0.0f, 0.0f, scaledWindowWidth, scaledWindowHeight, scaledWindowWidth, scaledWindowHeight);


    RenderSystem.defaultBlendFunc();
    RenderSystem.disableBlend();
    RenderSystem.depthMask(true);
    RenderSystem.enableDepthTest();


    int s = (int) ((int) yCenter + d + 5); //((double) context.getScaledWindowHeight() * 0.8325);

    if (f < 1.0f)
    {
      this.renderProgressBar(context, (int) (xCenter - r*0.8f), s - 6, (int) (xCenter + r*0.8f), s + 6, 1.0f - MathHelper.clamp(f, 0.0f, 1.0f));
    }
    if (f >= 2.0f)
    {
      this.client.setOverlay(null);
    }
    if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || progress >= 2.0f))
    {
      try
      {
        this.reload.throwException();
        this.exceptionHandler.accept(Optional.empty());
      }
      catch (Throwable throwable)
      {
        this.exceptionHandler.accept(Optional.of(throwable));
      }
      this.reloadCompleteTime = Util.getMeasuringTimeMs();
      if (this.client.currentScreen != null)
      {
        this.client.currentScreen.init(this.client, context.getScaledWindowWidth(), context.getScaledWindowHeight());
      }
    }

  }

}
