public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;
    @Override
    public String toString() {
        return name().charAt(0) + name().replace("_"," ").substring(1).toLowerCase();
    }
}
