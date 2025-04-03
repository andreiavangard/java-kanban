package task;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description){
        this.name = name;
        this.description = description;
    }

    public  void setId(int id){
        if(this.id==0) {
            //id можно устанавливать только для новых task, для существующих менять id запрещено
            this.id = id;
        }
    }

    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task clone(){
        Task taskClone = new Task(getName(), getDescription());
        taskClone.setId(getId());
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
        return "task.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
