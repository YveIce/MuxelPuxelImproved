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

package click.isreal.mpi;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import java.util.Random;

public final class Utils
{

  public static String randomString(String... text)
  {
    if (text.length <= 0) return "";
    Random rand = new Random();
    int randIndex = rand.nextInt(text.length);
    return text[randIndex];
  }

  // renders a color quad on texture quad layer
  // explain: on default render system, color quads are always above texture quads, ignoring z-order. don't know why mojang did this bullshit.
  public static void drawColorRect(MatrixStack matrices, int x, int y, int width, int height, int color)
  {
    float alpha = (float) ColorHelper.Argb.getAlpha(color) / 255.0f;
    float red = (float) ColorHelper.Argb.getRed(color) / 255.0f;
    float green = (float) ColorHelper.Argb.getGreen(color) / 255.0f;
    float blue = (float) ColorHelper.Argb.getBlue(color) / 255.0f;

    RenderSystem.setShader(GameRenderer::getPositionColorProgram);
    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
    BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder.vertex(matrix4f, x, y, 0).color(red, green, blue, alpha).next();
    bufferBuilder.vertex(matrix4f, x, y + height, 0).color(red, green, blue, alpha).next();
    bufferBuilder.vertex(matrix4f, x + width, y + height, 0).color(red, green, blue, alpha).next();
    bufferBuilder.vertex(matrix4f, x + width, y, 0).color(red, green, blue, alpha).next();
    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
  }

}
