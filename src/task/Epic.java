package task;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subTasks = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String epicStructure) {
        super(epicStructure);
    }

    public void setSubtask(Subtask subTasks, int idSubTasks) {
        this.subTasks.put(idSubTasks, subTasks);
    }

    public void deleteSubtask(int idSubTasks) {
        this.subTasks.remove(idSubTasks);
    }

    public void clearSubtask() {
        subTasks.clear();
    }

    public HashMap<Integer, Subtask> getSubTasks() {
        return subTasks;
    }

    public Subtask clone(Subtask task) {
        return null;
    }

    public Epic clone() {
        Epic epicClone = new Epic(getName(), getDescription());
        epicClone.setId(getId());
        epicClone.setStatus(getStatus());
        return epicClone;
    }

}
