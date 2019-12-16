package GameFlow;

import GameBoard.GameBoard;
import GameBoard.RoadTile;
import GameBoard.StructureTile;
import Player.Player;
import org.controlsfx.dialog.Wizard;

import java.util.ArrayList;

/**
 * controls the board related operations by taking data from game
 * @author Cevat Aykan Sevinc
 * @version 08.12.2019
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
	 *         -100 = outside the range of game board, invalid tile index
	 */
	public Response checkTile( int x, int y)
	{
		// Get the related data
		FlowManager flowManager = new FlowManager();
		Game game = Game.getInstance();
		Player currentP = flowManager.getCurrentPlayer();
		ResourceManager resManager = new ResourceManager();
		GameBoard board = game.getGameBoard();
		int gameStatus = game.getGameStatus();

		// Error check for hover
		if ( x < 0 || y < 0 )
		{
			return Response.ERROR_OUTSIDE_GAMEBOARD;
		}

		// Error check for hover
		if ( x >= board.WIDTH || y >= board.HEIGHT)
		{
			return Response.ERROR_OUTSIDE_GAMEBOARD;
		}

		if( board.isGameTile( x, y ) )
		{
			Response structureStatus = board.checkStructure( currentP, x, y, gameStatus);

			if( structureStatus == Response.ERROR_NO_CONNECTION_FOR_ROAD ||
					structureStatus == Response.ERROR_NO_CONNECTION_FOR_SETTLEMENT ||
					structureStatus == Response.ERROR_THERE_IS_NEAR_BUILDING_FOR_SETTLEMENT ||
					structureStatus == Response.ERROR_OCCUPIED_BY ) // return error because of game board
				return structureStatus;

			else if( structureStatus == Response.INFORM_ROAD_CAN_BE_BUILT ){ // road can be built in terms of game board
				if( resManager.hasEnoughResources( currentP, StructureTile.REQUIREMENTS_FOR_ROAD ) )
					return structureStatus;
				else
					return Response.ERROR_NO_RESOURCE_FOR_ROAD; // error because of resource
			}
			else if( structureStatus == Response.INFORM_SETTLEMENT_CAN_BE_BUILT ){ // settlement can be built in terms of game board
				if( resManager.hasEnoughResources( currentP, StructureTile.REQUIREMENTS_FOR_SETTLEMENT ) )
					return structureStatus;
				else
					return Response.ERROR_NO_RESOURCE_FOR_SETTLEMENT; // error because of resource
			}
			else if( structureStatus == Response.INFORM_CITY_CAN_BE_BUILT ){ // city can be built in terms of game board
				if( resManager.hasEnoughResources( currentP, StructureTile.REQUIREMENTS_FOR_CITY ) )
					return structureStatus;
				else
					return Response.ERROR_NO_RESOURCE_FOR_CITY; // error because of resource
			}
		}
		else{
			if( board.isInsideTile(x,y)){
				return Response.INFORM_INSIDE_TILE; // means inside tile
			}
			else
				return Response.INFORM_SEA_TILE;// means sea
		}
		return null;
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
		FlowManager flowManager = new FlowManager();
		Game game = Game.getInstance();
		Player cp = flowManager.getCurrentPlayer();
		GameBoard board = game.getGameBoard();
		ResourceDistributer distributor = ResourceDistributer.getInstance();
		TitleManager titleManager = new TitleManager();

		if( board.getTile( x, y) instanceof RoadTile)
		{
			board.setStructure( cp, x ,y );
			cp.buyRoad();
			titleManager.updateLongestRoad( cp);
		}
		else if ( !(((StructureTile)board.getTile( x, y)).getAvailability()) )
		{
			board.setStructure( cp, x ,y );
			distributor.addHexagonResource( cp, board.getTile( x, y) );
			cp.buySettlement();
			titleManager.updateLongestRoad();
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
		FlowManager flowManager = new FlowManager();

		// Return data :)
		return game.getGameBoard().getNeighborPlayers( flowManager.getCurrentPlayer(), x, y);
	}
}
