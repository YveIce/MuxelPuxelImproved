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
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.minecraft.text.StringVisitable.concat;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class DataManager
{
  private static DataManager instance;
  static public DataEntry COMPASS =
      new DataEntry("Kompass", "Zeigt dir die Himmelsrichtung in die du blickst.", "%cp", "?");
  static public DataEntry EXP =
      new DataEntry("Exp", "Deine Erfahrungspunkte.", "%xp", "0");
  //private static final Map<EntryId, Entry> dataMap = new HashMap<>();
  static public DataEntry FPS =
      new DataEntry("FPS", "Frames pro Sekunde.", "%fs", "0");
  static public DataEntry HEALLEVEL =
      new DataEntry("Gesundheit", "Dein Gesundheitslevel.", "%hl", "0");
  static public DataEntry HEALMAX =
      new DataEntry("Max. Gesundheit", "Dein maximales Gesundheitslevel.", "%hm", "0");
  static public DataEntry HEALBAR =
      new DataEntry("Gesundheits Herzen", "Zeigt dir deine Gesundheit als Herzen an.", "%hb", "0");
  static public DataEntry HUNGERLEVEL =
      new DataEntry("Hunger", "Zeigt dir deine Nahrungspunkte an.", "%fl", "0");
  static public DataEntry HUNGERMAX =
      new DataEntry("Max. Hunger", "Deine maximalen Nahrungspunkte.", "%fm", "0");
  static public DataEntry HUNGERBAR =
      new DataEntry("Hunger", "Zeigt dir deine Hunger als Fleischkeulen an.", "%fb", "0");
  static public DataEntry LOOKATBLOCK =
      new DataEntry("Block", "Zeigt dir den Typ des Blockes an, auf den du schaust.", "%lb", "0");
  static public DataEntry LOOKATLIGHT =
      new DataEntry("Light", "Zeigt dir den Helligkeitswert an, von dem Block auf den du schaust.", "%ll", "0");
  // YVE: COMPAS, EXP, FPS, HEAL, HEALMAX, HEARTS, HUNGER, LOOKATBLOCK, LOOKATLIGHT, MCTIME, MONEY, POSX, POSY, POSz, SERVER, TIME, TOOLDEMAGE, TOOLMAXDEMAGE, WORLD
  private final MinecraftClient client = MinecraftClient.getInstance();
  private final List<DataEntry> DataList = List.of(COMPASS, EXP, FPS, HEALBAR, HEALLEVEL, HEALMAX, HUNGERBAR, HUNGERLEVEL, HUNGERMAX, LOOKATBLOCK, LOOKATLIGHT);

  public static DataManager getInstance()
  {
    if (null == instance)
      instance = new DataManager();
    return instance;
  }
public void DataManager() {
    instance = this;
}

  /**
   * process a String with placeholders and returns a List of Styles Texts
   *
   * @param inputText String containing magic patterns to be replaced with the current values
   * @return List<Text>
   */
  public List<Text> processPatterns(String inputText)
  {
    List<Text> result = new ArrayList<>();
    Text currentText = Text.empty();
    Style style = Style.EMPTY;

    int index = 0;
    while (index < inputText.length())
    {
      char currentChar = inputText.charAt(index);

      if (currentChar == '&' && index + 1 < inputText.length() && inputText.charAt(index + 1) == '&')
      {
        currentText = (Text) concat(currentText, Text.literal("&").setStyle(style));
        index += 2;
      }
      else if (currentChar == '%' && index + 1 < inputText.length() && inputText.charAt(index + 1) == '%')
      {
        currentText = (Text) concat(currentText, Text.literal("%").setStyle(style));
        index += 2;
      }
      else if (currentChar == '&' && index + 1 < inputText.length())
      {
        char nextChar = inputText.charAt(index + 1);
        if ("0123456789abcdef".indexOf(nextChar) != -1)
        {
          style = style.withColor(Formatting.byCode(nextChar));
          index += 2;
        }
        else if ("lmnor".indexOf(nextChar) != -1)
        {
          style = switch (nextChar)
              {
                case 'r' -> Style.EMPTY;
                case 'l' -> style.withBold(true);
                case 'm' -> style.withStrikethrough(true);
                case 'n' -> style.withUnderline(true);
                case 'o' -> style.withItalic(true);
                default -> style;
              };
        }
        else
        {
          currentText = (Text) concat(currentText, Text.literal("&").setStyle(style));
          index++;
        }
      }
      else if (currentChar == '%' && index + 2 < inputText.length() &&
          Character.isLetter(inputText.charAt(index + 1)) &&
          Character.isLetter(inputText.charAt(index + 2)))
      {
        result.add(currentText);
        result.add(resolvePattern(inputText.substring(index, index + 3), style));
        currentText = Text.empty();
        index += 3;
      }
      else
      {
        currentText = (Text) concat(currentText, Text.literal(String.valueOf(currentChar)).setStyle(style));
        index++;
      }
    }
    // add the remaining Text to the List
    result.add(currentText);
    return result;
  }

  private Text resolvePattern(String pattern, Style style)
  {
    for (DataEntry entry : this.DataList)
    {
      if (entry.pattern().equals(pattern))
      {
        return Text.literal(entry.value()).setStyle(style);
      }
    }
    return Text.literal(pattern).setStyle(style);
  }

  // update only all values, that can change every frame, for better performance
  public void tick()
  {
    // fps formated to 3 char length, so text is not flickering around 99-100 fps
    FPS = FPS.setValue(String.format(Locale.ROOT, "%3d", client.getCurrentFps()));
    String compassText = "...N...ne...E...es...S...sw...W...wn";
    String compass;
    int compassIndex = (int) ((wrapDegrees(this.client.getCameraEntity().getYaw()) + 180.) / 360.0 * compassText.length()) % compassText.length();
    compass = (compassText.substring(compassIndex) + compassText.substring(0, compassIndex)).substring(0, 7);
    COMPASS = COMPASS.setValue(compass);


  }

  public List<Text> getDescriptionList()
  {
    List<Text> result = new ArrayList<>();
    result.add(Text.literal("§6§lPattern-List:"));
    result.add(Text.literal("§7§oThis is a list of all available patterns/placeholders you can use to customize the top and bottom bars display according to your needs. These will be automatically replaced by the corresponding values in the game."));
    String colorPatterns = "";
    for ( String colorname : Formatting.getNames(true,false))
    {
      if('r' != Formatting.byName(colorname).getCode())
        colorPatterns += "§e§l&" + Formatting.byName(colorname).getCode() + "§f " + Formatting.byName(colorname) +colorname+ Formatting.RESET + "" + Formatting.WHITE + ", ";
    }
    colorPatterns = colorPatterns.substring(0, colorPatterns.length() -2);
    result.add(Text.literal("§f§lFormatting patterns: §r"
        +"§e§l&r §fRESET (resets all previous formatting), "
        +"§e§l&l §f§lBOLD§r§f, "
        +"§e§l&o §f§oITALIC§r§f, "
        +"§e§l&m §f§mSTRIKETHROUGH§r§f, "
        +"§e§l&n §f§nUNDERLINE§r§f, "
    ));
    result.add(Text.literal("§f§lColor patterns: §r" + colorPatterns));
    result.add(Text.literal("§f§lData patterns: §r"));
    for (DataEntry entry : DataList)
    {
      result.add(Text.literal("§e§l"+entry.pattern()+"§r §f"+entry.name+" §7§o("+entry.description()+")"));
    }
    return result;
  }
  public record DataEntry(String name, String description, String pattern, String value)
  {
    public DataEntry setValue(String newValue)
    {
      return new DataEntry(name, description, pattern, newValue);
    }
  }
}
