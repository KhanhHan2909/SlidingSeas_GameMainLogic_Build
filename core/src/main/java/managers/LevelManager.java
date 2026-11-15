package managers;

import gameplay.LevelData;

import java.util.*;

public class LevelManager {
    private final Map<String, List<LevelData>> levelsByMode;

    private String currentGameMode;
    private LevelData currentLevel;

    public LevelManager(Map<String, List<LevelData>> levelConfigurations) {
        if (levelConfigurations == null) {
            throw new IllegalArgumentException(
                "Level configurations cannot be null"
            );
        }

        levelsByMode = new HashMap<>();

        for (Map.Entry<String, List<LevelData>> entry : levelConfigurations.entrySet()) {
            String mode = entry.getKey();
            List<LevelData> levels = entry.getValue();

            List<LevelData> copiedLevels = new ArrayList<>(levels);

            for (LevelData level : copiedLevels) {
                if (level == null) {
                    throw new IllegalArgumentException("Level data cannot be null");
                }
            }

            copiedLevels.sort(
                Comparator.comparingInt(LevelData::getLevelNumber)
            );

//            copiedLevels.sort((a, b) -> Integer.compare(
//                a.getLevelNumber(),
//                b.getLevelNumber()
//            ));

            levelsByMode.put(mode, Collections.unmodifiableList(copiedLevels));
        }
    }

    // Get all levels belonging to a GameMode
    public List<LevelData> getLevels(String mode) {
        List<LevelData> levels = levelsByMode.get(mode);

        if (levels == null) {
            return Collections.emptyList();
        }

        return levels;
    }

    // Select a level freely
    public boolean selectLevel(String mode, int levelNumber) {
        List<LevelData> levels = levelsByMode.get(mode);

        if (levels == null) {
            return false;
        }

        for (LevelData level : levels) {
            if (level.getLevelNumber() == levelNumber) {
                currentGameMode = mode;
                currentLevel = level;
                return true;
            }
        }

        return false;
    }

    public String getCurrentGameMode() {
        return currentGameMode;
    }

    public LevelData getCurrentLevel() {
        return currentLevel;
    }
}
