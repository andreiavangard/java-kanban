import java.util.HashMap;

public class TaskManager {
    private static int globalTaskId;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, Subtask>> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public static int getGlobalTaskId(){
        globalTaskId++;
        return globalTaskId;
    }

    public void addTask(Task task, Status status){
        int id = getGlobalTaskId();
        task.setId(id);
        task.setStatus(status);
        tasks.put(id, task);
    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void deleteTask(Task task){
        tasks.remove(task.getId());
    }

    public void deleteTaskById(int id){
        deleteTask(tasks.get(id));
    }

    public void clearTask(){
        tasks.clear();
    }

    public void addEpic(Epic epic){
        int id = getGlobalTaskId();
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epics.put(id, epic);
    }

    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
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

    public void addSubtask(Subtask subtask, Epic epic, Status status){
        int id = getGlobalTaskId();
        subtask.setId(id);
        subtask.setStatus(status);
        int idEpic = epic.getId();
        subtask.setIdEpic(idEpic);
        epic.setSubtask(subtask, id);
        subTasks.put(idEpic, epic.getSubTasks());
        Status newStatusEpic = statusEpicBySubtasks(epic);
        epic.setStatus(newStatusEpic);
    }

    public void updateSubtask(Subtask subtask, Epic epic){
        int idEpic = subtask.getIdEpic();
        epic.setSubtask(subtask, subtask.getId());
        subTasks.put(idEpic, epic.getSubTasks());
        Status newStatusEpic = statusEpicBySubtasks(epic);
        epic.setStatus(newStatusEpic);
    }

    public void deleteSubtask(Subtask subtask, Epic epic){
        epic.deleteSubtask(subtask.getId());
        subTasks.put(epic.getId(), epic.getSubTasks());
        Status newStatusEpic = statusEpicBySubtasks(epic);
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
            epic.setStatus(Status.NEW);
        }
    }

    public void clearSubtask(Epic epic){
        subTasks.remove(epic.getId());
        epic.setStatus(Status.NEW);
    }

    public HashMap<Integer, Subtask> getListSubTasksEpic(Epic epic){
        return subTasks.get(epic.getId());
    }

    public HashMap<Integer, Task> getListTasks(){
        return tasks;
    }

    public HashMap<Integer, Epic> getListEpics(){
        return epics;
    }

    public HashMap<Integer, HashMap<Integer, Subtask>> getListSubTasks() {
        return subTasks;
    }

    private Status statusEpicBySubtasks(Epic epic){
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
