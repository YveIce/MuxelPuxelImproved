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

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.WorldChunk;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class DataManager
{
  private static final Pattern formatterPattern = Pattern.compile("&([0-9a-flmnor])");
  private static DataManager instance;
  private final MinecraftClient client = MinecraftClient.getInstance();
  public Map<DataId, DataEntry> dataList = new HashMap<>();

  public DataManager()
  {
    instance = this;
    dataList.put(DataId.COMPASS, new DataEntry("Kompass", "Zeigt dir die Himmelsrichtung in die du blickst.", "%cp", "CP"));
    dataList.put(DataId.EXP, new DataEntry("Exp", "Deine Erfahrungspunkte.", "%xp", "0"));
    dataList.put(DataId.FPS, new DataEntry("FPS", "Frames pro Sekunde.", "%fs", "0"));
    dataList.put(DataId.HEALLEVEL, new DataEntry("Gesundheit", "Dein Gesundheitslevel.", "%hl", "0"));
    dataList.put(DataId.HEALMAX, new DataEntry("Max. Gesundheit", "Dein maximales Gesundheitslevel.", "%hm", "0"));
    dataList.put(DataId.HEALPERCENT, new DataEntry("Gesundheit Prozent", "Dein Gesundheitslevel in Prozent.", "%hp", "0"));
    dataList.put(DataId.HEALBAR, new DataEntry("Gesundheits Herzen", "Zeigt dir deine Gesundheit als Herzen an.", "%hb", "0"));
    dataList.put(DataId.HUNGERLEVEL, new DataEntry("Hunger", "Zeigt dir deine Nahrungspunkte an.", "%fl", "0"));
    dataList.put(DataId.HUNGERMAX, new DataEntry("Max. Hunger", "Deine maximalen Nahrungspunkte.", "%fm", "0"));
    dataList.put(DataId.HUNGERBAR, new DataEntry("Hunger", "Zeigt dir deine Hunger als Fleischkeulen an.", "%fb", "0"));
    dataList.put(DataId.LOOKATBLOCK, new DataEntry("Block", "Zeigt dir den Typ des Blockes an, auf den du schaust.", "%lb", "0"));
    dataList.put(DataId.LIGHTLEVEL, new DataEntry("Light", "Zeigt dir den Helligkeitswert an.", "%ll", "0"));
    dataList.put(DataId.POSX, new DataEntry("PosX", "Deine Position-X", "%px", "0"));
    dataList.put(DataId.POSY, new DataEntry("PosY", "Deine Position-Y", "%py", "0"));
    dataList.put(DataId.POSZ, new DataEntry("PosZ", "Deine Position-Z", "%pz", "0"));
    dataList.put(DataId.TIME, new DataEntry("Pc Zeit", "Aktuelle Uhrzeit laut deinem Pc im 24h Format", "%tr", "00:00"));
    dataList.put(DataId.TIMEMC, new DataEntry("MC Zeit", "Die InGame Uhrzeit konvertiert ins 24h Format", "%tm", "00:00"));
  }

  public static DataManager getInstance()
  {
    if (null == instance)
    {instance = new DataManager();}
    return instance;
  }

  public static String generateProgressBar(int value, String empty, String full, String half)
  {
    StringBuilder progressBar = new StringBuilder();
    int fullSteps = value / 2;
    int halfStep = value % 2;
    for (int i = 0; i < 10; i++)
    {
      if (i < fullSteps)
      {
        progressBar.append(full);
      }
      else if (i == fullSteps)
      {
        progressBar.append(halfStep == 0 ? empty : half);
      }
      else
      {
        progressBar.append(empty);
      }
    }
    return progressBar.toString();
  }

  /**
   * process a String with placeholders and returns a List of Styles Texts
   *
   * @param patternText String containing magic patterns to be replaced with the current values
   * @return List<Text>
   */
  public List<Text> processPatterns(String patternText)
  {
    String inputText = formatterPattern.matcher(patternText).replaceAll("§$1");
    List<Text> result = new ArrayList<>();
    StringBuilder currentText = new StringBuilder();

    int index = 0;
    while (index < inputText.length())
    {
      char currentChar = inputText.charAt(index);
      if (currentChar == '%' && index + 1 < inputText.length() && inputText.charAt(index + 1) == '%')
      {
        currentText.append("%");
        index += 2;
      }
      else if (currentChar == '%' && index + 2 < inputText.length() &&
          Character.isLetter(inputText.charAt(index + 1)) &&
          Character.isLetter(inputText.charAt(index + 2)))
      {
        currentText.append(resolvePattern(inputText.substring(index, index + 3)));
        index += 3;
      }
      else
      {
        currentText.append(currentChar);
        index++;
      }
    }
    result.add(Text.literal(currentText.toString()));
    return result;
  }

  private String resolvePattern(String pattern)
  {
    for (Map.Entry<DataId, DataEntry> mapEntry : dataList.entrySet())
    {
      DataEntry entry = mapEntry.getValue();
      if (entry.pattern().equals(pattern))
      {
        return entry.value();
      }
    }
    return pattern;
  }

  private PlayerEntity getCameraPlayer()
  {
    if (!(this.client.getCameraEntity() instanceof PlayerEntity))
    {
      return null;
    }
    return (PlayerEntity) this.client.getCameraEntity();
  }

  private String getHealBar()
  {
    PlayerEntity player = this.getCameraPlayer();
    if (player == null)
    {
      return "";
    }
    int healValue = MathHelper.ceil(player.getHealth());
    // base blink on systemtime, 0,5Hz
    boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
    int heal20 = Math.min(20, MathHelper.ceil(20 / player.getMaxHealth() * player.getHealth()));
    //player.rege
    char heartColor = '4';
    if (player.hasStatusEffect(StatusEffects.POISON))
    {heartColor = '2';}
    else if (player.hasStatusEffect(StatusEffects.WITHER))
    {heartColor = '7';}
    else if (player.isFrozen())
    {heartColor = 'b';}
    StringBuilder progressBar = new StringBuilder();
    int fullSteps = heal20 / 2;
    for (int i = 0; i < 10; i++)
    {
      if (i < (player.getAbsorptionAmount() / 2))
      {
        progressBar.append(((player.getAbsorptionAmount() % 2) != 0 && ((player.getAbsorptionAmount() / 2) - i < 1)) ? "§6❤" : "§e❤");
      }
      else if (i < fullSteps)
      {
        progressBar.append("§" + heartColor + "❤");
      }
      else if (i == fullSteps)
      {
        progressBar.append((heal20 % 2) == 0 ? "§8♥" : "§" + heartColor + "♥");
      }
      else
      {
        progressBar.append("§8♥");
      }
    }

    return progressBar.toString();
  }

  // update only all values, that can change every frame, for better performance
  public void tick()
  {
    PlayerEntity player = (PlayerEntity) this.client.getCameraEntity();
    HitResult hitResult = player.raycast(20.0, 0.0f, false);
    // fps formated to 3 char length, so text is not flickering around 99-100 fps
    dataList.put(DataId.FPS, dataList.get(DataId.FPS).setValue(String.format(Locale.ROOT, "%3d", client.getCurrentFps())));
    String compassText = "...N...ne...E...es...S...sw...W...wn";
    String compass;
    int compassIndex = (int) ((wrapDegrees(this.client.getCameraEntity().getYaw()) + 180.) / 360.0 * compassText.length()) % compassText.length();
    compass = (compassText.substring(compassIndex) + compassText.substring(0, compassIndex)).substring(0, 7);
    dataList.put(DataId.COMPASS, dataList.get(DataId.COMPASS).setValue(compass));
    if (hitResult.getType() == HitResult.Type.BLOCK)
    {
      dataList.put(DataId.LOOKATBLOCK, dataList.get(DataId.LOOKATBLOCK).setValue(
          String.valueOf(Registries.BLOCK.getId(this.client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock())).replace("minecraft:", "")
      ));
    }
    else
    {
      dataList.put(DataId.LOOKATBLOCK, dataList.get(DataId.LOOKATBLOCK).setValue("air"));
    }
    if (!((WorldChunk) this.client.world.getChunk(((BlockHitResult) hitResult).getBlockPos())).isEmpty())
    {
      dataList.put(DataId.LIGHTLEVEL, dataList.get(DataId.LIGHTLEVEL).setValue(String.format(Locale.ROOT, "%2d", this.client.world.getChunkManager().getLightingProvider().getLight(client.getCameraEntity().getBlockPos(), 0))));
    }
    else
    {
      dataList.put(DataId.LIGHTLEVEL, dataList.get(DataId.LIGHTLEVEL).setValue("-1"));
    }
    dataList.put(DataId.TIME, dataList.get(DataId.TIME).setValue(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));
    dataList.put(DataId.HEALBAR, dataList.get(DataId.HEALBAR).setValue(getHealBar()));
    dataList.put(DataId.HEALMAX, dataList.get(DataId.HEALMAX).setValue(String.format(Locale.ROOT, "%.2f", player.getMaxHealth())));
    dataList.put(DataId.HEALLEVEL, dataList.get(DataId.HEALLEVEL).setValue(String.format(Locale.ROOT, "%.2f", player.getHealth() + player.getAbsorptionAmount())));
    dataList.put(DataId.HEALPERCENT, dataList.get(DataId.HEALPERCENT).setValue(String.format(Locale.ROOT, "%3d%%", Math.round((player.getHealth() + player.getAbsorptionAmount()) / player.getMaxHealth() * 100))));

  }

  public void setTimeMc(String value)
  {
    dataList.put(DataId.TIMEMC, dataList.get(DataId.TIMEMC).setValue(value));
  }

  public List<Text> getDescriptionList()
  {
    List<Text> result = new ArrayList<>();
    result.add(Text.literal("§6§lPattern-List:"));
    result.add(Text.literal("§7§oThis is a list of all available patterns/placeholders you can use to customize the top and bottom bars display according to your needs. These will be automatically replaced by the corresponding values in the game."));
    String colorPatterns = "";
    for (String colorname : Formatting.getNames(true, false))
    {
      if ('r' != Formatting.byName(colorname).getCode())
      {colorPatterns += "§e§l&" + Formatting.byName(colorname).getCode() + "§f " + Formatting.byName(colorname) + colorname + Formatting.RESET + "" + Formatting.WHITE + ", ";}
    }
    colorPatterns = colorPatterns.substring(0, colorPatterns.length() - 2);
    result.add(Text.literal("§f§lFormatting patterns: §r"
        + "§e§l&r §fRESET (resets all previous formatting), "
        + "§e§l&l §f§lBOLD§r§f, "
        + "§e§l&o §f§oITALIC§r§f, "
        + "§e§l&m §f§mSTRIKETHROUGH§r§f, "
        + "§e§l&n §f§nUNDERLINE§r§f, "
    ));
    result.add(Text.literal("§f§lColor patterns: §r" + colorPatterns));
    result.add(Text.literal("§f§lData patterns: §r"));
    for (Map.Entry<DataId, DataEntry> mapEntry : dataList.entrySet())
    {
      DataEntry entry = mapEntry.getValue();
      result.add(Text.literal("§e§l" + entry.pattern() + "§r §f" + entry.name + " §7§o(" + entry.description() + ")"));
    }
    result.add(Text.literal("§e§lSymbols: §6⛏ \uD83D\uDDE1 \uD83C\uDFF9 \uD83E\uDE93 \uD83D\uDD31 \uD83C\uDFA3 ♡♥❤ ⭐ ★ ☆ ○ ◎ ⭘ "));
    return result;
  }

  public enum DataId
  {
    COMPASS, EXP, FPS, HEALLEVEL, HEALMAX, HEALPERCENT, HEALBAR, HEARTS, HUNGERLEVEL, HUNGERMAX, HUNGERBAR, LOOKATBLOCK, LIGHTLEVEL, TIMEMC, MONEY, POSX, POSY, POSZ, SERVER, TIME, TOOLDEMAGE, TOOLMAXDEMAGE, WORLD
  }

  public record DataEntry(String name, String description, String pattern, String value)
  {
    public DataEntry setValue(String newValue)
    {
      return new DataEntry(name, description, pattern, newValue);
    }
  }
}
