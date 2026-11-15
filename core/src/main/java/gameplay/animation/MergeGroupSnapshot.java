package gameplay.animation;

import board.Tile;

import java.util.ArrayList;
import java.util.List;

public record MergeGroupSnapshot(
    List<CellPosition> cells,
    CellPosition upgradedCell
) {
    public MergeGroupSnapshot {

        if (cells == null || cells.isEmpty()) {
            throw new IllegalArgumentException(
                "MatchGroup cannot be empty."
            );
        }

        cells = List.copyOf(cells);

        // upgradedCell == null dành riêng cho iceberg group.
        if (upgradedCell != null && !cells.contains(upgradedCell)) {
            throw new IllegalArgumentException(
                "Pivot must belong to MatchGroup."
            );
        }
    }

    // Capture MatchGroup bình thường.
    public static MergeGroupSnapshot capture(
        List<Tile> group,
        Tile upgradedTile
    ) {
        if (upgradedTile == null) {
            throw new IllegalArgumentException(
                "Normal MatchGroup must have a pivot."
            );
        }

        return new MergeGroupSnapshot(
            capturePositions(group),
            new CellPosition(
                upgradedTile.getRow(),
                upgradedTile.getCol()
            )
        );
    }

    // Capture MatchGroup iceberg.
    public static MergeGroupSnapshot captureIceberg(
        List<Tile> group
    ) {
        if (group == null || group.isEmpty()) {
            throw new IllegalArgumentException(
                "Iceberg group cannot be empty."
            );
        }

        for (Tile tile : group) {
            if (tile.getType() != 9) {
                throw new IllegalArgumentException(
                    "Iceberg group contains non-iceberg tile."
                );
            }
        }

        return new MergeGroupSnapshot(
            capturePositions(group),
            null
        );
    }

    private static List<CellPosition> capturePositions(
        List<Tile> group
    ) {
        List<CellPosition> positions = new ArrayList<>();

        for (Tile tile : group) {
            positions.add(new CellPosition(
                tile.getRow(),
                tile.getCol()
            ));
        }

        return positions;
    }

    public boolean isIcebergGroup() {
        return upgradedCell == null;
    }
}
