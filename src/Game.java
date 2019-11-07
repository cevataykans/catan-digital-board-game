import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;
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
public class Game
{
    // Constants
    private final int TOTAL_DEV_CARDS = 25;

    // Attributes
    private GameBoard board;
    private ArrayList<Player> players;
    private int turnNumber;
    private int playerCount;
    private int gameStatus;
    private int currentDice;

    private Stack<Card> devCards;

    // Constructors
    public Game( ArrayList<Player> players )
    {
        gameStatus = 0; // setup mode
        this.players = players;
        playerCount = players.size();
        board = new GameBoard();
        turnNumber = 0;
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
        board.configurate();
        Collections.shuffle(players);
        Collections.shuffle(devCards);

        return players;
    }

    public void endTurn(){
        turnNumber++;
        if(turnNumber == 2*playerCount) {
            board.collectResources();
            gameStatus = 1;
        }
    }

    public int getCurrentDice(){
        return currentDice;
    }

    public ArrayList<Integer> rollDice(){
        int firstDice =  (int)(Math.random()*6+1);
        int secondDice =  (int)(Math.random()*6+1);

        currentDice = firstDice + secondDice;

        ArrayList<Integer> ret = new ArrayList<>();
        ret.add(firstDice);
        ret.add(secondDice);
        return ret;
    }

    // ?????? 7
    public void collectResources(){
        if( currentDice == 7 ){
            ////// todo
        }
        else {
            board.collectResources(currentDice);
        }
    }



    /**
     * Returns the player list.
     * @return the player list.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Sets the player list to the given players list.
     * @param players is the new player list.
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
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
        if(gameStatus == 0 && turnNumber >= playerCount)
            return players.get(2 * playerCount - turnNumber - 1);
        return players.get(turnNumber % playerCount);
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
     * Returns the development cards stack.
     * @return the dev cards stack.
     */
    public Stack<Card> getDevCards() {
        return devCards;
    }

    /**
     * Sets the development cards stack to the given stack.
     * @param devCards is the new development cards stack.
     */
    public void setDevCards(Stack<Card> devCards) {
        this.devCards = devCards;
    }

    /**
     * todo
     * Adds a development card taken out from the stack to the current player.
     */
    public void addDevelopmentCard() // ?????
    {
        Card placeholderCard = new Card(Card.CardType.KNIGHT);
        if ( getCurrentPlayer().hasEnoughResources(placeholderCard.getRequirements())) {
            getCurrentPlayer().buyDevelopmentCard(placeholderCard.getRequirements(), devCards.pop());
        }
    }

    /**
     * todo
     * Plays a development card specified from the current player. The effect of the card will take place depending on
     * the type.
     * @param card is the development card that will be played with its effect.
     */
    public void playDevelopmentCard(Card card)
    {
        if ( card.getType() == Card.CardType.KNIGHT)
        {
            int x = 0; // PLACEHOLDER! THIS SHOULD BE THE UI X PLAYER CHOOSES!
            int y = 0; // PLACEHOLDER! THIS SHOULD BE THE UI Y PLAYER CHOOSES!
            board.changeRobber(x, y); // Move the robber to the specified place.
            getCurrentPlayer().incrementLargestArmy(); // Add 1 army point to the player.
            // SELECT A NEIGHBOURİNG PLAYER TO STEAL A RESOURCE.
        }
        else if ( card.getType() == Card.CardType.MONOPOLY)
        {
            int selectedMaterial = Materials.LUMBER; // PLACEHOLDER! THIS MATERIAL SHOULD BE SELECTED FROM USER IN UI!
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
        else if ( card.getType() == Card.CardType.ROADBUILDING)
        {
            int freeRequirements[] = new int[5];
            // Make the road requirement free.
            freeRequirements[0] = 0;
            freeRequirements[1] = 0;
            freeRequirements[2] = 0;
            freeRequirements[3] = 0;
            freeRequirements[4] = 0;
            int total = 0;
            // Do the free road building 2 valid times. If user clicks invalid location, ask for a new location by
            // repeating the loop.
            while ( total < 2) {
                int x = 0; // PLACEHOLDER! THIS SHOULD BE THE ROAD TILE PLAYER CHOOSES IN UI!
                int y = 0; // PLACEHOLDER! THIS SHOULD BE THE ROAD TILE PLAYER CHOOSES IN UI!
                if (board.checkStructure(getCurrentPlayer(), x, y, 1) == 0) {
                    board.setStructure(getCurrentPlayer(), x, y, Structure.Type.ROAD);
                    getCurrentPlayer().buyRoad(freeRequirements);
                    total++;
                }
            }
        }
        else if ( card.getType() == Card.CardType.VICTORYPOINT)
        {
            getCurrentPlayer().increaseScore(1); // Add 1 score to the player.
        }
        else if ( card.getType() == Card.CardType.YEAROFPLENTY)
        {
            //int material[2] = Materials.LUMBER; // PLACEHOLDER! THIS MATERIAL SHOULD BE SELECTED FROM USER IN UI!
            //getCurrentPlayer().collectMaterial(material[0], 1); // Give a free specified resource to the player.
            //getCurrentPlayer().collectMaterial(material[1], 1); // Give a free specified resource to the player.
        }
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
                int r[] = { 5, 5, 5, 5, 5 }; // ?????????????
                if( cp.hasEnoughResources( r) )
                    return structureStatus;
                else
                    return -5; // error because of resource
            }
            else if( structureStatus == 1 ){ // settlement can be built in terms of gameboard
                int r[] = { 5, 5, 5, 5, 5 }; // ?????????????
                if( cp.hasEnoughResources( r) )
                    return structureStatus;
                else
                    return -6; // error because of resource
            }
            else if( structureStatus == 2 ){ // city can be built in terms of gameboard
                int r[] = { 5, 5, 5, 5, 5 }; // ?????????????
                if( cp.hasEnoughResources( r) )
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
     * sets a structrure to (x,y) with the given type
     * (value of type can be directed from checkTile method)
     * @param x x coordinate
     * @param y y coordinate
     * @param type structure type
     */
    public void setTile( int x, int y, Structure.Type type){
        Player cp = getCurrentPlayer();
        if( type == Structure.Type.ROAD ){
            board.setStructure( cp, x ,y, type );
            cp.buyRoad();
        }
        else if( type == Structure.Type.CITY ){
            board.setStructure( cp, x ,y, type );
            cp.buyCity();
        }
        else if( type == Structure.Type.SETTLEMENT ){
            board.setStructure( cp, x ,y, type );
            cp.buySettlement();
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
        return getCurrentPlayer().hasPort(portType);
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


}
