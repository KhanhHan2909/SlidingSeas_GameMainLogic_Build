package managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.filters.Filters;
import gameplay.LevelData;
import screens.AboutScreen;
import screens.GameScreen;
import screens.LevelSelectScreen;
import screens.MainMenuScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenManager {
    private final Game game;
    private final LevelManager levelManager;
    public static Datastore datastore;

    public ScreenManager(Game game) {
        this.game = game;
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        datastore = Morphia.createDatastore(mongoClient, "slidingseas");
        datastore.getMapper().mapPackage("gameplay");

        LevelData emptyLevel = datastore.find(LevelData.class).filter(Filters.eq("mode", "")).first();
        if(emptyLevel == null) {
            LevelData sampleLevel = new LevelData();
            datastore.save(sampleLevel);
        }

        this.levelManager = new LevelManager(createLevelData());
    }

    // Khởi tạo dữ liệu Level cho tất cả Game Mode
    private Map<String, List<LevelData>> createLevelData() {
        Map<String, List<LevelData>> levels = new HashMap<>();

        for (String mode : MainMenuScreen.modeNames) {
            List<LevelData> modeLevels = datastore.find(LevelData.class)
                .filter(Filters.eq("mode", mode))
                .iterator()
                .toList();

            levels.put(mode, modeLevels);
        }

        return levels;
    }

    // Switch to a new screen
    public void setScreen(Screen newScreen) {
        Screen oldScreen = game.getScreen();

        if (oldScreen == newScreen) return;

        game.setScreen(newScreen);

        if (oldScreen != null) oldScreen.dispose();
    }

    // Main menu
    public void showMainMenuScreen() {
        setScreen(new MainMenuScreen(this));
    }

    // Game Mode menu
    public void showGameModeMenu() {
        MainMenuScreen menu = new MainMenuScreen(this);
        setScreen(menu);
        menu.showGameModeMenu();
    }

    // Level selection
    public void showLevelSelectScreen(String gameMode) {
        setScreen(new LevelSelectScreen(this, levelManager, gameMode));
    }

    // Gameplay
    public void showGameScreen(String selectedMode, LevelData selectedLevel) {
        setScreen(new GameScreen(this, selectedMode, selectedLevel));
    }

    // About
    public void showAboutScreen() {
        setScreen(new AboutScreen(this));
    }

    // Dispose current screen
    public void disposeCurrentScreen() {
        Screen currentScreen = game.getScreen();

        if (currentScreen != null) currentScreen.dispose();
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }
}
