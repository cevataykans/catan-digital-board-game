package GameFlow;

import GameBoard.GameBoard;
import Player.Player;

import java.util.ArrayList;
import java.util.Queue;

/**
 * controls the current turn operations( required moves, current players, ending turn etc)
 */
public class FlowManager {

    /**
     * Gets the current status of the game, 0 - Initial phase, 1 - Game phase
     * @return int - the current game status
     */
    public int getGameStatus()
    {
        // Get the related data
        Game game = Game.getInstance();

        return game.getGameStatus();
    }

    public int getCurrentPlayerIndex()
    {
        // Get the related data
        Game game = Game.getInstance();
        int gameStatus = game.getGameStatus();
        int turnNumber = game.getTurn();
        int playerCount = game.getPlayerCount();

        // If the game is at its construction stage and each player plays 2 turns at total
        if( gameStatus == 0 && turnNumber >= playerCount)
            return 2 * playerCount - turnNumber - 1;
        return turnNumber % playerCount; // If the game has started and turns are in a loop, return the player index with associated turn number.
    }

    /**
     * Returns the current turn to show on UI
     * @return the turn number.
     */
    public int getTurn()
    {
        // Get the related data
        Game game = Game.getInstance();
        int turnNumber = game.getTurn();
        int gameStatus = game.getGameStatus();
        int playerCount = game.getPlayerCount();

        if( gameStatus == 0 )
            return 0;
        return (turnNumber - playerCount * 2) / playerCount + 1;
    }

    /**
     * end turn and add must for the next turn
     * @return if the game is end
     */
    public boolean endTurn(){
        // Get the related data
        Game game = Game.getInstance();
        int turnNumber = game.getTurn();
        int playerCount = game.getPlayerCount();
        int gameStatus = game.getGameStatus();
        GameBoard board = game.getGameBoard();

        if( getCurrentPlayer().getScore() >= 10 ) // todo 10 puana ulasinca end turnu beklemeden bitmeli oyun
            return true;

        turnNumber++;
        if( turnNumber == 2 * playerCount )
        {
            ResourceDistributer.getInstance().collectResources( board.getRobber() );
            addMust( 7); // roll dice
            gameStatus = 1; // game play phase
        }
        else if( gameStatus == 0 )
        {
            addMust( 1); // settlement
            addMust( 0); // road
            addMust( 6); // end turn
        }
        else {
            addMust( 7); // roll dice // roll dice
        }
        return false;
    }

    /**
     * rolls dice and return result
     * @return dice numbers for both dice
     */
    public ArrayList<Integer> rollDice(){
        Game game = Game.getInstance();

        int firstDice =  ( int)( Math.random() * 6 + 1 );
        int secondDice =  ( int)( Math.random() * 6 + 1 );

        game.setCurrentDice( firstDice + secondDice);

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
        Game game = Game.getInstance();
        int currentDice = game.getCurrentDice();
        GameBoard board = game.getGameBoard();

        if( currentDice == 7 )
        {
            new ResourceManager().discardHalfOfResources();

            addMust( 3); // inside tile
            addMust( 8); // get neighbors
        }
        else
        {
            ResourceDistributer.getInstance().collectResources( currentDice, board.getRobber() );
        }
    }

    /**
     * Returns the player playing the current turn.
     * @return the current player.
     */
    public Player getCurrentPlayer()
    {
        Game game = Game.getInstance();
        int gameStatus = game.getGameStatus();
        int turnNumber = game.getTurn();
        int playerCount = game.getPlayerCount();
        ArrayList<Player> players = game.getPlayers();

        // If the game is at its construction stage and each player plays 2 turns at total
        if( gameStatus == 0 && turnNumber >= playerCount)
            return players.get( 2 * playerCount - turnNumber - 1);

        return players.get( turnNumber % playerCount); // If the game has started and turns are in a loop, return the player with associated turn number.
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
        Queue<Integer> must = Game.getInstance().getMust();

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
        Queue<Integer> must = Game.getInstance().getMust();

        if( must.size() == 0 )
            return -1;
        return must.peek();
    }

    /**
     * last-must has been completed
     */
    public void doneMust(){
        Queue<Integer> must = Game.getInstance().getMust();

        must.remove();
    }


}
