package gameplay.animation;

import java.util.List;

public record CascadePlan(List<CascadePhase> phases) {
    public CascadePlan {
        phases = List.copyOf(phases);
    }

    public boolean isEmpty() {
        return phases.isEmpty();
    }
}
