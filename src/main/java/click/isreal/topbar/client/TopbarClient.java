package click.isreal.topbar.client;

/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2022 YveIce
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
 ******************************************************************************/

import click.isreal.topbar.Topbar;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment( EnvType.CLIENT )
public class TopbarClient implements ClientModInitializer
{

    private static TopbarClient instance;
    private final MinecraftClient client = MinecraftClient.getInstance();
    // Storage for all TopBar Strings (Parts), so we only need to build/change them,
    // when things really changed. Saves some time in gui-render thread
    public String strSplitter = Formatting.GRAY + " | ";
    public String strTopLeft = "";
    public String strTopRight = "";
    public String strMoney = "";
    public String strMoneyPocket = "";
    public String strRolle = "";
    public String strKills = "";
    public String strDeath = "";
    public String strKD = "";
    public String strKffaMap = "";
    public String strKffaTime = "";
    public String strRangPoints = "";
    public String strAufstiegPoints = ""; //todo: give it a nice english name LUL
    public String strDimension = "";
    public String DEBUGTEXT = Formatting.YELLOW + "";
    public String strPlotName = "";
    public String strPlotOwner = "";
    public mpWorld world = mpWorld.OTHER;
    public discordRPC dc;
    private boolean _isMixel = false;

    {
        instance = this;
    }

    public static TopbarClient getInstance()
    {
        return instance;
    }

    @Override
    public void onInitializeClient()
    {
        System.out.println("\\u001b[0;35mYVE™ - Topbar: started at " + java.time.LocalDateTime.now());
        try
        {
            dc = new discordRPC();
        }
        catch (Exception e)
        {
            // Topbar.log.log(Level.WARN, "Error starting DC: \n" + e.getMessage());
        }
    }

    public void setisMixel( boolean Status )
    {
        _isMixel = Status;
    }

    public boolean isMixelPixel()
    {
        return _isMixel;
        // return (!this.client.isInSingleplayer() || this.client.getServer() != null && this.client.getServer().isRemote()) && (MinecraftClient.getInstance().getNetworkHandler() != null) && (MinecraftClient.getInstance().getNetworkHandler().getConnection().getAddress() != null) && MinecraftClient.getInstance().getNetworkHandler().getConnection().getAddress().toString().matches(".*45\\.135\\.203\\.18.*");
/*        if ( !this.client.isInSingleplayer() && this.client.world != null
                && MinecraftClient.getInstance().getNetworkHandler().getConnection().getAddress().toString().matches(".*45\\.135\\.203\\.18.*")
        ) return true;

 */
    }

    public String getWorldName( mpWorld world )
    {
        switch (world)
        {
            // ""+ <== Dirty Trick to force converting enum to String
            case HUB:
                return "" + Formatting.WHITE + Formatting.BOLD + "LOBBY";
            case SPAWN1:
                return "" + Formatting.WHITE + Formatting.BOLD + "SPAWN-1";
            case SPAWN2:
                return "" + Formatting.WHITE + Formatting.BOLD + "SPAWN-2";
            case SPAWN3:
                return "" + Formatting.WHITE + Formatting.BOLD + "SPAWN-3";
            case SPAWN4:
                return "" + Formatting.WHITE + Formatting.BOLD + "SPAWN-4";
            case WW:
                return "" + Formatting.GOLD + Formatting.BOLD + "WW";
            case KFFA:
                return "" + Formatting.AQUA + Formatting.BOLD + "KFFA";
            case FARMWORLD1:
                return "" + Formatting.GRAY + Formatting.BOLD + "FARMWELT-1";
            case FARMWORLD2:
                return "" + Formatting.GRAY + Formatting.BOLD + "FARMWELT-2";
            case FARMWORLD3:
                return "" + Formatting.GRAY + Formatting.BOLD + "FARMWELT-3";
            case FARMWORLD4:
                return "" + Formatting.GRAY + Formatting.BOLD + "FARMWELT-4";
            case SMALLFLORA:
                return "" + Formatting.GREEN + Formatting.BOLD + "CB-KLEIN BLUMEN";
            case SMALLAQUA:
                return "" + Formatting.AQUA + Formatting.BOLD + "CB-KLEIN AQUA";
            case SMALLVULKAN:
                return "" + Formatting.RED + Formatting.BOLD + "CB-KLEIN VULKAN";
            case SMALLDONNER:
                return "" + Formatting.GOLD + Formatting.BOLD + "CB-KLEIN DONNER";
            case BIGFLORA:
                return "" + Formatting.GREEN + Formatting.BOLD + "CB-GROSS BLUMEN";
            case BIGAQUA:
                return "" + Formatting.AQUA + Formatting.BOLD + "CB-GROSS AQUA";
            case BIGVULKAN:
                return "" + Formatting.RED + Formatting.BOLD + "CB-GROSS VULKAN";
            case BIGDONNER:
                return "" + Formatting.GOLD + Formatting.BOLD + "CB-GROSS DONNER";
            case EVENT:
                return "" + Formatting.WHITE + Formatting.BOLD + "EVENT";
            case XMASEVENT:
                return "" + Formatting.RED + Formatting.BOLD + "XMAS-EVENT";
            default:
                return "" + Formatting.RED + Formatting.BOLD + "UNKNOWN";
        }
    }

    public String getFPS()
    {
        if ( Topbar.getInstance().isFpsShow() )
        {
            String fps = "";
            fps += this.client.fpsDebugString.split("fps")[0].trim();
            while ( fps.length() < 3 )
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

        switch (world)
        {
            case HUB:
            case SPAWN1:
            case SPAWN2:
            case SPAWN3:
            case SPAWN4:
                strTopLeft = getWorldName(world) + Formatting.GRAY + " - " + strRolle;
                strTopRight = "";
                break;
            case WW:
                strTopLeft += getWorldName(world) + Formatting.GRAY + " - " + strRolle;
                strTopRight = strKills + strSplitter + strDeath + strSplitter + strKD + strSplitter;
                if ( Topbar.getInstance().isStreamerMode() ) strTopRight = Formatting.YELLOW + "[STREAMING]";
                else strTopRight = strMoney + strSplitter + strMoneyPocket;
                break;
            case KFFA:
                strTopLeft = "" + Formatting.BLUE + Formatting.BOLD + "MP" + Formatting.GRAY + " - " + getWorldName(world) + Formatting.GRAY + " - " + strKffaMap + strKffaTime + Formatting.GRAY + " - " + strRolle;
                strTopRight = strRangPoints + strAufstiegPoints + strSplitter + strKD + strSplitter;
                if ( Topbar.getInstance().isStreamerMode() ) strTopRight = Formatting.YELLOW + "[STREAMING]";
                else strTopRight = strMoney;
                break;
            case FARMWORLD1:
            case FARMWORLD2:
            case FARMWORLD3:
            case FARMWORLD4:
                strTopLeft += getWorldName(world);
                if ( null != MinecraftClient.getInstance().world )
                {
                    if ( World.END == MinecraftClient.getInstance().world.getRegistryKey() ) strDimension = "End";
                    else if ( World.NETHER == MinecraftClient.getInstance().world.getRegistryKey() )
                        strDimension = "Nether";
                    else if ( World.OVERWORLD == MinecraftClient.getInstance().world.getRegistryKey() )
                        strDimension = "Overworld";
                    else strDimension = "";
                }
                else strDimension = "";
                if ( Topbar.getInstance().isStreamerMode() ) strTopRight = Formatting.YELLOW + "[STREAMING]";
                else strTopRight = strMoney;
                break;
            case SMALLAQUA:
            case SMALLDONNER:
            case SMALLFLORA:
            case SMALLVULKAN:
            case BIGAQUA:
            case BIGDONNER:
            case BIGFLORA:
            case BIGVULKAN:
                strTopLeft += getWorldName(world);
                strDimension = "";
                if ( Topbar.getInstance().isStreamerMode() ) strTopRight = Formatting.YELLOW + "[STREAMING]";
                else strTopRight = strMoney;
                break;
            case XMASEVENT:
                strTopLeft += getWorldName(world);
                strTopRight = strDeath + strSplitter + strKills;
                break;
            default:
                strTopRight = Formatting.RED + "?"; // at this moment we don't know what to do ;-)
        }
    }

    public Screen createConfigScreen( Screen parent )
    {
        if ( FabricLoader.getInstance().isModLoaded("cloth-config2") )
        {

            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal(Formatting.LIGHT_PURPLE + "" + Formatting.BOLD + "YVE\u2122" + Formatting.WHITE + " - Topbar Mod \u2620"));
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("YveTopbar"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.setBackground(Identifier.tryParse("minecraft:textures/block/dragon_egg.png"));
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Streamer-Mode"), Topbar.getInstance().isStreamerMode()).setDefaultValue(false).setTooltip(Text.literal("If enabled, your ingame money value would be hidden on topbar.")).setSaveConsumer(Topbar.getInstance()::setStreamerMode).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color Background"), Topbar.getInstance().getColorBackground()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setColorBackground).build());
            general.addEntry(entryBuilder.startIntSlider(Text.literal("Effect Icon Size"), Topbar.getInstance().getEffectIconSize(), 6, 32).setDefaultValue(10).setTooltip(Text.literal("Scales the Effect Icon Size.")).setSaveConsumer(Topbar.getInstance()::setEffectIconSize).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color Effect Icon Positive"), Topbar.getInstance().getEffectColorPositive()).setDefaultValue(0xff00ff00).setAlphaMode(true).setTooltip(Text.literal("Sets the backgroundcolor of positive effect icon in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setEffectColorPositive).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color Effect Icon Negative"), Topbar.getInstance().getEffectColorNegative()).setDefaultValue(0xffff0000).setAlphaMode(true).setTooltip(Text.literal("Sets the backgroundcolor of negative effect icon in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setEffectColorNegative).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show FPS"), Topbar.getInstance().isFpsShow()).setDefaultValue(true).setTooltip(Text.literal("If enabled, FPS is shown left-most on the topbar.")).setSaveConsumer(Topbar.getInstance()::setFpsShow).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color FPS"), Topbar.getInstance().getFpsColor()).setDefaultValue(0xff808080).setAlphaMode(true).setTooltip(Text.literal("Sets the textcolor of FPS in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setFpsColor).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Time"), Topbar.getInstance().isTimeShow()).setDefaultValue(true).setTooltip(Text.literal("If enabled, Time of your computer is shown right-most on the topbar.")).setSaveConsumer(Topbar.getInstance()::setTimeShow).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color Time"), Topbar.getInstance().getTimeColor()).setDefaultValue(0xff808080).setAlphaMode(true).setTooltip(Text.literal("Sets the textcolor of Time in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setTimeColor).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Prevent sending false commands"), Topbar.getInstance().isPreventFalseCommands()).setDefaultValue(true).setTooltip(Text.literal("Prevents sending Chat-Messages starting with '7' or 't/'. As this are the most common typo errors.")).setSaveConsumer(Topbar.getInstance()::setPreventFalseCommands).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color Loading Screen"), Topbar.getInstance().getLoadscreenColor()).setTooltip(Text.literal("Sets the background color of the loadingscreen(the one with the mojang logo) in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setLoadscreenColor).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Discord"), Topbar.getInstance().isDiscordEnabled()).setDefaultValue(true).setTooltip(Text.literal("If enabled and Discord app is running, your profil will show that you are playing on MixelPixel.")).setSaveConsumer(Topbar.getInstance()::setDiscordEnabled).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Tool break warning"), Topbar.getInstance().isBreakwarnEnabled()).setDefaultValue(true).setTooltip(Text.literal("If enabled, a warning is displayed if the tool being used is about to be destroyed.")).setSaveConsumer(Topbar.getInstance()::setBreakwarnEnabled).build());

            return builder.build();
        }
        return null;
    }


    public enum mpWorld
    {
        HUB, WW, KFFA, SPAWN1, SPAWN2, SPAWN3, SPAWN4, FARMWORLD1, FARMWORLD2, FARMWORLD3, FARMWORLD4, SMALLFLORA, SMALLAQUA, SMALLVULKAN, SMALLDONNER, BIGFLORA, BIGAQUA, BIGVULKAN, BIGDONNER, EVENT, XMASEVENT, OTHER
    }
}
