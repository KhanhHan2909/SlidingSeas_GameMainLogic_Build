package board;

import gameplay.LevelData;
import gameplay.MergeSystem;
import input.SwipeDirection;
import managers.InputManager;

import java.util.ArrayList;
import java.util.List;

public class BoardManager {
    private Board board;

    private final String mode;
//    private final InputManager inputManager;

    private final int rows;
    private final int cols;
    private final List<Tile> fixedPositions = new ArrayList<>();
    private final int[] availableTypes;

    private final float tileSize;

    private final float boardX;
    private final float boardY;

//    private final MergeSystem mergeSystem;

    // Kích thước thế giới game
    private static final float WORLD_WIDTH = 1920f;
    private static final float WORLD_HEIGHT = 1080f;

    // Giới hạn khu vực dành cho bàn cờ
    private static final float MAX_BOARD_WIDTH = 700f;
    private static final float MAX_BOARD_HEIGHT = 700f;

    // Kích thước tối đa của một ô
    private static final float MAX_TILE_SIZE = 120f;


    public BoardManager(String mode, LevelData level) {
        this.mode = mode;
        this.rows = level.getRows();
        this.cols = level.getCols();
        this.availableTypes = level.getAvailableTypes();
//        this.inputManager = inputManager;

        // Tự động điều chỉnh kích thước ô
        tileSize = Math.min(
            MAX_TILE_SIZE,
            Math.min(
                MAX_BOARD_WIDTH / cols,
                MAX_BOARD_HEIGHT / rows
            )
        );

        // Căn giữa bàn cờ
        boardX = (WORLD_WIDTH - getBoardWidth()) / 2f;
        boardY = (WORLD_HEIGHT - getBoardHeight()) / 2f;

        board = BoardGenerator.generate(rows, cols, level.getFixedPositions(), level.getAvailableTypes());
    }

    public Board getBoard() {
        return board;
    }
    public Tile getTile(int row, int col) {
        return board.getTile(row, col);
    }
    public float getTileSize() {
        return tileSize;
    }
    public float getBoardX() {
        return boardX;
    }
    public float getBoardY() {
        return boardY;
    }
    public float getBoardWidth() {
        return cols * tileSize;
    }
    public float getBoardHeight() {
        return rows * tileSize;
    }

    public Tile getTileAt(float x, float y) {

        float localX = x - boardX;
        float localY = y - boardY;

        if (localX < 0 || localY < 0 ||
            localX >= getBoardWidth() ||
            localY >= getBoardHeight()) {
            return null;
        }

        int col = (int) (localX / tileSize);
        int row = rows - 1 - (int) (localY / tileSize);

        return board.getTile(row, col);
    }

    public Tile getAdjacentTile(Tile tile, SwipeDirection direction) {
        if (tile == null || direction == null) {
            return null;
        }

        int row = tile.getRow();
        int col = tile.getCol();

        switch (direction) {

            case UP:
                row--;
                break;

            case DOWN:
                row++;
                break;

            case LEFT:
                col--;
                break;

            case RIGHT:
                col++;
                break;
        }

        return board.getTile(row, col);
    }

    // Xử lý hợp nhất sau mỗi lượt di chuyển.
    public boolean processMerges(MergeSystem mergeSystem) {
        return mergeSystem.merge(mode, board);
    }
}
