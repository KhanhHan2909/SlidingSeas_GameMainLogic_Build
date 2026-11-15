package gameplay.animation;

public record TileMovement(int fromRow, int toRow, int col, int type, int firstEmptyRow, int lastEmptyRow) {
    public boolean passesThroughEmpty() {
        return firstEmptyRow >= 0;
    }
}
