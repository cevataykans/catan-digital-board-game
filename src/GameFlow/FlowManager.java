package GameFlow;

import GameBoard.GameBoard;
import Player.Player;
import ServerCommunication.ServerHandler;
import ServerCommunication.ServerInformation;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Queue;

/**
 * controls the current turn operations( required moves, current players, ending turn etc)
 * @author Yusuf Nevzat Sengun
 * @version 08.12.2019
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

    /**
     * returns the current player index
     * @return current player index
     */
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
        game.setTurnNumber( turnNumber);
        if( turnNumber == 2 * playerCount )
        {
            ResourceDistributer.getInstance().collectResources( board.getRobber() );
            addMust( Response.MUST_ROLL_DICE); // roll dice
            game.setGameStatus( 1); // game play phase
        }
        else if( gameStatus == 0 )
        {
            addMust( Response.MUST_SETTLEMENT_BUILD); // settlement
            addMust( Response.MUST_ROAD_BUILD); // road
            addMust( Response.MUST_END_TURN); // end turn
        }
        else {
            addMust( Response.MUST_ROLL_DICE); // roll dice // roll dice
        }
        return false;
    }

    /**
     * rolls dice, collect resources and return dices
     * @return dice numbers for both dice
     */
    public ArrayList<Integer> rollDice(){
        // Get the related data
        Game game = Game.getInstance();
        GameBoard board = game.getGameBoard();

        int firstDice = 0;
        int secondDice = 0;
        if(ServerHandler.getInstance().getStatus() == ServerHandler.Status.RECEIVER){
            JSONObject obj = ServerInformation.getInstance().getInformation();
            try{
                firstDice = obj.getInt("firstDice");
                secondDice = obj.getInt("secondDice");
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            firstDice =  ( int)( Math.random() * 6 + 1 );
            secondDice =  ( int)( Math.random() * 6 + 1 );
        }


        ArrayList<Integer> ret = new ArrayList<>();
        ret.add( firstDice);
        ret.add( secondDice);

        // After the dice is rolled, players must collect resource and current player must be able to play previously
        // bought development cards!
        if( firstDice + secondDice == 7 )
        {
            ArrayList<Integer>[] discarded = new ResourceManager().discardHalfOfResources();
            if(ServerHandler.getInstance().getStatus() == ServerHandler.Status.SENDER)
                ServerHandler.getInstance().rollDice(firstDice, secondDice, discarded);

            addMust( Response.MUST_INSIDE_TILE_SELECTION); // inside tile
            addMust( Response.MUST_GET_NEIGHBOR); // get neighbors
        }
        else
        {
            if(ServerHandler.getInstance().getStatus() == ServerHandler.Status.SENDER)
                ServerHandler.getInstance().rollDice(firstDice, secondDice);
            else if(ServerHandler.getInstance().getStatus() == ServerHandler.Status.RECEIVER)
                ServerInformation.getInstance().deleteInformation();
            ResourceDistributer.getInstance().collectResources( firstDice + secondDice, board.getRobber() );
        }

        new CardManager().makeCardsPlayable();

        return ret;
    }

    /**
     * Returns the player playing the current turn.
     * @return the current player.
     */
    public Player getCurrentPlayer()
    {
        // Get the related data
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

    /**
     * Returns a specific player given in the player index.
     * @param playerIndex is the specific index of the player.
     * @return the player in the given index.
     */
    public Player getPlayer(int playerIndex)
    {
        // Get the related data
        Game game = Game.getInstance();
        ArrayList<Player> players = game.getPlayers();

        return players.get( playerIndex);
    }

    //*****************************************************************************************************************
    //
    // Must Control
    //
    //*****************************************************************************************************************

    /**
     * adds a new must
     * @param response must type
     */
    public void addMust( Response response){
        // Get the related data
        Queue<Response> must = Game.getInstance().getMust();

        must.add( response);
    }

    /**
     * clears all musts
     */
    public void discardAllMust(){
        // Get the related data
        Queue<Response> must = Game.getInstance().getMust();

        must.clear();
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
    public Response checkMust(){
        // Get the related data
        Queue<Response> must = Game.getInstance().getMust();

        if( must.size() == 0 )
            return Response.MUST_EMPTY;
        return must.peek();
    }

    /**
     * last-must has been completed
     */
    public void doneMust(){
        // Get the related data
        Queue<Response> must = Game.getInstance().getMust();

        must.remove();
    }


}
