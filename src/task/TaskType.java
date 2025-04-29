package task;

public enum TaskType {
    TASK,
    EPIC,
    SUBTASK;

    public static TaskType getTypeFromString(String typeString) {
        if (typeString.equals("TASK")) {
            return TaskType.TASK;
        } else if (typeString.equals("EPIC")) {
            return TaskType.EPIC;
        } else {
            return TaskType.SUBTASK;
        }
    }

}

