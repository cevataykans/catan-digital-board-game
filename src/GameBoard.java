import java.util.ArrayList;
import java.util.Collections;

public class GameBoard {

    final int FIELDPEREDGE = 3;
    final int WIDTH = FIELDPEREDGE * 8 - 1;
    final int HEIGHT = FIELDPEREDGE * 8 - 3;

    Tile board[HEIGHT][WIDTH];
    ArrayList<Integer> diceNumbers;
    ArrayList<Integer> resources;

    public GameBoard(){
        addDiceNumbers();
        addResources();
        setUpGameBoard();
    }

    private void addDiceNumbers(){
        // through 2 to 12
        int[] diceCounts = {1,2,2,2,2,1,2,2,2,2,1};

        for( int i = 2 ; i <= 12 ; i++ )
            for( int j = 0 ; j < diceCounts[i - 2] ; j++ )
                diceNumbers.add(i);

        Collections.shuffle(diceNumbers);
    }

    private void addResources(){
        /*
            saman
            odun
            mermer
            kaya
            koyun
            col
         */
        int[] resourceCounts = {4,4,3,3,4};

        for( int i = 1 ; i <= 5 ; i++ )
            for( int j = 0 ; j < resourceCounts[i - 1] ; j++ )
                resources.add(i);

        Collections.shuffle(resources);
    }

    private void setUpGameBoard(){
        int x = WIDTH / 2 - 1;
        int y = 0;

        for( int i = 1 ; y < HEIGHT ; y += 4, i++ )
            setUpByTraversingHexagon( x, y, i);
    }

    private void setUpByTraversingHexagon( int x, int y, int numberOfField){
        setUpLeftHexagon( x, y, numberOfField);
        setUpRightHexagon( x + 4, y + 2, numberOfField);
    }

    private void setUpLeftHexagon( int x, int y, int numberOfField){
        int iterationNum = (numberOfField<=FIELDPEREDGE ? FIELDPEREDGE: FIELDPEREDGE * 2 - numberOfField);

        int dice, resource;
        for( int i = 1 ; i <= iterationNum ; x -= 4, y += 2, i++ ){
            dice = diceNumbers.get(diceNumbers.size() - 1);
            diceNumbers.remove(diceNumbers.size()-1);
            resource = 6;
            if( dice != 7 ) {
                resource = resources.get(resources.size() - 1);
                resources.remove( resources.size() - 1);
            }
            fillHexagon( x, y, dice, resource);
        }
    }

    private void setUpRightHexagon( int x, int y, int numberOfField){
        int iterationNum = (numberOfField<=FIELDPEREDGE ? FIELDPEREDGE - 1: FIELDPEREDGE * 2 - numberOfField - 1);

        int dice, resource;
        for( int i = 1 ; i <= iterationNum ; x += 4, y += 2, i++ ){
            dice = diceNumbers.get(diceNumbers.size() - 1);
            diceNumbers.remove(diceNumbers.size()-1);
            resource = 6;
            if( dice != 7 ) {
                resource = resources.get(resources.size() - 1);
                resources.remove( resources.size() - 1);
            }
            fillHexagon( x, y, dice, resource);
        }
    }

    private void fillHexagon( int x, int y, int dice, int resource){
        int[][] changeNext = {
                {-1,1}, {-1,1},
                {1,1}, {1,1},
                {1,0}, {1,0},
                {1,-1}, {1,-1},
                {-1,-1}, {-1,-1},
                {-1,0},
        };

        int startX = x;
        int startY = y;

        for( int i = 0 ; i < 11 ; i++ ){
            board.setIsGameTile(true);
            board.setDiceNumber(dice);
            board.setResource(resource);
            board.belong.add(startX,startY);

            x += changeNext[i][0];
            y += changeNext[i][1];
        }
    }
}
