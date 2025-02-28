package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager  {
    private int globalTaskId;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Subtask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    @Override
    public int addTask(Task taskAdd){
        int id = globalTaskId++;
        taskAdd.setId(id);
        taskAdd.setStatus(Status.NEW);
        tasks.put(id, taskAdd);
        return id;
    }

    @Override
    public void updateTask(Task taskUpdate){
        if(tasks.get(taskUpdate.getId())!=null) {
            tasks.put(taskUpdate.getId(), taskUpdate);
        }
    }

    @Override
    public void deleteTask(Task taskDelete){
        tasks.remove(taskDelete.getId());
    }

    @Override
    public void deleteTaskById(int id){
        deleteTask(tasks.get(id));
    }

    @Override
    public void clearTask(){
        tasks.clear();
    }

    @Override
    public Task getTask(int taskId){
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public int addEpic(Epic epic){
        int id = globalTaskId++;
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
        return id;
    }

    @Override
    public void updateEpic(Epic epic){
        if(epics.get(epic.getId())!=null) {
            epics.put(epic.getId(), epic);
        }
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    @Override
    public void deleteEpic(Epic epic){
        HashMap<Integer, Subtask> subTasksEpic = epic.getSubTasks();
        for(int key : subTasksEpic.keySet()){
            subTasks.remove(key);
        }
        epics.remove(epic.getId());
    }

    @Override
    public void deleteEpicById(int id){
         deleteEpic(epics.get(id));
    }

    @Override
    public void clearEpics(){
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpic(int epicId){
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int addSubtask(Subtask subtask, Epic epic){
        int id = globalTaskId++;
        subtask.setId(id);
        subtask.setStatus(Status.NEW);
        int idEpic = epic.getId();
        subtask.setIdEpic(idEpic);
        epic.setSubtask(subtask, id);
        subTasks.put(subtask.getId(), subtask);
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask, Epic epic){
        epic.setSubtask(subtask, subtask.getId());
        subTasks.put(subtask.getId(), subtask);
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    @Override
    public void deleteSubtask(Subtask subtask, Epic epic){
        epic.deleteSubtask(subtask.getId());
        subTasks.remove(subtask.getId());
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    @Override
    public void deleteSubtaskById(int id){
        Subtask subtask = subTasks.get(id);
        if(subtask != null) {
            Epic epic = epics.get(subtask.getIdEpic());
            deleteSubtask(subtask, epic);
        }
    }

    @Override
    public void clearSubtask(){
        subTasks.clear();
        for(Epic epic : epics.values()){
            epic.clearSubtask();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void clearSubtask(Epic epic){
        epic.clearSubtask();
        HashMap<Integer, Subtask> subTasksEpic = epic.getSubTasks();
        for(int key : subTasksEpic.keySet()){
            subTasks.remove(key);
        }
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    @Override
    public ArrayList<Subtask> getSubtasks(Epic epic){
        ArrayList<Subtask> outSubtasks = new ArrayList<>();
        for(Subtask subtask: subTasks.values()){
            if(subtask.getIdEpic()==epic.getId()){
                outSubtasks.add(subtask);
                historyManager.add(subtask);
            }
        }
        return outSubtasks;
    }

    @Override
    public Subtask getSubtask(int subtaskId){
        Subtask subTask = subTasks.get(subtaskId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public ArrayList<Task> getTasks(){
        for(Task taskForHystory : tasks.values()){
            historyManager.add(taskForHystory);
        }
       return new ArrayList<Task>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics(){
        for(Epic epicForHystory : epics.values()){
            historyManager.add(epicForHystory);
        }
        return new ArrayList<Epic>(epics.values());
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
        } else if(mapStatus.get(Status.DONE) >= 1 && mapStatus.get(Status.NEW) >= 1){
            return Status.IN_PROGRESS;
        } else if (mapStatus.get(Status.DONE) >= 1 && mapStatus.get(Status.NEW) == 0 && mapStatus.get(Status.IN_PROGRESS) == 0) {
            return Status.DONE;
        }

        return Status.NEW;

    }

}
