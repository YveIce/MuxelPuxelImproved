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

package click.isreal.mpi.domain;

import click.isreal.mpi.client.mpiClient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class UserData
{
  private final Map<Class<?>, Object> injections = new HashMap<>();
  MixelWorld mixelWorld;
  @Nullable String dimension;
  @Nullable String rank;
  @Nullable String money;
  @Nullable String jubiProgress;
  @Nullable String cbPlotName;
  @Nullable String cbPlotOwner;
  @Nullable String kffaKD;
  @Nullable String rankPoints;
  @Nullable String aufstiegPoints;
  @Nullable String kffaMap;
  @Nullable String kffaMapSwitch;

  public UserData(MixelWorld world)
  {
    this.mixelWorld = world;
  }

  public static UserData current()
  {
    return mpiClient.getInstance().getScoreboardData();
  }

  public MixelWorld mixelWorld()
  {
    return mixelWorld;
  }

  public UserData setMixelWorld(MixelWorld mixelWorld)
  {
    this.mixelWorld = mixelWorld;
    return this;
  }

  public String dimension()
  {
    return dimension;
  }

  public UserData setDimension(String dimension)
  {
    this.dimension = dimension;
    return this;
  }

  public String rank()
  {
    return rank;
  }

  public UserData setRank(String rank)
  {
    this.rank = rank;
    return this;
  }

  public String money()
  {
    return money;
  }

  public UserData setMoney(String money)
  {
    this.money = money;
    return this;
  }

  public String cbPlotName()
  {
    return cbPlotName;
  }

  public UserData setCbPlotName(String cbPlotName)
  {
    this.cbPlotName = cbPlotName;
    return this;
  }

  public String cbPlotOwner()
  {
    return cbPlotOwner;
  }

  public UserData setCbPlotOwner(String cbPlotOwner)
  {
    this.cbPlotOwner = cbPlotOwner;
    return this;
  }

  public String kffaKD()
  {
    return kffaKD;
  }

  public UserData setKffaKD(String kffaKD)
  {
    this.kffaKD = kffaKD;
    return this;
  }

  public String rankPoints()
  {
    return rankPoints;
  }

  public UserData setRankPoints(String rankPoints)
  {
    this.rankPoints = rankPoints;
    return this;
  }

  public String aufstiegPoints()
  {
    return aufstiegPoints;
  }

  public UserData setAufstiegPoints(String aufstiegPoints)
  {
    this.aufstiegPoints = aufstiegPoints;
    return this;
  }

  public String kffaMap()
  {
    return kffaMap;
  }

  public UserData setKffaMap(String kffaMap)
  {
    this.kffaMap = kffaMap;
    return this;
  }

  public String kffaMapSwitch()
  {
    return kffaMapSwitch;
  }

  public UserData setKffaMapSwitch(String kffaMapSwitch)
  {
    this.kffaMapSwitch = kffaMapSwitch;
    return this;
  }

  public @Nullable String getJubiProgress()
  {
    return jubiProgress;
  }

  public void setJubiProgress(String jubiProgress)
  {
    this.jubiProgress = jubiProgress;
  }

  public <T> T getInjection(Class<T> classType)
  {
    return (T) this.injections.get(classType);
  }

  public <T> String readSafeString(Class<T> classType, Function<T, String> f)
  {
    T object = getInjection(classType);
    if (object != null) return f.apply(object);
    return "";
  }

  public <T> void updateIfExists(Class<T> classType, Consumer<T> update)
  {
    T object = getInjection(classType);
    if (object != null) update.accept(object);
  }

  public <T> T createInjection(Class<T> classType) throws Exception
  {
    if (this.injections.containsKey(classType))
    {return (T) this.injections.get(classType);}
    return (T) this.injections.put(classType, classType.getDeclaredConstructor().newInstance());
  }
}
