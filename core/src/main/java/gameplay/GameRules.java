package gameplay;

import board.Tile;
import screens.GameScreen;
import screens.MainMenuScreen;

public class GameRules {
    private GameScreen gameScreen;
    private boolean levelPassed;
    private final LevelData level;

    public GameRules(LevelData level) {
        this.level = level;
    }

    public String setObjective() {
        switch (level.getMode()) {
            case "To Shelter":
                return "Bring " + level.getRequireNumber() + " survivors to shelters";
            case "Rescue Boat":
                return "Bring " + level.getRequireNumber() + " survivors to the rescue boat";
            case "Against Pirate":
                return "Defeat "+ level.getRequireNumber() + " pirate ships";
            case "Precious Items":
                return "Collect " + level.getRequireNumber() + " precious items";
            case "To The Shore":
                return "Bring " + level.getRequireNumber() + " survivors to the shore";
            default:
                return "";
        }
    }

    public boolean isLevelPassed() {
        return levelPassed;
    }

    public static boolean canMerge(Tile tile1, Tile tile2, Tile tile3) {
        return tile1.getType() == tile2.getType() && tile2.getType() == tile3.getType();
    }
}
