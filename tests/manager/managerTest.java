package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class managerTest {

    @Test
    void testManagers() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        int taskId = taskManager.addTask(task);
        Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Проверка инициализации. Ошибка работы с InMemoryTaskManager.");
        taskManager.getTask(taskId);
        List<Task> hystoryTask = taskManager.getHistory();
        assertTrue(hystoryTask.size()>0,"Проверка инициализации. Ошибка работы с InMemoryHistoryManager");

    }

    @Test
    void testHistory() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        int taskId = taskManager.addTask(task);
        taskManager.getTask(taskId);

        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        int epicId =  taskManager.addEpic(epic);
        taskManager.getEpic(epicId);

        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        int subtaskId = taskManager.addSubtask(subtask, epic);
        taskManager.getSubtask(subtaskId);

        List<Task> hystoryTask = taskManager.getHistory();
        assertTrue(hystoryTask.size()==3,"Проверка инициализации. Ошибка работы с InMemoryHistoryManager");

    }

}