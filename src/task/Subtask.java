package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String subtaskStructure) {
        super(subtaskStructure);
        String[] mTask = subtaskStructure.split(",");
        epicId = Integer.parseInt(mTask[5].trim());
    }

    public void setIdEpic(int idEpic) {
        this.epicId = idEpic;
    }

    public int getIdEpic() {
        return epicId;
    }

    public Subtask clone() {
        Subtask subtaskClone = new Subtask(getName(), getDescription());
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
