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

import click.isreal.topbar.client.TopbarClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment( EnvType.CLIENT )
@Mixin( ClientLoginNetworkHandler.class )
public class ClientLoginNetworkHandlerMixin
{

    @Shadow
    @Final
    private ClientConnection connection;


    @Inject( method = "onLoginSuccess", at = @At( "HEAD" ), cancellable = true )
    public void onLoginSuccess( LoginSuccessS2CPacket packet, final CallbackInfo callbackInfo )
    {
        boolean isMixel = (connection != null && connection.getAddress().toString().matches(".*45\\.135\\.203\\.18.*"));
        TopbarClient.getInstance().setisMixel(isMixel);
    }

    @Inject( method = "onDisconnected", at = @At( "HEAD" ), cancellable = true )
    public void onDisconnected( Text reason, final CallbackInfo callbackInfo )
    {
        TopbarClient.getInstance().setisMixel(false);
    }

    @Inject( method = "onDisconnect", at = @At( "HEAD" ), cancellable = true )
    public void onDisconnect( LoginDisconnectS2CPacket packet, final CallbackInfo callbackInfo )
    {
        TopbarClient.getInstance().setisMixel(false);
    }


}
