package task;

import com.google.gson.Gson;
import http.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HttpTaskManagerTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    protected LocalDateTime currentDate = LocalDateTime.now();
    protected int duration1m = 1;

    @BeforeEach
    public void setUp() throws IOException {
        manager.clearTask();
        manager.clearEpics();
        manager.clearSubtask();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    //тест проверяет что можно создать задачу
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        assertEquals("Некорректное имя задачи", "Тестовая задача", tasksFromManager.get(0).getName());
    }

    //тест проверяет что можно обновить  задачу
    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        Task task1 = tasksFromManager.get(0);
        task1.setName("Новое имя");
        taskJson = gson.toJson(task1);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", "Новое имя", tasksFromManager.get(0).getName());
    }

    //тест проверяет что можно удалить  задачу
    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/tasks/%s", tasksFromManager.get(0).getId()));
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 0, tasksFromManager.size());
    }

    //тест проверяет что можно получить задачу
    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/tasks/%s", tasksFromManager.get(0).getId()));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task task1 = gson.fromJson(response.body(), Task.class);
        assertNotNull(task1, "Задача не восстановилась");
    }

    //тест проверяет что можно получить задачи, много задач
    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Тестовая задача1", "Описание тестовой задачи1", currentDate, duration1m);
        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task task2 = new Task("Тестовая задача1", "Описание тестовой задачи1", currentDate.plusMinutes(10), duration1m);
        String taskJson2 = gson.toJson(task2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась два задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 2, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue("Не вернулся список", !response.body().isEmpty());

    }

    //тест проверяет что можно создать эпик
    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        assertEquals("Некорректное имя задачи", "Тестовый эпик", tasksFromManager.get(0).getName());
    }

    //тест проверяет что можно обновить  задачу
    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        Epic epic1 = tasksFromManager.get(0);
        epic1.setName("Новое имя");
        epicJson = gson.toJson(epic1);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        tasksFromManager = manager.getEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", "Новое имя", tasksFromManager.get(0).getName());
    }

    //тест проверяет что можно удалить  задачу
    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/epics/%s", tasksFromManager.get(0).getId()));
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        tasksFromManager = manager.getEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 0, tasksFromManager.size());
    }

    //тест проверяет что можно получить задачу
    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/epics/%s", tasksFromManager.get(0).getId()));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Epic epic1 = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epic1, "Задача не восстановилась");
    }

    //тест проверяет что можно получить задачи, много задач
    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Тестовый эпик1", "Описание тестового эпика1");
        String epicJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic epic2 = new Epic("Тестовый эпик2", "Описание тестового эпика2");
        String epicJson2 = gson.toJson(epic2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась два задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 2, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue("Не вернулся список", !response.body().isEmpty());

    }

    //тест проверяет что можно создать субтаск
    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        int idEpic = tasksFromManager.get(0).getId();


        Subtask subtask = new Subtask("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s", idEpic));
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subtaskFromManager = manager.getSubtasks(tasksFromManager.get(0));

        assertNotNull(subtaskFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, subtaskFromManager.size());

        assertEquals("Некорректное имя задачи", "Тестовая задача", subtaskFromManager.get(0).getName());


    }

    //тест проверяет что можно обновить  задачу
    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicFromManager = manager.getEpics();

        assertNotNull(epicFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, epicFromManager.size());

        int idEpic = epicFromManager.get(0).getId();

        Subtask subtask = new Subtask("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s", idEpic));
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks(epicFromManager.get(0));
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        Subtask task1 = tasksFromManager.get(0);
        task1.setName("Новое имя");
        taskJson = gson.toJson(task1);

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s", idEpic));
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        tasksFromManager = manager.getSubtasks(epicFromManager.get(0));
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", "Новое имя", tasksFromManager.get(0).getName());

    }

    //тест проверяет что можно удалить  задачу
    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicFromManager = manager.getEpics();

        assertNotNull(epicFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, epicFromManager.size());

        int idEpic = epicFromManager.get(0).getId();

        Subtask subtask = new Subtask("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s", idEpic));
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks(epicFromManager.get(0));
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s/%s", tasksFromManager.get(0).getId(), idEpic));
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        tasksFromManager = manager.getSubtasks(epicFromManager.get(0));
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 0, tasksFromManager.size());
    }

    //тест проверяет что можно получить задачу
    @Test
    public void testGetSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicFromManager = manager.getEpics();

        assertNotNull(epicFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, epicFromManager.size());

        int idEpic = epicFromManager.get(0).getId();

        Subtask subtask = new Subtask("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(subtask);

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s", idEpic));
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks(epicFromManager.get(0));
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s/%s", tasksFromManager.get(0).getId(), idEpic));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Subtask task1 = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(task1, "Задача не восстановилась");

    }

    //тест проверяет что можно получить задачи, много задач
    @Test
    public void testGetSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Тестовый эпик", "Описание тестового эпика");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicFromManager = manager.getEpics();

        assertNotNull(epicFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, epicFromManager.size());

        int idEpic = epicFromManager.get(0).getId();

        Subtask subtask1 = new Subtask("Тестовая задача1", "Описание тестовой задачи1", currentDate, duration1m);
        String taskJson = gson.toJson(subtask1);

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s", idEpic));
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask task2 = new Subtask("Тестовая задача1", "Описание тестовой задачи1", currentDate.plusMinutes(10), duration1m);
        String taskJson2 = gson.toJson(task2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась два задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks(epicFromManager.get(0));
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 2, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/subtasks/%s", idEpic));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue("Не вернулся список", !response.body().isEmpty());


    }

    //тест проверяет формирование истории
    @Test
    public void testHistoryTasks() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/tasks/%s", tasksFromManager.get(0).getId()));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task task1 = gson.fromJson(response.body(), Task.class);
        assertNotNull(task1, "Задача не восстановилась");

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue("Не вернулся список", !response.body().isEmpty());
    }

    //тест проверяет формирование списка приоритетов
    @Test
    public void testPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", currentDate, duration1m);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());

        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/tasks/%s", tasksFromManager.get(0).getId()));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task task1 = gson.fromJson(response.body(), Task.class);
        assertNotNull(task1, "Задача не восстановилась");

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue("Не вернулся список", !response.body().isEmpty());
    }
}

