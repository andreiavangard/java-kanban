package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import adapters.LocalDateTimeAdapter;
import adapters.DurationTypeAdapter;

public class HttpTaskServer {

    private static final int PORT = 8080;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public void start() throws IOException {
        // настройка и запуск HTTP-сервера
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start(); // запускаем сервер
    }

    public void stop() {
        httpServer.stop(1);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter());

        return gsonBuilder.create();
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}


