package team.overfullow.tolonbgeub.debate;

public enum Destination {
    DEBATE("/sub/debate/%s"),
    MATCHING("/sub/matching/%s"),
    MATCHING_USER("/user/matching/%s");

    private String format;

    public String getFormat() {
        return format;
    }

    Destination(String format) {
        this.format = format;
    }
}
