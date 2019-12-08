package GameFlow;

import GameBoard.GameBoard;
import Player.Player;

import java.util.ArrayList;

/**
 * handles title(longest road, largest army) related works here by taking the necessary data from game
 * @author Yusuf Nevzat Sengun
 * @version 08.12.2019
 */
public class TitleManager {
    /**
     * updates longest road by looking all players
     */
    public void updateLongestRoad(){
        // Get the related data
        Game game = Game.getInstance();
        int playerCount = game.getPlayerCount();
        ArrayList<Player> players = game.getPlayers();

        for( int i = 0 ; i < playerCount ; i++ ){
            updateLongestRoad( players.get(i));
        }
    }

    /**
     * updates longest road by looking at the specified player
     * @param player is the player who has just build a road, in building a city scenario, it is every player.
     */
    public void updateLongestRoad( Player player ){
        // Get the related data
        Game game = Game.getInstance();
        GameBoard board = game.getGameBoard();
        int longestRoad = game.getLongestRoad();
        Player longestRoadPlayer = game.getLongestRoadPlayer();

        int curRoadLength = board.longestRoadOfPlayer( player);
        player.setRoadLength( curRoadLength);
        if( curRoadLength > longestRoad ){

            game.setLongestRoad( curRoadLength);
            if ( longestRoadPlayer != null )
            {
                longestRoadPlayer.setLongestRoadTitle( false);
            }
            game.setLongestRoadPlayer( longestRoadPlayer);
            player.setLongestRoadTitle( true);
        }
    }

    /**
     * updates largest army of the current player
     */
    private void updateLargestArmy()
    {
        // Get the related data
        FlowManager flowManager = new FlowManager();
        Game game = Game.getInstance();
        int largestArmy = game.getLargestArmy();
        Player largestArmyPlayer = game.getLargestArmyPlayer();

        int curArmyCount = flowManager.getCurrentPlayer().getLargestArmyCount();
        if ( curArmyCount > largestArmy )
        {
            game.setLargestArmy( curArmyCount);
            if ( largestArmyPlayer != null )
            {
                largestArmyPlayer.setLargestArmyTitle( false);
            }
            game.setLargestArmyPlayer( flowManager.getCurrentPlayer());
            flowManager.getCurrentPlayer().setLargestArmyTitle( true);
        }
    }

    /**
     * returns the longest road player
     * @return longest road player
     */
    public Player getLongestRoadPlayer() {
        return Game.getInstance().getLongestRoadPlayer();
    }

    /**
     * returns the largest army player
     * @return largest army player
     */
    public Player getLargestArmyPlayer() {
        return Game.getInstance().getLargestArmyPlayer();
    }
}
