package task;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, LocalDateTime startTime, int durationInMinutes) {
        super(name, description, startTime, durationInMinutes);
    }

    public Subtask(String subtaskStructure) {
        super(subtaskStructure);
        String[] mTask = subtaskStructure.split(",");
        epicId = Integer.parseInt(mTask[7].trim());
    }

    public void setIdEpic(int idEpic) {
        this.epicId = idEpic;
    }

    public int getIdEpic() {
        return epicId;
    }

    public Subtask clone() {
        Subtask subtaskClone = new Subtask(getName(), getDescription(), getStartTime(), getDurationInMinutes());
        subtaskClone.setId(getId());
        subtaskClone.setStatus(getStatus());
        return subtaskClone;
    }

    @Override
    public String toString() {
        //к toString super добавить epicId
        String toStringSuper = super.toString();
        return String.format("%s %s", toStringSuper, epicId);
    }

}
