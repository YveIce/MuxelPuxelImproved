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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.VertexFormat;

import static net.minecraft.client.render.VertexFormats.POSITION_ELEMENT;
import static net.minecraft.client.render.VertexFormats.TEXTURE_ELEMENT;

public class Shaders
{
  // special version of POSITION_TEXTURE with 6 textures for skybox
  private static final MinecraftClient client = MinecraftClient.getInstance();
  public static final VertexFormat POSITION_6TEXTURE = new VertexFormat(
      ImmutableMap.<String, VertexFormatElement>builder()
      .put("Position", POSITION_ELEMENT)
      .put("UV0", TEXTURE_ELEMENT)
      .put("UV1", TEXTURE_ELEMENT)
      .put("UV2", TEXTURE_ELEMENT)
      .put("UV3", TEXTURE_ELEMENT)
      .put("UV4", TEXTURE_ELEMENT)
      .put("UV5", TEXTURE_ELEMENT)
    .build()
  );
  public static ShaderProgram shaderFlesh;
  public static ShaderProgram shaderMagicclouds;
  public static ShaderProgram shaderSpace;
  static public void registerShaders()
  {
    CoreShaderRegistrationCallback.EVENT.register(context -> {
      context.register(new Identifier("mpi", "flesh"), VertexFormats.POSITION, program -> shaderFlesh = program);
      context.register(new Identifier("mpi", "magicclouds"), VertexFormats.POSITION, program -> shaderMagicclouds = program);
      context.register(new Identifier("mpi", "space"), VertexFormats.POSITION, program -> shaderSpace = program);
    });
  }

  static public void drawShader(ShaderProgram shaderProgram, int x, int y, int width, int height)
  {
    RenderSystem.setShader(() -> shaderProgram);
    RenderSystem.setShaderGameTime(System.currentTimeMillis() / 50L, 0);
    RenderSystem.enableBlend();
    // RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    //RenderSystem.setShaderTexture();

    Matrix4f matrix4f = new Matrix4f(); //. .setPerspective(1.f, (float)client.getWindow().getFramebufferWidth() / (float)client.getWindow().getFramebufferHeight(), 0.05f, 10.0f);
    RenderSystem.backupProjectionMatrix();
    RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE);
    BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
    if (VertexFormats.POSITION.equals(shaderProgram.getFormat()))
    {
      bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
      bufferBuilder.vertex(matrix4f, x, y, 0).next();
      bufferBuilder.vertex(matrix4f, x, y + height, 0).next();
      bufferBuilder.vertex(matrix4f, x + width, y + height, 0).next();
      bufferBuilder.vertex(matrix4f, x + width, y, 0).next();
    }
    else if (POSITION_6TEXTURE.equals(shaderProgram.getFormat()))
    {
      setSkyBoxTextures();
      bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      bufferBuilder.vertex(matrix4f, x, y, 0).texture(0.0f, 0.0f).next();
      bufferBuilder.vertex(matrix4f, x, y + height, 0).texture(0.0f, 1.0f).next();
      bufferBuilder.vertex(matrix4f, x + width, y + height, 0).texture(1.0f, 1.0f).next();
      bufferBuilder.vertex(matrix4f, x + width, y, 0).texture(1.0f, 0.0f).next();
    }
    RenderSystem.restoreProjectionMatrix();
    RenderSystem.applyModelViewMatrix();
    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    RenderSystem.disableBlend();
  }

  static public void drawShaderFullscreen(ShaderProgram shader)
  {
    drawShader(shader, 0, 0, MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
  }

  private static void setSkyBoxTextures()
  {
    RenderSystem.setShaderTexture(0, new Identifier("minecraft","textures/gui/title/background/panorama_0.png"));
    RenderSystem.setShaderTexture(1, new Identifier("minecraft","textures/gui/title/background/panorama_1.png"));
    RenderSystem.setShaderTexture(2, new Identifier("minecraft","textures/gui/title/background/panorama_2.png"));
    RenderSystem.setShaderTexture(3, new Identifier("minecraft","textures/gui/title/background/panorama_3.png"));
    RenderSystem.setShaderTexture(4, new Identifier("minecraft","textures/gui/title/background/panorama_4.png"));
    RenderSystem.setShaderTexture(5, new Identifier("minecraft","textures/gui/title/background/panorama_5.png"));
  }
}
