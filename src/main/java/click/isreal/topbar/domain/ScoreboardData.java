package click.isreal.topbar.domain;

import click.isreal.topbar.client.TopbarClient;
import org.jetbrains.annotations.Nullable;

public class ScoreboardData{
    public static ScoreboardData current(){
        return TopbarClient.getInstance().getScoreboardData();
    }
    MixelWorld mixelWorld;
    @Nullable String dimension;
    @Nullable String rank;
    @Nullable String money;
    @Nullable String cbPlotName;
    @Nullable String cbPlotOwner;
    @Nullable String kffaKD;
    @Nullable String rankPoints;
    @Nullable String aufstiegPoints;
    @Nullable String kffaMap;
    @Nullable String kffaMapSwitch;
    public ScoreboardData(MixelWorld world) {
        this.mixelWorld = world;
    }

    public MixelWorld mixelWorld() {
        return mixelWorld;
    }

    public ScoreboardData setMixelWorld(MixelWorld mixelWorld) {
        this.mixelWorld = mixelWorld;
        return this;
    }

    public String dimension() {
        return dimension;
    }

    public ScoreboardData setDimension(String dimension) {
        this.dimension = dimension;
        return this;
    }

    public String rank() {
        return rank;
    }

    public ScoreboardData setRank(String rank) {
        this.rank = rank;
        return this;
    }

    public String money() {
        return money;
    }

    public ScoreboardData setMoney(String money) {
        this.money = money;
        return this;
    }

    public String cbPlotName() {
        return cbPlotName;
    }

    public ScoreboardData setCbPlotName(String cbPlotName) {
        this.cbPlotName = cbPlotName;
        return this;
    }

    public String cbPlotOwner() {
        return cbPlotOwner;
    }

    public ScoreboardData setCbPlotOwner(String cbPlotOwner) {
        this.cbPlotOwner = cbPlotOwner;
        return this;
    }

    public String kffaKD() {
        return kffaKD;
    }

    public ScoreboardData setKffaKD(String kffaKD) {
        this.kffaKD = kffaKD;
        return this;
    }

    public String rankPoints() {
        return rankPoints;
    }

    public ScoreboardData setRankPoints(String rankPoints) {
        this.rankPoints = rankPoints;
        return this;
    }

    public String aufstiegPoints() {
        return aufstiegPoints;
    }

    public ScoreboardData setAufstiegPoints(String aufstiegPoints) {
        this.aufstiegPoints = aufstiegPoints;
        return this;
    }

    public String kffaMap() {
        return kffaMap;
    }

    public ScoreboardData setKffaMap(String kffaMap) {
        this.kffaMap = kffaMap;
        return this;
    }

    public String kffaMapSwitch() {
        return kffaMapSwitch;
    }

    public ScoreboardData setKffaMapSwitch(String kffaMapSwitch) {
        this.kffaMapSwitch = kffaMapSwitch;
        return this;
    }
}
