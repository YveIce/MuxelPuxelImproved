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
import click.isreal.mpi.client.DataManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
  @Inject(method = "onWorldTimeUpdate", at = @At("RETURN"), cancellable = false)
  public void onWorldTimeUpdateInject(WorldTimeUpdateS2CPacket packet, CallbackInfo ci)
  {
    // doing it here, so we only update it, when changed => better performance
    // YVE: mctime is 0-24000 starting at 6:00am, full run is 20min RT
    long remotetime = packet.getTimeOfDay() % 24000L;
    if (0 > remotetime) remotetime *= -1L; // server can send negativ values
    long mctime = (long) ((float)remotetime / 24000.f * 86399.f); // yes i hate this random java math behaive
    DataManager.getInstance().setTimeMc(LocalTime.ofSecondOfDay(mctime).plusHours(6).format(DateTimeFormatter.ofPattern("HH:mm")));
  }
}
