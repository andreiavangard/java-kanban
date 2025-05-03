package task;

import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createTaskManager() {
        File tempFile;
        try {
            tempFile = File.createTempFile("fm_", ".csv");
        } catch (IOException e) {
            e.getMessage();
            tempFile = null;
        }
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        return taskManager;
    }

    @Override
    @Test
    void addNewTask() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.addNewTask();
    }

    @Override
    @Test
    void instancesTaskEqualIfTheirIdIsEqual() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.instancesTaskEqualIfTheirIdIsEqual();
    }

    @Override
    @Test
    void checkTheImmutabilityOfTaskWhenAddingToManager() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.checkTheImmutabilityOfTaskWhenAddingToManager();
    }

    @Override
    @Test
    void theStatusForNewOneIsSetCorrectly() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.theStatusForNewOneIsSetCorrectly();
    }

    @Override
    @Test
    void assignedIdCannotBeRedefined() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.assignedIdCannotBeRedefined();
    }

    @Override
    @Test
    void addNewEpicSubtask() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.addNewEpicSubtask();
    }

    @Override
    @Test
    void instancesEpicSubtaskEqualIfTheirIdIsEqual() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.instancesEpicSubtaskEqualIfTheirIdIsEqual();
    }

    @Override
    @Test
    void checkTheImmutabilityOfEpicSubtaskWhenAddingToManager() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.checkTheImmutabilityOfEpicSubtaskWhenAddingToManager();
    }

    @Override
    @Test
    void theStatusForNewEpicSubtaskOneIsSetCorrectly() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.theStatusForNewEpicSubtaskOneIsSetCorrectly();
    }

    @Override
    @Test
    void ownerIDCheck() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.ownerIDCheck();
    }

    @Override
    @Test
    void checkingEpicStatusSetting() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.checkingEpicStatusSetting();
    }

    //тест проверяет правильность записи пустого файла
    @Test
    public void testFileBackedEmptySave() {
        final File[] tempFile = new File[1];
        assertDoesNotThrow(() -> {
            tempFile[0] = File.createTempFile("fm_", ".csv");
            assertTrue(Files.exists(tempFile[0].toPath()));
        }, "Ошибка создания файла");

        assertDoesNotThrow(() -> {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile[0]);
            taskManager.save();
        }, "Сохранение задач в файл должно происходить без исключений");
    }

    //тест проверяет правильность чтения пустого файла
    @Test
    public void testFileBackedEmptyLoad() throws IOException {
        final File[] tempFile = new File[1];
        assertDoesNotThrow(() -> {
            tempFile[0] = File.createTempFile("fm_", ".csv");
            assertTrue(Files.exists(tempFile[0].toPath()));
        }, "Ошибка создания файла");

        assertDoesNotThrow(() -> {
            FileBackedTaskManager tm = null;
            tm = FileBackedTaskManager.loadFromFile(tempFile[0]);
            Assertions.assertNotNull(tm);
        }, "Загрузка задач из файла должна происходить без исключений");

    }

    //тест проверяет, что нельзя создать менеджер из несуществующего файла
    @Test
    void cannotCreateManagerFromNonExistentFile() {
        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager tm = null;
            tm = FileBackedTaskManager.loadFromFile(new File("nonexistent.csv"));
        });
        assertTrue(exception.getMessage().contains("Ошибка чтения файла: nonexistent.csv"));
    }

    //тест проверяет правильность записи/востановления данных из файла
    //строгая проверка по всем полям
    @Test
    void strictСorrectnessOfDataRecoveryFromFile() throws IOException {
        final File[] tempFile = new File[1];
        assertDoesNotThrow(() -> {
            tempFile[0] = File.createTempFile("fm_", ".csv");
            assertTrue(Files.exists(tempFile[0].toPath()));
        }, "Ошибка создания файла");

        FileBackedTaskManager saveManager = new FileBackedTaskManager(tempFile[0]);

        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        saveManager.addTask(task);
        int idTask = task.getId();
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        saveManager.addEpic(epic);
        int idEpic = epic.getId();
        Subtask subtask1 = new Subtask("Тестовая задача1", "Описание тестовой задачи1", currentDate.plusMinutes(10), duration1m);
        Subtask subtask2 = new Subtask("Тестовая задача2", "Описание тестовой задачи2", currentDate.plusMinutes(20), duration1m);
        saveManager.addSubtask(subtask1, epic);
        saveManager.addSubtask(subtask2, epic);
        int idSubtask1 = subtask1.getId();
        int idSubtask2 = subtask2.getId();

        FileBackedTaskManager readManager = FileBackedTaskManager.loadFromFile(tempFile[0]);
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
        final File[] tempFile = new File[1];
        assertDoesNotThrow(() -> {
            tempFile[0] = File.createTempFile("fm_", ".csv");
            assertTrue(Files.exists(tempFile[0].toPath()));
        }, "Ошибка создания файла");
        FileBackedTaskManager saveManager = new FileBackedTaskManager(tempFile[0]);

        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        saveManager.addTask(task);
        int idTask = task.getId();
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        saveManager.addEpic(epic);
        int idEpic = epic.getId();
        Subtask subtask1 = new Subtask("Тестовая задача1", "Описание тестовой задачи1", currentDate.plusMinutes(10), duration1m);
        Subtask subtask2 = new Subtask("Тестовая задача2", "Описание тестовой задачи2", currentDate.plusMinutes(20), duration1m);
        saveManager.addSubtask(subtask1, epic);
        saveManager.addSubtask(subtask2, epic);
        int idSubtask1 = subtask1.getId();
        int idSubtask2 = subtask2.getId();

        FileBackedTaskManager readManager = FileBackedTaskManager.loadFromFile(tempFile[0]);
        Assertions.assertTrue(readManager.getTask(idTask).equals(saveManager.getTask(idTask)), "Некорректно восстановилась task");
        Assertions.assertTrue(readManager.getEpic(idEpic).equals(saveManager.getEpic(idEpic)), "Некорректно восстановилась Epic");
        Assertions.assertTrue(readManager.getSubtask(idSubtask1).equals(saveManager.getSubtask(idSubtask1)), "Некорректно восстановилась Subtask1");
        Assertions.assertTrue(readManager.getSubtask(idSubtask2).equals(saveManager.getSubtask(idSubtask2)), "Некорректно восстановилась Subtask2");

    }

    //тест проверяет правильность генерирования строки описания задачи
    @Test
    void toStringForTaskGeneratedCorrectly() throws IOException {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 0);
        String formatDateTime = Task.getStringDateTime(dateTime);
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", dateTime, duration1m);
        taskManager.addTask(task);
        assertEquals(task.toString(), String.format("%s, TASK, Тестовая задача, NEW, Описание тестовой задачи, %s, 1,",
                task.getId(), formatDateTime), "Неверно работает генерация описания задачи для task");
    }

    //тест проверяет правильность восстановления задачи из строки
    @Test
    void correctnessTaskGenerationFromString() {
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 0);
        String strinfTaskOutFile = String.format("1, TASK, Тестовая задача, NEW, Описание тестовой задачи, %s, 1", Task.getStringDateTime(dateTime));
        Task task = new Task(strinfTaskOutFile);
        assertEquals(task.getId(), 1, "Неправильно восстановился id");
        assertEquals(task.getType(), TaskType.TASK, "Неправильно восстановился тип");
        assertEquals(task.getStatus(), Status.NEW, "Неправильно восстановился статус");
        assertEquals(task.getName(), "Тестовая задача", "Неправильно восстановилось наименование");
        assertEquals(task.getDescription(), "Описание тестовой задачи", "Неправильно восстановилось описание задачи");
        int durationInMinutes = (int) task.getDurationInMinutes();
        assertEquals(durationInMinutes, 1, "Неправильно восстановилась длительность");
        assertEquals(task.getStartTime(), dateTime, "Неправильно восстановилась дата начала");
    }

    //тест проверяет правильность определения типа задачи из строки
    @Test
    void determiningTaskTypeFromString() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        assertEquals(TaskType.getTypeFromString("TASK"), TaskType.TASK, "Неверно определяется из строки тип TASK");
        assertEquals(TaskType.getTypeFromString("EPIC"), TaskType.EPIC, "Неверно определяется из строки тип EPIC");
        assertEquals(TaskType.getTypeFromString("SUBTASK"), TaskType.SUBTASK, "Неверно определяется из строки тип SUBTASK");
    }

    //тест проверяет правильность определения статуса задачи из строки
    @Test
    void determiningTaskStatusFromString() {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        assertEquals(Status.getStatusFromString("NEW"), Status.NEW, "Неверно определяется из строки статус NEW");
        assertEquals(Status.getStatusFromString("IN_PROGRESS"), Status.IN_PROGRESS, "Неверно определяется из строки статус IN_PROGRESS");
        assertEquals(Status.getStatusFromString("DONE"), Status.DONE, "Неверно определяется из строки статус DONE");
    }

    //тест проверяет правильность генерирования строки заголовка для файла сохранения истории
    @Test
    void determiningCorrectFileHeader() {
        assertEquals(FileBackedTaskManager.getFileHeader(), "id, type, name, status, description, epic", "Неправильно формируется заголовок файла");
    }

    //тест проверяет правильность генерирования строки описания эпика
    @Test
    void toStringForEpicGeneratedCorrectly() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        assertEquals(epic.toString(), String.format("%s, EPIC, Тестовый эпик, NEW, Описание тестового эпика, null, 0,",
                epic.getId()), "Неверно работает генерация описания задачи для epic");
    }

    //тест проверяет правильность генерирования строки описания субтаска
    @Test
    void toStringForSubtaskGeneratedCorrectly() throws IOException {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 0);
        String formatDateTime = Task.getStringDateTime(dateTime);
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска", dateTime, duration1m);
        taskManager.addSubtask(subtask, epic);
        assertEquals(subtask.toString(), String.format("%s, SUBTASK, Тестовый субтаск, NEW, Описание тестового субтаска, %s, 1, %s",
                subtask.getId(), formatDateTime, epic.getId()), "Неверно работает генерация описания задачи для subtask");
    }

    //тест проверяет правильность восстановления эпика из строки
    @Test
    void correctnessEpickGenerationFromString() {
        String strinfTaskOutFile = "1, EPIC, Тестовый эпик, NEW, Описание тестового эпика, null, 0";
        Task epic = new Epic(strinfTaskOutFile);
        assertEquals(epic.getId(), 1, "Неправильно восстановился id");
        assertEquals(epic.getType(), TaskType.EPIC, "Неправильно восстановился тип");
        assertEquals(epic.getStatus(), Status.NEW, "Неправильно восстановился статус");
        assertEquals(epic.getName(), "Тестовый эпик", "Неправильно восстановилось наименование");
        assertEquals(epic.getDescription(), "Описание тестового эпика", "Неправильно восстановилось описание задачи");
    }

    //тест проверяет правильность восстановления субтаска из строки
    @Test
    void correctnessSubtaskGenerationFromString() throws IOException {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 0);
        String stringfTaskOutFile = String.format("2, SUBTASK, Тестовый субтаск, NEW, Описание тестового субтаска, %s, 1, 1", Task.getStringDateTime(dateTime));
        Subtask subtask = new Subtask(stringfTaskOutFile);
        assertEquals(subtask.getId(), 2, "Неправильно восстановился id субтаска");
        assertEquals(subtask.getIdEpic(), 1, "Неправильно восстановился id эпика");
        assertEquals(subtask.getType(), TaskType.SUBTASK, "Неправильно восстановился тип");
        assertEquals(subtask.getStatus(), Status.NEW, "Неправильно восстановился статус");
        assertEquals(subtask.getName(), "Тестовый субтаск", "Неправильно восстановилось наименование");
        assertEquals(subtask.getDescription(), "Описание тестового субтаска", "Неправильно восстановилось описание задачи");
        assertEquals(subtask.getStartTime(), dateTime, "Неправильновосстановилась дата старта");
        assertEquals(subtask.getDurationInMinutes(), 1, "Неправильно восстановилась длительность");

    }

    @Override
    @Test
    void cantAddEmptyDateInTasksOfPriority() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.cantAddEmptyDateInTasksOfPriority();
    }

    @Override
    @Test
    void priorityСalculatedСorrectly() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.priorityСalculatedСorrectly();
    }

    @Override
    @Test
    void taskAddedExceptionOccurred() {
        assertNotNull(taskManager, "Не удалось создать taskManager.");
        super.taskAddedExceptionOccurred();
    }

    @Override
    @Test
    void startDateEndDateDurationCalculatedCorrectly() {
        super.startDateEndDateDurationCalculatedCorrectly();
    }

    @Override
    @Test
    void updateTaskForPriorityListCorrectly() {
        super.updateTaskForPriorityListCorrectly();
    }

    @Override
    @Test
    void whenTasksDeletedAreRemovedFromPriorityList() {
        super.whenTasksDeletedAreRemovedFromPriorityList();
    }

}
