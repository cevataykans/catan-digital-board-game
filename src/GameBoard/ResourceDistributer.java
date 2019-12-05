package GameBoard;

import java.util.ArrayList;
import GameFlow.Player;

public class ResourceDistributer
{
	// Private node
	private class DistributionNode{
		public Player player;
		public StartTile startPoint;
		public int amount;
	}

	// Attributes
	private ArrayList<DistributionNode>[] resourceDistributionList;

	public ResourceDistributer()
	{
		resourceDistributionList = new ArrayList[11];

		// There are 11 resource dice denotion in range: 2-3-4-5-6-7-8-9-10-11-12
		for(int i = 0; i < 11; i++){
			resourceDistributionList[i] = new ArrayList<>();
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
			newNode.amount = 1;
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
