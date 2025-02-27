package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description){
        super(name, description);
    }

    public void setIdEpic(int idEpic) {
        this.epicId = idEpic;
    }

    public int getIdEpic() {
        return epicId;
    }

    public Subtask clone(){
        Subtask subtaskClone = new Subtask(getName(), getDescription());
        subtaskClone.setId(getId());
        subtaskClone.setStatus(getStatus());
        return subtaskClone;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + super.getId() + '\'' +
                ", idEpic=" + epicId + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                '}';
    }

}
