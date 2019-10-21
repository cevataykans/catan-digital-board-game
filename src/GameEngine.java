import Cards.*;

import java.awt.*;
import java.util.ArrayList;
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
public class GameEngine
{
    // Constants
    private final int TOTAL_DEV_CARDS = 25;

    // Attributes
    GameBoard board;
    Player[] players;
    int turnNumber;
    int robber;

    Stack<Card> devCards;

	// Constructor
    public GameEngine()
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
                card = new Knight();
            }
            else if ( i < 16)
            {
                card = new Monopoly();
            }
            else if ( i < 18)
            {
                card = new RoadBuilding();
            }
            else if ( i < 20)
            {
                card = new YearOfPlenty();
            }
            else
            {
                card = new VictoryPoint();
            }
            devCards.push(card);
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
            players[i] = new Player("Placeholder name", Color.BLACK);
        }
        turnNumber = 1;
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
    public void setPlayers(Player[] players) {
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
        return players[turnNumber % 4];
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
        Card devCard = new Knight();
        getCurrentPlayer().buyDevelopmentCard(devCard.getRequirements(), devCards.pop());
    }


    /**
     * This method is to allow a Player to build a road.
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

        if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == 0 ) { // I assumed gameStatus = 1
            board.setStructure(this.getCurrentPlayer(), x, y, 0);
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -1 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -2 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -3 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -4 ){
            //TODO
        }
        else{
            //TODO
        }

    }

    /**
     * This method is to allow a Player to build a statement.
     */
    public void buildSettlement(int x, int y)
    {
        this.getCurrentPlayer().buySettlement( Structure.REQUIREMENTS_FOR_SETTLEMENT );

        if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == 1 ) { // I assumed gameStatus = 1
            board.setStructure(this.getCurrentPlayer(), x, y, 1);
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -1 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -2 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -3 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -4 ){
            //TODO
        }
        else{
            //TODO
        }

    }

    /**
     * This method is to allow a Player to build a city.
     */
    public void buildCity(int x, int y)
    {
        this.getCurrentPlayer().buyCity( Structure.REQUIREMENTS_FOR_CITY );

        if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == 2 ) { // I assumed gameStatus = 1
            board.setStructure(this.getCurrentPlayer(), x, y, 2);
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -1 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -2 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -3 ){
            //TODO
        }
        else if( board.checkStructure(this.getCurrentPlayer(), x, y, 1 ) == -4 ){
            //TODO
        }
        else{
            //TODO
        }
    }

}
