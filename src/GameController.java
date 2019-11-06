import java.awt.*;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * GameEngine class combines logic of the game board and players to enable users to play the game through model view
 * controller. Game Engine would constantly communicate with UIEngine to handle player/user events.
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
public class GameController
{
    private String[] testNames = { "Cevat", "Talha", "Hakan", "Rafi"};

    // Constants
    private final int TOTAL_DEV_CARDS = 25;

    // Attributes
    private GameBoard board;
    private Player[] players;
    private int turnNumber;
    private int robber;
    private int gameStatus = 0;
    private boolean playersFirstPass = false;
    private int playerIndex = 0;

    private Stack<Card> devCards = new Stack<Card>();

	// Constructor
    public GameController()
    {
        board = new GameBoard();
        players = new Player[4];
        turnNumber = 0;
        robber = 0;
        for( int i = 0; i < TOTAL_DEV_CARDS; i++)
        {
            Card card;
            if ( i < 14)
            {
                card = new Card(Card.CardType.KNIGHT);
            }
            else if ( i < 16)
            {
                card = new Card(Card.CardType.MONOPOLY);
            }
            else if ( i < 18)
            {
                card = new Card(Card.CardType.ROADBUILDING);
            }
            else if ( i < 20)
            {
                card = new Card(Card.CardType.YEAROFPLENTY);
            }
            else
            {
                card = new Card(Card.CardType.VICTORYPOINT);
            }
            devCards.push( card);
        }
        Collections.shuffle(devCards);
    }

	// Functions
    /**
     * Initializes the game via initializing gameboard, players, robber and the turn.
     */
    public void initializeGame()
    {
        board.configurate();
        for ( int i = 0; i < 4; i++)
        {
            players[i] = new Player( this.testNames[ i], Color.BLACK);
        }
        robber = 0 /* Gameboard needs to have a method that returns the robber hexagon */;
    }

    /**
     * Returns the player list.
     * @return the player list.
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Sets the player list to the given players list.
     * @param players is the new player list.
     */
    public void setPlayers(Player[] players)
    {

        this.players = players;
    }


    /**
     * Returns a specific player given in the player index.
     * @param playerIndex is the specific index of the player.
     * @return the player in the given index.
     */
    public Player getPlayer(int playerIndex)
    {
        return players[playerIndex];
    }

    /**
     * Returns the player playing the current turn.
     * @return the current player.
     */
    public Player getCurrentPlayer()
    {
        return players[ this.playerIndex];
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
     * Shuffles the players randomly. This is used for defining the players' play order before starting the game.
     */
    public void shufflePlayerOrder()
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = players.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Player temp = players[index];
            players[index] = players[i];
            players[i] = temp;
        }
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
     * Adds a development card taken out from the stack to the current player.
     */
    public void addDevelopmentCard()
    {
        Card placeholderCard = new Card(Card.CardType.KNIGHT);
        if ( getCurrentPlayer().hasEnoughResources(placeholderCard.getRequirements())) {
            getCurrentPlayer().buyDevelopmentCard(placeholderCard.getRequirements(), devCards.pop());
        }
    }

    /**
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
            for ( int i = 0; i < players.length; i++) // Loop through every player
            {
                if ( players[i] != getCurrentPlayer()) // If the looped player isn't the one stealing, start the steal loop.
                {
                    for ( int p = 0; p < players[p].getResources()[selectedMaterial]; p++) // Steal the material until the player no longer has any.
                    {
                        getCurrentPlayer().collectMaterial(selectedMaterial, 1);
                        players[p].discardMaterial(selectedMaterial, 1);
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
            //int material[2] = logic.Materials.LUMBER; // PLACEHOLDER! THIS MATERIAL SHOULD BE SELECTED FROM USER IN UI!
            //getCurrentPlayer().collectMaterial(material[0], 1); // Give a free specified resource to the player.
            //getCurrentPlayer().collectMaterial(material[1], 1); // Give a free specified resource to the player.
        }
    }


    /**
     * This method is to allow a logic.Player to build a road.
     * @param roadBuildingCard is the flag to indicate if the user is allowed to build the road free.
     */
    public void buildRoad(boolean roadBuildingCard, int x, int y)
    {

        if( roadBuildingCard ) {
            this.getCurrentPlayer().buyRoad( new int[]{0, 0, 0, 0, 0} );
        }
        else {
            this.getCurrentPlayer().buyRoad( Structure.REQUIREMENTS_FOR_ROAD );
        }

        this.board.setStructure(this.getCurrentPlayer(), x, y, Structure.Type.ROAD);

    }

    /**
     * This method is to allow a logic.Player to build a statement.
     */
    public void buildSettlement(int x, int y)
    {
        this.getCurrentPlayer().buySettlement( Structure.REQUIREMENTS_FOR_SETTLEMENT );

        this.board.setStructure(this.getCurrentPlayer(), x, y, Structure.Type.SETTLEMENT);


    }

    /**
     * This method is to allow a logic.Player to build a city.
     */
    public void buildCity(int x, int y)
    {
        this.getCurrentPlayer().buyCity( Structure.REQUIREMENTS_FOR_CITY );

        this.board.setStructure(this.getCurrentPlayer(), x, y, Structure.Type.CITY);


    }

    /**
     * This method checks whether the player has this particular port
     * @param portType is the type of port wanted to check
     * @return true if the current player has this particular port, false otherwise
     */
    public boolean selectPort(Port.PortType portType){
	    return getCurrentPlayer().hasPort(portType);
    }

    /**
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


    public Tile[][] getGameBoard()
    {
        return this.board.getBoard();
    }

    public int checkStructure( int x, int y)
    {
        return this.board.checkStructure( this.getCurrentPlayer(), x, y, this.gameStatus);
    }

    public void endTurn()
    {
        if ( !this.playersFirstPass)
        {
            this.playerIndex++;
        }
    }


    private void switchToGamePlay()
    {
        return;
    }


}
