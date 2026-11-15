package input;

import board.BoardManager;
import board.Tile;

import gameplay.MergeSystem;
import managers.AnimationManager;
import screens.GameScreen;

public class MoveHandler {

    private enum MoveState {
        IDLE,
        SWAPPING,
        REVERSING,
        MERGING
    }

//    private final BoardManager boardManager;
    private final AnimationManager animationManager;

    private MoveState state = MoveState.IDLE;

    private Tile selectedTile;
    private Tile adjacentTile;

//    private boolean cascadeReady = false;
    private boolean inputLocked = false;
    private boolean mergeReady = false;
    private boolean mergeConvergenceFinished = false;

    public void setInputLocked(boolean locked) {
        this.inputLocked = locked;
    }

    public MoveHandler(
//        BoardManager boardManager,
        AnimationManager animationManager
    ) {

//        this.boardManager = boardManager;
        this.animationManager = animationManager;
    }

    // Nhận yêu cầu swipe.
    public void handleMove(
        BoardManager boardManager,
        Tile selectedTile,
        SwipeDirection direction
    ) {
        // Không nhận input khi đang animation.
        if (state != MoveState.IDLE || inputLocked) return;

        if (selectedTile == null || direction == null) return;

        Tile adjacent = boardManager.getAdjacentTile(
            selectedTile,
            direction
        );

        if (adjacent == null || adjacent.isEmpty()) return;

        this.selectedTile = selectedTile;
        this.adjacentTile = adjacent;

        // Swap logic Board.
        boardManager.getBoard().swapTiles(
            selectedTile,
            adjacent
        );

        // Bắt đầu swap animation.
        animationManager.startSwapAnimation(
            selectedTile,
            adjacent,
            boardManager.getTileSize()
        );

        state = MoveState.SWAPPING;
    }

    // Cập nhật trạng thái mỗi frame.
    public void update(
        BoardManager boardManager,
        MergeSystem mergeSystem,
        float delta
    ) {
        animationManager.update(delta);

        if (!animationManager.isAnimationFinished()) {
            return;
        }

        switch (state) {

            case IDLE:
                break;

            case SWAPPING:

                boolean mergeAvailable =
                    boardManager.processMerges(mergeSystem);

                if (mergeAvailable) {

                    // MergeSystem đã xử lý một lượt
                    // và lưu lại các MatchGroup.

                    mergeReady = true;

                    // Chờ GameScreen chạy
                    // MergeConvergeAnimation.
                    state = MoveState.MERGING;

                } else {

                    animationManager
                        .startReverseSwapAnimation();

                    state = MoveState.REVERSING;
                }

                break;

            case REVERSING:

                boardManager.getBoard().swapTiles(
                    selectedTile,
                    adjacentTile
                );

                resetMove();
                break;

            case MERGING:
                // Chờ GameScreen hoàn thành
                // MergeConvergeAnimation.
                if (!mergeConvergenceFinished) {
                    break;
                }

                // Hiệu ứng hội tụ đã kết thúc.
                // GameScreen đã bắt đầu cascade.
                resetMove();

                break;
        }
    }

    public boolean consumeMergeReady() {

        if (!mergeReady) {
            return false;
        }

        mergeReady = false;
        return true;
    }

    public void completeMergeConvergence() {

        if (state != MoveState.MERGING) {
            throw new IllegalStateException(
                "No merge convergence is in progress."
            );
        }

        mergeConvergenceFinished = true;
    }

    private void resetMove() {

        selectedTile = null;
        adjacentTile = null;

        mergeReady = false;
        mergeConvergenceFinished = false;

        state = MoveState.IDLE;
    }

    public boolean isMoving() {
        return state != MoveState.IDLE;
    }
}
