package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private TaskType type;
    private Duration duration;
    private LocalDateTime startTime;
    private static final DateTimeFormatter formatterDataTimeTask = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String name, String description, LocalDateTime startTime, int durationInMinutes) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationInMinutes);
    }

    public Task(String taskStructure) {
        String[] mTask = taskStructure.split(",");
        this.name = mTask[2].trim();
        this.description = mTask[4].trim();
        this.type = TaskType.getTypeFromString(mTask[1].trim());
        this.id = Integer.parseInt(mTask[0].trim());
        this.status = Status.getStatusFromString(mTask[3].trim());
        if (!mTask[5].trim().equals("null")) {
            this.startTime = LocalDateTime.parse(mTask[5].trim(), formatterDataTimeTask);
        }
        int durationInMinutes = (int) Integer.parseInt(mTask[6].trim());
        this.duration = Duration.ofMinutes(durationInMinutes);
    }

    public void setId(int id) {
        if (this.id == 0) {
            //id можно устанавливать только для новых task, для существующих менять id запрещено
            this.id = id;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public void setDurationInMinutes(int durationMinutes) {
        this.duration = Duration.ofMinutes(durationMinutes);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDurationInMinutes() {
        return (int) duration.toMinutes();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public Task clone() {
        Task taskClone = new Task(getName(), getDescription(), getStartTime(), getDurationInMinutes());
        taskClone.setId(getId());
        taskClone.setType(getType());
        taskClone.setStatus(getStatus());
        return taskClone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        long durationTask = 0;
        if (duration != null) {
            durationTask = duration.toMinutes();
        }
        String startTimeString = "null";
        if (startTime != null) {
            startTimeString = startTime.format(formatterDataTimeTask);
        }
        return String.format("%s, %s, %s, %s, %s, %s, %s,", id, type, name, status, description, startTimeString, durationTask);
    }

    public static String getStringDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatterDataTimeTask);
    }

    public static LocalDateTime getDateTimeOfString(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatterDataTimeTask);
    }


}
