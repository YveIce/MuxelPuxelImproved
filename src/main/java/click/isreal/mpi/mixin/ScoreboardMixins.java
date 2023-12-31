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

package click.isreal.mpi.mixin;

import click.isreal.mpi.Mpi;
import click.isreal.mpi.client.mpiClient;
import click.isreal.mpi.domain.MixelWorld;
import click.isreal.mpi.domain.UserData;
import click.isreal.mpi.domain.Winter22Event;
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

@Environment(EnvType.CLIENT)
@Mixin(Scoreboard.class)
public class ScoreboardMixins
{

  @Shadow
  @Final
  private Map<String, ScoreboardObjective> objectives;

  private String stripStr(String text)
  {
    return text.replaceAll("§[0-9a-fklmnor]", "").trim();
  }

  private void updateObjective(String player, ScoreboardObjective objective)
  {
    String text = stripStr(player).trim();
    String displayName = objective.getDisplayName().getString().trim();

    if (displayName.matches(".*KnockbackFFA.*"))
    {
      String trimmedName = displayName
          .replaceAll("-[0-9]", "").replaceAll("[^0-9\\:]", "");
      mpiClient.getInstance().getScoreboardData()
          .setMixelWorld(MixelWorld.KFFA)
          .setKffaMapSwitch(Formatting.DARK_AQUA + " [" + trimmedName + "]");
      if (text.matches("(.*)Map:(.*)"))
      {
        String map = Formatting.YELLOW + text.replaceAll("Map:", "").trim();
        UserData.current().setKffaMap(map);
      }
      else if (text.matches("(.*)Rang:(.*)"))
      {
        String rank = Formatting.YELLOW + text.replaceAll("Rang:", "").trim();
        UserData.current().setRank(rank);
      }
      else if (text.matches("(.*)Rangpunkte:(.*)"))
      {
        String rankPoints = Formatting.AQUA + "[" + text.replaceAll("[^0-9.]", "").trim() + "/";
        UserData.current().setRankPoints(rankPoints);
      }
      else if (text.matches("(.*)Aufstieg(.*)"))
      {
        String strAufstiegPoints = Formatting.AQUA + text.replaceAll("[^0-9.]", "").trim() + "]";
        UserData.current().setAufstiegPoints(strAufstiegPoints);
      }
      else if (text.matches("(.*)Coins:(.*)"))
      {
        String tmpMoney = text.replaceAll("[^0-9,]", "").replaceAll(",", ".");
        try
        {
          tmpMoney = Mpi.getInstance().moneyformat.format(Double.parseDouble(tmpMoney));
          UserData.current().setMoney(Formatting.GOLD + tmpMoney);
        }
        catch (NumberFormatException nfe)
        {
          UserData.current().setMoney(Formatting.GOLD + "<Versteckt>");
        }
      }
      else if (text.matches("(.*)K\\/D:(.*)"))
      {
        String kd = Formatting.YELLOW + text.replaceAll("[^0-9.]", "") + " ⌀";
        UserData.current().setAufstiegPoints(kd);
      }
    }
    else if (displayName.matches("(.*)CityBuild(.*)"))
    {
      MixelWorld world = MixelWorld.findWorld(text);
      if (world != MixelWorld.OTHER)
      {
        UserData.current().setMixelWorld(world);
        return;
      }
      if (text.matches("(.*) ⛀(.*)"))
      {
        String tmpMoney = text.replaceAll("[^0-9,]", "").replaceAll(",", ".");
        try
        {
          tmpMoney = Mpi.getInstance().moneyformat.format(Double.parseDouble(tmpMoney));
          UserData.current().setMoney(Formatting.YELLOW + tmpMoney);
        }
        catch (NumberFormatException nfe)
        {
          UserData.current().setMoney(Formatting.GOLD + "<Versteckt>");
        }
      }

      if (player.contains("%§8]"))
      {
        UserData.current().setJubiProgress(player);
      }
/*
            else if ( player.matches("§0§([4-9])§f §8• (.*)") )
            {
                // plotname
                ScoreboardData.current().setCbPlotName(player.replaceAll("§0§[4-9]§f §8• ", ""));
            }
            else if ( player.matches("§0§[4-9]§f  §8(.*)") )
            {
                // plotowner
                ScoreboardData.current().setCbPlotOwner(player.replaceAll("§0§[4-9]§f  §8► ", ""));
            }*/
    }
    else if (text.matches(".*Lobby.*"))
    {
      UserData.current().setMixelWorld(MixelWorld.HUB);
    }
    else if (text.matches("(.*)Rang:(.*)"))
    {
      String rank = Formatting.WHITE + text.replaceAll("• Rang:", "").trim();
      UserData.current().setRank(rank);
    }

    if (displayName.matches("(.*)Winterevent(.*)"))
    {
      if (text.matches("(.*)Türchen:(.*)"))
      {
        String tuer = Formatting.YELLOW + text.replaceAll("• Türchen:", "").trim();
        UserData.current().getInjection(Winter22Event.class).setTuer(tuer);
      }
      else if (text.matches("(.*)Modus:(.*)"))
      {
        String mode = Formatting.YELLOW + text.replaceAll("• Modus:", "").trim();
        UserData.current().getInjection(Winter22Event.class).setModus(mode);
      }
      else if (text.matches("(.*)Checkpoint:(.*)"))
      {
        String cp = Formatting.YELLOW + text.replaceAll("• Checkpoint:", "").trim();
        UserData.current().getInjection(Winter22Event.class).setCheckpoints(cp);
      }
    }
    else
    {
      UserData.current().updateIfExists(Winter22Event.class, (i) -> i.setTuer(null).setModus(null).setCheckpoints(null));
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
    mpiClient.getInstance().updateTopBar();
  }

  @Inject(method = "getPlayerScore", at = @At("HEAD"))
  public void getPlayerScoreInject(String player, ScoreboardObjective objective, CallbackInfoReturnable<ScoreboardPlayerScore> infoReturnable)
  {
    if (mpiClient.getInstance().isMixelPixel()) updateObjective(player, objective);
  }

  @Inject(method = "updateExistingObjective", at = @At("HEAD"))
  public void updateExistingObjectiveInject(ScoreboardObjective objective, final CallbackInfo callbackInfo)
  {
    if (mpiClient.getInstance().isMixelPixel() && objective != null) updateObjective("", objective);
  }

}
