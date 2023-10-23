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
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;
import java.util.regex.Pattern;

import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class DataManager
{
  private static DataManager instance;
  private final MinecraftClient client = MinecraftClient.getInstance();
  private static final Pattern formatterPattern = Pattern.compile("&([0-9a-flmnor])");

  public Map<DataId, DataEntry> dataList = new HashMap<>();

  public static DataManager getInstance()
  {
    if (null == instance)
    {instance = new DataManager();}
    return instance;
  }

  public DataManager()
  {
    instance = this;
    dataList.put(DataId.COMPASS, new DataEntry("Kompass", "Zeigt dir die Himmelsrichtung in die du blickst.", "%cp", "CP"));
    dataList.put(DataId.EXP, new DataEntry("Exp", "Deine Erfahrungspunkte.", "%xp", "0"));
    dataList.put(DataId.FPS, new DataEntry("FPS", "Frames pro Sekunde.", "%fs", "0"));
    dataList.put(DataId.HEALLEVEL, new DataEntry("Gesundheit", "Dein Gesundheitslevel.", "%hl", "0"));
    dataList.put(DataId.HEALMAX, new DataEntry("Max. Gesundheit", "Dein maximales Gesundheitslevel.", "%hm", "0"));
    dataList.put(DataId.HEALBAR, new DataEntry("Gesundheits Herzen", "Zeigt dir deine Gesundheit als Herzen an.", "%hb", "0"));
    dataList.put(DataId.HUNGERLEVEL, new DataEntry("Hunger", "Zeigt dir deine Nahrungspunkte an.", "%fl", "0"));
    dataList.put(DataId.HUNGERMAX, new DataEntry("Max. Hunger", "Deine maximalen Nahrungspunkte.", "%fm", "0"));
    dataList.put(DataId.HUNGERBAR, new DataEntry("Hunger", "Zeigt dir deine Hunger als Fleischkeulen an.", "%fb", "0"));
    dataList.put(DataId.LOOKATBLOCK, new DataEntry("Block", "Zeigt dir den Typ des Blockes an, auf den du schaust.", "%lb", "0"));
    dataList.put(DataId.LOOKATLIGHT, new DataEntry("Light", "Zeigt dir den Helligkeitswert an, von dem Block auf den du schaust.", "%ll", "0"));

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
    String currentText = "";
    int index = 0;
    while (index < inputText.length())
    {
      char currentChar = inputText.charAt(index);
      if (currentChar == '%' && index + 1 < inputText.length() && inputText.charAt(index + 1) == '%')
      {
        currentText += "%";
        index += 2;
      }
      else if (currentChar == '%' && index + 2 < inputText.length() &&
          Character.isLetter(inputText.charAt(index + 1)) &&
          Character.isLetter(inputText.charAt(index + 2)))
      {
        currentText += resolvePattern(inputText.substring(index, index + 3));
        index += 3;
      }
      else
      {
        currentText += String.valueOf(currentChar);
        index++;
      }
    }
    result.add(Text.literal(currentText));
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

  // update only all values, that can change every frame, for better performance
  public void tick()
  {
    Entity entity = this.client.getCameraEntity();
    BlockPos blockPos = entity.getBlockPos();
    HitResult hitResult = entity.raycast(20.0, 0.0f, false);
    // fps formated to 3 char length, so text is not flickering around 99-100 fps
    dataList.put(DataId.FPS, dataList.get(DataId.FPS).setValue(String.format(Locale.ROOT, "%3d", client.getCurrentFps())));
    String compassText = "...N...ne...E...es...S...sw...W...wn";
    String compass;
    int compassIndex = (int) ((wrapDegrees(this.client.getCameraEntity().getYaw()) + 180.) / 360.0 * compassText.length()) % compassText.length();
    compass = (compassText.substring(compassIndex) + compassText.substring(0, compassIndex)).substring(0, 7);
    dataList.put(DataId.COMPASS, dataList.get(DataId.COMPASS).setValue(compass));
    if (hitResult.getType() == HitResult.Type.BLOCK)
    {
      if (!((WorldChunk)this.client.world.getChunk(((BlockHitResult)hitResult).getBlockPos())).isEmpty())
      {
        dataList.put(DataId.LOOKATBLOCK, dataList.get(DataId.LOOKATBLOCK).setValue(String.valueOf(Registries.BLOCK.getId(this.client.world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock()))));
        dataList.put(DataId.LOOKATLIGHT, dataList.get(DataId.LOOKATLIGHT).setValue(String.valueOf(this.client.world.getChunkManager().getLightingProvider().getLight(((BlockHitResult)hitResult).getBlockPos(), 0))));
      }
    }
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
    return result;
  }

  public enum DataId
  {
    COMPASS, EXP, FPS, HEALLEVEL, HEALMAX, HEALBAR, HEARTS, HUNGERLEVEL, HUNGERMAX, HUNGERBAR, LOOKATBLOCK, LOOKATLIGHT, MCTIME, MONEY, POSX, POSY, POSz, SERVER, TIME, TOOLDEMAGE, TOOLMAXDEMAGE, WORLD
  }

  public record DataEntry(String name, String description, String pattern, String value)
  {
    public DataEntry setValue(String newValue)
    {
      return new DataEntry(name, description, pattern, newValue);
    }
  }
}
