package GameFlow;

import java.util.*;
import DevelopmentCards.*;
import GameBoard.*;
import Player.Player;

/**
 * GameFlow.Game class combines logic of the game board and players to enable users to play the game.
 * @author Yusuf Nevzat Sengun
 * @version 04.11.2019
 *
 * GameFlow.Game class combines logic of the game board and players to enable users to play the game.
 * @author Cevat Aykan Sevinc
 * @version 14.10.2019
 * Class is created to later combine all the logic.
 * Log 16.10.2019 (Talha)
 * -----------------------
 * Created class attributes, constructore and functions.
 * Added function templates.
 * Implemented some functions.
 * -----------------------
 * @version 07.12.2019
 *  This is a milestone in our project!
 *  Software architecture Yusuf has designed a system to rule all designs!
 *  Game design is changed into singleton and game now controls the data access by managers.
 */
public class Game
{
    // Game Singleton
    private static Game game = null;

    private Queue<Integer> must;

    // Constants
    private final int TOTAL_DEV_CARDS = 25;
    public static final int DICE_SEVEN = 7;

    // Attributes
    private GameBoard board;

    // Game related data
    private int turnNumber;
    private int gameStatus;
    private int currentDice;

    // Player Related data
    private ArrayList<Player> players;
    private int playerCount;
    private int longestRoad;
    private int largestArmy;
    private Player longestRoadPlayer;
    private Player largestArmyPlayer;

    // Dev card related data
    private Stack<Card> devCards;

    // Constructors
    private Game( ArrayList<Player> players )
    {
        longestRoadPlayer = null;
        longestRoad = 4; // minumum requierement to get this card
        largestArmy = 2; // minumum req to earn the army title
        largestArmyPlayer = null;
        gameStatus = 0; // initial phase ( setup mode )
        this.players = players;
        playerCount = players.size();
        board = new GameBoard();
        turnNumber = 0;
        devCards = new Stack<>();

        for( int i = 0; i < TOTAL_DEV_CARDS; i++)
        {
            Card card;
            if ( i < 14)
                card = new Knight();
            else if ( i < 16)
                card = new Monopoly();
            else if ( i < 18)
                card = new RoadBuilding();
            else if ( i < 20)
                card = new YearOfPlenty();
            else
                card = new VictoryPoint();
            devCards.push(card);
        }

        this.must = new LinkedList<>();
    }

    /**
     *
     * @param players
     * @return
     */
    public static Game getInstance( ArrayList<Player> players )
    {
        if ( game == null )
        {
            game = new Game( players);
            return game;
        }
        return game;
    }

    /**
     *
     * @return
     */
    public static Game getInstance()
    {
        if ( game != null )
        {
            return game;
        }
        return null;
    }

    // Functions
    /**
     * Initializes the game via initializing gameboard, players, robber and the turn.
     */
    public void configureGame()
    {
        // The order must not be changed!
        board.configurate();

        Collections.shuffle(players);
        Collections.shuffle(devCards);

        this.must.add( 1); // settlement
        this.must.add( 0); // road
        this.must.add( 6); // end turn
    }

    /**
     * end turn and add must for the next turn
     * @return if the game is end
     */
    public boolean endTurn(){
        if( getCurrentPlayer().getScore() >= 10 ) // todo 10 puana ulasinca end turnu beklemeden bitmeli oyun
            return true;

        turnNumber++;
        if( turnNumber == 2 * playerCount )
        {
            ResourceDistributer.getInstance().collectResources( board.getRobber() );
            must.add( 7); // roll dice
            gameStatus = 1; // game play phase
        }
        else if( gameStatus == 0 )
        {
            this.must.add( 1); // settlement
            this.must.add( 0); // road
            this.must.add( 6); // end turn
        }
        else {
            must.add( 7); // roll dice // roll dice
        }
        return false;
    }

    /**
     * rolls dice and return result
     * @return dice numbers for both dice
     */
    public ArrayList<Integer> rollDice(){
        int firstDice =  ( int)( Math.random() * 6 + 1 );
        int secondDice =  ( int)( Math.random() * 6 + 1 );

        currentDice = firstDice + secondDice;

        ArrayList<Integer> ret = new ArrayList<>();
        ret.add( firstDice);
        ret.add( secondDice);

        // After the dice is rolled, players must collect resource and current player must be able to play previously
        // bought development cards!
        collectResources();

        new CardManager().makeCardsPlayable();

        return ret;
    }

    /**
     * collect resources after dice has been rolled or moves robber
     */
    private void collectResources(){

        if( currentDice == 7 )
        {
            new ResourceManager().discardHalfOfResources();

            must.add( 3); // inside tile
            must.add( 8); // get neighbors
        }
        else
        {
            ResourceDistributer.getInstance().collectResources( currentDice, board.getRobber() );
        }
    }

    //*****************************************************************************************************************
    //
    // Data access
    //
    //*****************************************************************************************************************

    /**
     * Returns the development card stack data for processing.
     * @return Stack<Card> - development cards
     */
    public Stack<Card> getCardStack()
    {
        return devCards;
    }

    /**
     * Returns a specific player given in the player index.
     * @param playerIndex is the specific index of the player.
     * @return the player in the given index.
     */
    public Player getPlayer(int playerIndex)
    {
        return players.get( playerIndex);
    }

    /**
     * Returns the player playing the current turn.
     * @return the current player.
     */
    public Player getCurrentPlayer()
    {
        // If the game is at its construction stage and each player plays 2 turns at total
        if( gameStatus == 0 && turnNumber >= playerCount)
            return players.get( 2 * playerCount - turnNumber - 1);

        return players.get( turnNumber % playerCount); // If the game has started and turns are in a loop, return the player with associated turn number.
    }

    public int getCurrentPlayerIndex()
    {
        // If the game is at its construction stage and each player plays 2 turns at total
        if( gameStatus == 0 && turnNumber >= playerCount)
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
     * Gets the current status of the game, 0 - Initial phase, 1 - Game phase
     * @return int - the current game status
     */
    public int getGameStatus()
    {
        return gameStatus;
    }

    /**
     * updates longest road by looking all players
     */
    public void updateLongestRoad(){

        for( int i = 0 ; i < playerCount ; i++ ){
            updateLongestRoad( players.get(i));
        }
    }

    /**
     * updates longest road by looking at the specified player
     * @param player is the player who has just build a road, in building a city scenario, it is every player.
     */
    public void updateLongestRoad( Player player ){

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
     * Retuns GameBoard for board related data flow.
     * @return GameBoard board.
     */
    public GameBoard getGameBoard()
    {
        return board;
    }

    /**
     * Returns the tile board[][] 2D array for UI processing.
     * @return Tile[][] board.
     */
    public Tile[][] getTileBoard()
    {
        return board.getBoard();
    }

    /**
     * Return the player arraylist for manager to manipulate & use
     * @return the player array in the game.
     */
    public ArrayList<Player> getPlayers()
    {
        return this.players;
    }

    public Player getLongestRoadPlayer() {
        return longestRoadPlayer;
    }

    public Player getLargestArmyPlayer() {
        return largestArmyPlayer;
    }

    //*****************************************************************************************************************
    //
    // Must Control
    //
    //*****************************************************************************************************************

    /**
     * adds a new must
     * @param m must type
     */
    public void addMust( int m){
        must.add( m);
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
     *         --- 9 = get half resources from all players (for perfectly balanced card) *** can be implemented in card play function
     *         --- 10 = player gets a point (for victory point card) *** can be implemented in card play function
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
}
