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
import click.isreal.mpi.domain.MixelWorld;
import click.isreal.mpi.domain.MixelWorldType;
import click.isreal.mpi.domain.UserData;
import click.isreal.mpi.domain.Winter22Event;
import click.isreal.mpi.events.MixelJoinCallback;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class mpiClient implements ClientModInitializer
{

  private static mpiClient instance;
  private final MinecraftClient client = MinecraftClient.getInstance();
  private final UserData scoreboardData = new UserData(MixelWorld.OTHER);
  // Storage for all TopBar Strings (Parts), so we only need to build/change them,
  // when things really changed. Saves some time in gui-render thread
  public String strSplitter = Formatting.GRAY + " | ";
  public String strTopLeft = "";
  public String strTopRight = "";
  public DiscordRPC dc;
  private boolean _isMixel = false;

  {
    instance = this;
  }

  public static mpiClient getInstance()
  {
    return instance;
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
    return _isMixel;
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

  public String getFPS()
  {
    if (Mpi.getInstance().getFpsShow())
    {
      String fps = "";
      fps += this.client.fpsDebugString.split("fps")[0].trim();
      while (fps.length() < 3)
      {
        fps = " " + fps;
      }
      fps += " FPS";
      return fps;
    }
    return "";
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
          String money = Mpi.getInstance().getStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
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
          if (Mpi.getInstance().getStreamerMode()) {strTopRight = Formatting.YELLOW + "[STREAMING]";}
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
          String money = Mpi.getInstance().getStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
          String jubilaeum = UserData.current().getJubiProgress() != null ? UserData.current().getJubiProgress() + Formatting.GRAY + " | " : "";
          strTopRight = jubilaeum + money;
        }
        case SMALL_AQUA, SMALL_DONNER, SMALL_FLORA, SMALL_VULKAN, BIG_AQUA, BIG_DONNER, BIG_FLORA, BIG_VULKAN ->
        {
          strTopLeft += buildName(world);
          scoreboardData.setDimension("");
          String money = Mpi.getInstance().getStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
          String jubilaeum = UserData.current().getJubiProgress() != null ? UserData.current().getJubiProgress() + Formatting.GRAY + " | " : "";
          strTopRight = jubilaeum + money;
        }
        default -> strTopRight = Formatting.RED + "?"; // at this moment we don't know what to do ;-)
      }
    }
  }

  public Screen createConfigScreen(Screen parent)
  {
    if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
    {

      ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(
          Text.literal(Formatting.WHITE + "☠ " + Formatting.LIGHT_PURPLE + "" + Formatting.BOLD + "MuxelPuxel Improved" + Formatting.WHITE + " ☠"));
      ConfigEntryBuilder entryBuilder = builder.entryBuilder();

      ConfigCategory general = builder.getOrCreateCategory(Text.literal(Formatting.LIGHT_PURPLE + "General"));
      general.setBackground(Identifier.tryParse("minecraft:textures/block/stripped_crimson_stem.png"));
      general.addEntry(entryBuilder.startIntSlider(Text.literal("Topbar Scale"), Mpi.getInstance().getTopbarScale(), 10, 200).setDefaultValue(100).setTooltip(Text.literal("Scale of topbar in percent")).setSaveConsumer(Mpi.getInstance()::setTopbarScale).build());
      general.addEntry(entryBuilder.startColorField(Text.literal("Color Background"), Mpi.getInstance().getColorBackground()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(Mpi.getInstance()::setColorBackground).build());
      general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show FPS"), Mpi.getInstance().getFpsShow()).setDefaultValue(true).setTooltip(Text.literal("If enabled, FPS is shown left-most on the topbar.")).setSaveConsumer(Mpi.getInstance()::setFpsShow).build());
      general.addEntry(entryBuilder.startColorField(Text.literal("Color FPS"), Mpi.getInstance().getFpsColor()).setDefaultValue(0xff808080).setAlphaMode(true).setTooltip(Text.literal("Sets the textcolor of FPS in Hex. (#AARRGGBB)")).setSaveConsumer(Mpi.getInstance()::setFpsColor).build());
      general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Time"), Mpi.getInstance().getTimeShow()).setDefaultValue(true).setTooltip(Text.literal("If enabled, Time of your computer is shown right-most on the topbar.")).setSaveConsumer(Mpi.getInstance()::setTimeShow).build());
      general.addEntry(entryBuilder.startColorField(Text.literal("Color Time"), Mpi.getInstance().getTimeColor()).setDefaultValue(0xff808080).setAlphaMode(true).setTooltip(Text.literal("Sets the textcolor of Time in Hex. (#AARRGGBB)")).setSaveConsumer(Mpi.getInstance()::setTimeColor).build());
      general.addEntry(entryBuilder.startColorField(Text.literal("Color Loading Screen"), Mpi.getInstance().getLoadscreenColor()).setDefaultValue(0xffff007d).setAlphaMode(true).setTooltip(Text.literal("Sets the background color of the loadingscreen(the one with the mojang logo) in Hex. (#AARRGGBB)")).setSaveConsumer(Mpi.getInstance()::setLoadscreenColor).build());
      general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Streamer-Mode"), Mpi.getInstance().getStreamerMode()).setDefaultValue(false).setTooltip(Text.literal("If enabled, your ingame money value would be hidden on topbar.")).setSaveConsumer(Mpi.getInstance()::setStreamerMode).build());
      general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Discord"), Mpi.getInstance().getDiscordEnabled()).setDefaultValue(true).setTooltip(Text.literal("If enabled and Discord app is running, your profil will show that you are playing on MixelPixel.")).setSaveConsumer(Mpi.getInstance()::setDiscordEnabled).build());

      ConfigCategory tweaks = builder.getOrCreateCategory(Text.literal(Formatting.GOLD + "Tweaks"));
      tweaks.setBackground(Identifier.tryParse("minecraft:textures/block/soul_sand.png"));
      tweaks.addEntry(entryBuilder.startBooleanToggle(Text.literal("Prevent sending false commands"), Mpi.getInstance().getPreventFalseCommands()).setDefaultValue(true).setTooltip(Text.literal("Prevents sending Chat-Messages starting with '7' or 't/'. As this are the most common typo errors.")).setSaveConsumer(Mpi.getInstance()::setPreventFalseCommands).build());
      tweaks.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Tool break warning"), Mpi.getInstance().getBreakwarnEnabled()).setDefaultValue(true).setTooltip(Text.literal("If enabled, a warning is displayed if the tool being used is about to be destroyed.")).setSaveConsumer(Mpi.getInstance()::setBreakwarnEnabled).build());
      tweaks.addEntry(entryBuilder.startBooleanToggle(Text.literal("Toggle Unsecure Server Warning"), Mpi.getInstance().getUnsecureServerWarning()).setDefaultValue(false).setTooltip(Text.literal("If disabled, no Chat couldn't be verified message is displayed")).setSaveConsumer(Mpi.getInstance()::setUnsecureServerWarning).build());
      tweaks.addEntry(entryBuilder.startBooleanToggle(Text.literal("Toggle Horn Audio"), Mpi.getInstance().getHornAudio()).setDefaultValue(false).setTooltip(Text.literal("If disabled, horn sounds are blocked for your client")).setSaveConsumer(Mpi.getInstance()::setHornAudio).build());

      builder.transparentBackground();

      return builder.build();
    }
    return null;
  }
}
