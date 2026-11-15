package board;

import dev.morphia.annotations.Embedded;

@Embedded
public class Tile {
    private int row;
    private int col;

    // type = 0: empty tile/ wall
    // type = 1: sea 1
    // type = 2: sea 2
    // type = 3: sea 3
    // type = 4: sea 4
    // type = 5: land 1
    // type = 6: land 2
    // type = 7: land 3
    // type = 8: land 4 (To Shelter)
    // type = 9: iceberg
    // type = 10: land 4 (Against Pirate)
    // type = 11: coral 1 (biggest)
    // type = 12: coral 2
    // type = 13: coral 3 (smallest)
    // type = 14: transforming
    // type = 15: to-rescue-boat one
    // type = 16: pirate 1 (full health)
    // type = 17: pirate 2
    // type = 18: pirate 3
    // type = 19: pirate 4
    // type = 20: pirate 5 (most damaged)
    // type = 21: precious item 1 (fully sealed)
    // type = 22: precious item 2
    // type = 23: precious item 3
    // type = 24: precious item 4 (most opened)
    // type = 25: pending tile
    private int type;

    private int survivors;
    private boolean hasRaft, hasShelter;
    private int mineLevel;

    public Tile() {}

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Tile(int row, int col, int type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public Tile(int survivors, boolean hasRaft, boolean hasShelter, int mineLevel) {
        this.survivors = survivors;
        this.hasRaft = hasRaft;
        this.hasShelter = hasShelter;
        this.mineLevel = mineLevel;
    }

    public int getRow() {
        return row;
    }

    public int getCol() { return col; }

    public int getType() { return type; }

    public void setType(int type) {
        this.type = type;
    }

    public void levelUp() { this.type++; }

    public void toIceberg() { this.type = 9; }

    public boolean isEmpty() {
        return type == 0;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setSurvivors(int survivors) { this.survivors = survivors; }

    public int getSurvivors() {
        return survivors;
    }

    public boolean hasRaft() {
        return hasRaft;
    }

    public boolean hasShelter() {
        return hasShelter;
    }

    public int getMineLevel() {
        return mineLevel;
    }

    public void setMineLevel(int mineLevel) {
        this.mineLevel = mineLevel;
    }

    public void copyExtrasFrom(Tile other) {
        this.survivors = other.survivors;
        this.hasRaft = other.hasRaft;
        this.hasShelter = other.hasShelter;
        this.mineLevel = other.mineLevel;
    }

    public void clearExtras() {
        this.survivors = 0;
        this.hasRaft = false;
        this.hasShelter = false;
        this.mineLevel = 0;
    }

    public Tile copy() {
        Tile result = new Tile(row, col, type);
        result.copyExtrasFrom(this);
        return result;
    }

    public void copyAllValueFrom(Tile other) {
        this.row = other.row;
        this.col = other.col;
        this.type = other.type;
        this.copyExtrasFrom(other);
    }
}
