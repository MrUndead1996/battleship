package model;

import javax.json.*;

public class Message {
    private int gameID;
    private String login, action,body;
    private int[] xLine, yLine;
    private JsonObject object;

    public Message(int gameID, String login, String action, int[] xLine, int[] yLine) {
        this.gameID = gameID;
        this.login = login;
        this.action = action;
        this.xLine = xLine;
        this.yLine = yLine;
    }

    public Message(int gameID, String login, String action, String body) {
        this.gameID = gameID;
        this.login = login;
        this.action = action;
        this.body = body;
    }

    public Message(JsonObject object) {
        gameID = Integer.parseInt(String.valueOf(object.get("gameID")).replace("\"",""));
        login = object.getString("login");
        action = object.getString("action");
        xLine = new int[object.get("xLine").asJsonArray().toArray().length];
        for (int i = 0;i < object.get("xLine").asJsonArray().toArray().length;i++){
            xLine[i] = Integer.parseInt(object.get("xLine").asJsonArray().toArray()[i].toString().replace("\"",""));}
        yLine = new int[object.get("yLine").asJsonArray().toArray().length];
        for (int i = 0;i < object.get("yLine").asJsonArray().toArray().length;i++)
            yLine[i] = Integer.parseInt(object.get("yLine").asJsonArray().toArray()[i].toString().replace("\"",""));
        this.object = object;
    }

    public JsonObject asJson(){
        JsonArray xLineJson;
        JsonArray yLineJson;
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("gameID",gameID);
        builder.add("login",login);
        builder.add("action",action);
        if (body == null){
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (int value : xLine) arrayBuilder.add(value);
            xLineJson = arrayBuilder.build();
            arrayBuilder = Json.createArrayBuilder();
            for (int value : yLine) arrayBuilder.add(value);
            yLineJson = arrayBuilder.build();
            builder.add("xLine", xLineJson);
            builder.add("yLine",yLineJson);
        }
        else {
            builder.add("body",body);
        }
        return builder.build();
    }

    public int getGameID() {
        return gameID;
    }

    public String getLogin() {
        return login;
    }

    public String getAction() {
        return action;
    }

    public int[] getxLine() {
        return xLine;
    }

    public int[] getyLine() {
        return yLine;
    }
}
