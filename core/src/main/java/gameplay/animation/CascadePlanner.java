
package gameplay.animation;

import board.Board;
import board.BoardGenerator;
import board.Tile;
import gameplay.LevelData;
import screens.GameScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CascadePlanner {
    private static final int EMPTY = 0;
    private static final int PENDING = 25;

    public CascadePlan createPlan(LevelData level, Board board, boolean shouldSpawnSurvivor) {
        int rows = board.getRows();
        int cols = board.getCols();

        // Tạo snapshot để mô phỏng cascade.
        int[][] simulatedBoard = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                simulatedBoard[r][c] =
                    board.getTile(r, c).getType();
            }
        }

        List<CascadePhase> phases = new ArrayList<>();

        int phaseCount = 0;
        int maxPhases = rows * cols + 1;

        while (hasPending(simulatedBoard, rows, cols)) {

            if (++phaseCount > maxPhases) {
                throw new IllegalStateException(
                    "Cascade exceeded maximum phase count."
                );
            }

            List<ColumnDrop> columnDrops = new ArrayList<>();

            // Tất cả các cột được lập kế hoạch từ cùng
            // trạng thái Board đầu phase.
            for (int c = 0; c < cols; c++) {
                ColumnDrop drop =
                    planColumnPhase(simulatedBoard, rows, c, level);

                if (drop != null) {
                    columnDrops.add(drop);
                }
            }

            // Còn PENDING nhưng không cột nào tiến triển:
            // đây là trạng thái không hợp lệ cần phát hiện.
            if (columnDrops.isEmpty()) {
                throw new IllegalStateException(
                    "Cascade is stuck: pending cells cannot be filled."
                );
            }

            phases.add(new CascadePhase(columnDrops));
        }

        if(shouldSpawnSurvivor && !phases.isEmpty()) {
            List<ColumnDrop> firstPhaseDrops = phases.getFirst().columnDrops();
            Random random = new Random();
            int index = random.nextInt(firstPhaseDrops.size());
            firstPhaseDrops.get(index).spawn().tile().setSurvivors(1);
        }

        return new CascadePlan(phases);
    }

    private ColumnDrop planColumnPhase(
        int[][] simulatedBoard,
        int rows,
        int col, LevelData level
    ) {
        int[] current = new int[rows];

        for (int r = 0; r < rows; r++) {
            current[r] = simulatedBoard[r][col];
        }

        int[] next = current.clone();

        List<TileMovement> movements = new ArrayList<>();
        TileSpawn spawn = null;

        // Đọc PENDING từ snapshot current,
        // không đọc từ next đã thay đổi.
        for (int pendingRow = 0;
             pendingRow < rows;
             pendingRow++) {

            if (current[pendingRow] != PENDING) {
                continue;
            }

            int previousPending =
                findPreviousPending(current, pendingRow);

            List<Integer> sourceRows = getMovableRows(
                current,
                previousPending + 1,
                pendingRow - 1
            );

            // Không có tile nào phía trên PENDING.
            if (sourceRows.isEmpty()) {

                // Chỉ PENDING đầu tiên của cột được sinh tile mới.
                if (previousPending == -1) {
                    int newType = generateRandomType(level);

                    next[pendingRow] = newType;

                    Tile tile = new Tile(pendingRow, col, newType);

                    spawn = new TileSpawn(
                        pendingRow,
                        col,
                        tile
                    );
                }

                // Nếu có PENDING phía trên thì chờ phase sau.
                continue;
            }

            // Đoàn tàu di chuyển xuống.
            for (int i = 0; i < sourceRows.size(); i++) {

                int fromRow = sourceRows.get(i);

                int toRow = (i + 1 < sourceRows.size())
                    ? sourceRows.get(i + 1)
                    : pendingRow;

                int type = current[fromRow];

                int[] emptyRange = findEmptyRange(
                    current,
                    fromRow,
                    toRow
                );

                movements.add(new TileMovement(
                    fromRow,
                    toRow,
                    col,
                    type,
                    emptyRange[0],
                    emptyRange[1]
                ));

                next[toRow] = type;
            }

            int trainStart = sourceRows.get(0);

            if (previousPending == -1) {

                // Đoàn tàu đã di chuyển.
                // Tile mới sinh tại vị trí đầu đoàn tàu.
                int newType = generateRandomType(level);

                next[trainStart] = newType;

                Tile tile = new Tile(pendingRow, col, newType);

                spawn = new TileSpawn(
                    trainStart,
                    col,
                    tile
                );

            } else {

                // Đánh dấu vị trí đầu đoàn tàu
                // thành PENDING cho phase tiếp theo.
                next[trainStart] = PENDING;
            }
        }

        if (movements.isEmpty() && spawn == null) {
            return null;
        }

        // Commit kết quả mô phỏng của cột vào snapshot.
        for (int r = 0; r < rows; r++) {
            simulatedBoard[r][col] = next[r];
        }

        return new ColumnDrop(
            col,
            movements,
            spawn,
            next
        );
    }

    private int findPreviousPending(
        int[] column,
        int currentPendingRow
    ) {
        for (int r = currentPendingRow - 1; r >= 0; r--) {
            if (column[r] == PENDING) {
                return r;
            }
        }

        return -1;
    }

    private List<Integer> getMovableRows(
        int[] column,
        int startRow,
        int endRow
    ) {
        List<Integer> result = new ArrayList<>();

        for (int r = startRow; r <= endRow; r++) {
            int type = column[r];

            if (type != EMPTY && type != PENDING) {
                result.add(r);
            }
        }

        return result;
    }

    /**
     * Tìm chuỗi EMPTY nằm giữa fromRow và toRow.
     * Nếu không có EMPTY, trả về {-1, -1}.
     */
    private int[] findEmptyRange(
        int[] column,
        int fromRow,
        int toRow
    ) {
        int firstEmpty = -1;
        int lastEmpty = -1;

        for (int r = fromRow + 1; r < toRow; r++) {
            if (column[r] == EMPTY) {

                if (firstEmpty == -1) {
                    firstEmpty = r;
                }

                lastEmpty = r;
            }
        }

        return new int[] {
            firstEmpty,
            lastEmpty
        };
    }

    private int generateRandomType(LevelData level) {
        int index = BoardGenerator.random.nextInt(
            level.getAvailableTypes().length
        );

        return level.getAvailableTypes()[index];
    }

    private boolean hasPending(
        int[][] board,
        int rows,
        int cols
    ) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == PENDING) {
                    return true;
                }
            }
        }

        return false;
    }
}
