package control;


import model.ActionThread;
import model.Message;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ServerEndpoint("/socket")
public class WebSocket {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    private static final Map<String, Session> peers = new HashMap<>();


    @OnOpen
    public void onOpen(Session peer) {
        String login = peer.getRequestParameterMap().get("login").get(0);
        peers.put(login, peer);

    }

    @OnMessage
    public void onMessage(String message) {
        JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
        Message messageObject = new Message(jsonObject);
        threadPool.execute(new ActionThread(messageObject));
    }

    @OnClose
    public void onClose(Session session) {
        String login = session.getRequestParameterMap().get("login").get(0);
        peers.remove(login);
    }

    public static void sendResponse(Message response) {
        String login = response.getLogin();
        Session session = peers.get(login);
        try {
            session.getBasicRemote().sendText(response.asJson().toString());
        } catch (IOException e) {
            sendResponse(response);
            e.printStackTrace();
        }
    }

    public static void close() {
        threadPool.shutdown();
    }
}
