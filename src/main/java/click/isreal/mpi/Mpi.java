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

import click.isreal.mpi.client.mpiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Mpi implements ModInitializer
{

  public static final Logger LOGGER = LogManager.getFormatterLogger("MPI");
  private static Mpi instance;
  private final Gson configJson;
  private final String configPath = FabricLoader.getInstance().getConfigDir().resolve("MuxelPuxelImproved" + ".json").toString();
  public DecimalFormat moneyformat = new DecimalFormat("###,###,###.00 ⛀", new DecimalFormatSymbols(Locale.GERMANY));
  private ModConfig config = new ModConfig();

  {
    instance = this;
  }

  public Mpi()
  {
    configJson = new GsonBuilder().setPrettyPrinting().create();
    loadConfig();
  }

  public static Mpi getInstance()
  {
    return instance;
  }

  @Override
  public void onInitialize()
  {
    LOGGER.info("Plugin Initialized");
    //todo: Deprecated, do we still need it? Does it hurt?
    if (System.getProperty("log4j2.formatMsgNoLookups", "false") != "true")
    {
      System.setProperty("log4j2.formatMsgNoLookups", "true");
      LOGGER.info("Log4j2 Security Patch applied, now you are safe! ;-)");
    }
  }

  public void saveConfig()
  {
    try
    {
      FileWriter fWrite;
      configJson.toJson(config, fWrite = new FileWriter(configPath));
      fWrite.flush();
      fWrite.close();
    }
    catch (Exception e)
    {
      LOGGER.log(Level.WARN, "Could not save config file. Exception: " + e.getMessage());
    }
    mpiClient.getInstance().updateTopBar();
  }

  public void loadConfig()
  {
    try
    {
      config = configJson.fromJson(new FileReader(configPath), config.getClass());
    }
    catch (Exception e)
    {
      config = new ModConfig();
      LOGGER.log(Level.WARN, "Could not load config file. Exception: " + e.getMessage());
    }
  }

  public boolean getStreamerMode()
  {
    return config.streamerMode != 0;
  }

  public void setStreamerMode(boolean streamerMode)
  {
    config.streamerMode = streamerMode ? 1 : 0;
    saveConfig();
  }

  public int getColorBackground()
  {
    return config.colorBackground;
  }

  public void setColorBackground(int colorBackground)
  {
    config.colorBackground = colorBackground;
    saveConfig();
  }

  public int getEffectIconSize()
  {
    return Math.min(32, Math.max(6, config.effectIconSize));
  }

  public void setEffectIconSize(int effectIconSize)
  {
    config.effectIconSize = Math.min(32, Math.max(6, effectIconSize));
    saveConfig();
  }

  public int getEffectColorPositive()
  {
    return config.effectColorPositive;
  }

  public void setEffectColorPositive(int color)
  {
    config.effectColorPositive = color;
    saveConfig();
  }

  public int getEffectColorNegative()
  {
    return config.effectColorNegative;
  }

  public void setEffectColorNegative(int color)
  {
    config.effectColorNegative = color;
    saveConfig();
  }

  public boolean getFpsShow()
  {
    return config.fpsShow != 0;
  }

  public void setFpsShow(boolean fpsShow)
  {
    config.fpsShow = fpsShow ? 1 : 0;
    saveConfig();
  }

  public int getFpsColor()
  {
    return config.fpsColor;
  }

  public void setFpsColor(int fpsColor)
  {
    config.fpsColor = fpsColor;
    saveConfig();
  }

  public boolean getTimeShow()
  {
    return config.timeShow != 0;
  }

  public void setTimeShow(boolean timeShow)
  {
    config.timeShow = timeShow ? 1 : 0;
    saveConfig();
  }

  public int getTimeColor()
  {
    return config.timeColor;
  }

  public void setTimeColor(int timeColor)
  {
    config.timeColor = timeColor;
    saveConfig();
  }

  public boolean getPreventFalseCommands()
  {
    return config.preventFalseCommands != 0;
  }

  public void setPreventFalseCommands(boolean preventFalseCommands)
  {
    config.preventFalseCommands = preventFalseCommands ? 1 : 0;
    saveConfig();
  }

  public int getLoadscreenColor()
  {
    return config.loadscreenColor;
  }

  public void setLoadscreenColor(int loadscreenColor)
  {
    config.loadscreenColor = loadscreenColor;
    saveConfig();
  }

  public boolean getDiscordEnabled()
  {
    return config.discordEnabled != 0;
  }

  public void setDiscordEnabled(boolean discordEnabled)
  {
    config.discordEnabled = discordEnabled ? 1 : 0;
    if (discordEnabled)
    {
      mpiClient.getInstance().dc.start();
    }
    else
    {
      mpiClient.getInstance().dc.stop();
    }
    saveConfig();
  }

  public boolean getBreakwarnEnabled()
  {
    return config.breakwarnEnabled != 0;
  }

  public void setBreakwarnEnabled(boolean breakwarnEnabled)
  {
    config.breakwarnEnabled = breakwarnEnabled ? 1 : 0;
    saveConfig();
  }

  public boolean getUnsecureServerWarning()
  {
    return config.unsecureServerWarning != 0;
  }

  public void setUnsecureServerWarning(boolean secureServerWarning)
  {
    config.unsecureServerWarning = secureServerWarning ? 1 : 0;
    saveConfig();
  }

  public boolean getHornAudio()
  {
    return config.hornAudio != 0;
  }

  public void setHornAudio(boolean hornAudio)
  {
    config.hornAudio = hornAudio ? 1 : 0;
    saveConfig();
  }

  public int getTopbarScale() { return config.topbarScale; }

  public void setTopbarScale( int topbarScale )
  {
    config.topbarScale = topbarScale;
    saveConfig();
  }

}
