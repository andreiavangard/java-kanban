package http;

import java.io.IOException;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import manager.TaskManager;
import task.Task;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
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
                case GET_PRIORITIZED:
                    handleGetPrioritized(httpExchange);
                    break;
                default:
                    sendInternalError(httpExchange, "Неизвестный метод!", 404);
            }
        } catch (Exception e) {
            sendInternalError(httpExchange, "Ошибка сервера", 500);
        }
    }

    private void handleGetPrioritized(HttpExchange httpExchange) throws IOException {
        Task[] tasks = taskManager.getPrioritizedTasks();
        sendText(httpExchange, HttpTaskServer.getGson().toJson(tasks));
    }

}
