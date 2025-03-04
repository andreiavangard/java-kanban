package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    static TaskManager taskManager;
    @BeforeEach
    void beforeAll(){
        taskManager = Managers.getDefault();
    }


    //2) проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void addNewTask() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        int taskId = taskManager.addTask(task);
        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    //тест проверяет, что экземпляры с одинковым id равны
    @Test
    void instancesTaskEqualIfTheirIdIsEqual(){
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        Task cloneTask = task.clone();
        cloneTask.setName(cloneTask.getName()+"*");
        cloneTask.setDescription(cloneTask.getDescription()+"*");
        assertEquals(task, cloneTask, "Задачи c одинаковым id не совпадают.");
    }

    //4) создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    //проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void checkTheImmutabilityOfTaskWhenAddingToManager(){
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        int taskId = taskManager.addTask(task);
        Task savedTask = taskManager.getTask(taskId);
        assertEquals(task.getId(), savedTask.getId(), "Не совпадает ид");
        assertEquals(task.getName(), savedTask.getName(), "Не совпадает наименование");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Не совпадает описание");
    }

    //для новой задачи правильно устанавливается статус
    @Test
    void theStatusForNewOneIsSetCorrectly(){
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        taskManager.addTask(task);
        assertEquals(task.getStatus(), Status.NEW, "Задача при создании не установила статус новый.");
    }

    //3) проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    void tasksWithGivenIdAndGeneratedIdNotConflict(){
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setId(-1);
        taskManager.addTask(task);
        assertTrue(task.getId()>=0,"Присвоенный ид не переопределяется в менеджере");
    }

}