package task;

public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;

    public static Status getStatusFromString(String statusString) {
        if (statusString.equals("NEW")) {
            return Status.NEW;
        } else if (statusString.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        } else {
            return Status.DONE;
        }
    }
}
