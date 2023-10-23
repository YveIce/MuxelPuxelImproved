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
import click.isreal.mpi.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.util.function.IntSupplier;

@Environment(EnvType.CLIENT)
@Mixin(SplashOverlay.class)
public class SplashOverlayMixin
{
  @Shadow
  private static final IntSupplier BRAND_ARGB = () -> Config.getInstance().getLoadscreenColor();
  //todo: fixme: we need to load our own texture early. I think DefaultResourcePack functions needs to be patched
  @Shadow
  private static Identifier LOGO = new Identifier("textures/gui/title/mojangstudios.png");

  @Inject(method ="init", at = @At("HEAD"))
  private static void initInject(MinecraftClient client, CallbackInfo ci)
  {
    /*
    InputSupplier<InputStream> inputSupplier = client.getDefaultResourcePack().open(ResourceType.CLIENT_RESOURCES, new Identifier("mpi","textures/gui/title/splashlogo.png"));
    if (null != inputSupplier)
    {
      LOGO = new Identifier("mpi","textures/gui/title/splashlogo.png");
    }
     */
  }
}
