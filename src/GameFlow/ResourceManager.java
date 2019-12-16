package GameFlow;

import GameBoard.Harbor;
import Player.Player;
import ServerCommunication.ServerHandler;
import ServerCommunication.ServerInformation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Resource manager handles all the resource related data players have.
 * @author Cevat Aykan Sevinc
 * @version 05.12.2019
 * Implemented class while decreasing the responsibilities of other classes.
 */
public class ResourceManager
{
	// properties for resource array indexing
	public static final int LUMBER = 0;
	public static final int WOOL = 1;
	public static final int GRAIN = 2;
	public static final int BRICK = 3;
	public static final int ORE = 4;

	// Methods
	/**
	 * This method makes wanted trade between two users. Think this method from the point of view of current player!
	 * @param current is the Player.Player make the trade request
	 * @param other is the Player.Player accept the trade request
	 * @param toGive is the list of materials the offeree wants to give
	 * @param toTake is the list of materials the offerer wants to give
	 */
	public boolean tradeWithPlayer( Player current, Player other, int[] toGive, int[] toTake){

		if ( hasEnoughResources( other, toTake) )
		{
			// For each material on the toGive and toTake resource arrays, update player resources
			for ( int i = 0; i < toGive.length; i++ )
			{
				// Current player must collect the taken amount while discarding the offered giving
				current.collectMaterial( i, toTake[ i] );
				current.discardMaterial( i, toGive[ i] );

				// Other player must collect the given amount while discarding the taken amount of the current player.
				other.collectMaterial( i, toGive[ i] );
				other.discardMaterial( i, toTake[ i] );
			}
			return true;
		}
		return false;
	}

	/**
	 * A function to enable players steal a random material from other players when the robber is changed.
	 * @param other other player whose resource is being stolen by this player
	 */
	public int stealResourceFromPlayer( Player current, Player other )
	{
		int index = -1;
		// Get player resources
		int[] otherPlResources = other.getResources();

		int otherResCount = other.getTotalResCount();
		// Other player must have at least one resource
		if ( otherResCount > 0 )
		{
			// There is one resource to steal, find a suitable index until stealing can be performed
			int leftToSteal = 1;
			while ( leftToSteal > 0 )
			{
				int randomStealIndex = (int)(Math.random() * 5);
				if ( otherPlResources[ randomStealIndex] > 0 ) // steal
				{
					// Adjust total resources
					current.collectMaterial( randomStealIndex, 1);
					other.discardMaterial( randomStealIndex, 1);
					index = randomStealIndex;
					leftToSteal = 0;
				}
			}
		}
		return index;
	}

	/**
	 * Checks if the player has enough resources for a given resource requirement.
	 * @param resourceToCheck resources array to check if player has more than argument resources.
	 * @return true if the player has enough resources for the given argument, else false.
	 */
	public boolean hasEnoughResources( Player player, int[] resourceToCheck )
	{
		// Get the player resources
		int[] playerResources = player.getResources();

		// iterate over resources to check if player has sufficient resources;
		for ( int i = 0; i < playerResources.length; i++ )
		{
			if ( resourceToCheck[ i] > playerResources[ i] )
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * A private function for the CURRENT player to purchase a structure.
	 * @param resources is the amount of resources to be given to the bank.
	 */
	public void payForStructure( int[] resources)
	{
		// Get the related data
		FlowManager flowManager = new FlowManager();
		Game game = Game.getInstance();

		// Pay for the resources
		for ( int i = 0; i < resources.length; i++ )
		{
			// Must be tested! *****************************************************************
			flowManager.getCurrentPlayer().discardMaterial( i, resources[ i] );
		}
	}

	/**
	 * Randomly discards half of the resources if the player has more than 7 resources!
	 */
	public ArrayList<Integer> discardHalfOfResources( Player player)
	{
		ArrayList<Integer> discardedIndexes = new ArrayList<>();
		int totResources = player.getTotalResCount();

		// Check if the player has more than 7 resources.
		if ( totResources > Game.DICE_SEVEN )
		{
			int discardCount = totResources / 2; // take the floor to discard
			int[] resources = player.getResources();
			while ( discardCount > 0)
			{
				// Find a valid random index to discard resource
				int discardIndex = ( int)( Math.random() * 5);
				if ( resources[ discardIndex] > 0)
				{
					player.discardMaterial( discardIndex, 1);
					discardedIndexes.add(discardIndex);
					discardCount--;
				}
			}
		}
		System.out.println("SENDER discarded from " + player.getName() + ": " + discardedIndexes);
		return discardedIndexes; // will be implemented
	}

	public void discardHalfOfResourcesWithoutCondition()
	{
		Game game = Game.getInstance();
		ArrayList<Player> players = game.getPlayers();

		for ( Player player : players ) {
			int totResources = player.getTotalResCount();

			int discardCount = totResources / 2; // take the floor to discard
			int[] resources = player.getResources();
			while (discardCount > 0) {
				// Find a valid random index to discard resource
				int discardIndex = (int) (Math.random() * 5);
				if (resources[discardIndex] > 0) {
					player.discardMaterial(discardIndex, 1);
					discardCount--;
				}
			}
		}
	}

	/**
	 * Discards half of every players' resources
	 */
	public ArrayList<Integer>[] discardHalfOfResources()
	{
		// Get the related data
		Game game = Game.getInstance();
		ArrayList<Player> players = game.getPlayers();

		if(ServerHandler.getInstance().getStatus() == ServerHandler.Status.RECEIVER){
			JSONObject obj = ServerInformation.getInstance().getInformation();
			ServerInformation.getInstance().deleteInformation();
			try{
				JSONArray discarded = obj.getJSONArray("discarded");
				for(int i = 0 ; i < players.size() ; i++){
					JSONArray indexes = discarded.getJSONArray(i);
					for(int j = 0 ; j < indexes.length() ; j++){
						int discardIndex = indexes.getInt(j);
						System.out.println("RECEIVER discarded from " + players.get(i).getName() + ", index " + discardIndex);
						players.get(i).discardMaterial( discardIndex, 1);
					}
				}
				return null;
			}
			catch(Exception e){
				e.printStackTrace();;
			}
		}

		ArrayList<Integer>[] discarded = new ArrayList[4];


		// Traverse the player array to try discarding resources!
		for ( int i = 0; i < players.size(); i++ )
		{
			discarded[i] = this.discardHalfOfResources( players.get( i) );
		}
		return discarded; // will be implemented
	}

	/**
	 * This method makes wanted trade by using port
	 * @param portType is the type of port wanted to check
	 * @param discardedMaterial is the material wanted to be given by the player
	 * @param collectedMaterial is the material wanted to be taken by the player
	 */
	public void tradeWithHarbor(Harbor.HarborType portType, int discardedMaterial, int collectedMaterial)
	{
		// Get the related data
		FlowManager flowManager = new FlowManager();
		Game game = Game.getInstance();
		Player player = flowManager.getCurrentPlayer();

		if( portType == Harbor.HarborType.THREE_TO_ONE ){

			player.discardMaterial( discardedMaterial, 3);
			player.collectMaterial( collectedMaterial, 1);
		}
		else if( portType == Harbor.HarborType.TWO_TO_ONE_LUMBER ){

			player.discardMaterial( ResourceManager.LUMBER, 2);
			player.collectMaterial( collectedMaterial, 1);
		}
		else if( portType == Harbor.HarborType.TWO_TO_ONE_WOOL){

			player.discardMaterial(ResourceManager.WOOL, 2);
			player.collectMaterial(collectedMaterial, 1);
		}
		else if( portType == Harbor.HarborType.TWO_TO_ONE_GRAIN){

			player.discardMaterial(ResourceManager.GRAIN, 2);
			player.collectMaterial(collectedMaterial, 1);
		}
		else if( portType == Harbor.HarborType.TWO_TO_ONE_BRICK){

			player.discardMaterial(ResourceManager.BRICK, 2);
			player.collectMaterial(collectedMaterial, 1);
		}
		else if( portType == Harbor.HarborType.TWO_TO_ONE_ORE){

			player.discardMaterial(ResourceManager.ORE, 2);
			player.collectMaterial(collectedMaterial, 1);
		}
	}

	/**
	 * A basic function for trading a resource by the ratio 4:1
	 * @param discardedMaterial index for discarding 4 of that resource
	 * @param collectedMaterial index for gaining 1 of that resource
	 */
	public void tradeUsing4( int discardedMaterial, int collectedMaterial )
	{
		Player player = new FlowManager().getCurrentPlayer();

		player.discardMaterial( discardedMaterial, 4);
		player.collectMaterial( collectedMaterial, 1);
	}
}
