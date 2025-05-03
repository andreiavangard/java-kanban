package task;

import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    protected LocalDateTime currentDate = LocalDateTime.now();
    protected int duration1m = 1;

    @BeforeEach
    void setTaskManager() {
        taskManager = createTaskManager();
    }

    //тест проверяет, что TaskManager действительно добавляет задачи и может найти их по id;
    @Test
    void addNewTask() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        int taskId = taskManager.addTask(task);
        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    //тест проверяет, что TaskManager действительно добавляет таски субтаски и может найти их по id;
    @Test
    void addNewEpicSubtask() {
        //Эпики
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        int epicId = taskManager.addEpic(epic);
        Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");

        //Субтаски
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", currentDate, duration1m);
        int subtaskId = taskManager.addSubtask(subtask, epic);
        Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Субтаска не найдена.");
        assertEquals(subtask, savedSubtask, "Субтаски не совпадают.");
        List<Subtask> subtasks = taskManager.getSubtasks(epic);

        assertNotNull(subtasks, "Субтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество Субтаск.");
        assertEquals(subtask, subtasks.get(0), "Субтаски не совпадают.");
    }

    //тест проверяет, что экземпляры задач с одинковым id равны
    @Test
    void instancesTaskEqualIfTheirIdIsEqual() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        Task cloneTask = task.clone();
        cloneTask.setName(cloneTask.getName() + "*");
        cloneTask.setDescription(cloneTask.getDescription() + "*");
        assertEquals(task, cloneTask, "Задачи c одинаковым id не совпадают.");
    }

    //тест проверяет, что экземпляры эпиков субтасков с одинковым id равны
    @Test
    void instancesEpicSubtaskEqualIfTheirIdIsEqual() {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        Epic cloneEpic = epic.clone();
        cloneEpic.setName(cloneEpic.getName() + "*");
        cloneEpic.setDescription(cloneEpic.getDescription() + "*");
        assertEquals(epic, cloneEpic, "Эпики c одинаковым id не совпадают.");

        Subtask subtask = new Subtask("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        Subtask cloneSubtask = subtask.clone();
        cloneSubtask.setName(cloneSubtask.getName() + "*");
        cloneSubtask.setDescription(cloneSubtask.getDescription() + "*");
        assertEquals(subtask, cloneSubtask, "Задачи c одинаковым id не совпадают.");
    }

    //тест проверяет неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void checkTheImmutabilityOfTaskWhenAddingToManager() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        int taskId = taskManager.addTask(task);
        Task savedTask = taskManager.getTask(taskId);
        assertEquals(task.getId(), savedTask.getId(), "Не совпадает ид");
        assertEquals(task.getName(), savedTask.getName(), "Не совпадает наименование");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Не совпадает описание");
    }

    //4) создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    //проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void checkTheImmutabilityOfEpicSubtaskWhenAddingToManager() {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        int epicId = taskManager.addEpic(epic);
        Epic savedEpic = taskManager.getEpic(epicId);
        assertEquals(epic.getId(), savedEpic.getId(), "Не совпадает ид");
        assertEquals(epic.getName(), savedEpic.getName(), "Не совпадает наименование");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Не совпадает описание");

        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска", currentDate, duration1m);
        int SubtaskId = taskManager.addSubtask(subtask, epic);
        Subtask savedSubtask = taskManager.getSubtask(SubtaskId);
        assertEquals(subtask.getId(), savedSubtask.getId(), "Не совпадает ид");
        assertEquals(subtask.getName(), savedSubtask.getName(), "Не совпадает наименование");
        assertEquals(subtask.getDescription(), savedSubtask.getDescription(), "Не совпадает описание");
    }


    //для новой задачи правильно устанавливается статус
    @Test
    void theStatusForNewOneIsSetCorrectly() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        taskManager.addTask(task);
        assertEquals(task.getStatus(), Status.NEW, "Задача при создании не установила статус новый.");
    }

    //для новой задачи правильно устанавливается статус
    @Test
    void theStatusForNewEpicSubtaskOneIsSetCorrectly() {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        assertEquals(epic.getStatus(), Status.NEW, "Эпик при создании не установила статус новый.");

        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска", currentDate, duration1m);
        taskManager.addSubtask(subtask, epic);
        assertEquals(subtask.getStatus(), Status.NEW, "Эпик при создании не установила статус новый.");
    }


    //тест проверяет, что присвоенный в менеджере ид нельзя переопределить
    @Test
    void assignedIdCannotBeRedefined() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        int id = taskManager.addTask(task);
        task.setId(-1);
        assertEquals(task.getId(), id, "Присвоенный ид не переопределяется в менеджере");
    }

    //тест проверяет что у субтасок правильно заполняется ид
    @Test
    void ownerIDCheck() {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Тестовый субтаск1", "Описание тестового субтаска1", currentDate, duration1m);
        taskManager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Тестовый субтаск2", "Описание тестового субтаска2", currentDate.plusMinutes(10), duration1m);
        taskManager.addSubtask(subtask2, epic);

        HashMap<Integer, Subtask> subtasks = epic.getSubTasks();
        for (Subtask subtask : subtasks.values()) {
            assertEquals(subtask.getIdEpic(), epic.getId(), "У субтаска ид не совпадает с ид эпика.");
        }
    }

    //тест проверяет правильный расчет статуса
    @Test
    void checkingEpicStatusSetting() {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Тестовый субтаск1", "Описание тестового субтаска1", currentDate, duration1m);
        taskManager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Тестовый субтаск2", "Описание тестового субтаска2", currentDate.plusMinutes(10), duration1m);
        taskManager.addSubtask(subtask2, epic);

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1, epic);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Эпик не обновил статус IN_PROGRESS.");

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1, epic);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Эпик неправильно обновил статус IN_PROGRESS.");

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2, epic);
        assertEquals(epic.getStatus(), Status.DONE, "Эпик неправильно обновил статус DONE.");

        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2, epic);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Эпик неправильно возобновил статус IN_PROGRESS.");

        epic.clearSubtask();
        taskManager.updateEpic(epic);
        assertEquals(epic.getStatus(), Status.NEW, "Эпик неправильно возобновил статус NEW.");

    }

    //тест проверяет что в список задач по приоритету не попадают задачи с пустой датой начала
    @Test
    void cantAddEmptyDateInTasksOfPriority() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", null, duration1m);
        taskManager.addTask(task);
        Task[] tasks = taskManager.getPrioritizedTasks();
        assertEquals(tasks.length, 0, "В список задач по проиоретету попадают задачи с пустой датой старта");
    }

    //тест проверяет правильность расчета приоритета
    @Test
    void priorityСalculatedСorrectly() {
        LocalDateTime dateTime4 = LocalDateTime.of(2000, 4, 1, 1, 0);
        LocalDateTime dateTime3 = LocalDateTime.of(2000, 3, 1, 1, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2000, 2, 1, 1, 0);
        LocalDateTime dateTime1 = LocalDateTime.of(2000, 1, 1, 1, 0);

        Task task1 = new Task("Тестовая задача1", "Описание тестовой задачи1", dateTime4, duration1m);
        taskManager.addTask(task1);

        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Тестовый субтаск1", "Описание тестового субтаска1", dateTime3, duration1m);
        taskManager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Тестовый субтаск2", "Описание тестового субтаска2", dateTime1, duration1m);
        taskManager.addSubtask(subtask2, epic);

        Task task2 = new Task("Тестовая задача2", "Описание тестовой задачи2", dateTime2, duration1m);
        taskManager.addTask(task2);

        Task[] tasks = taskManager.getPrioritizedTasks();
        assertEquals(tasks.length, 4, "В список по приоритету попали не все задачи");
        assertEquals(tasks[0], subtask2, "Не правильно определена самая приоритетная задача");
        assertEquals(tasks[tasks.length - 1], task1, "Не правильно определена самая неприоритетная задача");
    }

    //тест проверяет что при добавлении задач с пересечением возникает исключение
    @Test
    void taskAddedExceptionOccurred() {
        LocalDateTime dateTime = LocalDateTime.of(2000, 4, 1, 1, 0);
        Task task1 = new Task("Тестовая задача1", "Описание тестовой задачи1", dateTime, duration1m);
        Task task2 = new Task("Тестовая задача2", "Описание тестовой задачи2", dateTime, duration1m);
        taskManager.addTask(task1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(task2);
        });
        assertTrue(exception.getMessage().contains(String.format("Задача %s пересекается с одной из существующих задач", task2.toString())));
    }

    //тест проверяет правильность расчета данных начала, окончания и длительности эпика
    @Test
    void startDateEndDateDurationCalculatedCorrectly() {
        LocalDateTime dateTime4 = LocalDateTime.of(2000, 4, 1, 1, 0);
        LocalDateTime dateTime3 = LocalDateTime.of(2000, 3, 1, 1, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2000, 2, 1, 1, 0);
        LocalDateTime dateTime1 = LocalDateTime.of(2000, 1, 1, 1, 0);

        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Тестовый субтаск1", "Описание тестового субтаска1", dateTime1, duration1m);
        taskManager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Тестовый субтаск2", "Описание тестового субтаска2", dateTime2, duration1m);
        taskManager.addSubtask(subtask2, epic);
        Subtask subtask3 = new Subtask("Тестовый субтаск3", "Описание тестового субтаска3", dateTime3, duration1m);
        taskManager.addSubtask(subtask3, epic);

        assertEquals(epic.getStartTime(), dateTime1, "Неправильно рассчиталась дата начала");
        assertEquals(epic.getEndTime(), dateTime3.plusMinutes(1), "Неправильно рассчиталась дата окончания");
        assertEquals(epic.getDurationInMinutes(), Duration.between(dateTime1, dateTime3).toMinutes() + 1, "Неправильно рассчиталась длительность");

        taskManager.deleteSubtask(subtask3, epic);
        assertEquals(epic.getStartTime(), dateTime1, "Неправильно рассчиталась дата начала после удаления субтаска");
        assertEquals(epic.getEndTime(), dateTime2.plusMinutes(1), "Неправильно рассчиталась дата окончания после удаления субтаска");
        assertEquals(epic.getDurationInMinutes(), Duration.between(dateTime1, dateTime2).toMinutes() + 1, "Неправильно рассчиталась длительность после удаления субтаска");

        epic.clearSubtask();
        assertNull("Не очистилась дата начала при очистке субтасков", epic.getStartTime());
        assertNull("Не очистилась дата начала при очистке субтасков", epic.getEndTime());
        assertEquals(epic.getDurationInMinutes(), 0, "Не очистилась длительность при очистке субтасков");

    }

    //тест проверяет, что можно корретно обновить задачу для списка приоритетов, задача не будет мешать сама себе
    @Test
    void updateTaskForPriorityListCorrectly() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        taskManager.addTask(task);
        assertEquals(taskManager.getPrioritizedTasks().length, 1, "Задача не записалась в историю");
        taskManager.updateTask(task);
    }

    //тест проверяет что при удалении задач, субтасков они удаляются из списка приоритетов
    @Test
    void whenTasksDeletedAreRemovedFromPriorityList() {
        LocalDateTime dateTime2 = LocalDateTime.of(2000, 2, 1, 1, 0);
        LocalDateTime dateTime1 = LocalDateTime.of(2000, 1, 1, 1, 0);

        Task task1 = new Task("Тестовая задача1", "Описание тестовой задачи1", dateTime1, duration1m);
        taskManager.addTask(task1);

        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Тестовый субтаск1", "Описание тестового субтаска1", dateTime2, duration1m);
        taskManager.addSubtask(subtask1, epic);
        assertEquals(taskManager.getPrioritizedTasks().length, 2, "Не все задачи добавлены в список приоритетов");
        taskManager.deleteTask(task1);
        taskManager.deleteSubtask(subtask1, epic);
        assertEquals(taskManager.getPrioritizedTasks().length, 0, "Не все задачи удалены из списка приоритетов");

    }

}
