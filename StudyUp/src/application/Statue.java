package application;
public enum Statue {
    TO_DO("to_do"),
    IN_PROGRESS("in_progress"),
    NEEDS_REVIEW("needs_review"),
    COMPLETE("complete");

    private final String value;

    private Statue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
