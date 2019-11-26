import java.util.ArrayList;
import java.util.Collections;

/**
 * Gameboard class that is used to represent the board of Catan game.
 * (Horizontal line is X, vertical line is Y)
 * @author Yusuf Nevzat Şengün
 * @version 16.10.2019
 * --------------
 * Log 20.10.2019 (Hakan)
 * Implemented collectResources, longestRoadOfPlayer methods
 * Added robberX and robberY attributes and theirs getter,setter methods
 * Small mistakes were fixed
 */

public class GameBoard {

    //constants
    final private int FIELDPEREDGE = 3;
    final private int WIDTH = FIELDPEREDGE * 8 - 1;
    final private int HEIGHT = FIELDPEREDGE * 8 - 3;

    //properties
    private Tile[][] board;
    private GameBoardBuilder builder;
    private StartTile robber;

    //constructor
    public GameBoard()
    {
        builder = new GameBoardBuilder();
    }

    /**
     * Configurates the game
     * Shuffles dices and resources and fills hexagons with them
     */
    public void configurate(){

        builder.configurate();
        this.robber = builder.getRobber();
        this.board = builder.getBoard();
    }

    /**
     * return rotation type of the road
     * @param x x coordinate of the road
     * @param y y coordinate of the road
     * @return rotation type of the road
     */
    public RoadTile.RotationType rotationType( int x, int y){
        return ((RoadTile)board[y][x]).getRotation();
    }

    /**
     * returns if (x,y) coordinate is a game tile
     * @param x x-coordinate
     * @param y y-coordinate
     * @return return if (x,y) is gametile
     */
    public boolean isGameTile( int x, int y){
        return board[y][x] instanceof StructureTile;
    }

    /**
     * return if this tile is inside tile but not gametile
     * @param x x coordinate
     * @param y y coordinate
     * @return the result
     */
    public boolean isInsideTile( int x, int y){
        return board[y][x] instanceof InsideTile;
    }

    /**
     * return gameBoard for UI
     * @return gameboard
     */
    public Tile[][] getBoard()
    {
        return this.board;
    }

    /**
     * get specified tile
     * @param x x coordinate
     * @param y y coordinate
     * @return the tile
     */
    public Tile getTile( int x, int y){
        return board[y][x];
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
        if( board[y][x] instanceof RoadTile ){ // road
            if( !((RoadTile)board[y][x]).getAvailability() )
                return isValidForRoad(player,x,y); // return 0 or -1
            else
                return -4;
        }
        else{ // settlement or city
            if( !((BuildingTile)board[y][x]).getAvailability() ){
                return isValidForSettlement(player, x, y, gameStatus); // return 1, -2 or -3
            }
            else if( isThereStructure(player, x , y) && ((BuildingTile)board[y][x]).getType() == BuildingTile.BuildingType.SETTLEMENT){
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
        int[][] possibleNeighbors = { // (x,y)
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
                ((StructureTile)board[y][x]).getAvailability() && ((StructureTile)board[y][x]).getOwner() == player;
    }

    /**
     * checks if there is a structure in (x,y), it can be road, city or settlement
     * @param x x-coordinate
     * @param y y-coordinate
     * @return returns true if it is possible
     */
    private boolean isThereStructure( int x, int y){
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && isGameTile(x, y) &&
                ((StructureTile)board[y][x]).getAvailability();
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
    private int isValidForSettlement( Player player, int x, int y, int gameStatus){
        int[][] possibleRoadNeighbors = { // (x,y)
                {-1,0}, {1,0}, {-1,-1}, {-1,1}, {1,-1}, {1,1}
        };
        int[][] possibleBuildingNeighbors = { // (x,y)
                {-2,0}, {2,0}, {-2,-2}, {-2,2}, {2,-2}, {2,2}
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

            if( isThereStructure( targetX, targetY) )
                buildingError = true;
        }

        if( gameStatus == 0 && !buildingError)
            return 1;
        else if( gameStatus == 1 && !buildingError && !connectionError)
            return 1;
        else if(buildingError)
            return -3;
        else if(connectionError)
            return -2;
        return -1;
    }

    /**
     * sets a structrure to (x,y) with the given player and type
     * (value of type can be directed from checkStructure class)
     * @param player current player
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void setStructure(Player player, int x, int y){
        if( board[y][x] instanceof RoadTile){ // road building
            ((RoadTile)board[y][x]).setAvailability();
            ((RoadTile)board[y][x]).setOwner( player);
            player.addStructure( (RoadTile)board[y][x]);
        }
        else if( !((BuildingTile)board[y][x]).getAvailability()){ // settlement building
            ((BuildingTile)board[y][x]).setAvailability();
            ((BuildingTile)board[y][x]).setOwner( player);
            player.addStructure( (BuildingTile)board[y][x]);

            if(((BuildingTile)board[y][x]).getPort() != null){
                player.addPort(((BuildingTile)board[y][x]).getPort());
            }
        }
        else{ // city building
            ((BuildingTile)board[y][x]).upgradeToCity();
        }
    }

    /**
     * returns neighbor player except the specified player
     * @param player expect this player
     * @param x x-coordinate
     * @param y y-coordinate
     * @return neighbor players except the current players
     */
    public ArrayList<Player> getNeighborPlayers( Player player, int x, int y){
        int[][] changeNext = {
                {-1,1}, {-1,1},
                {1,1}, {1,1},
                {1,0}, {1,0},
                {1,-1}, {1,-1},
                {-1,-1}, {-1,-1},
                {-1,0}
        };
        ArrayList<Player> ret = new ArrayList<>();

        x = ((InsideTile)board[y][x]).getStartTile().getX();
        y = ((InsideTile)board[y][x]).getStartTile().getY();

        for( int i = 0 ; i < 12 ; i++ ){
            if( ((StructureTile)board[y][x]).getAvailability() && ((StructureTile)board[y][x]).getOwner() != player && !ret.contains(((StructureTile)board[y][x]).getOwner()) )
                ret.add( ((StructureTile)board[y][x]).getOwner());

            if(i < 11){
                x += changeNext[i][0];
                y += changeNext[i][1];
            }
        }

        return ret;
    }

    /**
     * Returns robber.
     * @return robber
     */
    public StartTile getRobber(){
        return robber;
    }

    /**
     * change robber position
     * @param x x coordinate
     * @param y y coordinate
     */
    public void changeRobber( int x, int y){
        robber = ((InsideTile)board[y][x]).getStartTile();
    }

    /**
     * Return length of the longest road of the player.
     * @param player current player
     * @return  length of the longest road of the player
     */
    public int longestRoadOfPlayer(Player player){
        int [][] possibleNeighbors = {
            {1,0}, {1,1}, {1,-1}
        };
        ArrayList<Integer> distances = new ArrayList<>();
        for(int i = 0 ; i < player.getStructures().size() ; i++){
            if(player.getStructures().get(i) instanceof RoadTile){
                int [][] markedRoads = new int[HEIGHT][WIDTH];
                int roadX = player.getStructures().get(i).getX();
                int roadY = player.getStructures().get(i).getY();
                int j = 0;
                while( !(board[roadY + possibleNeighbors[j][1]][roadX + possibleNeighbors[j][0]] instanceof StructureTile))
                    j++;
                StructureTile cornerStructure = (StructureTile) board[roadY + possibleNeighbors[j][1]][roadX + possibleNeighbors[j][0]];
                if(!cornerStructure.getAvailability() || cornerStructure.getOwner() == player){
                    int startX = roadX - possibleNeighbors[j][0];
                    int startY = roadY - possibleNeighbors[j][1];
                    int targetX = roadX + possibleNeighbors[j][0];
                    int targetY = roadY + possibleNeighbors[j][1];
                    System.out.println("Road: " + roadX + ", " + roadY);
                    System.out.println("Beginning: " + startX + ", " + startY);
                    markedRoads[roadY][roadX] = 1;
                    int distance = checkLongestRoadFromThatEdge(player, targetX, targetY, markedRoads);
                    markedRoads[roadY][roadX] = 0;
                    distances.add(distance);
                }

                cornerStructure = (StructureTile) board[roadY - possibleNeighbors[j][1]][roadX - possibleNeighbors[j][0]];
                if(!cornerStructure.getAvailability() || cornerStructure.getOwner() == player){
                    int startX = roadX + possibleNeighbors[j][0];
                    int startY = roadY + possibleNeighbors[j][1];
                    int targetX = roadX - possibleNeighbors[j][0];
                    int targetY = roadY - possibleNeighbors[j][1];
                    System.out.println("Road: " + roadX + ", " + roadY);
                    System.out.println("Beginning: " + startX + ", " + startY);
                    markedRoads[roadY][roadX] = 1;
                    int distance = checkLongestRoadFromThatEdge(player, targetX, targetY, markedRoads);
                    markedRoads[roadY][roadX] = 0;
                    distances.add(distance);
                }
            }
        }
        if(distances.size() == 0)
            return 0;
        return Collections.max(distances);
    }

    /**
     * Private helper method for recursion
     */
    private int checkLongestRoadFromThatEdge(Player player, int x, int y, int[][] markedRoads){
        System.out.println("X: " + x + " Y: " + y);
        int[][] possibleNeighbors = {
            {2,0}, {-2,0}, {-2,-2}, {-2,2}, {2,-2}, {2,2}
        };

        ArrayList<Integer> distances = new ArrayList<>();

        boolean neighbor = false;
        for(int i = 0 ; i < 6 ; i++){
            int targetX = x + possibleNeighbors[i][0];
            int targetY = y + possibleNeighbors[i][1];
            if( targetY >= 0 && targetY < HEIGHT && targetX >= 0 && targetX < WIDTH && (board[targetY][targetX] instanceof StructureTile)){
                StructureTile targetStructure = (StructureTile) board[targetY][targetX];
                int roadX = x + possibleNeighbors[i][0] / 2;
                int roadY = y + possibleNeighbors[i][1] / 2;
                StructureTile road = (StructureTile) board[roadY][roadX];
                if(!road.getAvailability() && road.getOwner() == player && markedRoads[roadY][roadX] == 0){
                    neighbor = true;
                    if(!targetStructure.getAvailability() || targetStructure.getOwner() == player){
                        markedRoads[roadY][roadX] = 1;
                        int distance = checkLongestRoadFromThatEdge(player, targetX, targetY, markedRoads);
                        markedRoads[roadY][roadX] = 0;
                        distances.add(distance);
                        
                    }
                    else{
                        System.out.println("oops " + targetX + ", " + targetY);
                        distances.add(1);
                    } 
                }               
            }
        }
        if(neighbor)
            return Collections.max(distances) + 1;
        return 1;
    }
}
