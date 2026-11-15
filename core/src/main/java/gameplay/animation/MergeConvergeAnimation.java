package gameplay.animation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeConvergeAnimation {

    private static final int TRANSFORMING_TYPE = 14;

    private static final float TRANSFORM_DURATION = 0.15f;
    private static final float CONVERGE_DURATION = 0.2f;
    private static final float UPGRADE_HOLD = 0.12f;

    private final List<MergeGroupSnapshot> groups;
    private final Set<CellPosition> groupCells;

    private final int rows;
    private final float tileSize;
    private final float boardX;
    private final float boardY;

    private float elapsed;

    private final float convergeEnd;
    private final float totalDuration;

    public MergeConvergeAnimation(
        List<MergeGroupSnapshot> groups,
        int rows,
        float tileSize,
        float boardX,
        float boardY
    ) {
        if (groups == null || groups.isEmpty()) {
            throw new IllegalArgumentException(
                "Merge groups cannot be empty."
            );
        }

        if (rows <= 0 || tileSize <= 0f) {
            throw new IllegalArgumentException(
                "Invalid board dimensions."
            );
        }

        this.groups = List.copyOf(groups);
        this.groupCells = new HashSet<>();

        for (MergeGroupSnapshot group : groups) {
            groupCells.addAll(group.cells());
        }

        this.rows = rows;
        this.tileSize = tileSize;
        this.boardX = boardX;
        this.boardY = boardY;

        this.elapsed = 0f;

        this.convergeEnd =
            TRANSFORM_DURATION + CONVERGE_DURATION;

        this.totalDuration =
            convergeEnd + UPGRADE_HOLD;
    }

    public void update(float delta) {

        if (isFinished() || delta <= 0f) {
            return;
        }

        elapsed = Math.min(
            elapsed + delta,
            totalDuration
        );
    }

    public boolean isFinished() {
        return elapsed >= totalDuration;
    }

    /*
     * Các tile trong MatchGroup không được
     * render bằng texture của Board khi đang
     * transforming hoặc converging.
     *
     * Trong giai đoạn UPGRADE_HOLD,
     * Board được render bình thường.
     */
    public boolean isCellHidden(int row, int col) {

        if (elapsed >= convergeEnd) {
            return false;
        }

        return groupCells.contains(
            new CellPosition(row, col)
        );
    }

    /*
     * Sinh ra các TileVisual cho tất cả
     * MatchGroup trong frame hiện tại.
     *
     * Mọi nhóm được xử lý đồng thời.
     */
    public List<TileVisual> getVisuals() {

        List<TileVisual> visuals = new ArrayList<>();

        if (elapsed >= convergeEnd) {
            return visuals;
        }

        float progress = getConvergeProgress();

        for (MergeGroupSnapshot group : groups) {

            // ===== ICEBERG GROUP =====
            if (group.isIcebergGroup()) {

                // Giữ nguyên vị trí, chỉ thay đổi alpha.
                float alpha = 1f - progress;

                for (CellPosition cell : group.cells()) {

                    visuals.add(new TileVisual(
                        TRANSFORMING_TYPE,
                        cellX(cell.col()),
                        cellY(cell.row()),
                        tileSize,
                        tileSize,
                        alpha
                    ));
                }

                // Không xử lý hội tụ cho iceberg.
                continue;
            }

            // ===== NORMAL GROUP =====

            CellPosition upgradedCell = group.upgradedCell();

            float targetX = cellX(upgradedCell.col());
            float targetY = cellY(upgradedCell.row());

            for (CellPosition cell : group.cells()) {

                float startX = cellX(cell.col());
                float startY = cellY(cell.row());

                float x = lerp(
                    startX,
                    targetX,
                    progress
                );

                float y = lerp(
                    startY,
                    targetY,
                    progress
                );

                visuals.add(new TileVisual(
                    TRANSFORMING_TYPE,
                    x,
                    y,
                    tileSize,
                    tileSize,
                    1f
                ));
            }
        }

        return visuals;
    }

    /*
     * Trong TRANSFORM_DURATION:
     * progress = 0, tile giữ nguyên vị trí.
     *
     * Trong CONVERGE_DURATION:
     * progress tăng dần từ 0 đến 1.
     */
    private float getConvergeProgress() {

        if (elapsed <= TRANSFORM_DURATION) {
            return 0f;
        }

        float progress =
            (elapsed - TRANSFORM_DURATION)
                / CONVERGE_DURATION;

        return Math.min(1f, progress);
    }

    private float cellX(int col) {
        return boardX + col * tileSize;
    }

    private float cellY(int row) {
        return boardY
            + (rows - 1 - row) * tileSize;
    }

    private float lerp(
        float start,
        float end,
        float progress
    ) {
        return start + (end - start) * progress;
    }
}
