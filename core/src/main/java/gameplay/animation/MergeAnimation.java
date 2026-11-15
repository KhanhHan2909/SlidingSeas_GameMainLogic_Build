
package gameplay.animation;

import board.Board;

import java.util.ArrayList;
import java.util.List;

public class MergeAnimation {

    private final Board board;
    private final CascadePlan plan;

    private final float tileSize;
    private final float boardX;
    private final float boardY;
    private final float fallSpeed;

    private int phaseIndex;

    private CascadePhase currentPhase;

    private float elapsed;
    private float phaseDuration;

    private boolean finished;

    public MergeAnimation(
        Board board,
        CascadePlan plan,
        float tileSize,
        float boardX,
        float boardY,
        float fallSpeed
    ) {
        if (board == null || plan == null) {
            throw new IllegalArgumentException(
                "Board and CascadePlan cannot be null."
            );
        }

        if (tileSize <= 0 || fallSpeed <= 0) {
            throw new IllegalArgumentException(
                "tileSize and fallSpeed must be positive."
            );
        }

        this.board = board;
        this.plan = plan;

        this.tileSize = tileSize;
        this.boardX = boardX;
        this.boardY = boardY;
        this.fallSpeed = fallSpeed;

        this.phaseIndex = 0;
        this.elapsed = 0f;

        if (plan.isEmpty()) {
            finished = true;
        } else {
            startNextPhase();
        }
    }

    /**
     * Bắt đầu phase tiếp theo.
     */
    private void startNextPhase() {

        if (phaseIndex >= plan.phases().size()) {
            currentPhase = null;
            finished = true;
            return;
        }

        currentPhase = plan.phases().get(phaseIndex);

        elapsed = 0f;

        // Mọi movement và spawn trong phase có cùng duration.
        phaseDuration = tileSize / fallSpeed;
    }

    /**
     * Cập nhật animation.
     */
    public void update(float delta) {

        if (finished || delta <= 0f) {
            return;
        }

        float remainingDelta = delta;

        // Xử lý cả trường hợp một frame có delta lớn
        // đủ để hoàn tất nhiều phase.
        while (remainingDelta > 0f && !finished) {

            float remainingDuration =
                phaseDuration - elapsed;

            float step = Math.min(
                remainingDelta,
                remainingDuration
            );

            elapsed += step;
            remainingDelta -= step;

            if (elapsed >= phaseDuration) {

                finishCurrentPhase();
            }
        }
    }

    /**
     * Hoàn tất phase hiện tại và cập nhật Board.
     */
    private void finishCurrentPhase() {

        // Chỉ cập nhật Board sau khi toàn bộ animation
        // của phase đã hoàn tất.
        board.applyCascadePhase(currentPhase);

        phaseIndex++;

        startNextPhase();
    }

    /**
     * Progress chung của phase hiện tại.
     */
    public float getProgress() {

        if (finished || currentPhase == null) {
            return 1f;
        }

        return Math.min(
            1f,
            elapsed / phaseDuration
        );
    }

    /**
     * Trả về danh sách visual cần render trong frame hiện tại.
     */
    public List<TileVisual> getVisuals() {

        List<TileVisual> visuals = new ArrayList<>();

        if (finished || currentPhase == null) {
            return visuals;
        }

        float progress = getProgress();

        for (ColumnDrop drop : currentPhase.columnDrops()) {

            for (TileMovement movement : drop.movements()) {

                if (movement.passesThroughEmpty()) {

                    addEmptyCrossingVisuals(
                        visuals,
                        movement,
                        progress
                    );

                } else {

                    addNormalMovementVisual(
                        visuals,
                        movement,
                        progress
                    );
                }
            }

            TileSpawn spawn = drop.spawn();

            if (spawn != null) {
                addSpawnVisual(
                    visuals,
                    spawn,
                    progress
                );
            }
        }

        return visuals;
    }

    /**
     * Movement bình thường:
     * tile di chuyển từ tâm ô nguồn đến tâm ô đích.
     */
    private void addNormalMovementVisual(
        List<TileVisual> visuals,
        TileMovement movement,
        float progress
    ) {

        float startX = cellX(movement.col());

        float endX = startX;

        float startY = cellCenterY(movement.fromRow());

        float endY = cellCenterY(movement.toRow());

        float x = lerp(startX, endX, progress);

        float y = lerp(startY, endY, progress);

        visuals.add(new TileVisual(
            movement.type(),
            x,
            y - tileSize / 2f,
            tileSize,
            tileSize,
            1f
        ));
    }

    /**
     * Tile đi xuyên qua chuỗi EMPTY.
     *
     * Visual 1:
     * Tile lặn xuống phía trên chuỗi EMPTY và biến mất.
     *
     * Visual 2:
     * Tile mọc lên từ mép dưới chuỗi EMPTY.
     *
     * Cả hai sử dụng cùng progress.
     */
    private void addEmptyCrossingVisuals(
        List<TileVisual> visuals,
        TileMovement movement,
        float progress
    ) {

        float x = cellX(movement.col());

        // ----- Visual phía trên: lặn xuống -----

        float sourceStartY =
            cellCenterY(movement.fromRow());

        // Mép trên của chuỗi EMPTY.
        float upperEmptyEdgeY =
            cellBottomY(movement.firstEmptyRow())
                + tileSize;

        // Tâm tile kết thúc tại phía trong ô EMPTY đầu tiên.
        float sourceEndY =
            upperEmptyEdgeY - tileSize / 2f;

        float sourceCenterY = lerp(
            sourceStartY,
            sourceEndY,
            progress
        );

        visuals.add(new TileVisual(
            movement.type(),
            x,
            sourceCenterY - tileSize / 2f,
            tileSize,
            tileSize,
            1f - progress
        ));

        // ----- Visual phía dưới: mọc lên -----

        // Mép dưới của chuỗi EMPTY.
        float lowerEmptyEdgeY =
            cellBottomY(movement.lastEmptyRow());

        // Tile mọc từ mép dưới, theo hướng xuống dưới.
        float destinationHeight =
            tileSize * progress;

        float destinationY =
            lowerEmptyEdgeY - destinationHeight;

        visuals.add(new TileVisual(
            movement.type(),
            x,
            destinationY,
            tileSize,
            destinationHeight,
            progress
        ));
    }

    /**
     * Tile random mới mọc từ mép trên ô đích.
     *
     * Nếu phía trên có chuỗi EMPTY thì mép này
     * chính là mép dưới của chuỗi EMPTY.
     */
    private void addSpawnVisual(
        List<TileVisual> visuals,
        TileSpawn spawn,
        float progress
    ) {

        float x = cellX(spawn.col());

        float topY =
            cellBottomY(spawn.row()) + tileSize;

        float height = tileSize * progress;

        float y = topY - height;

        visuals.add(new TileVisual(
            spawn.tile().getType(),
            x,
            y,
            tileSize,
            height,
            progress
        ));
    }

    /**
     * Những ô nguồn không được render từ Board
     * vì chúng đang được render bằng TileVisual.
     */
    public boolean isCellHidden(int row, int col) {

        if (finished || currentPhase == null) {
            return false;
        }

        for (ColumnDrop drop : currentPhase.columnDrops()) {

            for (TileMovement movement : drop.movements()) {

                if (movement.fromRow() == row
                    && movement.col() == col) {

                    return true;
                }
            }
        }

        return false;
    }

    private float cellX(int col) {
        return boardX + col * tileSize;
    }

    private float cellBottomY(int row) {

        return boardY
            + (board.getRows() - 1 - row) * tileSize;
    }

    private float cellCenterY(int row) {
        return cellBottomY(row) + tileSize / 2f;
    }

    private float lerp(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getCurrentPhaseIndex() {
        return phaseIndex;
    }
}
