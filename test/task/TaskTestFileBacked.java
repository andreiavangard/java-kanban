package task;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTestFileBacked {

    //тест проверяет правильность записи пустого файла
    @Test
    public void testFileBackedEmptySave() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        try {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
            taskManager.save();
        } catch (Exception exception) {
            throw new AssertionError("Сохранение задач в файл должно происходить без исключений", exception);
        }
        Assertions.assertTrue(tempFile.exists());
    }

    //тест проверяет правильность чтения пустого файла
    @Test
    public void testFileBackedEmptyLoad() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = null;
        try {
            taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        } catch (Exception exception) {
            throw new AssertionError("Загрузка задач из файла должна происходить без исключений", exception);
        }
        Assertions.assertNotNull(taskManager);
    }

    //тест проверяет правильность записи/востановления данных из файла
    //строгая проверка по всем полям
    @Test
    void strictСorrectnessOfDataRecoveryFromFile() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager saveManager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        saveManager.addTask(task);
        int idTask = task.getId();
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        saveManager.addEpic(epic);
        int idEpic = epic.getId();
        Subtask subtask1 = new Subtask("Тестовая задача1", "Описание тестовой задачи1");
        Subtask subtask2 = new Subtask("Тестовая задача2", "Описание тестовой задачи2");
        saveManager.addSubtask(subtask1, epic);
        saveManager.addSubtask(subtask2, epic);
        int idSubtask1 = subtask1.getId();
        int idSubtask2 = subtask2.getId();

        FileBackedTaskManager readManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task task1 = saveManager.getTask(idTask);
        Task task2 = readManager.getTask(idTask);

        assertEquals(task1.getId(), task2.getId(), "Не совпадают ид для task");
        assertEquals(task1.getName(), task2.getName(), "Не совпадают имя для task");
        assertEquals(task1.getDescription(), task2.getDescription(), "Не совпадают описание для task");
        assertEquals(task1.getStatus(), task2.getStatus(), "Не совпадают статус для task");

        Epic epic1 = saveManager.getEpic(idEpic);
        Epic epic2 = readManager.getEpic(idEpic);
        assertEquals(epic1.getId(), epic2.getId(), "Не совпадают ид для epic");
        assertEquals(epic1.getName(), epic2.getName(), "Не совпадают имя для epic");
        assertEquals(epic1.getDescription(), epic2.getDescription(), "Не совпадают описание для epic");
        assertEquals(epic1.getStatus(), epic2.getStatus(), "Не совпадают статус для epic");

        Subtask subtask11 = saveManager.getSubtask(idSubtask1);
        Subtask subtask12 = readManager.getSubtask(idSubtask1);
        assertEquals(subtask11.getId(), subtask12.getId(), "Не совпадают ид для subtask1");
        assertEquals(subtask11.getName(), subtask12.getName(), "Не совпадают имя для subtask1");
        assertEquals(subtask11.getDescription(), subtask12.getDescription(), "Не совпадают описание для subtask1");
        assertEquals(subtask11.getStatus(), subtask12.getStatus(), "Не совпадают статус для subtask1");
        assertEquals(subtask11.getIdEpic(), subtask12.getIdEpic(), "Не совпадают ид эпик для subtask1");

        Subtask subtask21 = saveManager.getSubtask(idSubtask1);
        Subtask subtask22 = readManager.getSubtask(idSubtask1);
        assertEquals(subtask21.getId(), subtask22.getId(), "Не совпадают ид для subtask2");
        assertEquals(subtask21.getName(), subtask22.getName(), "Не совпадают имя для subtask2");
        assertEquals(subtask21.getDescription(), subtask22.getDescription(), "Не совпадают описание для subtask2");
        assertEquals(subtask21.getStatus(), subtask22.getStatus(), "Не совпадают статус для subtask2");
        assertEquals(subtask21.getIdEpic(), subtask22.getIdEpic(), "Не совпадают ид эпик для subtask2");

    }

    //тест проверяет правильность записи/востановления данных из файла
    //мягкая проверка через equals, по условию таски подобны если совпадает id
    @Test
    void softСorrectnessOfDataRecoveryFromFile() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager saveManager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        saveManager.addTask(task);
        int idTask = task.getId();
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        saveManager.addEpic(epic);
        int idEpic = epic.getId();
        Subtask subtask1 = new Subtask("Тестовая задача1", "Описание тестовой задачи1");
        Subtask subtask2 = new Subtask("Тестовая задача2", "Описание тестовой задачи2");
        saveManager.addSubtask(subtask1, epic);
        saveManager.addSubtask(subtask2, epic);
        int idSubtask1 = subtask1.getId();
        int idSubtask2 = subtask2.getId();

        FileBackedTaskManager readManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(readManager.getTask(idTask).equals(saveManager.getTask(idTask)), "Некорректно восстановилась task");
        assertTrue(readManager.getEpic(idEpic).equals(saveManager.getEpic(idEpic)), "Некорректно восстановилась Epic");
        assertTrue(readManager.getSubtask(idSubtask1).equals(saveManager.getSubtask(idSubtask1)), "Некорректно восстановилась Subtask1");
        assertTrue(readManager.getSubtask(idSubtask2).equals(saveManager.getSubtask(idSubtask2)), "Некорректно восстановилась Subtask2");

    }

    //тест проверяет правильность генерирования строки описания задачи
    @Test
    void toStringForTaskGeneratedCorrectly() throws IOException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        taskManager.addTask(task);
        assertEquals(task.toString(), String.format("%s, TASK, Тестовая задача, NEW, Описание тестовой задачи,", task.getId()), "Неверно работает генерация описания задачи для task");
    }

    //тест проверяет правильность восстановления задачи из строки
    @Test
    void correctnessTaskGenerationFromString() {
        String strinfTaskOutFile = "1, TASK, Тестовая задача, NEW, Описание тестовой задачи,";
        Task task = new Task(strinfTaskOutFile);
        assertEquals(task.getId(), 1, "Неправильно восстановился id");
        assertEquals(task.getType(), TaskType.TASK, "Неправильно восстановился тип");
        assertEquals(task.getStatus(), Status.NEW, "Неправильно восстановился статус");
        assertEquals(task.getName(), "Тестовая задача", "Неправильно восстановилось наименование");
        assertEquals(task.getDescription(), "Описание тестовой задачи", "Неправильно восстановилось описание задачи");
    }

    //тест проверяет правильность определения типа задачи из строки
    @Test
    void determiningTaskTypeFromString() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        assertEquals(TaskType.getTypeFromString("TASK"), TaskType.TASK, "Неверно определяется из строки тип TASK");
        assertEquals(TaskType.getTypeFromString("EPIC"), TaskType.EPIC, "Неверно определяется из строки тип EPIC");
        assertEquals(TaskType.getTypeFromString("SUBTASK"), TaskType.SUBTASK, "Неверно определяется из строки тип SUBTASK");
    }

    //тест проверяет правильность определения статуса задачи из строки
    @Test
    void determiningTaskStatusFromString() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        assertEquals(Status.getStatusFromString("NEW"), Status.NEW, "Неверно определяется из строки статус NEW");
        assertEquals(Status.getStatusFromString("IN_PROGRESS"), Status.IN_PROGRESS, "Неверно определяется из строки статус IN_PROGRESS");
        assertEquals(Status.getStatusFromString("DONE"), Status.DONE, "Неверно определяется из строки статус DONE");
    }

    //тест проверяет правильность генерирования строки заголовка для файла сохранения истории
    @Test
    void determiningCorrectFileHeader() {
        assertEquals(FileBackedTaskManager.getFileHeader(), "id, type, name, status, description, epic", "Неправильно формируется заголовок файла");
    }

    //тест проверяет, что InMemoryTaskManager действительно добавляет задачи и может найти их по id;
    @Test
    void addNewTask() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

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
    void instancesTaskEqualIfTheirIdIsEqual() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        Task cloneTask = task.clone();
        cloneTask.setName(cloneTask.getName() + "*");
        cloneTask.setDescription(cloneTask.getDescription() + "*");
        assertEquals(task, cloneTask, "Задачи c одинаковым id не совпадают.");
    }

    //тест проверяет неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void checkTheImmutabilityOfTaskWhenAddingToManager() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        int taskId = taskManager.addTask(task);
        Task savedTask = taskManager.getTask(taskId);
        assertEquals(task.getId(), savedTask.getId(), "Не совпадает ид");
        assertEquals(task.getName(), savedTask.getName(), "Не совпадает наименование");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Не совпадает описание");
    }

    //тест проверяет что для новой задачи правильно устанавливается статус
    @Test
    void theStatusForNewOneIsSetCorrectly() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        taskManager.addTask(task);
        assertEquals(task.getStatus(), Status.NEW, "Задача при создании не установила статус новый.");
    }

    //тест проверяет что присвоенный ид переопределяется в менеджере
    @Test
    void assignedIdCannotBeRedefined() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Тестовая задача", "Описание тестовой задачи");
        int id = taskManager.addTask(task);
        task.setId(-1);
        assertEquals(task.getId(), id, "Присвоенный ид не переопределяется в менеджере");
    }

}
