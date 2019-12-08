package GameFlow;

import GameBoard.GameBoard;
import Player.Player;

import java.util.ArrayList;

public class TitleManager {
    /**
     * updates longest road by looking all players
     */
    public void updateLongestRoad(){
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
            longestRoadPlayer = player;
            player.setLongestRoadTitle( true);
        }
    }

    /**
     *
     */
    private void updateLargestArmy()
    {
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
            largestArmyPlayer = flowManager.getCurrentPlayer();
            flowManager.getCurrentPlayer().setLargestArmyTitle( true);
        }
    }
    public Player getLongestRoadPlayer() {
        return Game.getInstance().getLongestRoadPlayer();
    }

    public Player getLargestArmyPlayer() {
        return Game.getInstance().getLargestArmyPlayer();
    }
}
