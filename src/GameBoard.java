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

    class DistributionNode{
        public Player player;
        public Tile startPoint;
        public int amount;
    }

    //constants
    final private int FIELDPEREDGE = 3;
    final private int WIDTH = FIELDPEREDGE * 8 - 1;
    final private int HEIGHT = FIELDPEREDGE * 8 - 3;

    //properties
    private Tile[][] board;
    private ArrayList<Integer> diceNumbers;
    private ArrayList<Integer> resources;
    private ArrayList<Port.PortType> ports;
    private ArrayList<DistributionNode>[] resourceDistributionList;
    private Tile robber;

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
        ports = new ArrayList<>();
        resourceDistributionList = new ArrayList[11];
        for(int i = 0; i < 11; i++){
            resourceDistributionList[i] = new ArrayList<>();
        }
        robber = null;
    }

    /**
     * Configurates the game
     * Shuffles dices and resources and fills hexagons with them
     */
    public void configurate(){
        addDiceNumbers();
        addResources();
        setPorts();
        setUpGameBoard();
    }

    /**
     * distrubutes ports to exact positions randomly
     */
    private void setPorts(){
        int[][] positions={ // (x,y,x,y)
                {10,0,12,0},{4,4,6,2},{0,10,2,8},{0,14,2,16},{6,18,8,18},{14,18,16,18},{20,16,22,14},{20,8,22,10},{16,2,18,4}
        };
        int[] portTypes={4,1,1,1,1,1};

        int ii = 0;
        for( Port.PortType pt : Port.PortType.values() ){
            for( int j = 0 ; j < portTypes[ii] ; j++ ){
                ports.add(pt);
            }
            ii++;
        }

        Collections.shuffle(ports);

        for( int i = 0 ; i < ports.size() ; i++ ){
            board[positions[i][1]][positions[i][0]].setPort(ports.get(i));
            board[positions[i][3]][positions[i][2]].setPort(ports.get(i));
        }
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
                robber = board[y][x];
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
                robber = board[y][x];
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

        for( int i = 0 ; i < 12 ; i++ ){
            if( x == startX && y == startY ){
                board[y][x].setDiceNumber(dice);
                board[y][x].setGameTile();
                board[y][x].setResource(resource);
                board[y][x].addStartPoint(board[startY][startX]);
                board[y][x].setStartPoint();
            }
            else {
                board[y][x].setGameTile();
                board[y][x].addStartPoint(board[startY][startX]);
            }

            if(i < 11){
                x += changeNext[i][0];
                y += changeNext[i][1];
            }
        }

        fillInsideWithStartPoint( startX, startY);
    }

    /**
     * fill inside of the given hexagon with their starting point by using bfs
     * @param x start point of the hexagon
     * @param y start point of the hexagon
     */
    private void fillInsideWithStartPoint( int x, int y){
        int[][] tiles = { // (x,y)
                {-1,2},{0,1},{0,2},{0,3},{1,1},{1,2},{1,3},{2,1},{2,2},{2,3},{3,2}
        };

        for( int i = 0 ; i < 11 ; i++){
            int targetX = x + tiles[i][0];
            int targetY = y + tiles[i][1];

            board[targetY][targetX].addStartPoint(board[y][x]);
        }
    }

    /**
     * returns if (x,y) coordinate is a game tile
     * @param x x-coordinate
     * @param y y-coordinate
     * @return return if (x,y) is gametile
     */
    public boolean isGameTile( int x, int y){
        return board[y][x].isItGameTile();
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
            if( board[y][x].getStructure() == null )
                return isValidForRoad(player,x,y); // return 0 or -1
            else
                return -4;
        }
        else{ // settlement or city
            if( board[y][x].getStructure() == null ){
                return isValidForCity(player, x, y, gameStatus); // return 1, -2 or -3
            }
            else if( isThereStructure(player, x , y) && board[y][x].getStructure().getType() == Structure.Type.SETTLEMENT){ //point value for settlement is 1, but it can be controlled by another way in the future
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
                board[y][x].getStructure() != null && board[y][x].getStructure().getOwner() == player;
    }

    /**
     * checks if there is a structure in (x,y), it can be road, city or settlement
     * @param x x-coordinate
     * @param y y-coordinate
     * @return returns true if it is possible
     */
    private boolean isThereStructure( int x, int y){
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && isGameTile(x, y) &&
                board[y][x].getStructure() != null;
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
     * @param type 0 = road
     *             1 = settlement
     *             2 = city
     */
    public void setStructure(Player player, int x, int y, Structure.Type type){
        if(type == Structure.Type.ROAD){
            Structure newRoad = new Road( player, x, y );
            board[y][x].setStructure(newRoad);
            player.addStructure(newRoad);
        }
        else if(type == Structure.Type.SETTLEMENT){
            Structure newSettlement = new Settlement( player, x, y );
            board[y][x].setStructure(newSettlement);
            player.addStructure(newSettlement);

            if(board[y][x].getPort() != null){
                player.addPort(board[y][x].getPort());
            }
        }
        else{
            Structure newCity = new City( player, x, y );
            board[y][x].setStructure(newCity);
            for(int i = 0 ; i < player.getStructures().size(); i++){
                if(player.getStructures().get(i).getX() == x && player.getStructures().get(i).getY() == y){
                    player.getStructures().remove(i);
                    player.addStructure(newCity);
                    break;
                }
            }
        }
        if(type != Structure.Type.ROAD){
            ArrayList<Tile> startPoints = board[y][x].getStartPoints();
            for(int i = 0; i < startPoints.size() ; i++){
                Tile startPoint = startPoints.get(i);
                int diceNumber = startPoint.getDiceNumber();
                boolean done = false;
                for(int j = 0; j < resourceDistributionList[diceNumber - 2].size() ; j++){
                    if(resourceDistributionList[diceNumber - 2].get(j).player == player){
                        resourceDistributionList[diceNumber - 2].get(j).amount++;
                        done = true;
                    }
                }
                DistributionNode newNode = new DistributionNode();
                newNode.player = player;
                newNode.startPoint = startPoint;
                newNode.amount = 1;
                resourceDistributionList[diceNumber - 2].add(newNode);
            }
        }
    }

    /**
     * Returns robber.
     * @return robber
     */
    public Tile getRobber(){
        return robber;
    }

    /**
     * Change location of the robber.
     * @param r new robber tile
     */
    public void changeRobber( Tile r ){
        robber = r;
    }

    public void changeRobber( int x, int y){
        Tile start = board[y][x].getStartPoints().get(0);

    }

    /**
     * Collect resources for each player before the game starts.
     */
    public void collectResources(){
        for(int i = 2; i < 13 ; i++){
            collectResources(i);
        }
    }

    /**
     * Collect resources for each player that belongs to hexagons related to given dice number.
     * @param diceNumber is given dice number
     */
    public void collectResources(int diceNumber){
        int len = resourceDistributionList[diceNumber - 2].size();
        for(int i = 0 ; i < len; i++){
            DistributionNode node = resourceDistributionList[diceNumber - 2].get(i);
            if(node.startPoint != robber)
                node.player.collectMaterial(node.startPoint.getResource(), node.amount);
        }
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
            if(player.getStructures().get(i).getType() == Structure.Type.ROAD){
                int [][] markedRoads = new int[HEIGHT][WIDTH];
                int roadX = player.getStructures().get(i).getX();
                int roadY = player.getStructures().get(i).getY();
                int j = 0;
                while(!board[roadY + possibleNeighbors[j][1]][roadX + possibleNeighbors[j][0]].isItGameTile())
                    j++;
                Structure cornerStructure = board[roadY + possibleNeighbors[j][1]][roadX + possibleNeighbors[j][0]].getStructure();
                if(cornerStructure == null || cornerStructure.getOwner() == player){
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

                cornerStructure = board[roadY - possibleNeighbors[j][1]][roadX - possibleNeighbors[j][0]].getStructure();
                if(cornerStructure == null || cornerStructure.getOwner() == player){
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
            if( targetY >= 0 && targetY < HEIGHT && targetX >= 0 && targetX < WIDTH && board[targetY][targetX].isItGameTile()){
                Structure targetStructure = board[targetY][targetX].getStructure();
                int roadX = x + possibleNeighbors[i][0] / 2;
                int roadY = y + possibleNeighbors[i][1] / 2;
                Structure road = board[roadY][roadX].getStructure();
                if(road != null && road.getOwner() == player && markedRoads[roadY][roadX] == 0){
                    neighbor = true;
                    if(targetStructure == null || targetStructure.getOwner() == player){
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
