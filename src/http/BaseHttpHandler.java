package http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected Endpoint endpoint;

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendText201(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain");
        httpExchange.sendResponseHeaders(201, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    public void sendNotFound() throws IOException {

    }

    public void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain");
        httpExchange.sendResponseHeaders(406, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    public void sendInternalError(HttpExchange httpExchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(HttpTaskServer.DEFAULT_CHARSET));
        }
        httpExchange.close();
    }

    protected String[] getArrayPath(String path) {
        String[] arrayPath;
        String[] pathSplit = path.split("/");
        if (pathSplit.length < 2) {
            return new String[0];
        }
        arrayPath = new String[pathSplit.length - 1];
        for (int i = 1; i < pathSplit.length; i++) {
            arrayPath[i - 1] = pathSplit[i];
        }
        return arrayPath;
    }

    protected Endpoint getEndpoint(String[] arrayPath, String method, String body) {
        switch (method) {
            case "GET":
                if (arrayPath[0].equals("prioritized")) {
                    return Endpoint.GET_PRIORITIZED;
                } else if (arrayPath[0].equals("history")) {
                    return Endpoint.GET_HISTORY;
                } else if (arrayPath.length == 2 && arrayPath[0].equals("subtasks")) {
                    return Endpoint.GET_SUBITEMS;
                } else if (arrayPath.length == 2) {
                    return Endpoint.GET_ITEM;
                } else if (arrayPath.length == 3) {
                    return Endpoint.GET_ITEM;
                } else if (arrayPath.length == 1) {
                    return Endpoint.GET_ITEMS;
                } else {
                    return Endpoint.UNKNOWN;
                }
            case "POST":
                if (!body.isEmpty() && bodyIsValidJson(body) && !inBodyIsId(body)) {
                    return Endpoint.CREATE_ITEM;
                } else if (!body.isEmpty() && bodyIsValidJson(body) && inBodyIsId(body)) {
                    return Endpoint.UPDATE_ITEM;
                } else {
                    return Endpoint.UNKNOWN;
                }
            case "DELETE":
                if (arrayPath.length == 2 || arrayPath.length == 3) {
                    return Endpoint.DELETE_ITEM;
                } else {
                    return Endpoint.UNKNOWN;
                }

        }
        return Endpoint.UNKNOWN;
    }

    private boolean bodyIsValidJson(String body) {
        try {
            JsonParser.parseString(body);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    private boolean inBodyIsId(String body) {
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();
        return id != 0;
    }

}
