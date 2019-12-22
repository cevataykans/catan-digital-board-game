package GameFlow;

import java.util.*;
import DevelopmentCards.*;
import GameBoard.*;
import Player.Player;
import ServerCommunication.ServerHandler;
import ServerCommunication.ServerInformation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private Queue<Response> must;

    // Constants
    private final int TOTAL_DEV_CARDS = 28;
    public static final int DICE_SEVEN = 7;

    // Attributes
    private GameBoard board;

    // Game related data
    private int turnNumber;
    private int gameStatus;

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
        this.longestRoadPlayer = null;
        this.longestRoad = 4; // minumum requierement to get this card
        this.largestArmy = 2; // minumum req to earn the army title
        this.largestArmyPlayer = null;
        this.gameStatus = 0; // initial phase ( setup mode )
        this.players = players;
        this.playerCount = players.size();
        this.board = new GameBoard();
        this.turnNumber = 0;
        this.devCards = new Stack<>();
        this.must = new LinkedList<>();
        if ( ServerHandler.getInstance().getStatus() == null) {
            for (int i = 0; i < TOTAL_DEV_CARDS; i++) {
                Card card;
                if (i < 14)
                    card = new Card(new Knight(),"knight");
                else if (i < 16)
                    card = new Card(new Monopoly(), "monopoly");
                else if (i < 18)
                    card = new Card(new RoadBuilding(), "Road-Building");
                else if (i < 20)
                    card = new Card(new YearOfPlenty(), "Year-of-Plenty");
                else if (i < 25)
                    card = new Card(new VictoryPoint(), "Victory-Point");
                else if (i < 27)
                    card = new Card(new ChangeOfFortune(), "Change-of-Fortune");
                else
                    card = new Card(new PerfectlyBalanced(), "Perfectly-Balanced");
                devCards.push(card);
            }
        }
        else
        {
            JSONObject obj = ServerInformation.getInstance().getInformation();
            try {
                JSONArray tempCards = (JSONArray) obj.get("cards");
                for ( int i = 0; i < tempCards.length(); i++)
                {
                    Card card;
                    switch (tempCards.getInt(i)) {
                        case 0:
                            card = new Card(new Knight(),"knight");
                            break;
                        case 1:
                            card = new Card(new Monopoly(), "monopoly");
                            break;
                        case 2:
                            card = new Card(new RoadBuilding(), "Road-Building");
                            break;
                        case 3:
                            card = new Card(new YearOfPlenty(), "Year-of-Plenty");
                            break;
                        case 4:
                            card = new Card(new VictoryPoint(), "Victory-Point");
                            break;
                        default:
                            card = null;
                    }
                    this.devCards.push(card);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * use only on initialization
     * returns initializes and return the instance
     * @param players game will be initialized by using the players
     * @return game
     */
    public static Tile[][] getInstance( ArrayList<Player> players )
    {
        if ( game == null )
        {
            game = new Game( players);
            game.configureGame();
            return game.board.getBoard();
        }
        return game.board.getBoard();
    }

    /**
     * use only to take the ready instance
     * return the singleton game
     * @return game
     */
    public static Game getInstance()
    {
        if ( game != null )
        {
            return game;
        }
        return null;
    }

    /**
     * Terminate the game when the game ends! must only be called by flow manager when single player ends!
     */
    public static void terminateGameData()
    {
        if ( game != null )
        {
            game = null;
        }
    }

    // Functions
    /**
     * Initializes the game via initializing gameboard, players, robber and the turn.
     */
    private void configureGame()
    {
        // The order must not be changed!
        board.configurate();

        if (ServerHandler.getInstance().getStatus() == null) {
            Collections.shuffle(players);
            Collections.shuffle(devCards);
        }

        if ( ServerHandler.getInstance().getStatus() != ServerHandler.Status.RECEIVER) {
            this.must.add(Response.MUST_SETTLEMENT_BUILD); // settlement
            this.must.add(Response.MUST_ROAD_BUILD); // road
            this.must.add(Response.MUST_END_TURN); // end turn
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
     * Returns the current turn number players are playing.
     * @return the turn number.
     */
    public int getTurn()
    {
        return turnNumber;
    }

    /**
     * sets the turn number
     * @param turnNumber turn number
     */
    public void setTurnNumber(int turnNumber)
    {
        this.turnNumber = turnNumber;
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
     * sets gamestatus
     * @param gameStatus gamestatus
     */
    public void setGameStatus(int gameStatus)
    {
        this.gameStatus = gameStatus;
    }

    /**
     * returns must queue
     * @return must
     */
    public Queue<Response> getMust(){
        return this.must;
    }

    /**
     * returns player count
     * @return player count
     */
    public int getPlayerCount(){
        return playerCount;
    }

    /**
     * returns the longest road player
     * @return longest road player
     */
    public Player getLongestRoadPlayer() {
        return longestRoadPlayer;
    }

    /**
     * sets the longest road player
     * @param longestRoadPlayer longest road player
     */
    public void setLongestRoadPlayer(Player longestRoadPlayer){
        this.longestRoadPlayer = longestRoadPlayer;
    }

    /**
     * returns the larget army player
     * @return largest army player
     */
    public Player getLargestArmyPlayer() {
        return largestArmyPlayer;
    }

    /**
     * sets the largest army player
     * @param largestArmyPlayer largest army player
     */
    public void setLargestArmyPlayer(Player largestArmyPlayer){
        this.largestArmyPlayer = largestArmyPlayer;
    }

    /**
     * returns the longest road
     * @return longest road
     */
    public int getLongestRoad(){
        return longestRoad;
    }

    /**
     * sets the longest road
     * @param longestRoad longest road
     */
    public void setLongestRoad(int longestRoad){
        this.longestRoad = longestRoad;
    }

    /**
     * returns the largest army
     * @return largest army
     */
    public int getLargestArmy(){
        return largestArmy;
    }

    /**
     * sets teh largest army
     * @param largestArmy largest army
     */
    public void setLargestArmy(int largestArmy){
        this.largestArmy = largestArmy;
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
     * Return the player arraylist for manager to manipulate & use
     * @return the player array in the game.
     */
    public ArrayList<Player> getPlayers()
    {
        return this.players;
    }
}
