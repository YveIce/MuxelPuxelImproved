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

package click.isreal.mpi.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.io.File;

import static click.isreal.mpi.Mpi.LOGGER;
import static net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC;

public class Screenshoot
{
  private static final MinecraftClient client = MinecraftClient.getInstance();

  public static Text takePanorama(File directory, int width, int height)
  {
    Framebuffer orgFramebuffer = client.getFramebuffer();
    int framebufferWidth = client.getWindow().getFramebufferWidth();
    int framebufferHeight = client.getWindow().getFramebufferHeight();

    SimpleFramebuffer framebuffer = new SimpleFramebuffer(width, height, true, IS_SYSTEM_MAC);
    client.framebuffer = framebuffer;

    float playerPitch = client.player.getPitch();
    float playerYaw = client.player.getYaw();
    float playerPrevPitch = client.player.prevPitch;
    float playerPrevYaw = client.player.prevYaw;
    float playerYawFixed = ((Math.round(playerYaw / 90.f) * 90.f) + 360.f) % 360.f;
    String filenamePrefix = Util.getFormattedCurrentTime() + "_";

    client.gameRenderer.setBlockOutlineEnabled(false);
    try
    {
      client.gameRenderer.setRenderingPanorama(true);

        client.worldRenderer.reloadTransparencyPostProcessor();
        client.getWindow().setFramebufferWidth(width);
        client.getWindow().setFramebufferHeight(height);

      for (int sideIndex = 0; sideIndex < 6; ++sideIndex)
      {
        switch (sideIndex)
        {
          case 0:
          {
            client.player.setYaw(playerYawFixed);
            client.player.setPitch(0.0f);
            break;
          }
          case 1:
          {
            client.player.setYaw((playerYawFixed + 90.0f) % 360.0f);
            client.player.setPitch(0.0f);
            break;
          }
          case 2:
          {
            client.player.setYaw((playerYawFixed + 180.0f) % 360.0f);
            client.player.setPitch(0.0f);
            break;
          }
          case 3:
          {
            client.player.setYaw((playerYawFixed - 90.0f) % 360.0f);
            client.player.setPitch(0.0f);
            break;
          }
          case 4:
          {
            client.player.setYaw(playerYawFixed);
            client.player.setPitch(-90.0f);
            break;
          }
          default:
          {
            client.player.setYaw(playerYawFixed);
            client.player.setPitch(90.0f);
          }
        }
        client.player.prevYaw = client.player.getYaw();
        client.player.prevPitch = client.player.getPitch();
        if (!FabricLoader.getInstance().isModLoaded("iris"))
        {
          framebuffer.beginWrite(true);
        }
        client.gameRenderer.renderWorld(1.0f, 0L, new MatrixStack());
        try
        {
          Thread.sleep(100L);
        }
        catch (InterruptedException interruptedException)
        {
          // empty catch block
        }
        ScreenshotRecorder.saveScreenshot(directory, filenamePrefix + "panorama_" + sideIndex + ".png", framebuffer, message -> {
        });
      }
      MutableText text = Text.literal(directory.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, directory.getAbsolutePath())));
      MutableText mutableText = Text.translatable("screenshot.success", text);
      return mutableText;
    }
    catch (Exception exception)
    {
      LOGGER.error("Couldn't save image", exception);
      MutableText mutableText = Text.translatable("screenshot.failure", exception.getMessage());
      return mutableText;
    }
    finally
    {
      client.player.setPitch(playerPitch);
      client.player.setYaw(playerYaw);
      client.player.prevPitch = playerPrevPitch;
      client.player.prevYaw = playerPrevYaw;
      client.gameRenderer.setBlockOutlineEnabled(true);

      framebuffer.delete();
      client.gameRenderer.setRenderingPanorama(false);

        client.getWindow().setFramebufferWidth(framebufferWidth);
        client.getWindow().setFramebufferHeight(framebufferHeight);
        client.worldRenderer.reloadTransparencyPostProcessor();
      client.framebuffer = orgFramebuffer;
      client.getFramebuffer().beginWrite(true);
    }
  }
}
