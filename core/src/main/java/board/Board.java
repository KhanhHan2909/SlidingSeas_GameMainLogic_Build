package board;

import gameplay.animation.CascadePhase;
import gameplay.animation.ColumnDrop;
import gameplay.animation.TileMovement;
import gameplay.animation.TileSpawn;

public class Board {

    private final int rows;
    private final int cols;

    private final Tile[][] tiles;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        tiles = new Tile[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                tiles[row][col] =
                    new Tile(row, col);
            }
        }
    }

    public Tile[][] getTiles() { return tiles; }

    public int getRows() { return rows; }

    public int getCols() { return cols; }

    public Tile getTile(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null;
        }

        return tiles[row][col];
    }

    public void setTile(Tile tile, int row, int col) {
        tiles[row][col] = tile;
    }

    public void setTileType(int row, int col, int type) {
        if (!isValidPosition(row, col)) {
            return;
        }

        tiles[row][col].setType(type);
    }

//    public TileType getTileType(int row, int col) {
//        return tiles[row][col].getType();
//    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void swapTiles(Tile tile1, Tile tile2) {

        if (tile1 == null || tile2 == null) {
            return;
        }

        int row1 = tile1.getRow();
        int col1 = tile1.getCol();

        int row2 = tile2.getRow();
        int col2 = tile2.getCol();

        if (!isValidPosition(row1, col1) ||
            !isValidPosition(row2, col2)) {
            return;
        }

        tiles[row1][col1] = tile2;
        tiles[row2][col2] = tile1;

        tile1.setPosition(row2, col2);
        tile2.setPosition(row1, col1);
    }

    public void applyCascadePhase(CascadePhase phase) {

        for (ColumnDrop drop : phase.columnDrops()) {

            int col = drop.column();

            Tile[] before = new Tile[rows];
            Tile[] after = new Tile[rows];

            // Chụp trạng thái đầu phase.
            for (int r = 0; r < rows; r++) {
                before[r] = tiles[r][col].copy();
                after[r] = before[r].copy();
            }

            // Xóa thuộc tính ở những vị trí tile rời đi.
            for (TileMovement move : drop.movements()) {
                after[move.fromRow()].clearExtras();
            }

            // Chuyển thuộc tính đến vị trí mới,
            // luôn đọc từ snapshot before.
            for (TileMovement move : drop.movements()) {
                after[move.toRow()].copyExtrasFrom(
                    before[move.fromRow()]
                );
            }

            // Gán thuộc tính của tile vừa sinh.
            TileSpawn spawn = drop.spawn();

            if (spawn != null) {
                after[spawn.row()].copyExtrasFrom(
                    spawn.tile()
                );
            }

            // Cập nhật kết quả phase.
            int[] types = drop.resultingTypes();

            for (int r = 0; r < rows; r++) {

                if (types[r] == 0) {
                    continue; // EMPTY không thay đổi
                }

                after[r].setType(types[r]);

                if (types[r] == 25) {
                    after[r].clearExtras();
                }

                after[r].setPosition(r, col);

                tiles[r][col] = after[r];
            }
        }
    }
}
