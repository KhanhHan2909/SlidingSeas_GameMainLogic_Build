package gameplay.animation;

import java.util.List;

public record CascadePhase(List<ColumnDrop> columnDrops) {
    public CascadePhase {
        columnDrops = List.copyOf(columnDrops);
    }
}
