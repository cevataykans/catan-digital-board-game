import java.awt.Color;
import java.util.ArrayList;

/**
 * The player class that represents the player in catan gameboard.
 * @author Cevat Aykan Sevinc
 * @version 14.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 * To be implemented: structure, card data collection, buy special card, buying structures, making cards playable,
 * buying special cards, playing special cards.
 */
public class Player
{
	// Constants
	private static final int TOT_CARDS = 20; // Array or array list implementation?
	private static final int TOT_STRUCTURES = 20;
	private static final int DICE_SEVEN = 7;

	// Player Attributes
	private String name;
	private Color color; // needs to be discuessed for finalization;

	private int[] resources = { 0, 0, 4, 6, 0}; // pre construct for initial resources, not final!
	private int totResources;

	private Structure[] structures; // needs to be discuessed for finalization;
	private ArrayList<Card> cards; // trouble! I suggest arrayList!

	// Player Attributes related to score and board
	private int score;
	private boolean hasLargestArmy;
	private boolean hasLongestRoad;
	private int roadLength;
	private int armyCount;

	// Constructors
	public Player(String name, Color color)
	{
		this.name = name;
		this.color = color;
		this.totResources = 10; // pre construct for initial resources, not final!
		this.cards = new ArrayList<Card>();
		this.structures = new Structure[ TOT_STRUCTURES];

		this.score = 0;
		this.hasLargestArmy = false;
		this.hasLongestRoad = false;
		this.roadLength = -1;
		this.armyCount = -1;
	}

	// Functions
	/**
	 * Makes the player trade resources with another player.
	 * @param other The player that the trading is offered to.
	 * @param toGive The resources offered to the other player by this player.
	 * @param toTake The resources requested from the other player by this player.
	 */
	public void tradeWithPlayer( Player other, int[] toGive, int[] toTake )
	{
		// For each material on the toGive and toTake resource arrays, update player resources
		for ( int i = 0; i < toGive.length; i++ )
		{
			this.resources[ i] -= toGive[ i] + toTake[ i];
			this.totResources -= toGive[ i] + toTake[ i];

			other.resources[ i] -= toTake[ i] + toGive[ i];
			other.totResources -= toTake[ i] + toGive[ i];
		}
	}

	/**
	 * Gives the player the amount of material stated by the material constant.
	 * @param material is one of the index of resource array:
	 *                 Materials.LUMBER, Materials.WOOL, Materials.GRAIN, Materials.BRICK, Materials.ORE.
	 * @param amount is the number of materials to give to this player.
	 */
	public void collectMaterial( int material, int amount)
	{
		this.resources[ material] += amount;
		this.totResources += amount;
	}

	/**
	 * Checks if the player has enough resources for a given resource requirement.
	 * @param resourceToCheck resources array to check if player has more than argument resources.
	 * @return true if the player has enough resources for the given argument, else false.
	 */
	public boolean hasEnoughResources( int[] resourceToCheck )
	{
		// iterate over resources to check if player has sufficient resources;
		for ( int i = 0; i < resourceToCheck.length; i++ )
		{
			if ( resourceToCheck[ i] > this.resources[ i] )
			{
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param resources
	 * @param card
	 */
	public void buySpecialCard( int[] resources, Card card)
	{
		// to be implemented when card class is finished.
	}

	/**
	 * Randomly discards half of the resources if the player has more than 7 resources!
	 */
	public void discardHalfResources()
	{
		// Check if the player has more than 7 resources.
		if ( this.totResources > DICE_SEVEN)
		{
			int discardCount = this.totResources / 2; // take the floor to discard
			this.totResources -= discardCount; // update the after resource count
			while ( discardCount > 0)
			{
				// Find a valid random index to discard resource
				int discardIndex = ( int)( Math.random() * 5);
				if ( this.resources[ discardIndex] > 0)
				{
					this.resources[ discardIndex]--;
					discardCount--;
				}
			}
		}
	}

	/**
	 * Builds a road for the player with the cost withdrawn from resources.
	 * @param resources is the amount of resources paid for settlement structure.
	 */
	public void buySettlement( int[] resources)
	{
		this.payForStructure( resources);
		// Add structure to the player structure data collection. Not implemented yet as if it is array or AL

		this.score += 2;
	}

	/**
	 * Builds a road for the player with the cost withdrawn from resources.
	 * @param resources is the amount of resources paid for road structure.
	 */
	public void buydRoad( int[] resources)
	{
		this.payForStructure( resources);
		}
		// Add structure to the player structure data collection. Not implemented yet as if it is array or AL
	}

	/**
	 * Builds a city for the player with the cost withdrawn from resources.
	 * @param resources is the amount of resources paid for city structure.
	 */
	public void buyCity( int[] resources)
	{
		this.payForStructure( resources);
		// Add structure to the player structure data collection. Not implemented yet as if it is array or AL

		this.score += 2;
	}

	/**
	 * Call this function at the beginning turn of a player to allow the player to play previously bought special cards.
	 */
	public void makeCardsPlayable()
	{
		for ( int i = 0; i < cards.size(); i++ )
		{
			//cards.get( i).setPlayable();
			i++; // this line to be deleted and implemented later when card class is implemented.
		}
	}

	/**
	 * Gets the score of the player considering the longest road and the largest army.
	 * @return the score of the player.
	 */
	public int getScore()
	{
		// Get the current score with the addition of if the player has the largest army and the longest road titles.
		return this.score + (this.hasLargestArmy ? 2 : 0) + (this.hasLongestRoad ? 2 : 0);
	}

	/**
	 * Player plays the special card stated by the card index.
	 * @param cardIndex is the index of the special card to play.
	 */
	public void playSpecialCard( int cardIndex)
	{
		//cards.remove( cardIndex).play(); to be implemented after card class is implemented.
	}

	/**
	 * Gets the largest army count of the player if the player has the largest army title.
	 * @return the largest army count of the player. If the player does not have the largest army, return -1 pivot.
	 */
	public int getLargestArmyCount()
	{
		if ( this.hasLargestArmy)
		{
			return this.armyCount;
		}
		return -1;
	}

	/**
	 * Gets the longest road length of the player if the player has the longest road title.
	 * @return the longest road length of the player. If the player does not have the longest road, return -1 pivot.
	 */
	public int getLongestRoadLength()
	{
		if ( this.hasLongestRoad)
		{
			return this.roadLength;
		}
		return -1;
	}

	/**
	 * Sets the hasLongestRoad attribute to true and its road length.
	 * @param roadLength is the length of the road that is >= 5
	 */
	public void setLongestRoad( int roadLength )
	{
		this.roadLength = roadLength;
		this.hasLongestRoad = true;
	}

	/**
	 * Sets the hasLongestRoad attribute of the player false.
	 */
	public void setLongestRoad()
	{
		this.hasLongestRoad = false;
	}

	/**
	 * Sets the has largest army attribute of the player to true and its army count.
	 * @param armyCount is the count of the army that is >= 3.
	 */
	public void setLargestArmy( int armyCount)
	{
		this.armyCount = armyCount;
		this.hasLargestArmy = true;
	}

	/**
	 * Sets the hasLargestArmy attribute of the player to false.
	 */
	public void setLargestArmy()
	{
		this.hasLargestArmy = false;
	}

	/*
	******Open to discussion*******

	public void tradeWithWord() // 4:1
	public void tradeWithPort() // 3:1
	public void tradeWithSpecialPort // 2:1 and material
	 */

	/**
	 * A private function for the player to purchase a structure.
	 * @param resources is the amount of resources to be given to the bank.
	 */
	private void payForStructure( int[] resources)
	{
		// Pay for the resources
		for ( int i = 0; i < resources.length; i++ )
		{
			this.resources[ i] -= resources[ i];
			this.totResources -= resources[ i];
		}
	}
}
