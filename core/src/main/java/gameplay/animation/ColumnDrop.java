package gameplay.animation;

import java.util.List;

public record ColumnDrop(int column, List<TileMovement> movements, TileSpawn spawn, int[] resultingTypes) {
    public ColumnDrop {
        movements = List.copyOf(movements);
        resultingTypes = resultingTypes.clone();
    }

    @Override
    public int[] resultingTypes() {
        return resultingTypes.clone();
    }
}
