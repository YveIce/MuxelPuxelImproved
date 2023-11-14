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

public class ConfigData
{
  // we are using int instead of boolean, where 0 means FALSE, anything else TRUE
  // because of GSON can't handle booleans on it's on correctly
  public int streamerMode = 0;
  public int effectIconSize = 10;
  public int effectColorPositive = 0xff00ff00;
  public int effectColorNegative = 0xffff0000;
  public int fpsShow = 1;
  public int fpsColor = 0xff808080;
  public int timeShow = 1;
  public int timeColor = 0xffff007D;
  public int preventFalseCommands = 0;
  public int loadscreenColor = 0xffff007D;
  public int solidBackgroundEnabled = 1;
  public int discordEnabled = 1;
  public int breakwarnEnabled = 1;
  public int unsecureServerWarning = 0;
  public int hornAudio = 0;
  public int renderScoreboard = 0;
  public int barScale = 100;

  public int barTop1enabled = 1;
  public int barTop2enabled = 0;
  public int barBottom1enabled = 1;
  public int barBottom2enabled = 0;
  public int barTop1Color = 0xBD000000;
  public int barTop2Color = 0x7F000000;
  public int barBottom1Color = 0xBD000000;
  public int barBottom2Color = 0x7F000000;

  public String barTop1LeftPattern = "TL1";
  public String barTop1CenterPattern = "%cp";
  public String barTop1RightPattern = "";
  public String barTop2LeftPattern = "";
  public String barTop2CenterPattern = "";
  public String barTop2RightPattern = "";
  public String barBottom1LeftPattern = "";
  public String barBottom1CenterPattern = "";
  public String barBottom1RightPattern = "";
  public String barBottom2LeftPattern = "";
  public String barBottom2CenterPattern = "";
  public String barBottom2RightPattern = "";
  public String titleScreenTheme = "default";
}
