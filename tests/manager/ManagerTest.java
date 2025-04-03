package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {
    static TaskManager taskManager;
    @BeforeEach
    void beforeAll(){
        taskManager = Managers.getDefault();
    }

    //1) убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    void testManagers() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "taskManager не проинициализирован явно не готов.");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(taskManager, "historyManager не проинициализирован явно не готов.");
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

        List<Task> hystoriTask = taskManager.getHistory();
        assertTrue(hystoriTask.size()==3,"Проверка инициализации. Ошибка работы с InMemoryHistoryManager");

    }

    //5) убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    //задача, добавляемая в HistoryManager, сохраняет предыдущую версию задачи и её данных.
    @Test
    void epicSubtaskAdedToTheHistoryManagerSavesPreviousVersionOfTaskAndData(){
        //создали эпик в переменную epic
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        int epicId = taskManager.addEpic(epic);
        //вызываем getEpic чтобы эпик попал в историю
        taskManager.getEpic(epicId);
        //получаем историю
        List<Task> hystoriTask = taskManager.getHistory();
        //защита от пустой истории, если история пустая то здесь упадем
        assertTrue(hystoriTask.size()>0,"В истории нет задач");
        //получаем последний эпик из истории
        Task savedHistoryEpic = hystoriTask.get(hystoriTask.size()-1);
        //меняем эпик
        epic.setName(epic.getName()+"*");
        //проверяем что новое имя не совпадает со старым
        assertNotEquals(epic.getName(), savedHistoryEpic.getName(), "Эпик из истории не сохранила версию.");

        //создаем субтаску помещаем ее в переменную task
        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска");
        int subtaskId = taskManager.addSubtask(subtask ,epic);
        //вызываем метод getSubtask чтобы субтаска попала в историю
        taskManager.getSubtask(subtaskId);
        //получаем историю
        hystoriTask = taskManager.getHistory();
        //защита от пустой истории, если история пустая то здесь упадем
        assertTrue(hystoriTask.size()>0,"В истории нет задач");
        //получаем последнюю субтаску из истории
        Task savedHistorySubtask = hystoriTask.get(hystoriTask.size()-1);
        subtask.setName(subtask.getName()+"*");
        //проверяем что новое имя не совпадает со старым
        assertNotEquals(subtask.getName(), savedHistorySubtask.getName(), "Субтаск из истории не сохранила версию.");
    }

    //5) убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    //задача, добавляемая в HistoryManager, сохраняет предыдущую версию задачи и её данных.
    @Test
    void taskAdedToTheHistoryManagerSavesPreviousVersionOfTaskAndData(){
        //создаем таску помещаем ее в переменную task
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        int taskId = taskManager.addTask(task);
        //вызываем метод getTask чтобы таска попала в историю
        taskManager.getTask(taskId);
        //получаем задачу из истории
        Task savedHistoryTask = taskManager.getTaskInHistoryById(taskId);
        assertNotNull(savedHistoryTask, "Задача не записалась в историю");
        task.setName(task.getName()+"**");
        //проверяем что новое имя не совпадает со старым
        String n1 = task.getName();
        String n2 = savedHistoryTask.getName();
        assertNotEquals(task.getName(), savedHistoryTask.getName(), "Задача из истории не сохранила версию.");
    }

}