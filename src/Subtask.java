public class Subtask extends Task {
    private int idEpic;

    public Subtask(String name, String description){
        super(name, description);
    }

    public void setIdEpic(int idEpic) {
        if(this.idEpic==0) {
            this.idEpic = idEpic;
        }
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + super.getId() + '\'' +
                ", idEpic=" + idEpic + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                '}';
    }

}
