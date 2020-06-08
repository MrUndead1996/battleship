package model;

import java.util.HashMap;
import java.util.Map;

public class Games {
    private static Map<Integer, Game> games = new HashMap<>();

    private static int getID() {
        int result = 0;
        for (int key : games.keySet()) {
            result++;
        }
        return result;
    }

    public static int addGame(Game game) {
        int id = getID();
        games.put(id, game);
        return id;
    }

    public static Game getGame(int id){
       return games.get(id);
    }
}
