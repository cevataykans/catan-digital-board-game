import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Game class combines logic of the game board and players to enable users to play the game.
 * @author Yusuf Nevzat Sengun
 * @version 04.11.2019
 *
 * Game class combines logic of the game board and players to enable users to play the game.
 * @author Cevat Aykan Sevinc
 * @version 14.10.2019
 * Class is created to later combine all the logic.
 * Log 16.10.2019 (Talha)
 * -----------------------
 * Created class attributes, constructore and functions.
 * Added function templates.
 * Implemented some functions.
 * -----------------------
 */

// todo port

public class Game
{
    // Constants
    private final int TOTAL_DEV_CARDS = 25;

    // Attributes
    private GameBoard board;
    private ArrayList<Player> players;
    private ResourceDistributer distributor;

    private int turnNumber;
    private int playerCount;
    private int gameStatus;
    private int currentDice;
    private int longestRoad;
    private int largestArmy;
    private Player longestRoadPlayer;
    private Player largestArmyPlayer;

    /*
       -1 = there is no must
        0 = road need to be built
        1 = settlement need to be built
        2 = city need to be built
        3 = inside tile selection ( for robber selection )
        4 = resource selection (for monopoly card)
        5 = resource selection (for year of plenty card)
        6 = end turn ( we will end the turn automatically, do not wait player to end )
        7 = roll dice
        8 = get neighbor players ( after robber is places ) (resource selection for robber placement?)
     */
    private Queue<Integer> must;

    private Stack<Card> devCards;

    // Constructors
    public Game( ArrayList<Player> players )
    {
        longestRoadPlayer = null;
        longestRoad = 4; // minumum requierement to get this card
        this.largestArmy = 2; // minumum req to earn the army title
        this.largestArmyPlayer = null;
        gameStatus = 0; // setup mode
        this.players = players;
        playerCount = players.size();
        board = new GameBoard();
        turnNumber = 0;
        devCards = new Stack<>();
        must = new LinkedList<>();
        for( int i = 0; i < TOTAL_DEV_CARDS; i++)
        {
            Card card;
            if ( i < 14)
                card = new Card(Card.CardType.KNIGHT);
            else if ( i < 16)
                card = new Card(Card.CardType.MONOPOLY);
            else if ( i < 18)
                card = new Card(Card.CardType.ROADBUILDING);
            else if ( i < 20)
                card = new Card(Card.CardType.YEAROFPLENTY);
            else
                card = new Card(Card.CardType.VICTORYPOINT);
            devCards.push(card);
        }
    }

    // Functions
    /**
     * Initializes the game via initializing gameboard, players, robber and the turn.
     */
    public ArrayList<Player> configureGame()
    {
        // The order must not be changed!
        board.configurate();
        distributor = new ResourceDistributer( board.getBoard() );

        Collections.shuffle(players);
        Collections.shuffle(devCards);

        must.add(1); // settlement
        must.add(0); // road
        must.add(6); // end turn

        return players;
    }

    /**
     * end turn and add must for the next turn
     * @return if the game is end
     */
    public boolean endTurn(){
        if( getCurrentPlayer().getScore() >= 10 ) // todo 10 puana ulasinca end turnu beklemeden bitmeli oyun
            return true;

        turnNumber++;
        if( turnNumber == 2*playerCount)
        {
            distributor.collectResources( board.getRobber() );
            must.add(7); // roll dice
            gameStatus = 1;
        }
        else if( gameStatus == 0 )
        {
            must.add(1); // settlement
            must.add(0); // road
            must.add(6); // end turn
        }
        else {
            must.add(7); // roll dice
        }
        return false;
    }

    /**
     * rolls dice and return result
     * @return dice numbers for both dice
     */
    public ArrayList<Integer> rollDice(){
        int firstDice =  (int)(Math.random()*6+1);
        int secondDice =  (int)(Math.random()*6+1);

        currentDice = firstDice + secondDice;

        ArrayList<Integer> ret = new ArrayList<>();
        ret.add(firstDice);
        ret.add(secondDice);

        // After the dice is rolled, players must collect resource and current player must be able to play previously
        // bought development cards!
        collectResources();
        getCurrentPlayer().makeCardsPlayable();

        return ret;
    }

    /**
     * collect resources after dice has been rolled or moves robber
     */
    private void collectResources(){
        if( currentDice == 7 )
        {
            for( Player player : players )
            {
                player.discardHalfOfResources();
            }
            must.add(3); // inside tile
            must.add(8); // get neighbors
        }
        else {
            distributor.collectResources( currentDice, board.getRobber() );
        }
    }


    /**
     * Returns a specific player given in the player index.
     * @param playerIndex is the specific index of the player.
     * @return the player in the given index.
     */
    public Player getPlayer(int playerIndex)
    {
        return players.get(playerIndex);
    }

    /**
     * Returns the player playing the current turn.
     * @return the current player.
     */
    public Player getCurrentPlayer()
    {
        // If the game is at its construction stage and each player plays 2 turns at total
        if(gameStatus == 0 && turnNumber >= playerCount)
            return players.get(2 * playerCount - turnNumber - 1);
        return players.get(turnNumber % playerCount); // If the game has started and turns are in a loop, return the player with associated turn number.
    }

    public int getCurrentPlayerIndex()
    {
        // If the game is at its construction stage and each player plays 2 turns at total
        if(gameStatus == 0 && turnNumber >= playerCount)
            return 2 * playerCount - turnNumber - 1;
        return turnNumber % playerCount; // If the game has started and turns are in a loop, return the player index with associated turn number.
    }

    /**
     * Returns the current turn number players are playing.
     * @return the turn number.
     */
    public int getTurn()
    {
        return turnNumber;
    }

    /**
     * Adds a development card taken out from the stack to the current player.
     */
    public void addDevelopmentCard()
    {
        Player cp = getCurrentPlayer();

        Card tmp = devCards.peek();
        devCards.pop();

        cp.buyDevelopmentCard( Card.REQUIREMENTS_FOR_CARD , tmp);
    }

    /**
     * Plays a development card specified from the current player. The effect of the card will take place depending on
     * the type. must will be loaded. actions will take place after input is taken at other specified functions
     * @param card is the development card that will be played with its effect.
     */
    public void playDevelopmentCard(Card card)
    {
        if ( card.getType() == Card.CardType.KNIGHT)
        {
            must.add(3); // inside tile
            must.add(8); // get neighbor
            getCurrentPlayer().incrementLargestArmy(); // Add 1 army point to the player.
            this.updateLargestArmy();
        }
        else if ( card.getType() == Card.CardType.MONOPOLY)
        {
            must.add(4); // monopoly
        }
        else if ( card.getType() == Card.CardType.ROADBUILDING)
        {
            getCurrentPlayer().addResource(StructureTile.REQUIREMENTS_FOR_ROAD);
            getCurrentPlayer().addResource(StructureTile.REQUIREMENTS_FOR_ROAD);

            must.add(0); //road
            must.add(0); //road
        }
        else if ( card.getType() == Card.CardType.VICTORYPOINT)
        {
            getCurrentPlayer().increaseScore(1); // Add 1 score to the player.
        }
        else if ( card.getType() == Card.CardType.YEAROFPLENTY)
        {
            must.add(5); // year of plenty
        }
    }

    /**
     * when robber is placed, this method returns the players who have city/settlement at that hexagon
     * @param x x-coordinate
     * @param y y-coordinate
     * @return players that have settlement at the hexagon
     */
    public ArrayList<Player> getNeighborPlayers( int x, int y){
        return board.getNeighborPlayers( getCurrentPlayer(), x, y);
    }

    /**
     * change robber position
     * @param x x coordinate
     * @param y y coordinate
     */
    public void changeRobber( int x, int y){
        board.changeRobber( x, y);
    }

    /**
     * after user picked a material this method takes all materials from other players and add to the current player
     * @param selectedMaterial selected material to play monopoly
     */
    public void playMonopoly( int selectedMaterial){
        for ( int i = 0; i < playerCount; i++) // Loop through every player
        {
            if ( players.get(i) != getCurrentPlayer()) // If the looped player isn't the one stealing, start the steal loop.
            {
                for ( int p = 0; p < players.get(p).getResources()[selectedMaterial]; p++) // Steal the material until the player no longer has any.
                {
                    getCurrentPlayer().collectMaterial(selectedMaterial, 1);
                    players.get(p).discardMaterial(selectedMaterial, 1);
                }
            }
        }
    }

    /**
     * after user selected a material, this method gives two selected material to the current user
     * @param selectedMaterial selected material to give
     */
    public void playYearOfPlenty( int selectedMaterial){
        getCurrentPlayer().collectMaterial( selectedMaterial, 1);
        getCurrentPlayer().collectMaterial( selectedMaterial, 1);
    }

    /**
     * return if there is any must operation for this player and its type
     * @return -1 = there is no must
     *          0 = road need to be built
     *          1 = settlement need to be built
     *          2 = city need to be built
     *          3 = inside tile selection
     *          4 = resource selection (for monopoly card)
     *          5 = resource selection (for year of plenty card)
     *          6 = end turn
     *          7 = roll dice
     *          8 = get neighbor players ( after robber is placed )
     */
    public int checkMust(){
        if( must.size() == 0 )
            return -1;
        return must.peek();
    }

    /**
     * last-must has been completed
     */
    public void doneMust(){
        must.remove();
    }

    /**
     * This method returns all possibilities for any tile
     * @param x x coordinate
     * @param y y coordinate
     * @return 0 = road can be built here
     *         1 = settlement can be built here
     *         2 = city can be built here
     *         3 = this tile is an inside tile
     *         4 = this tile is a sea tile
     *         errors
     *         -1 = there is no connection for road to build
     *         -2 = there is no connection for city to build
     *         -3 = there is a building near
     *         -4 = this tile is occupied by a road, city or other players structure, in this case there is no need to explain anything
     *         -5 = there is no enough resource for road
     *         -6 = there is no enough resource for settlement
     *         -7 = there is no enough resource for city
     */
    public int checkTile( int x, int y){
        Player cp = getCurrentPlayer();
        if( board.isGameTile(x,y) ){
            int structureStatus = board.checkStructure( cp, x, y, gameStatus);

            if( structureStatus >= -4 && structureStatus <= -1 ) // return error because of gameboard
                return structureStatus;

            else if( structureStatus == 0 ){ // road can be built in terms of gameboard
                if( cp.hasEnoughResources( StructureTile.REQUIREMENTS_FOR_ROAD ) )
                    return structureStatus;
                else
                    return -5; // error because of resource
            }
            else if( structureStatus == 1 ){ // settlement can be built in terms of gameboard
                if( cp.hasEnoughResources( StructureTile.REQUIREMENTS_FOR_SETTLEMENT ) )
                    return structureStatus;
                else
                    return -6; // error because of resource
            }
            else if( structureStatus == 2 ){ // city can be built in terms of gameboard
                if( cp.hasEnoughResources( StructureTile.REQUIREMENTS_FOR_CITY ) )
                    return structureStatus;
                else
                    return -7; // error because of resource
            }
        }
        else{
            if( board.isInsideTile(x,y)){
                return 3; // means inside tile
            }
            else
                return 4;// means sea
        }
        return 5; // never work this return, this return is just for IDE
    }

    /**
     * return rotation type of the road
     * @param x x coordinate of the road
     * @param y y coordinate of the road
     * @return rotation type of the road
     */
    public RoadTile.RotationType rotationType( int x, int y){
        return board.rotationType( x, y);
    }

    /**
     * sets a structrure to (x,y) with the given type
     * (value of type can be directed from checkTile method)
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setTile( int x, int y){
        Player cp = getCurrentPlayer();
        if( board.getTile( x, y) instanceof RoadTile ){
            board.setStructure( cp, x ,y );
            cp.buyRoad();
            updateLongestRoad( cp);
        }
        else if( !(((StructureTile)board.getTile( x, y)).getAvailability()) ){
            board.setStructure( cp, x ,y );
            distributor.addHexagonResource( cp, x, y);
            cp.buySettlement();
            updateLongestRoad();
        }
        else{
            board.setStructure( cp, x ,y);
            distributor.addHexagonResource( cp, x, y);
            cp.buyCity();
        }
    }

    /**
     * updates longest road by looking all players
     */
    private void updateLongestRoad(){

        for( int i = 0 ; i < playerCount ; i++ ){
            updateLongestRoad( players.get(i));
        }
    }

    /**
     * updates longest road by looking at the specified player
     * @param player is the player who has just build a road, in building a city scenario, it is every player.
     */
    private void updateLongestRoad( Player player ){

        int curRoadLength = board.longestRoadOfPlayer( player);
        player.setRoadLength( curRoadLength);
        if( curRoadLength > longestRoad ){

            longestRoad = curRoadLength;
            if ( longestRoadPlayer != null )
            {
                longestRoadPlayer.setLongestRoadTitle( false);
            }
            longestRoadPlayer = player;
            player.setLongestRoadTitle( true);
        }
    }

    /**
     *
     */
    private void updateLargestArmy()
    {
        int curArmyCount = this.getCurrentPlayer().getLargestArmyCount();
        if ( curArmyCount > this.largestArmy )
        {
            this.largestArmy = curArmyCount;
            if ( this.largestArmyPlayer != null )
            {
                this.largestArmyPlayer.setLargestArmyTitle( false);
            }
            this.largestArmyPlayer = this.getCurrentPlayer();
            this.getCurrentPlayer().setLargestArmyTitle( true);
        }
    }

    /**
     * return gameBoard for UI
     * @return gameboard
     */
    public Tile[][] getBoard()
    {
        return board.getBoard();
    }

    /**
     * todo
     * This method checks whether the player has this particular port
     * @param portType is the type of port wanted to check
     * @return true if the current player has this particular port, false otherwise
     */
    public boolean selectPort(Port.PortType portType){
        return getCurrentPlayer().hasPort( portType);
    }

    /**
     * todo
     * This method makes wanted trade by using port
     * @param portType is the type of port wanted to check
     * @param discardedMaterial is the material wanted to be given by the player
     * @param collectedMaterial is the material wanted to be taken by the player
     */
    public void usePort(Port.PortType portType, int discardedMaterial, int collectedMaterial){
        Player player = getCurrentPlayer();
        if(portType == Port.PortType.THREE_TO_ONE){
            player.discardMaterial(discardedMaterial, 3);
            player.collectMaterial(collectedMaterial, 1);
        }

        else if(portType == Port.PortType.TWO_TO_ONE_LUMBER){
            player.discardMaterial(Materials.LUMBER, 2);
            player.collectMaterial(collectedMaterial, 1);
        }

        else if(portType == Port.PortType.TWO_TO_ONE_WOOL){
            player.discardMaterial(Materials.WOOL, 2);
            player.collectMaterial(collectedMaterial, 1);
        }

        else if(portType == Port.PortType.TWO_TO_ONE_GRAIN){
            player.discardMaterial(Materials.GRAIN, 2);
            player.collectMaterial(collectedMaterial, 1);
        }
        else if(portType == Port.PortType.TWO_TO_ONE_BRICK){
            player.discardMaterial(Materials.BRICK, 2);
            player.collectMaterial(collectedMaterial, 1);
        }

        else if(portType == Port.PortType.TWO_TO_ONE_ORE){
            player.discardMaterial(Materials.ORE, 2);
            player.collectMaterial(collectedMaterial, 1);
        }
    }

    /**
     * This method makes wanted trade between two users
     * @param offerer is the Player make the trade request
     * @param offeree is the Player accept the trade request
     * @param toOfferer is the list of materials the offeree wants to give
     * @param toOfferee is the list of materials the offerer wants to give
     */
    public boolean tradeWithPlayer(Player offerer, Player offeree, int[] toOfferer, int[] toOfferee){
        if ( offeree.hasEnoughResources(toOfferer))
        {
            offerer.tradeWithPlayer(offeree, toOfferee, toOfferer);
            return true;
        }
        return false;
    }


    public Player getLongestRoadPlayer() {
        return longestRoadPlayer;
    }

    public Player getLargestArmyPlayer() {
        return largestArmyPlayer;
    }
}
