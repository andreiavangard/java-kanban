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
