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

import click.isreal.mpi.client.Shaders;
import click.isreal.mpi.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RotatingCubeMapRenderer.class)
public class RotatingCubeMapRendererMixin
{
  @Shadow
  MinecraftClient client;

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void renderInject(float delta, float alpha, CallbackInfo ci)
  {
    // float f = (float) ((double) delta * this.client.options.getPanoramaSpeed().getValue());
    switch (Config.getInstance().getTitlescreenTheme())
    {
      case "magicclouds":
        Shaders.drawShaderFullscreen(Shaders.shaderMagicclouds);
        ci.cancel();
        break;
      case "space":
        Shaders.drawShaderFullscreen(Shaders.shaderSpace);
        ci.cancel();
        break;
      case "evilyoungflesh":
        Shaders.drawShaderFullscreen(Shaders.shaderFlesh);
        ci.cancel();
        break;

    }
    //Shaders.drawShaderFullscreen(Shaders.shaderSpace);
    //ci.cancel();
  }
}
