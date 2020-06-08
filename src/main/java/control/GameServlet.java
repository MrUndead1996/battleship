package control;

import model.Game;
import model.Games;
import model.Player;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

@WebServlet("/game")
public class GameServlet extends HttpServlet {
    Queue<Player> players = new LinkedList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/view/game.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        String login = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("login"))
                login = cookie.getValue();
        }
        Player player = new Player(login);
        if (players.size() > 0) {
            Player second = players.poll();
            Game game = new Game(second, player);
            int gameID = Games.addGame(game);
            CheckAsyncServlet.addReadyUser(gameID,second.getName());
            resp.addCookie(new Cookie("gameID", String.valueOf(gameID)));
            resp.sendRedirect("/game");
        } else {
            players.offer(player);
            resp.getWriter().write("waitEnemy");
        }
    }
}
