package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public class Epic extends Task {
    private LocalDateTime endTime;
    private HashMap<Integer, Subtask> subTasks = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description, null, 0);
    }

    public Epic(String epicStructure) {
        super(epicStructure);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setSubtask(Subtask subTasks, int idSubTasks) {
        this.subTasks.put(idSubTasks, subTasks);
    }

    public void deleteSubtask(int idSubTasks) {
        this.subTasks.remove(idSubTasks);
    }

    public void clearSubtask() {
        subTasks.clear();
        resetDateTimeDuration();
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

    public void updateDateTimeDuration() {
        if (!subTasks.isEmpty()) {
            //min
            Optional<Subtask> minTimeSubtask = subTasks.values().stream()
                    .filter(task -> task.getStartTime() != null)
                    .min(Comparator.comparing(Task::getStartTime));
            if (minTimeSubtask.isPresent()) {
                //max
                Optional<Subtask> maxTimeSubtask = subTasks.values().stream()
                        .filter(task -> task.getStartTime() != null)
                        .max(Comparator.comparing(Task::getEndTime));
                LocalDateTime startTime = minTimeSubtask.get().getStartTime();
                setStartTime(startTime);
                endTime = maxTimeSubtask.get().getEndTime();
                setDurationInMinutes((int) Duration.between(startTime, endTime).toMinutes());
            } else {
                resetDateTimeDuration();
            }
        } else {
            resetDateTimeDuration();
        }
    }

    private void resetDateTimeDuration() {
        setDurationInMinutes(0);
        setStartTime(null);
        endTime = null;
    }

}
