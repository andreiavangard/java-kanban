import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int globalTaskId;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    /*
    subTask не может жить без своего epic, поэтому subTasks это HashMap в HashMap
    id эпик
           id1 субтаск1
           ...
           idn субтаскn
    */
    private HashMap<Integer, HashMap<Integer, Subtask>> subTasks = new HashMap<>();
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
        epics.remove(epic.getId());
        subTasks.remove(epic.getId());
    }

    public void deleteEpicById(int id){
         deleteEpic(epics.get(id));
    }

    public void clearEpic(){
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
        subTasks.put(idEpic, epic.getSubTasks());
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    public void updateSubtask(Subtask subtask, Epic epic){
        int idEpic = subtask.getIdEpic();
        epic.setSubtask(subtask, subtask.getId());
        subTasks.put(idEpic, epic.getSubTasks());
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    public void deleteSubtask(Subtask subtask, Epic epic){
        epic.deleteSubtask(subtask.getId());
        subTasks.put(epic.getId(), epic.getSubTasks());
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    public void deleteSubtaskById(int id){
        Subtask subtask = getSubtaskById(id);
        if(subtask != null) {
            Epic epic = epics.get(subtask.getIdEpic());
            deleteSubtask(subtask, epic);
        }
    }

    public void clearSubtask(){
        subTasks.clear();
        for(Epic epic : epics.values()){
            Status newStatusEpic = updateEpicStatus(epic);
            epic.setStatus(newStatusEpic);
        }
    }

    public void clearSubtask(Epic epic){
        /*
        здесь чистим субтаски по одному эпику
        так как subTasks организована в разрезе эпиков, то есть в виде дерева, то можно удалить
        все субтаски эпика по ключу этого эпика
        */
        subTasks.remove(epic.getId());
        Status newStatusEpic = updateEpicStatus(epic);
        epic.setStatus(newStatusEpic);
    }

    public ArrayList<Subtask> getSubtasks(Epic epic){
        return new ArrayList<Subtask>(subTasks.get(epic.getId()).values());
    }

    public ArrayList<Task> getTasks(){
        return new ArrayList<Task>(tasks.values());
    }

    public ArrayList<Epic> getEpics(){
        return new ArrayList<Epic>(epics.values());
    }

    public HashMap<Integer, HashMap<Integer, Subtask>> getListSubTasks() {
        return subTasks;
    }

    private Status updateEpicStatus(Epic epic){
        HashMap<Status, Integer> mapStatus = new HashMap<>();
        mapStatus.put(Status.NEW,0);
        mapStatus.put(Status.IN_PROGRESS,0);
        mapStatus.put(Status.DONE,0);

        for(Subtask subtask : subTasks.get(epic.getId()).values()){
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

    private Subtask getSubtaskById(int id){
        for (HashMap<Integer, Subtask> epic : subTasks.values()){
            for(Integer key : epic.keySet()){
                if(key==id){
                    return epic.get(key);
                }
            }
        }
        return null;
    }

}
