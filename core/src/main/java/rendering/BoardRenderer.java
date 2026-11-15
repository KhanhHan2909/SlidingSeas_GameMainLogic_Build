
package rendering;

import board.Board;
import board.Tile;

import com.badlogic.gdx.graphics.Color;
import gameplay.animation.MergeAnimation;
import gameplay.animation.TileVisual;
import gameplay.animation.MergeConvergeAnimation;
import gameplay.animation.MergeAnimation;
import gameplay.animation.TileVisual;

import managers.AssetManager;
import managers.AnimationManager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public class BoardRenderer {

    private final SpriteBatch batch;
    private final AssetManager assetManager;
    private final AnimationManager animationManager;

    private final float tileSize;
    private final float boardX;
    private final float boardY;

    public BoardRenderer(
        SpriteBatch batch,
        AssetManager assetManager,
        AnimationManager animationManager,
        float tileSize,
        float boardX,
        float boardY
    ) {
        this.batch = batch;
        this.assetManager = assetManager;
        this.animationManager = animationManager;

        this.tileSize = tileSize;
        this.boardX = boardX;
        this.boardY = boardY;
    }

    /**
     * Render bình thường, không có cascade animation.
     */
//    public void render(Board board) {
//        render(board, null);
//    }

    /**
     * Render Board cùng cascade animation.
     */
    public void render(Board board, MergeAnimation cascadeAnimation, MergeConvergeAnimation convergeAnimation) {
        batch.begin();

        // Render các tile bình thường trên Board.
        renderBoardTiles(board, cascadeAnimation, convergeAnimation);

        batch.setColor(1f, 1f, 1f, 1f);

        // Render các tile đang rơi và tile mới sinh.
        if (cascadeAnimation != null) {

            for (TileVisual visual :
                cascadeAnimation.getVisuals()) {

                drawTileVisual(visual);
            }
        }

        // Render các tile transforming đang hội tụ.
        if (convergeAnimation != null) {

            for (TileVisual visual :
                convergeAnimation.getVisuals()) {

                drawTileVisual(visual);
            }
        }

        batch.end();
    }

    private void renderBoardTiles(
        Board board,
        MergeAnimation cascadeAnimation, MergeConvergeAnimation convergeAnimation
    ) {

        for (int row = 0; row < board.getRows(); row++) {

            for (int col = 0; col < board.getCols(); col++) {

                // Ẩn tile đang tham gia hội tụ.
                if (convergeAnimation != null
                    && convergeAnimation.isCellHidden(row, col)) {
                    continue;
                }

                // Ẩn source tile đang rơi trong cascade.
                if (cascadeAnimation != null
                    && cascadeAnimation.isCellHidden(row, col)) {
                    continue;
                }

                Tile tile = board.getTile(row, col);

                if (tile == null || tile.getType() == 25) {
                    continue;
                }

                Texture texture =
                    assetManager.getTileTexture(tile.getType());

                if (texture == null) {
                    continue;
                }

                float x = boardX + col * tileSize;

                float y = boardY
                    + (board.getRows() - 1 - row) * tileSize;

                // Offset từ AnimationManager:
                // phục vụ swap/reverse swap.
                x += animationManager.getOffsetX(tile);
                y += animationManager.getOffsetY(tile);

                float scale = animationManager.getScale(
                    row,
                    col
                );

                float size = tileSize * scale;

                float offset = (size - tileSize) / 2f;

                batch.setColor(1f, 1f, 1f, 1f);

                batch.draw(
                    texture,
                    x - offset,
                    y - offset,
                    size,
                    size
                );
            }
        }
    }

    /**
     * Render các visual tạm thời của MergeAnimation.
     */
    private void renderAnimatedTiles(
        List<TileVisual> visuals
    ) {

        for (TileVisual visual : visuals) {

            if (visual.width() <= 0f
                || visual.height() <= 0f
                || visual.alpha() <= 0f) {
                continue;
            }

            Texture texture =
                assetManager.getTileTexture(visual.type());

            if (texture == null) {
                continue;
            }

            batch.setColor(
                1f,
                1f,
                1f,
                Math.max(0f, Math.min(1f, visual.alpha()))
            );

            batch.draw(
                texture,
                visual.x(),
                visual.y(),
                visual.width(),
                visual.height()
            );
        }

        // Tránh alpha ảnh hưởng đến các lần render tiếp theo.
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawTileVisual(TileVisual visual) {

        Texture texture =
            assetManager.getTileTexture(
                visual.type()
            );

        if (texture == null) {
            return;
        }

        batch.setColor(
            1f,
            1f,
            1f,
            visual.alpha()
        );

        batch.draw(
            texture,
            visual.x(),
            visual.y(),
            visual.width(),
            visual.height()
        );

        batch.setColor(Color.WHITE);
    }
}
