package model;

import java.util.LinkedList;

public class Game {
    private final Player firstPlayer, secondPlayer;
    private Cell[][] firstPlayerGameField;
    private Cell[][] secondPlayerGameField;
    private LinkedList<String> journal;

    public Game(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        firstPlayerGameField = initField();
        secondPlayerGameField = initField();
        journal = new LinkedList<>();
    }

    public void setShipOnPlayerGameField(String player, int[] positions) {
        Cell[][] gameField = getGameField(player);
        String typeOfShip = "";
        switch (positions.length) {
            case 2:
                typeOfShip = "boat";
                break;
            case 4:
                typeOfShip = "destroyer";
                break;
            case 6:
                typeOfShip = "cruiser";
                break;
            case 8:
                typeOfShip = "battleship";
                break;
        }
        LinkedList<Cell> parts = new LinkedList<>();
        for (int pos = 0; pos < positions.length; pos += 2) {
            int x = positions[pos];
            int y = positions[pos + 1];
            gameField[x][y].setValue(CellValues.SHIP);
            parts.add(gameField[x][y]);
        }
        for (Cell cell : parts) {
            gameField[cell.getxLine()][cell.getyLine()].setShip(new Ship(typeOfShip, parts));
        }
    }


    public CellValues shoot(int xPosition, int yPosition, String playerTarget) {
        Cell[][] gameField = getGameField(playerTarget);
        String player = playerTarget.equals(firstPlayer.getName()) ? secondPlayer.getName() : firstPlayer.getName();
        Cell target = gameField[xPosition][yPosition];
        switch (target.getValue()) {
            case EMPTY: {
                gameField[xPosition][yPosition].setValue(CellValues.MISS);
                addRecord(player, CellValues.MISS, xPosition, yPosition);
                return CellValues.MISS;
            }
            case SHIP: {
                LinkedList<Cell> parts = gameField[xPosition][yPosition].getShip().getParts();
                boolean isKilled = true;
                gameField[xPosition][yPosition].setValue(CellValues.HIT);
                LinkedList<Cell> partsTarget = gameField[xPosition][yPosition].getShip().getParts();
                for (Cell cell : partsTarget) {
                    if (cell.getValue() == CellValues.SHIP) {
                        isKilled = false;
                        break;
                    }
                }
                if (isKilled) {
                    for (Cell cell : gameField[xPosition][yPosition].getShip().getParts()) {
                        gameField[cell.getxLine()][cell.getyLine()].setValue(CellValues.KILL);
                    }
                    addRecord(player, CellValues.KILL, xPosition, yPosition);
                    return CellValues.KILL;
                } else {
                    gameField[xPosition][yPosition].setValue(CellValues.HIT);
                    addRecord(player, CellValues.HIT, xPosition, yPosition);
                    return CellValues.HIT;
                }
            }
        }
        return null;
    }

    public boolean hasWinner(Cell[][] gameField) {
        int killCounter = 0;
        for (Cell[] cells : gameField) {
            for (Cell cell : cells) {
                if (cell.getValue().equals(CellValues.KILL))
                    killCounter++;
            }
        }
        return killCounter == 20;
    }

    public void addRecord(String player, CellValues value, int xPosition, int yPosition) {
        if (journal.size() >= 10)
            journal.clear();

        String rec = player + ";" + value.toString() + ";" + xPosition + "-" + yPosition;
        journal.add(rec);
    }

    private Cell[][] initField() {
        Cell[][] field = new Cell[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field[i][j] = new Cell(i, j);
            }
        }
        return field;
    }


    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public Cell[][] getGameField(String player) {
        Cell[][] gameField;
        if (player.equals(firstPlayer.getName()))
            gameField = firstPlayerGameField;
        else gameField = secondPlayerGameField;
        return gameField;
    }

    public Cell[][] getFirstPlayerGameField() {
        return firstPlayerGameField;
    }

    public Cell[][] getSecondPlayerGameField() {
        return secondPlayerGameField;
    }

    public LinkedList<String> getJournal() {
        return journal;
    }
}
