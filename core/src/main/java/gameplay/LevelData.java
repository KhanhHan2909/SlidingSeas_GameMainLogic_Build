package gameplay;

import board.Tile;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(value = "levelData", useDiscriminator = false)
public class LevelData {
    @Id
    private ObjectId id;
    private int levelNumber;
    private int rows;
    private int cols;
    private String mode = "";
    private int requireNumber;
    private int[] availableTypes = new int[] {};
    private List<Tile> fixedPositions = new ArrayList<>();

    public LevelData() {}

    public int getLevelNumber() {
        return levelNumber;
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
    public int getRequireNumber() { return requireNumber; }

    public int[] getAvailableTypes() {
        return availableTypes;
    }

    public String getMode() {
        return mode;
    }

    public List<Tile> getFixedPositions() {
        return fixedPositions;
    }
}
