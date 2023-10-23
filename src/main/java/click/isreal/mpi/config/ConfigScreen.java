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

import click.isreal.mpi.client.DataManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class ConfigScreen
{
  private static ConfigScreen instance;
  private final Config config = Config.getInstance();

  {
    instance = this;
  }

  public static ConfigScreen getInstance()
  {
    if (null == instance)
    {instance = new ConfigScreen();}
    return instance;
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
      general.addEntry(entryBuilder.startIntSlider(Text.literal("Topbar Scale"), config.getTopbarScale(), 10, 200).setDefaultValue(100).setTooltip(Text.literal("Scale of topbar in percent")).setSaveConsumer(config::setTopbarScale).build());
//      general.addEntry(entryBuilder.startColorField(Text.literal("Color Background"), config.getColorBackground()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(config::setColorBackground).build());
      general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show FPS"), config.getFpsShow()).setDefaultValue(true).setTooltip(Text.literal("If enabled, FPS is shown left-most on the topbar.")).setSaveConsumer(config::setFpsShow).build());
      general.addEntry(entryBuilder.startColorField(Text.literal("Color FPS"), config.getFpsColor()).setDefaultValue(0xff808080).setAlphaMode(true).setTooltip(Text.literal("Sets the textcolor of FPS in Hex. (#AARRGGBB)")).setSaveConsumer(config::setFpsColor).build());
      general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Time"), config.getTimeShow()).setDefaultValue(true).setTooltip(Text.literal("If enabled, Time of your computer is shown right-most on the topbar.")).setSaveConsumer(config::setTimeShow).build());
      general.addEntry(entryBuilder.startColorField(Text.literal("Color Time"), config.getTimeColor()).setDefaultValue(0xff808080).setAlphaMode(true).setTooltip(Text.literal("Sets the textcolor of Time in Hex. (#AARRGGBB)")).setSaveConsumer(config::setTimeColor).build());
      general.addEntry(entryBuilder.startColorField(Text.literal("Color Loading Screen"), config.getLoadscreenColor()).setDefaultValue(0xffff007d).setAlphaMode(true).setTooltip(Text.literal("Sets the background color of the loadingscreen(the one with the mojang logo) in Hex. (#AARRGGBB)")).setSaveConsumer(config::setLoadscreenColor).build());
      general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Streamer-Mode"), config.getStreamerMode()).setDefaultValue(false).setTooltip(Text.literal("If enabled, your ingame money value would be hidden on topbar.")).setSaveConsumer(config::setStreamerMode).build());
      general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Discord"), config.getDiscordEnabled()).setDefaultValue(true).setTooltip(Text.literal("If enabled and Discord app is running, your profil will show that you are playing on MixelPixel.")).setSaveConsumer(config::setDiscordEnabled).build());

      ConfigCategory bars = builder.getOrCreateCategory(Text.literal(Formatting.WHITE + "Bars"));
      bars.setBackground(Identifier.tryParse("minecraft:textures/block/polished_blackstone.png"));
      SubCategoryBuilder catTopbar1 = entryBuilder.startSubCategory(Text.of("Topbar 1"));
      catTopbar1.add(entryBuilder.startBooleanToggle(Text.literal("Show"), config.getBarTop1enabled()).setDefaultValue(true).setTooltip(Text.literal("Enables the top most bar on the ingame screen.")).setSaveConsumer(config::setBarTop1enabled).build());
      catTopbar1.add(entryBuilder.startColorField(Text.literal("Background Color"), config.getBarTop1Color()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(config::setBarTop1Color).build());
      catTopbar1.add(entryBuilder.startStrField(Text.literal("Left Pattern"), config.getBarTop1LeftPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the left part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarTop1LeftPattern).build());
      catTopbar1.add(entryBuilder.startStrField(Text.literal("Center Pattern"), config.getBarTop1CenterPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the center part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarTop1CenterPattern).build());
      catTopbar1.add(entryBuilder.startStrField(Text.literal("Right Pattern"), config.getBarTop1RightPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the right part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarTop1RightPattern).build());
      bars.addEntry(catTopbar1.build());
      SubCategoryBuilder catTopbar2 = entryBuilder.startSubCategory(Text.of("Topbar 2"));
      catTopbar2.add(entryBuilder.startBooleanToggle(Text.literal("Show"), config.getBarTop2enabled()).setDefaultValue(true).setTooltip(Text.literal("Enables the top most bar on the ingame screen.")).setSaveConsumer(config::setBarTop2enabled).build());
      catTopbar2.add(entryBuilder.startColorField(Text.literal("Background Color"), config.getBarTop2Color()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(config::setBarTop2Color).build());
      catTopbar2.add(entryBuilder.startStrField(Text.literal("Left Pattern"), config.getBarTop2LeftPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the left part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarTop2LeftPattern).build());
      catTopbar2.add(entryBuilder.startStrField(Text.literal("Center Pattern"), config.getBarTop2CenterPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the center part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarTop2CenterPattern).build());
      catTopbar2.add(entryBuilder.startStrField(Text.literal("Right Pattern"), config.getBarTop2RightPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the right part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarTop2RightPattern).build());
      bars.addEntry(catTopbar2.build());
      SubCategoryBuilder catBottombar1 = entryBuilder.startSubCategory(Text.of("Bottombar 1"));
      catBottombar1.add(entryBuilder.startBooleanToggle(Text.literal("Show"), config.getBarBottom1enabled()).setDefaultValue(true).setTooltip(Text.literal("Enables the top most bar on the ingame screen.")).setSaveConsumer(config::setBarBottom1enabled).build());
      catBottombar1.add(entryBuilder.startColorField(Text.literal("Background Color"), config.getBarBottom1Color()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(config::setBarBottom1Color).build());
      catBottombar1.add(entryBuilder.startStrField(Text.literal("Left Pattern"), config.getBarBottom1LeftPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the left part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarBottom1LeftPattern).build());
      catBottombar1.add(entryBuilder.startStrField(Text.literal("Center Pattern"), config.getBarBottom1CenterPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the center part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarBottom1CenterPattern).build());
      catBottombar1.add(entryBuilder.startStrField(Text.literal("Right Pattern"), config.getBarBottom1RightPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the right part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarBottom1RightPattern).build());
      bars.addEntry(catBottombar1.build());
      SubCategoryBuilder catBottombar2 = entryBuilder.startSubCategory(Text.of("Bottombar 2"));
      catBottombar2.add(entryBuilder.startBooleanToggle(Text.literal("Show"), config.getBarBottom2enabled()).setDefaultValue(true).setTooltip(Text.literal("Enables the top most bar on the ingame screen.")).setSaveConsumer(config::setBarBottom2enabled).build());
      catBottombar2.add(entryBuilder.startColorField(Text.literal("Background Color"), config.getBarBottom2Color()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(config::setBarBottom2Color).build());
      catBottombar2.add(entryBuilder.startStrField(Text.literal("Left Pattern"), config.getBarBottom2LeftPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the left part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarBottom2LeftPattern).build());
      catBottombar2.add(entryBuilder.startStrField(Text.literal("Center Pattern"), config.getBarBottom2CenterPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the center part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarBottom2CenterPattern).build());
      catBottombar2.add(entryBuilder.startStrField(Text.literal("Right Pattern"), config.getBarBottom2RightPattern()).setDefaultValue("").setTooltip(Text.literal("Template of the right part of the bar. You can use § color-codes here and % variable patterns.")).setSaveConsumer(config::setBarBottom2RightPattern).build());
      bars.addEntry(catBottombar2.build());
      SubCategoryBuilder catBarHelp = entryBuilder.startSubCategory(Text.of("Help"));
      for (Text text:DataManager.getInstance().getDescriptionList() )
      {
        catBarHelp.add(entryBuilder.startTextDescription(text).build());
      }
      bars.addEntry(catBarHelp.build());

      ConfigCategory tweaks = builder.getOrCreateCategory(Text.literal(Formatting.GOLD + "Tweaks"));
      tweaks.setBackground(Identifier.tryParse("minecraft:textures/block/soul_sand.png"));
      tweaks.addEntry(entryBuilder.startBooleanToggle(Text.literal("Prevent sending false commands"), config.getPreventFalseCommands()).setDefaultValue(true).setTooltip(Text.literal("Prevents sending Chat-Messages starting with '7' or 't/'. As this are the most common typo errors.")).setSaveConsumer(config::setPreventFalseCommands).build());
      tweaks.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Tool break warning"), config.getBreakwarnEnabled()).setDefaultValue(true).setTooltip(Text.literal("If enabled, a warning is displayed if the tool being used is about to be destroyed.")).setSaveConsumer(config::setBreakwarnEnabled).build());
      tweaks.addEntry(entryBuilder.startBooleanToggle(Text.literal("Toggle Unsecure Server Warning"), config.getUnsecureServerWarning()).setDefaultValue(false).setTooltip(Text.literal("If disabled, no Chat couldn't be verified message is displayed")).setSaveConsumer(config::setUnsecureServerWarning).build());
      tweaks.addEntry(entryBuilder.startBooleanToggle(Text.literal("Toggle Horn Audio"), config.getHornAudio()).setDefaultValue(false).setTooltip(Text.literal("If disabled, horn sounds are blocked for your client")).setSaveConsumer(config::setHornAudio).build());

      ConfigCategory titlescreen = builder.getOrCreateCategory(Text.literal(Formatting.AQUA + "Titlescreen"));
      titlescreen.setBackground(Identifier.tryParse("minecraft:textures/block/warped_nylium.png"));
      titlescreen.addEntry(
          entryBuilder
              .startStringDropdownMenu(Text.of("Titlescreen Theme"), config.getTitlescreenTheme())
              .setSelections(Arrays.asList("default", "magicclouds", "space", "evilyoungflesh"))
              .setDefaultValue("")
              .setTooltip(Text.literal("Select the default or a fancy background for the titlescreen."))
              .setSaveConsumer(config::setTitlescreenTheme)
              .build()
      );


//      general.addEntry(entryBuilder.startColorField(Text.literal("Color Background"), config.getColorBackground()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(config::setColorBackground).build());


      builder.transparentBackground();

      return builder.build();
    }
    return null;
  }
}
