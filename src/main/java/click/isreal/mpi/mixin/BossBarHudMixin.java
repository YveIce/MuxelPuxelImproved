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

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(BossBarHud.class)
public class BossBarHudMixin
{
  @Shadow
  private static Identifier BARS_TEXTURE = new Identifier("textures/gui/bars.png");
  @Shadow
  Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();
  @Shadow
  private MinecraftClient client;

  @Shadow
  private void renderBossBar(DrawContext context, int x, int y, BossBar bossBar)
  {
  }

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void renderInject(DrawContext context, CallbackInfo callbackInfo)
  {
    if (!this.bossBars.isEmpty())
    {
      int i = this.client.getWindow().getScaledWidth();
      int j = 24;
      Iterator var4 = this.bossBars.values().iterator();

      while (var4.hasNext())
      {
        ClientBossBar clientBossBar = (ClientBossBar) var4.next();
        int k = i / 2 - 91;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BARS_TEXTURE);
        this.renderBossBar(context, k, j, clientBossBar);
        Text text = clientBossBar.getName();
        int m = this.client.textRenderer.getWidth(text);
        int n = i / 2 - m / 2;
        int o = j - 9;
        context.drawTextWithShadow(this.client.textRenderer, text, n, o, 16777215);
        Objects.requireNonNull(this.client.textRenderer);
        j += 22 + 9;
        if (j >= this.client.getWindow().getScaledHeight() / 3)
        {
          break;
        }
      }
    }
    callbackInfo.cancel();
  }
}
