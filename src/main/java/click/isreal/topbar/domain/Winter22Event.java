package click.isreal.topbar.domain;

public class Winter22Event {
    private String tuer;
    private String modus;
    private String checkpoints;

    public String tuer() {
        return tuer;
    }

    public Winter22Event setTuer(String tuer) {
        this.tuer = tuer;
        return this;
    }

    public String modus() {
        return modus;
    }

    public Winter22Event setModus(String modus) {
        this.modus = modus;
        return this;
    }

    public String checkpoints() {
        return checkpoints;
    }

    public Winter22Event setCheckpoints(String checkpoints) {
        this.checkpoints = checkpoints;
        return this;
    }
}
