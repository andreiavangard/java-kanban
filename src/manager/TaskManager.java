package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import java.util.List;


public interface TaskManager {
    int addTask(Task taskAdd);

    void updateTask(Task taskUpdate);

    void deleteTask(Task taskDelete);

    void deleteTaskById(int id);

    void clearTask();

    Task getTask(int taskId);

    int addEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(Epic epic);

    void deleteEpicById(int id);

    void clearEpics();

    Epic getEpic(int epicId);

    int addSubtask(Subtask subtask, Epic epic);

    void updateSubtask(Subtask subtask, Epic epic);

    void deleteSubtask(Subtask subtask, Epic epic);

    void deleteSubtaskById(int id);

    void clearSubtask();

    Subtask getSubtask(int subtaskId);

    void clearSubtask(Epic epic);

    List<Subtask> getSubtasks(Epic epic);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Task> getHistory();

    Task getTaskInHistoryById(int id);
}
