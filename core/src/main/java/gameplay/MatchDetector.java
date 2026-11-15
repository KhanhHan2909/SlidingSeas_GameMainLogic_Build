//package gameplay;
//
//import board.Board;
//import board.Tile;
//import board.TileType;
//
//import java.util.*;
//
//public class MatchDetector {
//    // 4 hướng di chuyển
//    private static final int[][] DIRECTIONS = {
//        {-1, 0},
//        {1, 0},
//        {0, -1},
//        {0, 1}
//    };
//
//    /**
//     * Tìm tất cả nhóm ô cùng cấp có thể hợp nhất.
//     */
//    public List<List<Tile>> findMatches(Board board) {
//        List<List<Tile>> matchGroups = new ArrayList<>();
//
//        int rows = board.getRows();
//        int cols = board.getCols();
//
//        boolean[][] isMatched = new boolean[rows][cols];
//
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < cols; c++) {
//                if (isMatched[r][c]) continue;
//
//                int tileType = board.getTile(r, c).getType();
//                //Nếu duyệt trúng tường hoặc ô đất cao tối đa thì bỏ qua
//                if(tileType == 0 || tileType == 8 || tileType == 10) continue;
//
//                List<Tile> tileGroup = new ArrayList<>();
//                boolean[][] isCheckedInThisGroup = new boolean[rows][cols];
//
//                checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, r, c, rows, cols);
//                checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, c, rows, cols);
//
//                if (!tileGroup.isEmpty()) matchGroups.add(tileGroup);
//            }
//        }
//
//        return matchGroups;
//    }
//
//    private void checkMatchHorizontally(Board board, boolean[][] isMatched, boolean[][] isCheckedInThisGroup,
//        List<Tile> tileGroup, int r, int c, int rows, int cols) {
//        int i = c;
//        while (true) {
//            if (i < cols - 2) {
//                if (board.getTile(r, i + 1).getType() == board.getTile(r, i).getType()
//                    && board.getTile(r, i + 2).getType() == board.getTile(r, i + 1).getType()
//                    && !isMatched[r][i + 2] && !isCheckedInThisGroup[r][i]) {
//                    addToTileGroup(board, isMatched, tileGroup, r, i);
//                    addToTileGroup(board, isMatched, tileGroup, r, i + 1);
//                    addToTileGroup(board, isMatched, tileGroup, r, i + 2);
//                    isCheckedInThisGroup[r][i] = true;
//                    checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, i, rows, cols);
//                    i++;
//                } else {
//                    if (!isCheckedInThisGroup[r][i] && tileGroup.contains(board.getTile(r, i))) {
//                        checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, i, rows, cols);
//                        isCheckedInThisGroup[r][i] = true;
//                    }
//                    if (!isCheckedInThisGroup[r][i + 1] && tileGroup.contains(board.getTile(r, i + 1))) {
//                        checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, i + 1, rows, cols);
//                        isCheckedInThisGroup[r][i + 1] = true;
//                    }
//                    break;
//                }
//            } else break;
//        }
//
//        i = c;
//        if (i > 0 && i < cols - 1) {
//            if (board.getTile(r, i + 1).getType() == board.getTile(r, i).getType()
//                && board.getTile(r, i - 1).getType() == board.getTile(r, i).getType()) {
//                addToTileGroup(board, isMatched, tileGroup, r, i);
//                addToTileGroup(board, isMatched, tileGroup, r, i + 1);
//                addToTileGroup(board, isMatched, tileGroup, r, i - 1);
//                if(!isCheckedInThisGroup[r][i + 1]) checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, i + 1, rows, cols);
//                if(!isCheckedInThisGroup[r][i - 1]) checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, i - 1, rows, cols);
//            }
//        }
//
//        while (true) {
//            if (i > 1) {
//                if (board.getTile(r, i - 1).getType() == board.getTile(r, i).getType()
//                    && board.getTile(r, i - 2).getType() == board.getTile(r, i - 1).getType()
//                    && !isMatched[r][i - 2]) {
//                    addToTileGroup(board, isMatched, tileGroup, r, i);
//                    addToTileGroup(board, isMatched, tileGroup, r, i - 1);
//                    addToTileGroup(board, isMatched, tileGroup, r, i - 2);
//                    isCheckedInThisGroup[r][i] = true;
//                    checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, i, rows, cols);
//                    i--;
//                } else {
//                    if (!isCheckedInThisGroup[r][i] && tileGroup.contains(board.getTile(r, i))) {
//                        checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, i, rows, cols);
//                        isCheckedInThisGroup[r][i] = true;
//                    }
//                    if (!isCheckedInThisGroup[r][i - 1] && tileGroup.contains(board.getTile(r, i - 1))) {
//                        checkMatchVertically(board, isMatched, isCheckedInThisGroup, tileGroup, r, i - 1, rows, cols);
//                        isCheckedInThisGroup[r][i - 1] = true;
//                    }
//                    break;
//                }
//            } else break;
//        }
//    }
//
//    private void checkMatchVertically(Board board, boolean[][] isMatched, boolean[][] isCheckedInThisGroup,
//        List<Tile> tileGroup, int r, int c, int rows, int cols) {
//        int i = r;
//        while (true) {
//            if (i < rows - 2) {
//                if (board.getTile(i + 1, c).getType() == board.getTile(i, c).getType()
//                    && board.getTile(i + 2, c).getType() == board.getTile(i + 1, c).getType()
//                    && !isMatched[i + 2][c] && !isCheckedInThisGroup[i][c]) {
//                    addToTileGroup(board, isMatched, tileGroup, i, c);
//                    addToTileGroup(board, isMatched, tileGroup, i + 1, c);
//                    addToTileGroup(board, isMatched, tileGroup, i + 2, c);
//                    isCheckedInThisGroup[i][c] = true;
//                    checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, i, c, rows, cols);
//                    i++;
//                } else {
//                    if (!isCheckedInThisGroup[i][c] && tileGroup.contains(board.getTile(i, c))) {
//                        checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, i, c, rows, cols);
//                        isCheckedInThisGroup[i][c] = true;
//                    }
//                    if (!isCheckedInThisGroup[i + 1][c] && tileGroup.contains(board.getTile(i + 1, c))) {
//                        checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, i + 1, c, rows, cols);
//                        isCheckedInThisGroup[i + 1][c] = true;
//                    }
//                    break;
//                }
//            } else break;
//        }
//
//        i = r;
//        if (i > 0 && i < rows - 1) {
//            if (board.getTile(i + 1, c).getType() == board.getTile(i, c).getType()
//                && board.getTile(i - 1, c).getType() == board.getTile(i, c).getType()) {
//                addToTileGroup(board, isMatched, tileGroup, i, c);
//                addToTileGroup(board, isMatched, tileGroup, i + 1, c);
//                addToTileGroup(board, isMatched, tileGroup, i - 1, c);
//                if(!isCheckedInThisGroup[i + 1][c]) checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, i + 1, c, rows, cols);
//                if(!isCheckedInThisGroup[i - 1][c]) checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, i - 1, c, rows, cols);
//            }
//        }
//
//        while (true) {
//            if (i > 1) {
//                if (board.getTile(i - 1, c).getType() == board.getTile(i, c).getType()
//                    && board.getTile(i - 2, c).getType() == board.getTile(i - 1, c).getType()
//                    && !isMatched[i - 2][c]) {
//                    addToTileGroup(board, isMatched, tileGroup, i, c);
//                    addToTileGroup(board, isMatched, tileGroup, i - 1, c);
//                    addToTileGroup(board, isMatched, tileGroup, i - 2, c);
//                    isCheckedInThisGroup[i][c] = true;
//                    checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, i, c, rows, cols);
//                    i--;
//                } else {
//                    if (!isCheckedInThisGroup[i][c] && tileGroup.contains(board.getTile(i, c))) {
//                        checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, i, c, rows, cols);
//                        isCheckedInThisGroup[i][c] = true;
//                    }
//                    if (!isCheckedInThisGroup[i - 1][c] && tileGroup.contains(board.getTile(i - 1, c))) {
//                        checkMatchHorizontally(board, isMatched, isCheckedInThisGroup, tileGroup, i - 1, c, rows, cols);
//                        isCheckedInThisGroup[i - 1][c] = true;
//                    }
//                    break;
//                }
//            } else break;
//        }
//    }
//
//    private void addToTileGroup(Board board, boolean[][] isMatched, List<Tile> tileGroup, int r, int c) {
//        if (!isMatched[r][c]) {
//            tileGroup.add(board.getTile(r, c));
//            isMatched[r][c] = true;
//        }
//    }
//}


package gameplay;

import board.Board;
import board.Tile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MatchDetector {

    public List<List<Tile>> findMatches(Board board) {

        int rows = board.getRows();
        int cols = board.getCols();

        UnionFind uf = new UnionFind(rows * cols);

        boolean[] matched = new boolean[rows * cols];

        // 1. Tìm tất cả chuỗi ngang dài ít nhất 3 tile.
        for (int r = 0; r < rows; r++) {

            int c = 0;

            while (c < cols) {

                int type = board.getTile(r, c).getType();

                if (!isMatchable(type)) {
                    c++;
                    continue;
                }

                int start = c;

                // Đi đến cuối chuỗi cùng type.
                while (c < cols
                    && board.getTile(r, c).getType() == type) {
                    c++;
                }

                int length = c - start;

                if (length >= 3) {

                    int anchor = index(r, start, cols);

                    for (int k = start; k < c; k++) {

                        int id = index(r, k, cols);

                        matched[id] = true;

                        uf.union(anchor, id);
                    }
                }
            }
        }

        // 2. Tìm tất cả chuỗi dọc dài ít nhất 3 tile.
        for (int c = 0; c < cols; c++) {

            int r = 0;

            while (r < rows) {

                int type = board.getTile(r, c).getType();

                if (!isMatchable(type)) {
                    r++;
                    continue;
                }

                int start = r;

                // Đi đến cuối chuỗi cùng type.
                while (r < rows
                    && board.getTile(r, c).getType() == type) {
                    r++;
                }

                int length = r - start;

                if (length >= 3) {

                    int anchor = index(start, c, cols);

                    for (int k = start; k < r; k++) {

                        int id = index(k, c, cols);

                        matched[id] = true;

                        uf.union(anchor, id);
                    }
                }
            }
        }

        // 3. Gom các tile đã match thành nhóm.
        Map<Integer, List<Tile>> groups =
            new LinkedHashMap<>();

        for (int r = 0; r < rows; r++) {

            for (int c = 0; c < cols; c++) {

                int id = index(r, c, cols);

                if (!matched[id]) {
                    continue;
                }

                int root = uf.find(id);

                groups.computeIfAbsent(
                    root,
                    ignored -> new ArrayList<>()
                ).add(board.getTile(r, c));
            }
        }

        return new ArrayList<>(groups.values());
    }

    /**
     * Giữ quy tắc lọc tile hiện tại.
     */
    private boolean isMatchable(int type) {

        return type != 0
            && type != 8
            && type < 10;
    }

    private int index(int row, int col, int cols) {
        return row * cols + col;
    }

    /**
     * Union-Find dùng để gom các chuỗi match
     * có chung một hoặc nhiều tile.
     */
    private static final class UnionFind {

        private final int[] parent;
        private final int[] size;

        private UnionFind(int count) {

            parent = new int[count];
            size = new int[count];

            for (int i = 0; i < count; i++) {

                parent[i] = i;
                size[i] = 1;
            }
        }

        /**
         * Tìm root, có path compression.
         * Không sử dụng đệ quy.
         */
        private int find(int x) {

            while (parent[x] != x) {

                parent[x] = parent[parent[x]];

                x = parent[x];
            }

            return x;
        }

        /**
         * Hợp nhất hai tập hợp.
         */
        private void union(int a, int b) {

            int rootA = find(a);
            int rootB = find(b);

            if (rootA == rootB) {
                return;
            }

            // Union by size.
            if (size[rootA] < size[rootB]) {

                int temp = rootA;
                rootA = rootB;
                rootB = temp;
            }

            parent[rootB] = rootA;

            size[rootA] += size[rootB];
        }
    }
}

