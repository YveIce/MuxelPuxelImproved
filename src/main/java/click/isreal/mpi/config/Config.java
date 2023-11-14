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

package click.isreal.mpi.config;

import click.isreal.mpi.client.mpiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;

import java.io.FileReader;
import java.io.FileWriter;

import static click.isreal.mpi.Mpi.LOGGER;

public class Config
{
  private static Config instance;
  private final Gson configJson;
  private final String configPath = FabricLoader.getInstance().getConfigDir().resolve("MuxelPuxelImproved" + ".json").toString();
  private ConfigData configData = new ConfigData();

  private int lineHeight = 0;
  private int topShift = 0;
  private int bottomShift = 0;

  public Config()
  {
    instance = this;
    configJson = new GsonBuilder().setPrettyPrinting().create();
    load();
  }

  public void updateShift()
  {
    MinecraftClient client = MinecraftClient.getInstance();
    if (null != client && null != client.textRenderer)
    {
      float scale = (float) this.getTopbarScale() / 100.0f;
      lineHeight = client.textRenderer.getWrappedLinesHeight("_", 1000)+1;
      int scaledLineHeight = Math.round(lineHeight * scale);
      topShift = 0;
      if (getBarTop2enabled()) topShift = scaledLineHeight *2;
      else if (getBarTop1enabled()) topShift = scaledLineHeight;
      bottomShift = 0;
      if (getBarBottom2enabled()) bottomShift = scaledLineHeight *2;
      else if (getBarBottom1enabled()) bottomShift = scaledLineHeight;

    }
  }

  public static Config getInstance()
  {
    if (null == instance)
    {
      instance = new Config();
    }
    return instance;
  }

  public void load()
  {
    try
    {
      configData = configJson.fromJson(new FileReader(configPath), configData.getClass());
    }
    catch (Exception e)
    {
      configData = new ConfigData();
      LOGGER.log(Level.WARN, "Could not load config file. Exception: " + e.getMessage());
    }
  }

  public void save()
  {
    try
    {
      FileWriter fWrite;
      configJson.toJson(configData, fWrite = new FileWriter(configPath));
      fWrite.flush();
      fWrite.close();
    }
    catch (Exception e)
    {
      LOGGER.log(Level.WARN, "Could not save config file. Exception: " + e.getMessage());
    }
    mpiClient.getInstance().updateTopBar();
  }

  public int getLineHeight()
  {
    return lineHeight;
  }

  public int getTopShift()
  {
    return topShift;
  }

  public int getBottomShift()
  {
    return bottomShift;
  }
  public boolean getStreamerMode()
  {
    return configData.streamerMode != 0;
  }

  public void setStreamerMode(boolean streamerMode)
  {
    configData.streamerMode = streamerMode ? 1 : 0;
    save();
  }

  public boolean getPreventFalseCommands()
  {
    return configData.preventFalseCommands != 0;
  }

  public void setPreventFalseCommands(boolean preventFalseCommands)
  {
    configData.preventFalseCommands = preventFalseCommands ? 1 : 0;
    save();
  }

  public int getLoadscreenColor()
  {
    return configData.loadscreenColor;
  }

  public void setLoadscreenColor(int loadscreenColor)
  {
    configData.loadscreenColor = loadscreenColor;
    save();
  }

  public boolean getDiscordEnabled()
  {
    return configData.discordEnabled != 0;
  }

  public void setDiscordEnabled(boolean discordEnabled)
  {
    configData.discordEnabled = discordEnabled ? 1 : 0;
    if (discordEnabled)
    {
      mpiClient.getInstance().dc.start();
    }
    else
    {
      mpiClient.getInstance().dc.stop();
    }
    save();
  }

  public boolean getBreakwarnEnabled()
  {
    return configData.breakwarnEnabled != 0;
  }

  public void setBreakwarnEnabled(boolean breakwarnEnabled)
  {
    configData.breakwarnEnabled = breakwarnEnabled ? 1 : 0;
    save();
  }
  public boolean getSolidBackgroundEnabled()
  {
    return configData.solidBackgroundEnabled != 0;
  }

  public void setSolidBackgroundEnabled(boolean solidBackgroundEnabled)
  {
    configData.solidBackgroundEnabled = solidBackgroundEnabled ? 1 : 0;
    save();
  }

  public boolean getUnsecureServerWarning()
  {
    return configData.unsecureServerWarning != 0;
  }

  public void setUnsecureServerWarning(boolean secureServerWarning)
  {
    configData.unsecureServerWarning = secureServerWarning ? 1 : 0;
    save();
  }

  public boolean getHornAudio()
  {
    return configData.hornAudio != 0;
  }

  public void setHornAudio(boolean hornAudio)
  {
    configData.hornAudio = hornAudio ? 1 : 0;
    save();
  }

  public boolean getRenderScoreboard()
  {
    return configData.renderScoreboard != 0;
  }

  public void setRenderScoreboard(boolean renderScoreboard)
  {
    configData.renderScoreboard = renderScoreboard ? 1 : 0;
    save();
  }

  public String getTitlescreenTheme()
  {
    return configData.titleScreenTheme;
  }

  public void setTitlescreenTheme(String titlescreenTheme)
  {
    configData.titleScreenTheme = titlescreenTheme.trim().toLowerCase();
    save();
  }

  public int getTopbarScale()
  {
    return configData.barScale;
  }

  public void setTopbarScale(int topbarScale)
  {
    configData.barScale = topbarScale;
    save();
    updateShift();
  }

  public boolean getBarTop1enabled()
  {
    return configData.barTop1enabled != 0;
  }

  public void setBarTop1enabled(boolean barTop1enabled)
  {
    configData.barTop1enabled = barTop1enabled ? 1 : 0;
    save();
    updateShift();
  }

  public boolean getBarTop2enabled()
  {
    return configData.barTop2enabled != 0;
  }

  public void setBarTop2enabled(boolean barTop2enabled)
  {
    configData.barTop2enabled = barTop2enabled ? 1 : 0;
    save();
    updateShift();
  }

  public boolean getBarBottom1enabled()
  {
    return configData.barBottom1enabled != 0;
  }

  public void setBarBottom1enabled(boolean barBottom1enabled)
  {
    configData.barBottom1enabled = barBottom1enabled ? 1 : 0;
    save();
    updateShift();
  }

  public boolean getBarBottom2enabled()
  {
    return configData.barBottom2enabled != 0;
  }

  public void setBarBottom2enabled(boolean barBottom2enabled)
  {
    configData.barBottom2enabled = barBottom2enabled ? 1 : 0;
    save();
    updateShift();
  }

  public int getBarTop1Color()
  {
    return configData.barTop1Color;
  }

  public void setBarTop1Color(int barTop1Color)
  {
    configData.barTop1Color = barTop1Color;
    save();
  }

  public int getBarTop2Color()
  {
    return configData.barTop2Color;
  }

  public void setBarTop2Color(int barTop2Color)
  {
    configData.barTop2Color = barTop2Color;
    save();
  }

  public int getBarBottom1Color()
  {
    return configData.barBottom1Color;
  }

  public void setBarBottom1Color(int barBottom1Color)
  {
    configData.barBottom1Color = barBottom1Color;
    save();
  }

  public int getBarBottom2Color()
  {
    return configData.barBottom2Color;
  }

  public void setBarBottom2Color(int barBottom2Color)
  {
    configData.barBottom2Color = barBottom2Color;
    save();
  }

  public String getBarTop1LeftPattern()
  {
    return configData.barTop1LeftPattern;
  }

  public void setBarTop1LeftPattern(String barTop1LeftPattern)
  {
    configData.barTop1LeftPattern = barTop1LeftPattern;
    save();
  }
  public String getBarTop1CenterPattern()
  {
    return configData.barTop1CenterPattern;
  }

  public void setBarTop1CenterPattern(String barTop1CenterPattern)
  {
    configData.barTop1CenterPattern = barTop1CenterPattern;
    save();
  }

  public String getBarTop1RightPattern()
  {
    return configData.barTop1RightPattern;
  }

  public void setBarTop1RightPattern(String barTop1RightPattern)
  {
    configData.barTop1RightPattern = barTop1RightPattern;
    save();
  }
  public String getBarTop2LeftPattern()
  {
    return configData.barTop2LeftPattern;
  }

  public void setBarTop2LeftPattern(String barTop2LeftPattern)
  {
    configData.barTop2LeftPattern = barTop2LeftPattern;
    save();
  }
  public String getBarTop2CenterPattern()
  {
    return configData.barTop2CenterPattern;
  }

  public void setBarTop2CenterPattern(String barTop2CenterPattern)
  {
    configData.barTop2CenterPattern = barTop2CenterPattern;
    save();
  }

  public String getBarTop2RightPattern()
  {
    return configData.barTop2RightPattern;
  }

  public void setBarTop2RightPattern(String barTop2RightPattern)
  {
    configData.barTop2RightPattern = barTop2RightPattern;
    save();
  }
public String getBarBottom1LeftPattern()
{
  return configData.barBottom1LeftPattern;
}

  public void setBarBottom1LeftPattern(String barBottom1LeftPattern)
  {
    configData.barBottom1LeftPattern = barBottom1LeftPattern;
    save();
  }
  public String getBarBottom1CenterPattern()
  {
    return configData.barBottom1CenterPattern;
  }

  public void setBarBottom1CenterPattern(String barBottom1CenterPattern)
  {
    configData.barBottom1CenterPattern = barBottom1CenterPattern;
    save();
  }

  public String getBarBottom1RightPattern()
  {
    return configData.barBottom1RightPattern;
  }

  public void setBarBottom1RightPattern(String barBottom1RightPattern)
  {
    configData.barBottom1RightPattern = barBottom1RightPattern;
    save();
  }
  public String getBarBottom2LeftPattern()
  {
    return configData.barBottom2LeftPattern;
  }

  public void setBarBottom2LeftPattern(String barBottom2LeftPattern)
  {
    configData.barBottom2LeftPattern = barBottom2LeftPattern;
    save();
  }
  public String getBarBottom2CenterPattern()
  {
    return configData.barBottom2CenterPattern;
  }

  public void setBarBottom2CenterPattern(String barBottom2CenterPattern)
  {
    configData.barBottom2CenterPattern = barBottom2CenterPattern;
    save();
  }

  public String getBarBottom2RightPattern()
  {
    return configData.barBottom2RightPattern;
  }

  public void setBarBottom2RightPattern(String barBottom2RightPattern)
  {
    configData.barBottom2RightPattern = barBottom2RightPattern;
    save();
  }
}
