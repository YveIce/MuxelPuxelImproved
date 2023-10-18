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

  public Config()
  {
    instance = this;
    configJson = new GsonBuilder().setPrettyPrinting().create();
    load();
  }
  public static Config getInstance()
  {
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

  public boolean getStreamerMode()
  {
    return configData.streamerMode != 0;
  }

  public void setStreamerMode(boolean streamerMode)
  {
    configData.streamerMode = streamerMode ? 1 : 0;
    save();
  }

  public int getColorBackground()
  {
    return configData.colorBackground;
  }

  public void setColorBackground(int colorBackground)
  {
    configData.colorBackground = colorBackground;
    save();
  }

  public int getEffectIconSize()
  {
    return Math.min(32, Math.max(6, configData.effectIconSize));
  }

  public void setEffectIconSize(int effectIconSize)
  {
    configData.effectIconSize = Math.min(32, Math.max(6, effectIconSize));
    save();
  }

  public int getEffectColorPositive()
  {
    return configData.effectColorPositive;
  }

  public void setEffectColorPositive(int color)
  {
    configData.effectColorPositive = color;
    save();
  }

  public int getEffectColorNegative()
  {
    return configData.effectColorNegative;
  }

  public void setEffectColorNegative(int color)
  {
    configData.effectColorNegative = color;
    save();
  }

  public boolean getFpsShow()
  {
    return configData.fpsShow != 0;
  }

  public void setFpsShow(boolean fpsShow)
  {
    configData.fpsShow = fpsShow ? 1 : 0;
    save();
  }

  public int getFpsColor()
  {
    return configData.fpsColor;
  }

  public void setFpsColor(int fpsColor)
  {
    configData.fpsColor = fpsColor;
    save();
  }

  public boolean getTimeShow()
  {
    return configData.timeShow != 0;
  }

  public void setTimeShow(boolean timeShow)
  {
    configData.timeShow = timeShow ? 1 : 0;
    save();
  }

  public int getTimeColor()
  {
    return configData.timeColor;
  }

  public void setTimeColor(int timeColor)
  {
    configData.timeColor = timeColor;
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

  public int getTopbarScale() { return configData.topbarScale; }

  public void setTopbarScale( int topbarScale )
  {
    configData.topbarScale = topbarScale;
    save();
  }
  public String getTitlescreenTheme() { return configData.titleScreenTheme; }

  public void setTitlescreenTheme( String titlescreenTheme )
  {
    configData.titleScreenTheme = titlescreenTheme.trim().toLowerCase();
    save();
  }
}
