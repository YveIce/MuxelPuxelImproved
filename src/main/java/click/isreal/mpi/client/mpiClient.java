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

import click.isreal.mpi.Mpi;
import click.isreal.mpi.config.Config;
import click.isreal.mpi.domain.MixelWorld;
import click.isreal.mpi.domain.MixelWorldType;
import click.isreal.mpi.domain.UserData;
import click.isreal.mpi.domain.Winter22Event;
import click.isreal.mpi.events.MixelJoinCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class mpiClient implements ClientModInitializer
{

  private static mpiClient instance;
  private final MinecraftClient client = MinecraftClient.getInstance();
  private final Config config = Config.getInstance();
  private final DataManager dataManager = DataManager.getInstance();
  private final UserData scoreboardData = new UserData(MixelWorld.OTHER);
  // Storage for all TopBar Strings (Parts), so we only need to build/change them,
  // when things really changed. Saves some time in gui-render thread
  public String strSplitter = Formatting.GRAY + " | ";
  public String strTopLeft = "";
  public String strTopRight = "";
  public DiscordRPC dc;
  KeyBinding panoramicScreenshotKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.panoramic_screenshot", GLFW.GLFW_KEY_F9, KeyBinding.MISC_CATEGORY));
  private boolean _isMixel = false;

  {
    instance = this;
  }

  public static mpiClient getInstance()
  {
    return instance;
  }

  public DataManager getDataManager()
  {
    return dataManager;
  }

  @Deprecated
  public UserData getScoreboardData()
  {
    return scoreboardData;
  }

  public MixelWorld getWorld()
  {
    return scoreboardData.mixelWorld();
  }

  @Override
  public void onInitializeClient()
  {
    try
    {
      this.loadInjections();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    this.initEventCallbacks();
    System.out.println("\\u001b[0;35mMuxelpuxel Improved: started at " + java.time.LocalDateTime.now());

    try
    {
      dc = new DiscordRPC();
    }
    catch (Exception e)
    {
      Mpi.LOGGER.warn("Error starting DC: \n" + e.getMessage());
    }
    Shaders.registerShaders();
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (panoramicScreenshotKeybind.wasPressed())
      {
        if (client.player != null)
        {
          client.player.sendMessage(Screenshoot.takePanorama(client.runDirectory, 512 * 8, 512 * 8));
        }
      }
    });
  }

  private void loadInjections() throws Exception
  {
    this.getScoreboardData().createInjection(Winter22Event.class);
  }

  private void initEventCallbacks()
  {
    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> setisMixel(false));
    ClientPlayConnectionEvents.INIT.register(new MixelJoinCallback());
  }

  public void setisMixel(boolean Status)
  {
    Mpi.LOGGER.info((Status ? "Joining" : "Leaving") + " Mixelpixel Server");
    _isMixel = Status;
  }

  public boolean isMixelPixel()
  {
    return true; //_isMixel;
  }

  public String buildName(MixelWorld world)
  {
    String name = world.getFormatting().toString() + Formatting.BOLD + world.getType().getName().toUpperCase();
    if (world.getType() == MixelWorldType.FARMWORLD || world.getType() == MixelWorldType.SPAWN)
    {name += "-" + world.getSubtype();}
    if (world.getType() == MixelWorldType.SMALL_CB || world.getType() == MixelWorldType.BIG_CB)
    {name += " " + world.getSubtype();}
    return name;
  }

  public void updateTopBar()
  {
    // reserve 7 char space for fps String, if we show it
    strTopLeft = "" + Formatting.BLUE + Formatting.BOLD + "MixelPixel.net" + Formatting.GRAY + " - ";

    if (getScoreboardData().getInjection(Winter22Event.class).tuer() != null)
    {
      Winter22Event event = getScoreboardData().getInjection(Winter22Event.class);
      strTopLeft = Formatting.RED + "Winterevent";
      strTopRight = Formatting.YELLOW + "Türchen: " + event.tuer() +
          Formatting.GRAY + " | " + Formatting.YELLOW + "Modus: " + event.modus();
      if (event.checkpoints() != null)
      {strTopRight += Formatting.GRAY + " | " + Formatting.YELLOW + "Checkpoint: " + event.checkpoints();}
    }
    else
    {
      MixelWorld world = getWorld();
      switch (world)
      {
        case HUB ->
        {
          strTopLeft = buildName(world) + Formatting.GRAY + " - " + UserData.current().rank();
          strTopRight = "";
        }
        case SPAWN_1, SPAWN_2, SPAWN_3, SPAWN_4 ->
        {
          strTopLeft = buildName(world) + Formatting.GRAY + " - " + UserData.current().rank();
          String money = config.getStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
          String jubilaeum = UserData.current().getJubiProgress() != null ? UserData.current().getJubiProgress() + Formatting.GRAY + " | " : "";
          strTopRight = jubilaeum + money;
        }
        case KFFA ->
        {
          strTopLeft = "" + Formatting.BLUE + Formatting.BOLD + "MP" + Formatting.GRAY + " - "
              + buildName(world) + Formatting.GRAY + " - " + scoreboardData.kffaMap()
              + scoreboardData.kffaMapSwitch() + Formatting.GRAY + " - " + scoreboardData.rank();
          strTopRight = scoreboardData.rankPoints() + scoreboardData.aufstiegPoints() +
              strSplitter + scoreboardData.kffaKD() + strSplitter;
          if (config.getStreamerMode()) {strTopRight = Formatting.YELLOW + "[STREAMING]";}
          else {strTopRight = scoreboardData.money();}
        }
        case FARMWORLD_1, FARMWORLD_2, FARMWORLD_3, FARMWORLD_4 ->
        {
          strTopLeft += buildName(world);
          if (null != MinecraftClient.getInstance().world)
          {
            if (World.END == MinecraftClient.getInstance().world.getRegistryKey())
            {scoreboardData.setDimension("End");}
            else if (World.NETHER == MinecraftClient.getInstance().world.getRegistryKey())
            {scoreboardData.setDimension("Nether");}
            else if (World.OVERWORLD == MinecraftClient.getInstance().world.getRegistryKey())
            {scoreboardData.setDimension("Overworld");}
            else {scoreboardData.setDimension("");}
          }
          else {scoreboardData.setDimension("");}
          String money = config.getStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
          String jubilaeum = UserData.current().getJubiProgress() != null ? UserData.current().getJubiProgress() + Formatting.GRAY + " | " : "";
          strTopRight = jubilaeum + money;
        }
        case SMALL_AQUA, SMALL_DONNER, SMALL_FLORA, SMALL_VULKAN, BIG_AQUA, BIG_DONNER, BIG_FLORA, BIG_VULKAN ->
        {
          strTopLeft += buildName(world);
          scoreboardData.setDimension("");
          String money = config.getStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
          String jubilaeum = UserData.current().getJubiProgress() != null ? UserData.current().getJubiProgress() + Formatting.GRAY + " | " : "";
          strTopRight = jubilaeum + money;
        }
        default -> strTopRight = Formatting.RED + "?"; // at this moment we don't know what to do ;-)
      }
    }
  }


}
