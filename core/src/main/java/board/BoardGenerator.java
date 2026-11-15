package board;

import gameplay.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class BoardGenerator {
    public static final Random random = new Random();
//    public static final int[]
//    private Board board;
//    private int rows, cols;
//    private List<Tile> fixedPositions;

//    public void boardGeneralBuild() {
//        board = new Board(rows, cols);
//        for(Tile t : fixedPositions) board.setTile(t, t.getRow(), t.getCol());
//    }

    public static Board generate(int rows, int cols, List<Tile> fixedPositions, int[] availableTypes) {
        Board board = new Board(rows, cols);
        for(Tile t : fixedPositions) board.setTile(t, t.getRow(), t.getCol());

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean outerContinue = false;
                for(Tile t : fixedPositions)
                    if(r == t.getRow() && c == t.getCol()) {
                        board.setTileType(r, c, t.getType());
                        outerContinue = true;
                        break;
                    }
                if(outerContinue) continue;

                int index = random.nextInt(availableTypes.length);
                if(r < 2 && c > 1) {
                    while(board.getTile(r, c - 2).getType() == availableTypes[index]
                        && board.getTile(r, c - 1).getType() == availableTypes[index])
                        index = random.nextInt(availableTypes.length);
                }
                else if(c < 2 && r > 1) {
                    while(board.getTile(r - 2, c).getType() == availableTypes[index]
                        && board.getTile(r - 1, c).getType() == availableTypes[index])
                        index = random.nextInt(availableTypes.length);
                }
                else if(r > 1) {
                    while(board.getTile(r, c - 2).getType() == availableTypes[index]
                        && board.getTile(r, c - 1).getType() == availableTypes[index]
                        || board.getTile(r - 2, c).getType() == availableTypes[index]
                        && board.getTile(r - 1, c).getType() == availableTypes[index])
                        index = random.nextInt(availableTypes.length);
                }
                board.setTileType(r, c, availableTypes[index]);
            }
        }

        if(hasAvailableMove(board, rows, cols)) return board;
        else return generate(rows, cols, fixedPositions, availableTypes);
    }

    public static boolean hasAvailableMove(Board board, int rows, int cols) {
        boolean isAvailable = false, outerLoopBreak = false;
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                if(c < cols - 2) {
                    if(r < rows - 1) {
                        if(GameRules.canMerge(board.getTile(r, c), board.getTile(r, c + 1), board.getTile(r + 1, c + 2))
                            || GameRules.canMerge(board.getTile(r, c), board.getTile(r + 1, c + 1), board.getTile(r, c + 2))
                            || GameRules.canMerge(board.getTile(r, c), board.getTile(r + 1, c + 1), board.getTile(r + 1, c + 2))) {
                            isAvailable = true;
                            outerLoopBreak = true;
                            break;
                        }
                    }
                    if(r > 0) {
                        if(GameRules.canMerge(board.getTile(r, c), board.getTile(r, c + 1), board.getTile(r - 1, c + 2))
                            || GameRules.canMerge(board.getTile(r, c), board.getTile(r - 1, c + 1), board.getTile(r, c + 2))
                            || GameRules.canMerge(board.getTile(r, c), board.getTile(r - 1, c + 1), board.getTile(r - 1, c + 2))) {
                            isAvailable = true;
                            outerLoopBreak = true;
                            break;
                        }
                    }
                }
                if(r < rows - 2) {
                    if(c < cols - 1) {
                        if(GameRules.canMerge(board.getTile(r, c), board.getTile(r + 1, c), board.getTile(r + 2, c + 1))
                            || GameRules.canMerge(board.getTile(r, c), board.getTile(r + 1, c + 1), board.getTile(r + 2, c))
                            || GameRules.canMerge(board.getTile(r, c), board.getTile(r + 1, c + 1), board.getTile(r + 2, c + 1))) {
                            isAvailable = true;
                            outerLoopBreak = true;
                            break;
                        }
                    }
                    if(c > 0) {
                        if(GameRules.canMerge(board.getTile(r, c), board.getTile(r + 1, c), board.getTile(r + 2, c - 1))
                            || GameRules.canMerge(board.getTile(r, c), board.getTile(r + 1, c - 1), board.getTile(r + 2, c))
                            || GameRules.canMerge(board.getTile(r, c), board.getTile(r + 1, c - 1), board.getTile(r + 2, c - 1))) {
                            isAvailable = true;
                            outerLoopBreak = true;
                            break;
                        }
                    }
                }
            }
            if(outerLoopBreak) break;
        }
        return isAvailable;
    }

    public static void shuffleBoard(Board board, int rows, int cols) {
        Tile[] boardTilesList = new Tile[board.getRows()*board.getCols()];
        int[] indexList = new int[board.getRows()*board.getCols()];
        int count = 0;

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                int tileType = board.getTile(r, c).getType();
                if(tileType > 0 && tileType < 11) {
                    boardTilesList[count] = board.getTile(r, c);
                    indexList[count] = count;
                    count++;
                }
            }
        }


    }

    void swapTile(Tile t1, Tile t2) {
        Tile temp = new Tile();
        temp.copyAllValueFrom(t1);
        t1.copyAllValueFrom(t2);
        t2.copyAllValueFrom(temp);
    }

    void swapTileIndex(int i1, int i2) {
        int temp = i1;
        i1 = i2;
        i2 = temp;
    }

    void partitionSort(int[] indexList, Tile[] tileList, int L, int R) {
        if(L >= R) return;
        int i = L + 1, j = R;
        while(i <= j)
        {
            while(indexList[i] < indexList[L]) i++;
            while(indexList[j] > indexList[L]) j--;
            if(i < j)
            {
                swapTileIndex(indexList[i], indexList[j]);
                swapTile(tileList[i], tileList[j]);
                i++;
                j--;
            }
        }
        swapTileIndex(indexList[L], indexList[j]);
        swapTile(tileList[L], tileList[j]);
        partitionSort(indexList, tileList, L, j - 1);
        partitionSort(indexList, tileList, j + 1, R);
    }

    void shuffleList(int[] indexList, Tile[] tileList, int listSize) {
        TreeMap map = new TreeMap<>();
        int i, j, k, bsize = 1, randomvalue;
        boolean randomvaluechanged;
        int *b = (int*)malloc(n*sizeof(int));
        srand(time(NULL));
        a[0] = rand()%listSize;
        b[0] = a[0];
        for(i=1;i<listSize-1;i++) {
            randomvalue = rand()%listSize;
            randomvaluechanged = false;
            for(j=0;j<bsize;j++) {
                if(j==b[j]) {
                    if(randomvalue==j) {
                        randomvalue++;
                        randomvaluechanged = true;
                    }
                }
                else break;
            }
            if(!randomvaluechanged)
                for(j=bsize-1;j>=0;j--)  {
                    if(j<b[j]) {
                        if(randomvalue==b[j]) {
                            if(b[j]==j-bsize+listSize) randomvalue--;
                            else {
                                k = j;
                                while(randomvalue==b[k]) {
                                    randomvalue++;
                                    k++;
                                }
                                break;
                            }
                        }
                    }
                    else break;
                }
            a[i] = randomvalue;
            b[bsize] = randomvalue;
            partitionsort(b,0,bsize);
            bsize++;
        }
        for(i=0;i<bsize;i++)
            if(b[i]>i) {
                a[listSize-1] = i;
                return;
            }
        if(i==bsize) a[listSize-1] = listSize - 1;
    }
}
