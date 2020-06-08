package model;

import control.WebSocket;

import java.util.LinkedList;

public class ActionThread implements Runnable {
    String enemyField = "enemyField";
    String mainField = "mainField";
    String enemyLogin;
    Message request;
    String action;
    int gameID;
    String login;
    int[] xLine;
    int[] yLine;
    Game game;
    Message response;

    public ActionThread(Message request) {
        this.request = request;
    }

    @Override
    public void run() {
        action = request.getAction();
        gameID = request.getGameID();
        login = request.getLogin();
        xLine = request.getxLine();
        yLine = request.getyLine();
        game = Games.getGame(request.getGameID());
        enemyLogin = login.equals(game.getFirstPlayer().getName()) ? game.getSecondPlayer().getName() : game.getFirstPlayer().getName();
        switch (action) {
            case "giveUp": {
                response = new Message(gameID,enemyLogin,"giveUp","null");
                WebSocket.sendResponse(response);
                break;
            }
            case "setShips": {
                int[] position = new int[xLine.length + yLine.length];
                int counter = 0;
                for (int x = 0; x < xLine.length; x++) {
                    position[counter] = xLine[x];
                    position[counter + 1] = yLine[x];
                    counter += 2;
                }
                game.setShipOnPlayerGameField(login, position);
                break;
            }
            case "shoot": {
                int x = xLine[0];
                int y = yLine[0];
                CellValues values = game.shoot(x, y, enemyLogin);
                switch (values) {
                    case HIT: {
                        WebSocket.sendResponse(getYouMoveMessage(login));
                        WebSocket.sendResponse(getWaitMessage(enemyLogin));
                        WebSocket.sendResponse(getJournalMessage(login));
                        WebSocket.sendResponse(getJournalMessage(enemyLogin));
                        response = getCellValueMessage(login, enemyField, CellValues.HIT, x, y);
                        WebSocket.sendResponse(response);
                        response = getCellValueMessage(enemyLogin, mainField, CellValues.HIT, x, y);
                        WebSocket.sendResponse(response);
                        break;
                    }
                    case MISS: {
                        WebSocket.sendResponse(getYouMoveMessage(enemyLogin));
                        WebSocket.sendResponse(getWaitMessage(login));
                        WebSocket.sendResponse(getJournalMessage(login));
                        WebSocket.sendResponse(getJournalMessage(enemyLogin));
                        response = getCellValueMessage(login, enemyField, CellValues.MISS, x, y);
                        WebSocket.sendResponse(response);
                        response = getCellValueMessage(enemyLogin, mainField, CellValues.MISS, x, y);
                        WebSocket.sendResponse(response);
                        break;
                    }
                    case KILL: {
                        for (Cell cell : game.getGameField(enemyLogin)[x][y].getShip().getParts()) {
                            if (game.hasWinner(game.getGameField(enemyLogin))) {
                                response = new Message(gameID, login, "win", "null");
                                WebSocket.sendResponse(response);
                                response = new Message(gameID, enemyLogin, "loose", "null");
                                WebSocket.sendResponse(response);
                            }
                            response = getCellValueMessage(login, enemyField, CellValues.KILL, cell.getxLine(), cell.getyLine());
                            WebSocket.sendResponse(response);
                            WebSocket.sendResponse(getWaitMessage(enemyLogin));
                            response = getCellValueMessage(enemyLogin, mainField, CellValues.KILL, cell.getxLine(), cell.getyLine());
                            WebSocket.sendResponse(response);
                            WebSocket.sendResponse(getYouMoveMessage(login));
                        }
                        WebSocket.sendResponse(getJournalMessage(login));
                        WebSocket.sendResponse(getJournalMessage(enemyLogin));
                        break;
                    }
                }
                break;
            }

            case "getGameFields": {
                Cell[][] gameField;
                LinkedList<String> journal = game.getJournal();
                for (String s : journal) {
                    response = new Message(gameID, login, "journal", s);
                    WebSocket.sendResponse(response);
                }
                gameField  = game.getGameField(login);
                for (Cell[] cells : gameField) {
                    for (Cell cell : cells) {
                        response = getCellValueMessage(login, mainField, cell.getValue(), cell.getxLine(), cell.getyLine());
                        WebSocket.sendResponse(response);
                    }
                }
                gameField = game.getGameField(enemyLogin);
                for (Cell[] cells : gameField) {
                    for (Cell cell : cells) {
                        switch (cell.getValue()) {
                            case KILL:
                            case HIT:
                            case MISS:
                                response = getCellValueMessage(login, enemyField, cell.getValue(), cell.getxLine(), cell.getyLine());
                                WebSocket.sendResponse(response);
                                break;
                        }
                    }
                }
                break;
            }
        }
    }

    private Message getJournalMessage(String login) {
        String lastRecord = game.getJournal().getLast();
        return new Message(gameID, login, "journal", lastRecord);
    }

    private Message getCellValueMessage(String login, String field, CellValues value, int x, int y) {
        String messageBody = field + ";" + value + ";" + x + "-" + y;
        return new Message(gameID, login, "shoot", messageBody);
    }

    private Message getWaitMessage(String login) {
        return new Message(gameID, login, "wait", "null");
    }

    private Message getYouMoveMessage(String login) {
        return new Message(gameID, login, "youMove", "null");
    }
}
