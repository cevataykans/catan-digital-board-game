package GameFlow;

import GameBoard.GameBoard;
import GameBoard.RoadTile;
import GameBoard.StructureTile;
import Player.Player;

import java.util.ArrayList;

/**
 *
 */
public class BoardManager
{
	// Methods
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
	public int checkTile( int x, int y)
	{
		Game game = Game.getInstance();
		Player currentP = game.getCurrentPlayer();
		ResourceManager resManager = new ResourceManager();
		GameBoard board = game.getGameBoard();
		int gameStatus = game.getGameStatus();

		if( board.isGameTile( x, y ) )
		{
			int structureStatus = board.checkStructure( currentP, x, y, gameStatus);

			if( structureStatus >= -4 && structureStatus <= -1 ) // return error because of game board
				return structureStatus;

			else if( structureStatus == 0 ){ // road can be built in terms of game board
				if( resManager.hasEnoughResources( currentP, StructureTile.REQUIREMENTS_FOR_ROAD ) )
					return structureStatus;
				else
					return -5; // error because of resource
			}
			else if( structureStatus == 1 ){ // settlement can be built in terms of game board
				if( resManager.hasEnoughResources( currentP, StructureTile.REQUIREMENTS_FOR_SETTLEMENT ) )
					return structureStatus;
				else
					return -6; // error because of resource
			}
			else if( structureStatus == 2 ){ // city can be built in terms of game board
				if( resManager.hasEnoughResources( currentP, StructureTile.REQUIREMENTS_FOR_CITY ) )
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
	 * return rotation type of the road
	 * @param x x coordinate of the road
	 * @param y y coordinate of the road
	 * @return rotation type of the road
	 */
	public RoadTile.RotationType rotationType( int x, int y)
	{
		// Get necessary data for processing
		Game game = Game.getInstance();
		GameBoard board = game.getGameBoard();

		// Return the rotation type
		return board.rotationType( x, y);
	}

	/**
	 * sets a structrure to (x,y) with the given type
	 * (value of type can be directed from checkTile method)
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void setTile( int x, int y)
	{
		// Get necessary data for processing
		Game game = Game.getInstance();
		Player cp = game.getCurrentPlayer();
		GameBoard board = game.getGameBoard();
		ResourceDistributer distributor = ResourceDistributer.getInstance();

		if( board.getTile( x, y) instanceof RoadTile)
		{
			board.setStructure( cp, x ,y );
			cp.buyRoad();
			game.updateLongestRoad( cp);
		}
		else if ( !(((StructureTile)board.getTile( x, y)).getAvailability()) )
		{
			board.setStructure( cp, x ,y );
			distributor.addHexagonResource( cp, board.getTile( x, y) );
			cp.buySettlement();
			game.updateLongestRoad();
		}
		else
		{
			board.setStructure( cp, x ,y);
			distributor.addHexagonResource( cp, board.getTile( x, y) );
			cp.buyCity();
		}
	}

	/**
	 * change robber position
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public void changeRobber( int x, int y)
	{
		// Get necessary data
		Game game = Game.getInstance();
		GameBoard board = game.getGameBoard();

		// Process data
		board.changeRobber( x, y);
	}

	/**
	 * when robber is placed, this method returns the players who have city/settlement at that hexagon
	 * board[y][x] must be an inside tile
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return players that have settlement at the hexagon
	 */
	public ArrayList<Player> getNeighborPlayers(int x, int y)
	{
		// Get necessary data
		Game game = Game.getInstance();

		// Return data :)
		return game.getGameBoard().getNeighborPlayers( game.getCurrentPlayer(), x, y);
	}
}
