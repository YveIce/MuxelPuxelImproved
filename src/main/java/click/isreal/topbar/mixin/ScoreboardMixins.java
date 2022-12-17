package click.isreal.topbar.mixin;

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
import click.isreal.topbar.client.TopbarClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Environment( EnvType.CLIENT )
@Mixin( Scoreboard.class )
public class ScoreboardMixins
{

    @Shadow
    @Final
    private Map<String, ScoreboardObjective> objectives;

    private String stripStr( String text )
    {
        return text.replaceAll("§[0-9a-fklmnor]", "").trim();
    }

    private void updateObjective( String player, ScoreboardObjective objective )
    {
        String text = stripStr(player).trim();
        String displayName = objective.getDisplayName().getString().trim();

        if ( displayName.matches(".*KnockbackFFA.*") )
        {
            TopbarClient.getInstance().world = TopbarClient.mpWorld.KFFA;
            TopbarClient.getInstance().strKffaTime = Formatting.DARK_AQUA + " [" + displayName.replaceAll("-[0-9]", "").replaceAll("[^0-9\\:]", "") + "]";
            if ( text.matches("(.*)Map:(.*)") )
            {
                TopbarClient.getInstance().strKffaMap = Formatting.YELLOW + text.replaceAll("Map:", "").trim();
            }
            else if ( text.matches("(.*)Rang:(.*)") )
            {
                TopbarClient.getInstance().strRolle = Formatting.WHITE + text.replaceAll("Rang:", "").trim();
            }
            else if ( text.matches("(.*)Rangpunkte:(.*)") )
            {
                TopbarClient.getInstance().strRangPoints = Formatting.AQUA + "[" + text.replaceAll("[^0-9.]", "").trim() + "/";
            }
            else if ( text.matches("(.*)Aufstieg(.*)") )
            {
                TopbarClient.getInstance().strAufstiegPoints = Formatting.AQUA + text.replaceAll("[^0-9.]", "").trim() + "]";
            }
            else if ( text.matches("(.*)Coins:(.*)") )
            {
                String tmpMoney = text.replaceAll("[^0-9,]", "").replaceAll(",", ".");
                tmpMoney = Topbar.getInstance().moneyformat.format(Double.parseDouble(tmpMoney));
                TopbarClient.getInstance().strMoney = Formatting.GOLD + tmpMoney;
            }
            else if ( text.matches("(.*)K\\/D:(.*)") )
            {
                TopbarClient.getInstance().strKD = Formatting.YELLOW + text.replaceAll("[^0-9.]", "") + " \u2300";
            }
        }
        else if ( displayName.matches("(.*)CityBuild(.*)") )
        {
            if ( text.matches(".*Flora Klein.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.SMALLFLORA;
            }
            else if ( text.matches(".*Aqua Klein.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.SMALLAQUA;
            }
            else if ( text.matches(".*Vulkan Klein.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.SMALLVULKAN;
            }
            else if ( text.matches(".*Donner Klein.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.SMALLDONNER;
            }
            else if ( text.matches(".*Flora Groß.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.BIGFLORA;
            }
            else if ( text.matches(".*Aqua Groß.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.BIGAQUA;
            }
            else if ( text.matches(".*Vulkan Groß.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.BIGVULKAN;
            }
            else if ( text.matches(".*Donner Groß.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.BIGDONNER;
            }
            else if ( text.matches(".*Farmwelt 1.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.FARMWORLD1;
            }
            else if ( text.matches(".*Farmwelt 2.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.FARMWORLD2;
            }
            else if ( text.matches(".*Farmwelt 3.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.FARMWORLD3;
            }
            else if ( text.matches(".*Farmwelt 4.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.FARMWORLD4;
            }
            else if ( text.matches(".*Spawn 1.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.SPAWN1;
            }
            else if ( text.matches(".*Spawn 2.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.SPAWN2;
            }
            else if ( text.matches(".*Spawn 3.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.SPAWN3;
            }
            else if ( text.matches(".*Spawn 4.*") )
            {
                TopbarClient.getInstance().world = TopbarClient.mpWorld.SPAWN4;
            }

            else if ( text.matches("(.*) ⛀(.*)") )
            {
                String tmpMoney = text.replaceAll("[^0-9,]", "").replaceAll(",", ".");
                tmpMoney = Topbar.getInstance().moneyformat.format(Double.parseDouble(tmpMoney));
                TopbarClient.getInstance().strMoney = Formatting.YELLOW + tmpMoney;
            }

            else if ( player.matches("§0§([4-9])§f §8• (.*)") )
            {
                // plotname
                TopbarClient.getInstance().strPlotName = player.replaceAll("§0§[4-9]§f §8• ", "");
            }
            else if ( player.matches("§0§[4-9]§f  §8(.*)") )
            {
                // plotowner
                TopbarClient.getInstance().strPlotOwner = player.replaceAll("§0§[4-9]§f  §8► ", "");
            }
            else if ( text == "" )
            {
                TopbarClient.getInstance().strPlotName = "";
                TopbarClient.getInstance().strPlotOwner = "";
            }

        }
        else if ( displayName.matches(".*Weihnachtsevent.*") )
        {
            TopbarClient.getInstance().world = TopbarClient.mpWorld.XMASEVENT;
            if ( text.matches("(.*)/100$") )
            {
                String tmp = text.replaceAll("/100", "").replaceAll("[^0-9]", "");
                TopbarClient.getInstance().strDeath = Formatting.RED + "Geschenke: " + Formatting.WHITE + tmp + "/100";
            }
            if ( text.matches("(.*)/10$") )
            {
                String tmp = text.replaceAll("/10", "").replaceAll("[^0-9]", "");
                TopbarClient.getInstance().strKills = Formatting.GREEN + "Nussknacker: " + Formatting.WHITE + tmp + "/10";
            }
        }
        else if ( text.matches(".*Lobby.*") )
        {
            TopbarClient.getInstance().world = TopbarClient.mpWorld.HUB;
        }
        else if ( text.matches("(.*)Rang:(.*)") )
        {
            TopbarClient.getInstance().strRolle = Formatting.WHITE + text.replaceAll("• Rang:", "").trim();
        }
/*    else
    {
      Topbar.getInstance().log.log(Level.WARN, "Scoreboard->getPlayerScore =>\n"
              + " -> Player: "      + stripStr(player) + "\n"
              + " -> Name: "        + objective.getName() + "\n"
              + " -> DisplayName: " + objective.getDisplayName().getString() + "\n"
              + " -> RenderType: "  + objective.getRenderType().getName() + "\n"
              + " -> Criterion: "   + objective.getCriterion().getName() + "\n\n"
      );
    }
*/
        TopbarClient.getInstance().updateTopBar();
    }

    @Inject( method = "getPlayerScore", at = @At( "HEAD" ) )
    public void getPlayerScore( String player, ScoreboardObjective objective, CallbackInfoReturnable<ScoreboardPlayerScore> infoReturnable )
    {
        if ( player.length() <= 40 )
        {
            updateObjective(player, objective);
        }
    }

    @Inject( method = "updateExistingObjective", at = @At( "HEAD" ) )
    public void updateExistingObjective( ScoreboardObjective objective, final CallbackInfo callbackInfo )
    {
        if ( objective != null ) updateObjective("", objective);
    }

}
