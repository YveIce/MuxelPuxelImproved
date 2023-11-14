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

import click.isreal.mpi.client.DataManager;
import click.isreal.mpi.client.mpiClient;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
  private final mpiClient mpiClient = click.isreal.mpi.client.mpiClient.getInstance();

  public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile)
  {
    super(world, profile);
  }

  @Shadow
  public abstract boolean isMainPlayer();

  @Shadow protected abstract boolean isCamera();

  @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
  public void tickInject(CallbackInfo ci)
  {
    if (mpiClient.isMixelPixel() && this.isMainPlayer() && this.isCamera())
    {
      mpiClient.getDataManager().dataList.put(
          DataManager.DataId.POSX,
          mpiClient.getDataManager().dataList.get(DataManager.DataId.POSX).setValue(String.valueOf(this.getBlockX()))
      );
      mpiClient.getDataManager().dataList.put(
          DataManager.DataId.POSY,
          mpiClient.getDataManager().dataList.get(DataManager.DataId.POSY).setValue(String.valueOf(this.getBlockY()))
      );
      mpiClient.getDataManager().dataList.put(
          DataManager.DataId.POSZ,
          mpiClient.getDataManager().dataList.get(DataManager.DataId.POSZ).setValue(String.valueOf(this.getBlockZ()))
      );
    }
  }


}
