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
        System.out.println( "******************* Road Title *************************");
        System.out.println( "Cur longest road in game is: " + longestRoad );
        System.out.println( player.getName() + "'s Longest Road is: " +  curRoadLength );
        player.setRoadLength( curRoadLength);
        System.out.println( player.getName() + "'s Longest Road is: " +  player.getLongestRoadLength() );
        if( curRoadLength > longestRoad ){

            game.setLongestRoad( curRoadLength);
            if ( longestRoadPlayer != null )
            {
                System.out.println( longestRoadPlayer.getName() + " has title: " + player.hasLongestRoad );
                longestRoadPlayer.setLongestRoadTitle( false);
                System.out.println( longestRoadPlayer.getName() + " has title: " + player.hasLongestRoad );

            }
            game.setLongestRoadPlayer( player);
            player.setLongestRoadTitle( true);

            System.out.println( "New Longest Road Player name is: " + player.getName() );
            System.out.println( "New Longest Road Player name is: " + this.getLongestRoadPlayer().getName() );
            System.out.println( "New Longest Road Player has title: " + this.getLongestRoadPlayer().hasLongestRoad );
        }
    }

    /**
     * updates largest army of the current player
     */
    public void updateLargestArmy()
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
