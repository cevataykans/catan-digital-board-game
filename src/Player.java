import java.awt.Color;
import java.util.ArrayList;

/**
 * The player class that represents the player in catan gameboard.
 * @author Cevat Aykan Sevinc
 * @version 14.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 * To be implemented: structure, card data collection, buy special card, buying structures, making cards playable,
 * buying special cards, playing special cards.
 * Log 15.10.2019 (Talha)
 * --------------
 * Removed setLongestRoad(), setLongestRoad(int), setLargestArmy() and setLargestArmy(int).
 * Added incrementLongestRoad(), incrementLargestArmy(), checkLongestRoad(int), checkLargestArmy(int) and increaseScore(int).
 * Added documentation.
 * --------------
 * Log 16.10.2019 (Talha)
 * Implemented buyDevelopmentCard() function.
 * Added getters and setters to name and color attributes.
 * --------------
 * Log 20.10.2019 (Hakan)
 * Implemented addStructure() and getStructure() methods.
 * --------------
 * Log 25.10.2019 (Hakan)
 * Implemented ports attributes and addPort(), getPorts() methods
 */
public class Player
{
	// Constants
	private static final int TOT_CARDS = 20; // Array or array list implementation?
	private static final int TOT_STRUCTURES = 20;
	private static final int DICE_SEVEN = 7;

	// Player Attributes
	private String name;
	private javafx.scene.paint.Color color; // needs to be discussed for finalization;

	private int[] resources = { 0, 0, 4, 6, 0}; // pre construct for initial resources, not final!
	private int totResources;

	private ArrayList<Structure> structures; // needs to be discuessed for finalization;
	private ArrayList<Card> cards; // trouble! I suggest arrayList!
	private ArrayList<Port> ports;

	// Player Attributes related to score and board
	private int score;
	private boolean hasLargestArmy;
	private boolean hasLongestRoad;
	private int roadLength;
	private int armyCount;

	// Constructors
	public Player(String name, javafx.scene.paint.Color color)
	{
		this.name = name;
		this.color = color;
		this.totResources = 10; // pre construct for initial resources, not final!
		this.cards = new ArrayList<Card>();
		this.structures = new ArrayList<Structure>();
		this.ports = new ArrayList<Port>();

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
		resources[ material] += amount;
		totResources += amount;
	}

	/**
	 * Takes from the player the amount of material stated by the material constant.
	 * @param material is one of the index of resource array:
	 *                 Materials.LUMBER, Materials.WOOL, Materials.GRAIN, Materials.BRICK, Materials.ORE.
	 * @param amount is the number of materials to discard from this player.
	 */
	public void discardMaterial( int material, int amount)
	{
		resources[material] -= amount;
		totResources -= amount;
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
	 * Buys a development card from the bank and the card is chosen randomly.
	 * @param requirements is the required resources player needs to give to buy a development card.
	 * @param card is the development card player buys.
	 */
	public void buyDevelopmentCard( int[] requirements, Card card)
	{
		resources[0] -= requirements[0];
		resources[1] -= requirements[1];
		resources[2] -= requirements[2];
		resources[3] -= requirements[3];
		resources[4] -= requirements[4];
		cards.add(card);
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
	 */
	public void buySettlement()
	{
		this.payForStructure( Structure.REQUIREMENTS_FOR_SETTLEMENT );
		// Add structure to the player structure data collection. Not implemented yet as if it is array or AL

		this.score += Structure.VICTORY_POINTS_FOR_SETTLEMENT;
	}

	/**
	 * Builds a road for the player with the cost withdrawn from resources.
	 */
	public void buyRoad()
	{
		this.payForStructure( Structure.REQUIREMENTS_FOR_ROAD );

		// Add structure to the player structure data collection. Not implemented yet as if it is array or AL

		this.score += Structure.VICTORY_POINTS_FOR_ROAD;
	}

	/**
	 * Builds a city for the player with the cost withdrawn from resources.
	 */
	public void buyCity()
	{
		this.payForStructure( Structure.REQUIREMENTS_FOR_CITY );
		// Add structure to the player structure data collection. Not implemented yet as if it is array or AL

		this.score += Structure.VICTORY_POINTS_FOR_CITY;
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
	 * ------------------DEPRECATED---------------------------
	 * Player plays the special card stated by the card index.
	 * @param cardIndex is the index of the special card to play.

	public void playSpecialCard( int cardIndex)
	{
		//cards.remove( cardIndex).play(); to be implemented after card class is implemented.
	}
	  */

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
	 * sets the player's longest road
	 */
	public void setRoadLength( int roadLength ) { this.roadLength = roadLength; }

	/**
	 * Increments the player's largest army count by 1 when player plays the "Knight" special card.
	 */
	public void incrementLargestArmy() { armyCount++; }

	/**
	 * Checks if the player has more roads than the current "Longest Road" card holder. If they do, they get the
	 * "Longest Road" card.
	 * @param currentLongest is the current longest road count of a player.
	 * @return roadLength when the player achieves the longest road count after building a road.
	 * @return currentLongest when player either equals or falls below the current longest road holder.
	 */
	public int checkLongestRoad(int currentLongest)
	{
		if ( roadLength > currentLongest) {
			hasLongestRoad = true;
			return roadLength;
		}
		return currentLongest;
	}

	/**
	 * Checks if the player has more "Knight" cards played than the current "Largest Army" card holder. If they do, they get the
	 * "Largest Army" card.
	 * @param currentLargest is the current longest road count of a player.
	 * @return armyCount when the player achieves the largest army count after playing the Knight card.
	 * @return currentLargest when player either equals or falls below the current largest army holder.
	 */
	public int checkLargestArmy(int currentLargest)
	{
		if ( armyCount > currentLargest) {
			hasLargestArmy = true;
			return armyCount;
		}
		return currentLargest;
	}

	/**
	 * Increases the score of the player by the given amount.
	 * @param scoreAmount is the amount of score that will be added to the player.
	 */
	public void increaseScore(int scoreAmount)
	{
		score += scoreAmount;
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

	/**
	 * Gets the player's structures.
	 * @return player's structures as ArrayList.
	 */
	public ArrayList<Structure> getStructures(){
		return this.structures;
	}

	/**
	 * Add given structure to the structures ArrayList
	 * @param structure Given structure that is wanted to add.
	 */
	public void addStructure(Structure structure){
		this.structures.add(structure);
	}

	/**
	 * Gets the player's name.
	 * @return player's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the player name to the given name.
	 * @param name The name that will be set as player's name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the player's color.
	 * @return player's color.
	 */
	public javafx.scene.paint.Color getColor() {
		return color;
	}

	/**
	 * Sets the player color to the given color.
	 * @param color The color that will be set as player's color.
	 */
	public void setColor(javafx.scene.paint.Color color) {
		this.color = color;
	}

	public int[] getResources() {
		return resources;
	}

	public void setResources(int[] resources) {
		this.resources = resources;
	}
	
	/**
	 * Add a new port to the player's ports ArrayList
	 * @param port is the portType wanted to add
	 */
	public void addPort(Port.PortType port){
		ports.add(new Port(port));
	}

	/**
	 * Return player's port list
	 * @return ports which is player's port list
	 */
	public boolean hasPort(Port.PortType port){
		boolean result = false;
		for(int i = 0 ; i < ports.size() ; i++){
			if(ports.get(i).getPortType() == port)
				result = true;
		}
		return result;
	}
}
