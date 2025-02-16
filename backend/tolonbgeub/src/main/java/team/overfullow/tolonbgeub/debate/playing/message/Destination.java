package team.overfullow.tolonbgeub.debate.playing.message;

public enum Destination {
    DEBATE("/sub/debate/%s");

    private String format;

    public String getFormat() {
        return format;
    }

    Destination(String format) {
        this.format = format;
    }
}
