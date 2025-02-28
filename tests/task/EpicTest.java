package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    static TaskManager taskManager;
    @BeforeEach
    void beforeAll(){
        taskManager = Managers.getDefault();
    }

    //2) проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
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
    void instancesTaskEqualIfTheirIdIsEqual(){
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        Epic cloneEpic = epic.clone();
        cloneEpic.setName(cloneEpic.getName()+"*");
        cloneEpic.setDescription(cloneEpic.getDescription()+"*");
        assertEquals(epic, cloneEpic, "Эпики c одинаковым id не совпадают.");

        Subtask subtask = new Subtask("Тестовая задача", "Описание тестовой задачи");
        Subtask cloneSubtask = subtask.clone();
        cloneSubtask.setName(cloneSubtask.getName()+"*");
        cloneSubtask.setDescription(cloneSubtask.getDescription()+"*");
        assertEquals(subtask, cloneSubtask, "Задачи c одинаковым id не совпадают.");
    }

    //4) создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    //проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void checkTheImmutabilityOfTaskWhenAddingToManager(){
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

    //5) убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    //задача, добавляемая в HistoryManager, сохраняет предыдущую версию задачи и её данных.
    @Test
    void taskAdedToTheHistoryManagerSavesPreviousVersionOfTaskAndData(){
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        int epicId = taskManager.addEpic(epic);
        Epic cloneEpic = epic.clone();
        cloneEpic.setStatus(Status.NEW);
        taskManager.getEpic(epicId);
        List<Task> hystoryTask = taskManager.getHistory();
        assertTrue(hystoryTask.size()>0,"В истории нет задач");
        Task savedHistoryTask = hystoryTask.get(hystoryTask.size()-1);
        epic.setName(epic.getName()+"*");
        epic.setDescription(epic.getDescription()+"*");
        assertEquals(cloneEpic, savedHistoryTask, "Эпик из истории не сохранила версию.");

        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска");
        int subtaskId = taskManager.addSubtask(subtask ,epic);
        Subtask cloneSubtask = subtask.clone();
        cloneSubtask.setStatus(Status.NEW);
        taskManager.getSubtask(subtaskId);
        hystoryTask = taskManager.getHistory();
        assertTrue(hystoryTask.size()>0,"В истории нет задач");
        savedHistoryTask = hystoryTask.get(hystoryTask.size()-1);
        subtask.setName(subtask.getName()+"*");
        subtask.setDescription(subtask.getDescription()+"*");
        assertEquals(cloneSubtask, savedHistoryTask, "Субтаск из истории не сохранила версию.");
    }

    //для новой задачи правильно устанавливается статус
    @Test
    void theStatusForNewOneIsSetCorrectly(){
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        assertEquals(epic.getStatus(), Status.NEW, "Эпик при создании не установила статус новый.");

        Subtask subtask = new Subtask("Тестовый субтаск", "Описание тестового субтаска");
        taskManager.addSubtask(subtask, epic);
        assertEquals(subtask.getStatus(), Status.NEW, "Эпик при создании не установила статус новый.");
    }

    //тест проверяет что у субтасок правильно заполняется ид
    @Test
    void ownerIDCheck(){
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Тестовый субтаск1", "Описание тестового субтаска1");
        taskManager.addSubtask(subtask1, epic);
        Subtask subtask2 = new Subtask("Тестовый субтаск2", "Описание тестового субтаска2");
        taskManager.addSubtask(subtask2, epic);

        HashMap<Integer, Subtask> subtasks = epic.getSubTasks();
        for (Subtask subtask : subtasks.values()){
            assertEquals(subtask.getIdEpic(), epic.getId(), "У субтаска ид не совпадает с ид эпика.");
        }
    }

    //тест проверяет правильный расчет статуса
    @Test
    void checkingEpicStatusSetting(){
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