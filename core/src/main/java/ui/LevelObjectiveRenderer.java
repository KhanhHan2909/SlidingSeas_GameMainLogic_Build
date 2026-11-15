
package ui;

import board.BoardManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class LevelObjectiveRenderer {

    private final BoardManager boardManager;
    private final BitmapFont font;
    private final GlyphLayout layout;

    private String objective = "";

    private static final float TOP_PADDING = 85f;

    public LevelObjectiveRenderer(BoardManager boardManager) {
        this.boardManager = boardManager;

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/segoeuib.ttf")
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = Math.max(
            1,
            Math.round(boardManager.getTileSize() / 2.5f)
        );

        parameter.color = Color.WHITE;

        // Viền đen
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2f;

        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);

        generator.dispose();

        layout = new GlyphLayout();
    }

    // Thiết lập mục tiêu màn chơi
    public void setObjective(String objective) {
        this.objective = objective == null ? "" : objective;
        layout.setText(font, this.objective);
    }

    // Vẽ mục tiêu phía trên board
    public void render(Batch batch) {
        if (objective.isEmpty()) return;

        float boardCenterX =
            boardManager.getBoardX()
                + boardManager.getBoardWidth() / 2f;

        float boardTop =
            boardManager.getBoardY()
                + boardManager.getBoardHeight();

        float textX =
            boardCenterX - layout.width / 2f;

        float textY =
            boardTop + TOP_PADDING + layout.height;

        font.draw(batch, layout, textX, textY);
    }

    public void dispose() {
        font.dispose();
    }
}
