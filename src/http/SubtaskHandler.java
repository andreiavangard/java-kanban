package http;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import manager.NotFoundException;
import manager.TaskManager;
import task.Epic;
import task.Subtask;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
                case GET_SUBITEMS:
                    handleGetSubtasks(httpExchange, arrayPath[1]);
                    break;
                case GET_ITEM:
                    handleGetSubtask(httpExchange, arrayPath[1]);
                    break;
                case DELETE_ITEM:
                    handleDeleteSubtask(httpExchange, arrayPath[1], arrayPath[2]);
                case CREATE_ITEM:
                    handleCreateSubtask(httpExchange, body, arrayPath[1]);
                    break;
                case UPDATE_ITEM:
                    handleUpdateSubtask(httpExchange, body, arrayPath[1]);
                    break;
                default:
                    sendInternalError(httpExchange, "Неизвестный метод!", 404);
            }
        } catch (Exception e) {
            sendInternalError(httpExchange, "Ошибка сервера", 500);
        }
    }

    private void handleGetSubtasks(HttpExchange httpExchange, String idAsStringEpic) throws IOException {
        int idEpic = Integer.parseInt(idAsStringEpic);
        try {
            Epic epic = taskManager.getEpic(idEpic);
            List<Subtask> subtasks = taskManager.getSubtasks(epic);
            sendText(httpExchange, HttpTaskServer.getGson().toJson(subtasks));
        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Эпик с ид %s не найдено", idEpic), 404);
        }
    }

    private void handleGetSubtask(HttpExchange httpExchange, String idAsString) throws IOException {
        int id = Integer.parseInt(idAsString);
        try {
            Subtask subtask = taskManager.getSubtask(id);
            sendText(httpExchange, HttpTaskServer.getGson().toJson(subtask));
        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", idAsString), 404);
        }
    }

    private void handleDeleteSubtask(HttpExchange httpExchange, String idAsString, String idAsStringEpic) throws IOException {
        int id = Integer.parseInt(idAsString);
        int idEpic = Integer.parseInt(idAsStringEpic);
        try {
            Epic epic = taskManager.getEpic(idEpic);
            Subtask subtask = taskManager.getSubtask(id);
            taskManager.deleteSubtask(subtask, epic);
            sendText(httpExchange, "Успешно удалили");

        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", idAsString), 404);
        }
    }

    private void handleCreateSubtask(HttpExchange httpExchange, String body, String idAsStringEpic) throws IOException {
        int idEpic = Integer.parseInt(idAsStringEpic);

        try {
            Epic epic = taskManager.getEpic(idEpic);
            Subtask subtask = HttpTaskServer.getGson().fromJson(body, Subtask.class);
            if (!taskManager.isIntersectionsTaskInTasksOfPriority(subtask)) {
                taskManager.addSubtask(subtask, epic);
                //обратно вернем с установленным ид
                sendText(httpExchange, HttpTaskServer.getGson().toJson(subtask));
            } else {
                sendHasInteractions(httpExchange, "Задача пересекается по времени с другой задачей");
            }
        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Эпика с ид %s не найдено", idAsStringEpic), 404);
        }
    }

    private void handleUpdateSubtask(HttpExchange httpExchange, String body, String idAsStringEpic) throws IOException {
        int idEpic = Integer.parseInt(idAsStringEpic);
        Subtask subtaskPrototype = HttpTaskServer.getGson().fromJson(body, Subtask.class);
        try {
            Epic epic = taskManager.getEpic(idEpic);
            Subtask subtask = taskManager.getSubtask(subtaskPrototype.getId());
            subtask.setId(subtaskPrototype.getId());
            subtask.setName(subtaskPrototype.getName());
            subtask.setDescription(subtaskPrototype.getDescription());
            subtask.setStatus(subtaskPrototype.getStatus());
            subtask.setDurationInMinutes(subtaskPrototype.getDurationInMinutes());
            subtask.setStartTime(subtaskPrototype.getStartTime());
            subtask.setIdEpic(subtaskPrototype.getIdEpic());
            taskManager.updateSubtask(subtask, epic);
            sendText201(httpExchange, HttpTaskServer.getGson().toJson(subtask));

        } catch (NotFoundException e) {
            sendInternalError(httpExchange, String.format("Задачи с ид %s не найдено", subtaskPrototype.getId()), 404);
        }

    }

}
