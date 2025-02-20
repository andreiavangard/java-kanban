package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int globalTaskId;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public void addTask(Task taskAdd){
        int id = globalTaskId++;
        taskAdd.setId(id);
        taskAdd.setStatus(Status.NEW);
        tasks.put(id, taskAdd);
    }

    public void updateTask(Task taskUpdate){
        if(tasks.get(taskUpdate.getId())!=null) {
            tasks.put(taskUpdate.getId(), taskUpdate);
        }
    }

    public void deleteTask(Task taskDelete){
        tasks.remove(taskDelete.getId());
    }

    public void deleteTaskById(int id){
        deleteTask(tasks.get(id));
    }

    public void clearTask(){
        tasks.clear();
    }

    public void addEpic(Epic epic){
        int id = globalTaskId++;
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
    }

    public void updateEpic(Epic epic){
        if(epics.get(epic.getId())!=null) {
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteEpic(Epic epic){
        HashMap<Integer, Subtask> subTasksEpic = epic.getSubTasks();
        for(int key : subTasksEpic.keySet()){
            subTasks.remove(key);
        }
        epics.remove(epic.getId());
    }

    public void deleteEpicById(int id){
         deleteEpic(epics.get(id));
    }

    public void clearEpics(){
        epics.clear();
        subTasks.clear();
    }

    public void addSubtask(Subtask subtask, Epic epic){
        int id = globalTaskId++;
        subtask.setId(id);
        subtask.setStatus(Status.NEW);
        int idEpic = epic.getId();
        subtask.setIdEpic(idEpic);
        epic.setSubtask(subtask, id);
        subTasks.put(subtask.getId(), subtask);
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    public void updateSubtask(Subtask subtask, Epic epic){
        epic.setSubtask(subtask, subtask.getId());
        subTasks.put(subtask.getId(), subtask);
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    public void deleteSubtask(Subtask subtask, Epic epic){
        epic.deleteSubtask(subtask.getId());
        subTasks.remove(subtask.getId());
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    public void deleteSubtaskById(int id){
        Subtask subtask = subTasks.get(id);
        if(subtask != null) {
            Epic epic = epics.get(subtask.getIdEpic());
            deleteSubtask(subtask, epic);
        }
    }

    public void clearSubtask(){
        subTasks.clear();
        for(Epic epic : epics.values()){
            epic.clearSubtask();
            epic.setStatus(Status.NEW);
        }
    }

    public void clearSubtask(Epic epic){
        epic.clearSubtask();
        HashMap<Integer, Subtask> subTasksEpic = epic.getSubTasks();
        for(int key : subTasksEpic.keySet()){
            subTasks.remove(key);
        }
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    public ArrayList<Subtask> getSubtasks(Epic epic){
        ArrayList<Subtask> outSubtasks = new ArrayList<>();
        for(Subtask subtask: subTasks.values()){
            if(subtask.getIdEpic()==epic.getId()){
                outSubtasks.add(subtask);
            }
        }
        return outSubtasks;
    }

    public ArrayList<Task> getTasks(){
        return new ArrayList<Task>(tasks.values());
    }

    public ArrayList<Epic> getEpics(){
        return new ArrayList<Epic>(epics.values());
    }

    private Status updateEpicStatus(Epic epic){
        HashMap<Status, Integer> mapStatus = new HashMap<>();
        mapStatus.put(Status.NEW,0);
        mapStatus.put(Status.IN_PROGRESS,0);
        mapStatus.put(Status.DONE,0);

        for(Subtask subtask : epic.getSubTasks().values()){
            int value = 0;
            switch (subtask.getStatus()){
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

        if (mapStatus.get(Status.IN_PROGRESS)>=1){
            return Status.IN_PROGRESS;
        } else if (mapStatus.get(Status.DONE)>=1 && mapStatus.get(Status.NEW)==0 && mapStatus.get(Status.IN_PROGRESS)==0) {
            return Status.DONE;
        }

        return Status.NEW;

    }


}
