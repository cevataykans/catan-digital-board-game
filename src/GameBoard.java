import java.util.ArrayList;
import java.util.Collections;

/**
 * Gameboard class that is used to represent the board of Catan game.
 * @author Yusuf Nevzat Şengün
 * @version 16.10.2019
 */

public class GameBoard {

    //constants
    final private int FIELDPEREDGE = 3;
    final private int WIDTH = FIELDPEREDGE * 8 - 1;
    final private int HEIGHT = FIELDPEREDGE * 8 - 3;

    //properties
    private Tile[][] board;
    private ArrayList<Integer> diceNumbers;
    private ArrayList<Integer> resources;

    //constructor
    public GameBoard(){
        board = new Tile[HEIGHT][WIDTH];
        for( int i = 0 ; i < HEIGHT ; i++ ){
            for( int j = 0 ; j < WIDTH ; j++ ){
                board[i][j] = new Tile();
            }
        }
        diceNumbers = new ArrayList<>();
        resources = new ArrayList<>();
    }

    /**
     * Configurates the game
     * Shuffles dices and resources and fills hexagons with them
     */
    public void configurate(){
        addDiceNumbers();
        addResources();
        setUpGameBoard();
    }

    /**
     * After decide counts of dice numbers add them to an arraylist and shuffles.
     * diceCounts are filled through 2 to 12
     */
    private void addDiceNumbers(){
        int[] diceCounts = {1,2,2,2,2,1,2,2,2,2,1};

        for( int i = 2 ; i <= 12 ; i++ )
            for( int j = 0 ; j < diceCounts[i - 2] ; j++ )
                diceNumbers.add(i);

        Collections.shuffle(diceNumbers);
    }

    /**
     * After decide counts of resources add them to an arraylist and shuffles
     * for the resourceCount array:
     *      0-index = saman
     *      1-index = odun
     *      2-index = mermer
     *      3-index = kaya
     *      4-index = koyun
     *      çöl -> will be assigned automatically when dice is 7
     */
    private void addResources(){
        int[] resourceCounts = {4,4,3,3,4};

        for( int i = 0 ; i <= 4 ; i++ )
            for( int j = 0 ; j < resourceCounts[i] ; j++ )
                resources.add(i);

        Collections.shuffle(resources);
    }

    /**
     * Determine the most upper-left game tile and start the process from there, downward
     */
    private void setUpGameBoard(){
        int x = WIDTH / 2 - 1;
        int y = 0;

        for( int i = 1 ; y < HEIGHT ; y += 4, i++ )
            setUpByTraversingHexagon( x, y, i);
    }

    /**
     * Starting from the given x and y traverse toward left and right, traverse only the start points of the hexagons,
     * other tiles of the hexagons are filled in another method
     * @param x x-coordinate of the start point
     * @param y y-coordinate of the start point
     * @param numberOfHexagon number of the field from the top, with this param we can know the boundaries of game board
     */
    private void setUpByTraversingHexagon( int x, int y, int numberOfHexagon){
        setUpLeftHexagon( x, y, numberOfHexagon); //center hexagon is traversed here
        setUpRightHexagon( x + 4, y + 2, numberOfHexagon);
    }

    /**
     * traverse toward left, traverse only the starting point of the hexagons
     * @param x x-coordinate of the start point
     * @param y y-coordinate of the start point
     * @param numberOfHexagon number of the field from the top, with this param we can know the boundaries of game board
     */
    private void setUpLeftHexagon( int x, int y, int numberOfHexagon){
        int iterationNum = (numberOfHexagon<=FIELDPEREDGE ? FIELDPEREDGE: FIELDPEREDGE * 2 - numberOfHexagon);

        int dice, resource;
        for( int i = 1 ; i <= iterationNum ; x -= 4, y += 2, i++ ){
            dice = diceNumbers.get(diceNumbers.size() - 1);
            diceNumbers.remove(diceNumbers.size()-1);

            if( dice != 7 ) {
                resource = resources.get(resources.size() - 1);
                resources.remove( resources.size() - 1);
            }
            else{ // if dice is 7 then this hexagon will be desert
                resource = 5;
            }
            fillHexagon( x, y, dice, resource);
        }
    }

    /**
     * traverse toward right, traverse only the starting point of the hexagons
     * @param x x-coordinate of the start point
     * @param y y-coordinate of the start point
     * @param numberOfHexagon number of the field from the top, with this param we can know the boundaries of game board
     */
    private void setUpRightHexagon( int x, int y, int numberOfHexagon){
        int iterationNum = (numberOfHexagon<=FIELDPEREDGE ? FIELDPEREDGE : FIELDPEREDGE * 2 - numberOfHexagon ) - 1;

        int dice, resource;
        for( int i = 1 ; i <= iterationNum ; x += 4, y += 2, i++ ){
            dice = diceNumbers.get(diceNumbers.size() - 1);
            diceNumbers.remove(diceNumbers.size()-1);
            if( dice != 7 ) {
                resource = resources.get(resources.size() - 1);
                resources.remove( resources.size() - 1);
            }
            else{ // if dice is 7 then this hexagon will be desert
                resource = 5;
            }
            fillHexagon( x, y, dice, resource);
        }
    }

    /**
     * traverse all tiles of the current hexagon
     * resource and dice info are kept on start tile of each hexagon,
     * and all other tiles will know their all starting points as a list(1 tile can belong to more than one hexagon)
     * @param x x-coordinate of the start point of this hexagon
     * @param y y-coordinate of the start point of this hexagon
     * @param dice determined dice number for this hexagon
     * @param resource determined resource for this hexagon
     */
    private void fillHexagon( int x, int y, int dice, int resource){
        // keeps the distance from the last tile as x, y
        int[][] changeNext = {
                {-1,1}, {-1,1},
                {1,1}, {1,1},
                {1,0}, {1,0},
                {1,-1}, {1,-1},
                {-1,-1}, {-1,-1},
                {-1,0},
        };

        // keeps the start point of this hexagon
        int startX = x;
        int startY = y;

        for( int i = 0 ; i < 11 ; i++ ){
            if( x == startX && y == startY ){
                board[x][y].setDiceNumber(dice);
                board[x][y].setGameTile();
                board[x][y].setResource(resource);
                board[x][y].addStartPoint(board[startX][startY]);
                board[x][y].setStartPoint();
            }
            else {
                board[x][y].setGameTile();
                board[x][y].addStartPoint(board[startX][startY]);
            }

            x += changeNext[i][0];
            y += changeNext[i][1];
        }
    }

    /**
     * returns if (x,y) coordinate is a game tile
     * @param x x-coordinate
     * @param y y-coordinate
     * @return return if (x,y) is gametile
     */
    public boolean isGameTile( int x, int y){
        return board[x][y].isItGameTile();
    }

    /**
     * (x,y) TILE NEED TO BE GAMETILE, IF NOT THEN DO NOT USE THIS METHOD !!
     *
     * this method shows a structure that can be build in (x,y) by current player if it is not possible then returns an error
     * @param player the player who want to build something
     * @param x x-coordinate
     * @param y y-coordinate
     * @param gameStatus if the game in setup mode then gamestatus = 0, otherwise 1 (according to me)
     * @return  0 = road can be built here
     *          1 = settlement can be built here
     *          2 = city can be built here
     *          errors
     *          -1 = there is no connection for road to build
     *          -2 = there is no connection for city to build
     *          -3 = there is a building near
     *          -4 = this tile is occupied by a road, city or other players structure, in this case there is no need to explain anything
     */
    public int checkStructure( Player player, int x, int y, int gameStatus){
        if( x % 2 == 1 ){ // road
            if( board[x][y].getStructure() == null )
                return isValidForRoad(player,x,y); // return 0 or -1
            else
                return -4;
        }
        else{ // settlement or city
            if( board[x][y].getStructure() == null ){
                return isValidForCity(player, x, y, gameStatus); // return 1, -2 or -3
            }
            else if( isThereStructure(player, x , y) && board[x][y].getStructure().getPointValue() == 1){ //point value for settlement is 1, but it can be controlled by another way in the future
                return 2;
            }
            else
                return -4;
        }
    }

    /**
     * checks if it is possible to build a road here for this player
     * @param player current player
     * @param x x-coordinate
     * @param y y-coordinate
     * @return  0 = road can be built here
     *          -1 = there is no connection for road to build
     */
    private int isValidForRoad( Player player, int x, int y){
        int[][] possibleNeighbors = {
                {-2,-1}, {-2,1}, {2,-1}, {2,1}, {0,-2}, {0,2}, // possible road neighbors
                {-1,0}, {1,0}, {-1,-1}, {1,1}, {-1,1}, {1, -1} // possible settlement/city neighbors
        };
        int returnValue = -1;

        for( int i = 0 ; i < 12 ; i++ ){
            int targetX = x + possibleNeighbors[i][0];
            int targetY = y + possibleNeighbors[i][1];

            if( isThereStructure(player, targetX, targetY))
                returnValue = 0;
        }

        return returnValue;
    }

    /**
     * checks if there is a structure in (x,y) belong to the specified player, it can be road, city or settlement
     * @param player current player
     * @param x x-coordinate
     * @param y y-coordinate
     * @return returns true if it is possible
     */
    private boolean isThereStructure( Player player, int x, int y){
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && isGameTile(x, y) &&
                board[x][y].getStructure() != null && board[x][y].getStructure().getOwner() == player;
    }

    /**
     * checks if it is possible to build a building here for this player
     * @param player current playere
     * @param x x-coordinate
     * @param y y-coordinate
     * @param gameStatus 0 = game is in setup mode, otherwise not 0
     * @return  1 = settlement can be built here
     *          -2 = there is no connection for city to build
     *          -3 = there is a building near
     */
    private int isValidForCity( Player player, int x, int y, int gameStatus){
        int[][] possibleRoadNeighbors = {
                {-2,-1}, {-2,1}, {2,-1}, {2,1}, {0,-2}, {0,2}
        };
        int[][] possibleBuildingNeighbors = {
                {-1,0}, {1,0}, {-1,-1}, {1,1}, {-1,1}, {1, -1}
        };
        boolean buildingError = false;
        boolean connectionError = true;

        for( int i = 0 ; i < 6 ; i++ ){ // checking road connection
            int targetX = x + possibleRoadNeighbors[i][0];
            int targetY = y + possibleRoadNeighbors[i][1];

            if( isThereStructure(player, targetX, targetY) )
                connectionError = false;
        }

        for( int i = 0 ; i < 6 ; i++ ){ // checking any neighbor building
            int targetX = x + possibleBuildingNeighbors[i][0];
            int targetY = y + possibleBuildingNeighbors[i][1];

            if( isThereStructure(player, targetX, targetY) )
                buildingError = true;
        }

        if( gameStatus == 0 && buildingError == false )
            return 1;
        else if( gameStatus == 1 && buildingError == false && connectionError == false )
            return 1;
        else if( buildingError == true )
            return -3;
        else if( connectionError == true )
            return -2;
    }

    /**
     * sets a structrure to (x,y) with the given player and type
     * (value of type can be directed from checkStructure class)
     * @param player current player
     * @param x x-coordinate
     * @param y y-coordinate
     * @param type 0 = road
     *             1 = settlement
     *             2 = city
     */
    public void setStructure(Player player, int x, int y, int type){
        board[x][y].setStructure( new Structure( type, player));
    }
}
