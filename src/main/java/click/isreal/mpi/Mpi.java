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

import click.isreal.mpi.client.DataManager;
import click.isreal.mpi.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Mpi implements ModInitializer
{

  public static final Logger LOGGER = LogManager.getFormatterLogger("MPI");
  private static Mpi instance;
  public DecimalFormat moneyformat = new DecimalFormat("###,###,###.00 ⛀", new DecimalFormatSymbols(Locale.GERMANY));

  {
    instance = this;
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

    // Yve: Patch Realms Button on Start Screen
    ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
    {
      if (screen instanceof TitleScreen)
      {
        for (ClickableWidget widget : Screens.getButtons(screen))
        {
          if (widget.getMessage().getString().toLowerCase().contains("realms"))
          {
            widget.visible = false;
          }
        }
        Screens.getButtons(screen).add(
            ButtonWidget.builder(
                    Text.literal(Formatting.GOLD + "Play MixelPixel"),
                    button -> this.joinMixel()
                )
                .dimensions(scaledWidth / 2 - 100, scaledHeight / 4 + 96, 200, 20)
                .build()
        );
      }
    });
  }

  public void joinMixel()
  {
    ServerInfo mpServer = new ServerInfo("MixelPixel", "play.mixelpixel.net", false);
    ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), MinecraftClient.getInstance(), ServerAddress.parse(mpServer.address), mpServer, false);
  }
}
