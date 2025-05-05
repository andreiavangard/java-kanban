package http;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import task.Task;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();
            String body = new String(httpExchange.getRequestBody().readAllBytes(), HttpTaskServer.DEFAULT_CHARSET);
            String[] arrayPath = getArrayPath(path);
            Endpoint endpoint = getEndpoint(arrayPath, method, body);

            switch (endpoint) {
                case GET_ITEMS:
                    handleGetTasks(httpExchange, body);
                    break;
                case GET_ITEM:
                    handleGetTask(httpExchange, arrayPath[1]);
                    break;
                case DELETE_ITEM:
                    handleDeleteTask(httpExchange, arrayPath[1]);
                case CREATE_ITEM:
                    handleCreateTask(httpExchange, body);
                    break;
                case UPDATE_ITEM:
                    handleUpdateTask(httpExchange, body);
                    break;
                default:
                    sendInternalError(httpExchange, "Неизвестный метод!", 404);
            }
        } catch (Exception e) {
            sendInternalError(httpExchange, "Ошибка сервера", 500);
        }

    }

    private void handleGetTasks(HttpExchange httpExchange, String body) throws IOException {
        List<Task> tasks = taskManager.getTasks();
        sendText(httpExchange, HttpTaskServer.getGson().toJson(tasks));
    }

    private void handleGetTask(HttpExchange httpExchange, String idAsString) throws IOException {
        int id = Integer.parseInt(idAsString);
        try {
            Task task = taskManager.getTask(id);
            sendText(httpExchange, HttpTaskServer.getGson().toJson(task));
        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", idAsString), 404);
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange, String idAsString) throws IOException {
        int id = Integer.parseInt(idAsString);
        try {
            Task task = taskManager.getTask(id);
            taskManager.deleteTask(task);
            sendText(httpExchange, "Успешно удалили");

        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", idAsString), 404);
        }
    }

    private void handleCreateTask(HttpExchange httpExchange, String body) throws IOException {
        Task task = HttpTaskServer.getGson().fromJson(body, Task.class);
        if (!taskManager.isIntersectionsTaskInTasksOfPriority(task)) {
            taskManager.addTask(task);
            //обратно вернем с установленным ид
            sendText(httpExchange, HttpTaskServer.getGson().toJson(task));
        } else {
            sendHasInteractions(httpExchange, "Задача пересекается по времени с другой задачей");
        }
    }

    private void handleUpdateTask(HttpExchange httpExchange, String body) throws IOException {
        Task taskPrototype = HttpTaskServer.getGson().fromJson(body, Task.class);
        try {
            Task task = taskManager.getTask(taskPrototype.getId());
            task.setId(taskPrototype.getId());
            task.setName(taskPrototype.getName());
            task.setDescription(taskPrototype.getDescription());
            task.setStatus(taskPrototype.getStatus());
            task.setDurationInMinutes(taskPrototype.getDurationInMinutes());
            task.setStartTime(taskPrototype.getStartTime());
            taskManager.updateTask(task);
            sendText201(httpExchange, HttpTaskServer.getGson().toJson(task));

        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", taskPrototype.getId()), 404);
        }

    }

}
