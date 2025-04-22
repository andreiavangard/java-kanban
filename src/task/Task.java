package task;

import task.Status;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private TaskType type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String taskStructure) {
        String[] mTask = taskStructure.split(",");
        this.name = mTask[2].trim();
        this.description = mTask[4].trim();
        this.type = TaskType.getTypeFromString(mTask[1].trim());
        this.id = Integer.parseInt(mTask[0].trim());
        this.status = Status.getStatusFromString(mTask[3].trim());
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

    public Task clone() {
        Task taskClone = new Task(getName(), getDescription());
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
        return String.format("%s, %s, %s, %s, %s,", id, type, name, status, description);
    }


}
