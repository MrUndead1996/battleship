package control;

import model.Games;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/check")
public class CheckAsyncServlet extends HttpServlet {

    static Map<Integer,String> loginsReadyToPlay = new HashMap<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = "";
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("login"))
                login = cookie.getValue();
        }
        if (loginsReadyToPlay.containsValue(login)) {
            int gameID = 0;
            for (int id: loginsReadyToPlay.keySet()){
                if (loginsReadyToPlay.get(id).equals(login))
                    gameID = id;
            }
            resp.addCookie(new Cookie("gameID", String.valueOf(gameID)));
            resp.sendRedirect("/game");
            loginsReadyToPlay.remove(gameID);
        } else resp.getWriter().write("waitEnemy");
    }

    public static void addReadyUser(int gameID,String login) {
        loginsReadyToPlay.put(gameID,login);

    }
}
