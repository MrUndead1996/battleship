package model;

public class Cell {
    private int xLine, yLine;
    private CellValues value;
    private Ship ship;

    public Cell(int xLine, int yLine) {
        this.xLine = xLine;
        this.yLine = yLine;
        value = CellValues.EMPTY;
    }

    public Cell(int xLine, int yLine, CellValues value, Ship ship) {
        this.xLine = xLine;
        this.yLine = yLine;
        this.value = value;
        this.ship = ship;
    }

    public CellValues getValue() {
        return value;
    }

    public void setValue(CellValues value) {
        this.value = value;
    }

    public int getxLine() {
        return xLine;
    }

    public int getyLine() {
        return yLine;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "value=" + value +
                '}';
    }
}
