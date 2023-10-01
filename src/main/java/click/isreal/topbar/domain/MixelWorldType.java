package click.isreal.topbar.domain;

public enum MixelWorldType {
    LOBBY("Lobby"),
    SPAWN("Spawn"),
    KFFA("KFFA"),
    FARMWORLD("Farmwelt"),
    SMALL_CB("CB-Klein"),
    BIG_CB("CB-Gross"),
    EVENT("Event"),
    OTHER("Unkown");

    private final String name;

    MixelWorldType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
