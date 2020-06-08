package model;

import java.util.LinkedList;

public class Ship {
    String type;
    LinkedList<Cell> parts;

    public Ship(String type, LinkedList<Cell> parts) {
        this.type = type;
        this.parts = parts;
    }

    public String getType() {
        return type;
    }

    public LinkedList<Cell> getParts() {
        return parts;
    }
}
