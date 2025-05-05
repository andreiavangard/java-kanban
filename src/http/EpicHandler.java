package http;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import manager.NotFoundException;
import manager.TaskManager;
import task.Epic;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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

            //System.out.println(endpoint);

            switch (endpoint) {
                case GET_ITEMS:
                    handleGetEpics(httpExchange, body);
                    break;
                case GET_ITEM:
                    handleGetEpic(httpExchange, arrayPath[1]);
                    break;
                case DELETE_ITEM:
                    handleDeleteEpic(httpExchange, arrayPath[1]);
                case CREATE_ITEM:
                    handleCreateEpic(httpExchange, body);
                    break;
                case UPDATE_ITEM:
                    handleUpdateEpic(httpExchange, body);
                    break;
                default:
                    sendInternalError(httpExchange, "Неизвестный метод!", 404);
            }
        } catch (Exception e) {
            sendInternalError(httpExchange, "Ошибка сервера", 500);
        }
    }

    private void handleGetEpics(HttpExchange httpExchange, String body) throws IOException {
        List<Epic> tasks = taskManager.getEpics();
        sendText(httpExchange, HttpTaskServer.getGson().toJson(tasks));
    }

    private void handleGetEpic(HttpExchange httpExchange, String idAsString) throws IOException {
        int id = Integer.parseInt(idAsString);
        try {
            Epic epic = taskManager.getEpic(id);
            sendText(httpExchange, HttpTaskServer.getGson().toJson(epic));
        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", idAsString), 404);
        }
    }

    private void handleDeleteEpic(HttpExchange httpExchange, String idAsString) throws IOException {
        int id = Integer.parseInt(idAsString);
        try {
            Epic epic = taskManager.getEpic(id);
            taskManager.deleteEpic(epic);
            sendText(httpExchange, "Успешно удалили");

        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", idAsString), 404);
        }
    }

    private void handleCreateEpic(HttpExchange httpExchange, String body) throws IOException {
        Epic epic = HttpTaskServer.getGson().fromJson(body, Epic.class);
        if (!taskManager.isIntersectionsTaskInTasksOfPriority(epic)) {
            taskManager.addEpic(epic);
            //обратно вернем с установленным ид
            sendText(httpExchange, HttpTaskServer.getGson().toJson(epic));
        } else {
            sendHasInteractions(httpExchange, "Задача пересекается по времени с другой задачей");
        }
    }

    private void handleUpdateEpic(HttpExchange httpExchange, String body) throws IOException {
        Epic epicPrototype = HttpTaskServer.getGson().fromJson(body, Epic.class);
        try {
            Epic epic = taskManager.getEpic(epicPrototype.getId());
            epic.setId(epicPrototype.getId());
            epic.setName(epicPrototype.getName());
            epic.setDescription(epicPrototype.getDescription());
            epic.setStatus(epicPrototype.getStatus());
            epic.setDurationInMinutes(epicPrototype.getDurationInMinutes());
            epic.setStartTime(epicPrototype.getStartTime());
            taskManager.updateEpic(epic);
            sendText201(httpExchange, HttpTaskServer.getGson().toJson(epic));

        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", epicPrototype.getId()), 404);
        }

    }

}
