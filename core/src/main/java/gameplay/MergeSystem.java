package gameplay;

import board.Board;
import board.BoardGenerator;
import board.Tile;
import gameplay.animation.MergeGroupSnapshot;
import managers.InputManager;
import screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class MergeSystem {
    private final InputManager inputManager;
    private final MatchDetector matchDetector;
    private final List<MergeGroupSnapshot> lastMergeGroups = new ArrayList<>();

    public MergeSystem(InputManager inputManager) {
        this.inputManager = inputManager;
        this.matchDetector = new MatchDetector();
    }

    public List<MergeGroupSnapshot> consumeLastMergeGroups() {

        List<MergeGroupSnapshot> result =
            List.copyOf(lastMergeGroups);

        lastMergeGroups.clear();

        return result;
    }
    /**
     * Merge lần đầu sau thao tác swap của người chơi.
     *
     * Ưu tiên tile vừa swap làm pivot nếu nó nằm
     * trong nhóm match.
     */
    public boolean merge(String mode, Board board) {
        lastMergeGroups.clear();
        return mergeRound(mode, board, true);
    }

    /**
     * Merge tự động sau khi cascade hoàn tất.
     *
     * Không sử dụng tile người chơi đã chọn để
     * xác định pivot.
     */
    public boolean mergeAutomatic(String mode, Board board) {
        lastMergeGroups.clear();
        return mergeRound(mode, board, false);
    }

    /**
     * Thực hiện đúng một lượt merge.
     *
     * Không refill và không lặp liên tục.
     */
    private boolean mergeRound(String mode, Board board, boolean usePlayerPivot) {
        List<List<Tile>> matches =
            matchDetector.findMatches(board);

        if (matches.isEmpty()) {
            return false;
        }

        Tile selectedTile = null;
        Tile adjacentTile = null;

        if (usePlayerPivot) {
            selectedTile = inputManager.getSelectedTile();
            adjacentTile = inputManager.getAdjacentTile();
        }

        for (List<Tile> group : matches) {

            Tile pivot = null;

            if (usePlayerPivot) {

                if (selectedTile != null
                    && group.contains(selectedTile)) {

                    pivot = selectedTile;

                } else if (adjacentTile != null
                    && group.contains(adjacentTile)) {

                    pivot = adjacentTile;
                }
            }

            mergeGroup(mode, group, pivot);
        }

//        GameScreen.mergeTurnCount++;
        return true;
    }


    /**
     * Hợp nhất một nhóm đảo.
     */
    private void mergeGroup(
        String mode,
        List<Tile> group,
        Tile pivotTile
    ) {
        if (group == null || group.isEmpty()) {
            return;
        }

        // 1. Trường hợp đặc biệt: ICEBERG GROUP.
        if (group.getFirst().getType() == 9) {

            // Lưu toàn bộ vị trí iceberg.
            // Không có pivot.
            lastMergeGroups.add(
                MergeGroupSnapshot.captureIceberg(group)
            );

            // Tất cả iceberg trở thành PENDING.
            for (Tile tile : group) {
                tile.setType(25);
            }

            return;
        }

        // 2. MatchGroup bình thường.
        Tile upgradedTile = pivotTile;

        // Không có pivot từ người chơi:
        // chọn pivot theo logic merge tự động.
        if (upgradedTile == null) {
            upgradedTile = chooseAutomaticPivot(group);
        }

        // Lưu thông tin group trước khi thay đổi Board.
        lastMergeGroups.add(
            MergeGroupSnapshot.capture(
                group,
                upgradedTile
            )
        );

        // Nâng cấp pivot.
        upgradeTile(mode, upgradedTile);
        int survivorTotal = upgradedTile.getSurvivors(), mineLevel = upgradedTile.getMineLevel();
        boolean hasRaft = upgradedTile.hasRaft(), hasShelter = upgradedTile.hasShelter();
        if(group.size() > 3) {
            if(mode.equals("To Shelter")) {
                hasRaft = upgradedTile.getType() < 5;
                hasShelter = upgradedTile.getType() > 4;
            }
            else if(mode.equals("Against Pirate"))
                if(mineLevel < 3) mineLevel++;
        }

        if(mode.equals("To Shelter") && upgradedTile.getType() == 8) hasShelter = true;

        // Các tile còn lại chuyển thành PENDING.
        for (Tile tile : group) {
            if (tile != upgradedTile) {
                survivorTotal += tile.getSurvivors();
                if(tile.getMineLevel() + mineLevel <= 3) mineLevel += tile.getMineLevel();
                else mineLevel = 3;
                if(tile.hasRaft()) if(tile.getType() < 4 && !hasRaft) hasRaft = true;
                if(tile.hasShelter()) if(!hasShelter) hasShelter = true;
                tile.clearExtras();
                tile.setType(25);
            }
        }

        Tile clone = new Tile(survivorTotal, hasRaft, hasShelter, mineLevel);
        upgradedTile.copyExtrasFrom(clone);
    }

    private Tile chooseAutomaticPivot(List<Tile> group) {

        int size = group.size();

        // Nhóm nằm ngang hoặc nằm dọc
        boolean horizontal = true;
        int row = group.getFirst().getRow();

        for (Tile tile : group) {
            if (tile.getRow() != row) {
                horizontal = false;
                break;
            }
        }

        boolean vertical = true;
        int col = group.getFirst().getCol();

        for (Tile tile : group) {
            if (tile.getCol() != col) {
                vertical = false;
                break;
            }
        }

        if (horizontal || vertical) {
            return group.get(size / 2);
        }

        // Nhóm hình L, T hoặc có giao nhau:
        // tìm tile có ít nhất một tile cùng hàng
        // và một tile khác cùng cột.
        for (Tile candidate : group) {

            boolean sameRow = false;
            boolean sameCol = false;

            for (Tile other : group) {

                if (other == candidate) {
                    continue;
                }

                if (other.getRow() == candidate.getRow()) {
                    sameRow = true;
                }

                if (other.getCol() == candidate.getCol()) {
                    sameCol = true;
                }
            }

            if (sameRow && sameCol) {
                return candidate;
            }
        }

        throw new IllegalStateException(
            "Cannot determine automatic pivot."
        );
    }

    private void upgradeTile(String mode, Tile tile) {
        if (tile.getType() == 4) {
            tile.setType(
                mode.equals("Rescue Boat")
                    || mode.equals("Precious Items")
                    ? 9 : 5
            );
        }
        else if(tile.getType() == 7) {
            tile.setType(mode.equals("To Shelter") ? 8 : 10);
        }
        else if (tile.getType() != 9) {
            tile.levelUp();
        }
        else tile.setType(25);
    }
}
