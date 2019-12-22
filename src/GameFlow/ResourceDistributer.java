package GameFlow;

import java.util.ArrayList;

import GameBoard.BuildingTile;
import GameBoard.StartTile;
import GameBoard.Tile;
import Player.Player;

/**
 * Distributes the resources throughout the game
 * singleton class
 * @author Cevat Aykan Sevinc
 * @version 08.12.2019
 */
public class ResourceDistributer
{
	// Private node
	private class DistributionNode{
		public Player player;
		public StartTile startPoint;
		public int amount;
	}

	//singleton object
	private static ResourceDistributer distributor = null;

	// Attributes
	private ArrayList<DistributionNode>[] resourceDistributionList;

	//private constructor
	private ResourceDistributer()
	{
		resourceDistributionList = new ArrayList[11];

		// There are 11 resource dice denotion in range: 2-3-4-5-6-7-8-9-10-11-12
		for(int i = 0; i < 11; i++){
			resourceDistributionList[i] = new ArrayList<>();
		}
	}

	/**
	 * returns the singleton instance
	 * @return singleton resource distributor object
	 */
	public static ResourceDistributer getInstance()
	{
		if ( distributor == null )
		{
			distributor = new ResourceDistributer();
			return distributor;
		}
		return distributor;
	}

	/**
	 * Terminates resource distributer data for a new game when the game ends. Must only be called by flow manager when
	 * game ends.
	 */
	public static void terminateResourceDistributerData()
	{
		if ( distributor != null )
		{
			distributor = null;
		}
	}

	/**
	 * This function adds a resource node to distribute to the player
	 * @param player player who puchases the building tile.
	 * @param tile is board[ y][ x] for getting start points.
	 */
	public void addHexagonResource( Player player, Tile tile )
	{
		ArrayList<StartTile> startPoints = ( (BuildingTile) tile).getStartTiles();
		for(int i = 0; i < startPoints.size() ; i++){
			StartTile startPoint = startPoints.get(i);
			int diceNumber = startPoint.getDiceNumber();
			DistributionNode newNode = new DistributionNode();
			newNode.player = player;
			newNode.startPoint = startPoint;
			newNode.amount = 10;
			resourceDistributionList[diceNumber - 2].add(newNode);
		}
	}

	/**
	 * Collect resources for each player before the game starts.
	 */
	public void collectResources( Tile robber){
		for(int i = 2; i < 13 ; i++){
			collectResources( i, robber);
		}
	}

	/**
	 * Collect resources for each player that belongs to hexagons related to given dice number.
	 * @param diceNumber is given dice number
	 */
	public void collectResources( int diceNumber, Tile robber)
	{
		int len = resourceDistributionList[diceNumber - 2].size();
		for(int i = 0 ; i < len; i++){
			DistributionNode node = resourceDistributionList[diceNumber - 2].get(i);
			if( node.startPoint != robber )
				node.player.collectMaterial(node.startPoint.getResource(), node.amount);
		}
	}
}
