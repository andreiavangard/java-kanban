package task;

import manager.FileBackedTaskManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicTestFileBacked {

    //тест проверяет правильность генерирования строки описания эпика
    @Test
    void toStringForEpicGeneratedCorrectly() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        assertEquals(epic.toString(), String.format("%s, EPIC, Тестовый эпик, NEW, Описание тестового эпика,", epic.getId()), "Неверно работает генерация описания задачи для epic");
    }

    //тест проверяет правильность генерирования строки описания субтаска
    @Test
    void toStringForSubtaskGeneratedCorrectly() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска");
        taskManager.addSubtask(subtask, epic);
        assertEquals(subtask.toString(), String.format("%s, SUBTASK, Тестовый субтаск, NEW, Описание тестового субтаска, %s", subtask.getId(), epic.getId()), "Неверно работает генерация описания задачи для subtask");
    }

    //тест проверяет правильность восстановления эпика из строки
    @Test
    void correctnessEpickGenerationFromString() {
        String strinfTaskOutFile = "1, EPIC, Тестовый эпик, NEW, Описание тестового эпика,";
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
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        String strinfTaskOutFile = "2, SUBTASK, Тестовый субтаск, NEW, Описание тестового субтаска, 1";
        Subtask subtask = new Subtask(strinfTaskOutFile);
        assertEquals(subtask.getId(), 2, "Неправильно восстановился id субтаска");
        assertEquals(subtask.getIdEpic(), 1, "Неправильно восстановился id эпика");
        assertEquals(subtask.getType(), TaskType.SUBTASK, "Неправильно восстановился тип");
        assertEquals(subtask.getStatus(), Status.NEW, "Неправильно восстановился статус");
        assertEquals(subtask.getName(), "Тестовый субтаск", "Неправильно восстановилось наименование");
        assertEquals(subtask.getDescription(), "Описание тестового субтаска", "Неправильно восстановилось описание задачи");
    }

    //тест проверяет, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void addNewEpicSubtask() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
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
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description");
        int subtaskId = taskManager.addSubtask(subtask, epic);
        Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Субтаска не найдена.");
        assertEquals(subtask, savedSubtask, "Субтаски не совпадают.");
        List<Subtask> subtasks = taskManager.getSubtasks(epic);

        assertNotNull(subtasks, "Субтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество Субтаск.");
        assertEquals(subtask, subtasks.get(0), "Субтаски не совпадают.");
    }

    //тест проверяет, что экземпляры с одинковым id равны
    @Test
    void instancesTaskEqualIfTheirIdIsEqual() {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        Epic cloneEpic = epic.clone();
        cloneEpic.setName(cloneEpic.getName() + "*");
        cloneEpic.setDescription(cloneEpic.getDescription() + "*");
        assertEquals(epic, cloneEpic, "Эпики c одинаковым id не совпадают.");

        Subtask subtask = new Subtask("Тестовая задача", "Описание тестовой задачи");
        Subtask cloneSubtask = subtask.clone();
        cloneSubtask.setName(cloneSubtask.getName() + "*");
        cloneSubtask.setDescription(cloneSubtask.getDescription() + "*");
        assertEquals(subtask, cloneSubtask, "Задачи c одинаковым id не совпадают.");
    }

    //тест проверяет неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void checkTheImmutabilityOfTaskWhenAddingToManager() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        int epicId = taskManager.addEpic(epic);
        Epic savedEpic = taskManager.getEpic(epicId);
        assertEquals(epic.getId(), savedEpic.getId(), "Не совпадает ид");
        assertEquals(epic.getName(), savedEpic.getName(), "Не совпадает наименование");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Не совпадает описание");

        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска");
        int SubtaskId = taskManager.addSubtask(subtask, epic);
        Subtask savedSubtask = taskManager.getSubtask(SubtaskId);
        assertEquals(subtask.getId(), savedSubtask.getId(), "Не совпадает ид");
        assertEquals(subtask.getName(), savedSubtask.getName(), "Не совпадает наименование");
        assertEquals(subtask.getDescription(), savedSubtask.getDescription(), "Не совпадает описание");
    }

    //тест проверяет, что для новой задачи правильно устанавливается статус
    @Test
    void theStatusForNewOneIsSetCorrectly() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        assertEquals(epic.getStatus(), Status.NEW, "Эпик при создании не установила статус новый.");

        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска");
        taskManager.addSubtask(subtask, epic);
        assertEquals(subtask.getStatus(), Status.NEW, "Эпик при создании не установила статус новый.");
    }

    //тест проверяет что у субтасок правильно заполняется ид
    @Test
    void ownerIDCheck() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Тестовый субтаск1", "Описание тестового субтаска1");
        taskManager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Тестовый субтаск2", "Описание тестового субтаска2");
        taskManager.addSubtask(subtask2, epic);

        HashMap<Integer, Subtask> subtasks = epic.getSubTasks();
        for (Subtask subtask : subtasks.values()) {
            assertEquals(subtask.getIdEpic(), epic.getId(), "У субтаска ид не совпадает с ид эпика.");
        }
    }

    //тест проверяет правильный расчет статуса
    @Test
    void checkingEpicStatusSetting() throws IOException {
        File tempFile = File.createTempFile("fm_", "csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Тестовый субтаск1", "Описание тестового субтаска1");
        taskManager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Тестовый субтаск2", "Описание тестового субтаска2");
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
}
