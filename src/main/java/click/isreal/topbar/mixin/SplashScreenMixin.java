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

import click.isreal.topbar.Topbar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment( EnvType.CLIENT )
@Mixin( SplashScreen.class )
public class SplashScreenMixin
{
    @Shadow
    private static int BRAND_RGB;
    @Shadow
    private static int BRAND_ARGB;

    @Shadow
    private static Identifier LOGO;

    private boolean logoinit = false;

    @Inject( method = "init", at = @At( "HEAD" ), cancellable = true )
    private static void init( MinecraftClient client, CallbackInfo callbackInfo )
    {
    }

    @Inject( method = "render", at = @At( "HEAD" ) )
    public void render( MatrixStack matrices, int mouseX, int mouseY, float delta, final CallbackInfo callbackInfo )
    {
        if ( !logoinit )
        {
            LOGO = new Identifier("textures/gui/title/mixelpixel.png");
            logoinit = true;
        }

        BRAND_ARGB = Topbar.getInstance().getLoadscreenColor();
        BRAND_RGB = BRAND_ARGB & 16777215;
    }

}
