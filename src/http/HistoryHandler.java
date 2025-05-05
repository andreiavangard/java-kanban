package http;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import manager.TaskManager;
import task.Task;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
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
                case GET_HISTORY:
                    handleGetHistory(httpExchange);
                    break;
                default:
                    sendInternalError(httpExchange, "Неизвестный метод!", 404);
            }
        } catch (Exception e) {
            sendInternalError(httpExchange, "Ошибка сервера", 500);
        }
    }

    private void handleGetHistory(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = taskManager.getHistory();
        sendText(httpExchange, HttpTaskServer.getGson().toJson(tasks));
    }


}
