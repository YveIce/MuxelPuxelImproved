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

import click.isreal.mpi.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

public class BarHud
{
  final Config config = Config.getInstance();
  private final MinecraftClient client;
  private final DataManager dataManager = DataManager.getInstance();
  private int scaledWidth = 1;
  private int scaledHeight = 1;
  private int lineHeight = 1;

  public BarHud(MinecraftClient client)
  {
    this.client = client;
  }

  public void drawBar(DrawContext context, int y, int backgroundColor, String patternLeft, String patternCenter, String patternRight)
  {
    // draw background
    context.fill(0, y, scaledWidth, y + lineHeight, backgroundColor);
    int offset = 0;
    for (Text text : dataManager.processPatterns(patternLeft))
    {
      context.drawText(client.textRenderer, text, offset, y, -1, false);
      offset += client.textRenderer.getWidth("x") * net.minecraft.util.Formatting.strip(text.getString()).length();
    }
    offset = 0;
    List<Text> rightTexts = dataManager.processPatterns(patternRight);
    for (Text text : rightTexts)
    {
      offset += client.textRenderer.getWidth("x") * net.minecraft.util.Formatting.strip(text.getString()).length();
    }
    offset = scaledWidth-offset;
    for (Text text : rightTexts)
    {
      context.drawText(client.textRenderer, text, offset, y, -1, false);
      offset += client.textRenderer.getWidth("x") * net.minecraft.util.Formatting.strip(text.getString()).length();
    }
    offset = 0;
    List<Text> centerTexts = dataManager.processPatterns(patternCenter);
    for (Text text : centerTexts)
    {
      offset += client.textRenderer.getWidth("x") * net.minecraft.util.Formatting.strip(text.getString()).length();
    }
    offset = Math.round((scaledWidth / 2) - (Math.max(offset, 1) / 2));
    for (Text text : centerTexts)
    {
      context.drawText(client.textRenderer, text, offset, y, -1, false);
      offset += client.textRenderer.getWidth("x") * net.minecraft.util.Formatting.strip(text.getString()).length();
    }
  }

  public void render(DrawContext context)
  {
    float scale = (float) config.getTopbarScale() / 100.0f;
    this.scaledWidth = Math.round((float) context.getScaledWindowWidth() / scale);
    this.scaledHeight = Math.round((float) context.getScaledWindowHeight() / scale);
    this.lineHeight = client.textRenderer.getWrappedLinesHeight("_", 1000);
    dataManager.tick();
    context.getMatrices().push();
    context.getMatrices().scale(scale, scale, 0.0f);
    // draw the bars now scaled
    if (config.getBarTop1enabled())
    {drawBar(context, 0, config.getBarTop1Color(), config.getBarTop1LeftPattern(), config.getBarTop1CenterPattern(), config.getBarTop1RightPattern());}
    if (config.getBarTop2enabled())
    {drawBar(context, lineHeight, config.getBarTop2Color(), config.getBarTop2LeftPattern(), config.getBarTop2CenterPattern(), config.getBarTop2RightPattern());}
    if (config.getBarBottom1enabled())
    {drawBar(context, scaledHeight - lineHeight, config.getBarBottom1Color(), config.getBarBottom1LeftPattern(), config.getBarBottom1CenterPattern(), config.getBarBottom1RightPattern());}
    if (config.getBarBottom2enabled())
    {drawBar(context, scaledHeight - (lineHeight * 2), config.getBarBottom2Color(), config.getBarBottom2LeftPattern(), config.getBarBottom2CenterPattern(), config.getBarBottom2RightPattern());}
    context.getMatrices().pop();
  }
}
