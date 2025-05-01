package manager;

import task.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int globalTaskId = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Subtask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> tasksOfPriority = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task getTaskInHistoryById(int id) {
        return historyManager.getTaskInHistoryById(id);
    }

    @Override
    public Task[] getPrioritizedTasks() {
        return tasksOfPriority.stream().toArray(Task[]::new);
    }

    @Override
    public int addTask(Task taskAdd) {
        int id = globalTaskId++;
        taskAdd.setId(id);
        taskAdd.setType(TaskType.TASK);
        taskAdd.setStatus(Status.NEW);
        tasks.put(id, taskAdd);
        if (!isIntersectionsTaskInTasksOfPriority(taskAdd)) {
            addTaskIntasksOfPriority(taskAdd);
        } else {
            throw new IllegalArgumentException(String.format("Задача %s пересекается с одной из существующих задач", taskAdd.toString()));
        }
        return id;
    }

    @Override
    public void updateTask(Task taskUpdate) {
        if (tasks.get(taskUpdate.getId()) != null) {
            tasks.put(taskUpdate.getId(), taskUpdate);
        }
        deleteTaskInTasksOfPriority(taskUpdate);
        if (!isIntersectionsTaskInTasksOfPriority(taskUpdate)) {
            addTaskIntasksOfPriority(taskUpdate);
        } else {
            throw new IllegalArgumentException(String.format("Задача %s пересекается с одной из существующих задач", taskUpdate.toString()));
        }

    }

    @Override
    public void deleteTask(Task taskDelete) {
        tasks.remove(taskDelete.getId());
        historyManager.remove(taskDelete.getId());
        deleteTaskInTasksOfPriority(taskDelete);
    }

    @Override
    public void deleteTaskById(int id) {
        deleteTask(tasks.get(id));
    }

    @Override
    public void clearTask() {
        tasks.entrySet().stream()
                .forEach(
                        entry -> deleteTaskInTasksOfPriority(entry.getValue())
                );

        tasks.clear();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task.clone());
        return task;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = globalTaskId++;
        epic.setId(id);
        epic.setType(TaskType.EPIC);
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.get(epic.getId()) != null) {
            epics.put(epic.getId(), epic);
        }
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
        epic.UpdateDateTimeDuration();
    }

    @Override
    public void deleteEpic(Epic epic) {
        HashMap<Integer, Subtask> subTasksEpic = epic.getSubTasks();
        subTasksEpic.entrySet().stream()
                .forEach(
                        entry -> {
                            subTasks.remove(entry.getKey());
                            historyManager.remove(entry.getKey());
                            deleteTaskInTasksOfPriority(entry.getValue());
                        }
                );
        epics.remove(epic.getId());
        historyManager.remove(epic.getId());
    }

    @Override
    public void deleteEpicById(int id) {
        deleteEpic(epics.get(id));
    }

    @Override
    public void clearEpics() {
        //очистить историю
        for (int key : epics.keySet()) {
            historyManager.remove(key);
        }
        subTasks.entrySet().stream()
                .forEach(
                        entry -> {
                            historyManager.remove(entry.getKey());
                            deleteTaskInTasksOfPriority(entry.getValue());
                        }
                );
        //очистить таски
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic.clone());
        return epic;
    }

    @Override
    public int addSubtask(Subtask subtask, Epic epic) {
        int id = globalTaskId++;
        subtask.setId(id);
        subtask.setType(TaskType.SUBTASK);
        subtask.setStatus(Status.NEW);
        int idEpic = epic.getId();
        subtask.setIdEpic(idEpic);
        epic.setSubtask(subtask, id);
        subTasks.put(subtask.getId(), subtask);
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
        epic.UpdateDateTimeDuration();
        if (!isIntersectionsTaskInTasksOfPriority(subtask)) {
            addTaskIntasksOfPriority(subtask);
        } else {
            throw new IllegalArgumentException(String.format("Задача %s пересекается с одной из существующих задач", subtask.toString()));
        }
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask, Epic epic) {
        deleteTaskInTasksOfPriority(subtask);
        epic.setSubtask(subtask, subtask.getId());
        subTasks.put(subtask.getId(), subtask);
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
        epic.UpdateDateTimeDuration();
        if (!isIntersectionsTaskInTasksOfPriority(subtask)) {
            addTaskIntasksOfPriority(subtask);
        } else {
            throw new IllegalArgumentException(String.format("Задача %s пересекается с одной из существующих задач", subtask.toString()));
        }
    }

    @Override
    public void deleteSubtask(Subtask subtask, Epic epic) {
        epic.deleteSubtask(subtask.getId());
        subTasks.remove(subtask.getId());
        historyManager.remove(subtask.getId());
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
        epic.UpdateDateTimeDuration();
        deleteTaskInTasksOfPriority(subtask);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subTasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getIdEpic());
            deleteSubtask(subtask, epic);
        }
    }

    @Override
    public void clearSubtask() {
        subTasks.entrySet().stream()
                .forEach(
                        entry -> {
                            historyManager.remove(entry.getKey());
                            deleteTaskInTasksOfPriority(entry.getValue());
                        }
                );

        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void clearSubtask(Epic epic) {
        epic.clearSubtask();
        HashMap<Integer, Subtask> subTasksEpic = epic.getSubTasks();
        subTasksEpic.entrySet().stream()
                .forEach(
                        entry -> {
                            historyManager.remove(entry.getKey());
                            subTasks.remove(entry.getKey());
                            deleteTaskInTasksOfPriority(entry.getValue());
                        }
                );
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
        epic.UpdateDateTimeDuration();
    }

    @Override
    public ArrayList<Subtask> getSubtasks(Epic epic) {
        ArrayList<Subtask> outSubtasks = new ArrayList<>();
        for (Subtask subtask : subTasks.values()) {
            if (subtask.getIdEpic() == epic.getId()) {
                outSubtasks.add(subtask);
                historyManager.add(subtask.clone());
            }
        }
        return outSubtasks;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subTask = subTasks.get(subtaskId);
        historyManager.add(subTask.clone());
        return subTask;
    }

    @Override
    public ArrayList<Task> getTasks() {
        for (Task taskForHistory : tasks.values()) {
            historyManager.add(taskForHistory.clone());
        }
        return new ArrayList<Task>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        for (Epic epicForHistory : epics.values()) {
            historyManager.add(epicForHistory.clone());
        }
        return new ArrayList<Epic>(epics.values());
    }

    protected Map<Integer, Task> getMapTasks() {
        return tasks;
    }

    protected Map<Integer, Subtask> getMapMapSubtasks() {
        return subTasks;
    }

    protected Map<Integer, Epic> getMapEpics() {
        return epics;
    }

    protected void setTaskInTasks(int id, Task task) {
        tasks.put(id, task);
    }

    protected void setSubtaskInSubtasks(int id, Subtask subtask) {
        subTasks.put(id, subtask);
    }

    protected void setEpicInEpics(int id, Epic epic) {
        epics.put(id, epic);
    }

    private Status updateEpicStatus(Epic epic) {
        HashMap<Status, Integer> mapStatus = new HashMap<>();
        mapStatus.put(Status.NEW, 0);
        mapStatus.put(Status.IN_PROGRESS, 0);
        mapStatus.put(Status.DONE, 0);

        for (Subtask subtask : epic.getSubTasks().values()) {
            int value = 0;
            switch (subtask.getStatus()) {
                case NEW:
                    value = mapStatus.get(Status.NEW);
                    value++;
                    mapStatus.put(Status.NEW, value);
                    break;
                case IN_PROGRESS:
                    value = mapStatus.get(Status.IN_PROGRESS);
                    value++;
                    mapStatus.put(Status.IN_PROGRESS, value);
                    break;
                case DONE:
                    value = mapStatus.get(Status.DONE);
                    value++;
                    mapStatus.put(Status.DONE, value);
                    break;
            }
        }

        if (mapStatus.get(Status.IN_PROGRESS) >= 1) {
            return Status.IN_PROGRESS;
        } else if (mapStatus.get(Status.DONE) >= 1 && mapStatus.get(Status.NEW) >= 1) {
            return Status.IN_PROGRESS;
        } else if (mapStatus.get(Status.DONE) >= 1 && mapStatus.get(Status.NEW) == 0 && mapStatus.get(Status.IN_PROGRESS) == 0) {
            return Status.DONE;
        }

        return Status.NEW;

    }

    private void addTaskIntasksOfPriority(Task task) {
        //добавляем только с непустой датой старта
        if (task.getStartTime() != null) {
            tasksOfPriority.add(task);
        }
    }

    private boolean isIntersectionsTaskInTasksOfPriority(Task task) {
        return tasksOfPriority.stream()
                .anyMatch(existingTask -> isIntersectionsTwoTask(existingTask, task));
    }

    private boolean isIntersectionsTwoTask(Task task1, Task task2) {
        return task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getStartTime());
    }

    private void deleteTaskInTasksOfPriority(Task task) {
        //TreeSet.contains() может работать неккоректно, сравнение идет по compareTo()
        if (tasksOfPriority.stream().toList().contains(task)) {
            tasksOfPriority.remove(task);
        }
    }

}
