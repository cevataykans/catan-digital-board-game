package Player;

import DevelopmentCards.Card;
import GameBoard.Harbor;
import GameBoard.StructureTile;
import GameFlow.Game;
import GameFlow.ResourceManager;
import javafx.scene.paint.Color;
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
 * --------------
 * Log 13.11.2019 (Cevat)
 * Implemented stealing a random resource through robber placement function. Corrected total resource implementation in
 * functions.
 */
public class Player
{
	// Player.Player Attributes
	private String name;
	private javafx.scene.paint.Color color; // needs to be discussed for finalization;

	private int[] resources = { 4, 2, 2, 4, 0};
	private int totResources;

	private ArrayList<StructureTile> structures; // needs to be discuessed for finalization;
	private ArrayList<Card> cards;
	private ArrayList<Harbor> ports;

	// Player.Player Attributes related to score and board
	private int score;
	private int settlementCount;
	private int roadCount;
	private int cityCount;
	private boolean hasLargestArmy;
	public boolean hasLongestRoad;
	private int roadLength;
	private int armyCount;

	// Constructors
	public Player(String name, Color color)
	{
		this.name = name;
		this.color = color;
		this.totResources = 12; // pre construct for initial resources, not final!
		this.cards = new ArrayList<>();
		this.structures = new ArrayList<>();
		this.ports = new ArrayList<>();

		this.score = 0;
		this.settlementCount = 0;
		this.roadCount = 0;
		this.cityCount = 0;
		this.hasLargestArmy = false;
		this.hasLongestRoad = false;
		this.roadLength = -1;
		this.armyCount = 0;
	}


	/**
	 * Gives the player the amount of material stated by the material constant.
	 * @param material is one of the index of resource array:
	 *                 ResourceManager.LUMBER, ResourceManager.WOOL, ResourceManager.GRAIN, ResourceManager.BRICK, ResourceManager.ORE.
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
	 *                 ResourceManager.LUMBER, ResourceManager.WOOL, ResourceManager.GRAIN, ResourceManager.BRICK, ResourceManager.ORE.
	 * @param amount is the number of materials to discard from this player.
	 */
	public void discardMaterial( int material, int amount)
	{
		resources[material] -= amount;
		totResources -= amount;
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
		totResources -= 3;
		cards.add( card);
	}

	/**
	 * Builds a road for the player with the cost withdrawn from resources.
	 */
	public void buySettlement()
	{
		// Discard resources from the player required for a settlement.
		//this.payForStructure( StructureTile.REQUIREMENTS_FOR_SETTLEMENT );
		new ResourceManager().payForStructure( StructureTile.REQUIREMENTS_FOR_SETTLEMENT);

		// Increase the score of the player by 1 and increase the player's settlement count by 1.
		this.score += StructureTile.VICTORY_POINTS_FOR_SETTLEMENT;
		this.settlementCount += 1;
	}

	/**
	 * Builds a road for the player with the cost withdrawn from resources.
	 */
	public void buyRoad()
	{
		// Discard resources from the player required for a road.
		//this.payForStructure( StructureTile.REQUIREMENTS_FOR_ROAD );
		new ResourceManager().payForStructure( StructureTile.REQUIREMENTS_FOR_ROAD);

		// Increase the score of the player by 0 and increase the player's road count by 1.
		this.score += StructureTile.VICTORY_POINTS_FOR_ROAD;
		this.roadCount += 1;
	}

	/**
	 * Builds a city for the player with the cost withdrawn from resources.
	 */
	public void buyCity()
	{
		// Discard resources from the player required for a city.
		//this.payForStructure( StructureTile.REQUIREMENTS_FOR_CITY );
		new ResourceManager().payForStructure( StructureTile.REQUIREMENTS_FOR_CITY);

		// Increase the score of the player by 2 and increase the player's city count by 1.
		this.score -= StructureTile.VICTORY_POINTS_FOR_SETTLEMENT;
		this.score += StructureTile.VICTORY_POINTS_FOR_CITY;
		this.cityCount += 1;
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
	 * Gets the largest army count of the player.
	 * @return the largest army count of the player. If the player does not have the largest army, return -1 pivot.
	 */
	public int getLargestArmyCount()
	{
		return this.armyCount;
	}

	/**
	 * Gets the longest road length of the player.
	 * @return the longest road length of the player. If the player does not have the longest road, return -1 pivot.
	 */
	public int getLongestRoadLength()
	{
		return this.roadLength;
	}

	/**
	 * sets the player's longest road.
	 */
	public void setRoadLength( int roadLength ) { this.roadLength = roadLength; }

	/**
	 * Increments the player's largest army count by 1 when player plays the "DevelopmentCards.Knight" special card.
	 */
	public void incrementLargestArmy() { this.armyCount++; }

	/**
	 * Sets the longest road title of the player.
	 * @param hasLongestRoad is the boolean value to set the title - true if the player has the title else false
	 */
	public void setLongestRoadTitle( boolean hasLongestRoad)
	{
		this.hasLongestRoad = hasLongestRoad;
	}

	/**
	 * Sets the largest army title of the player.
	 * @param hasLargestArmy is the boolean value to set the title - true if the player has the title else false
	 */
	public void setLargestArmyTitle( boolean hasLargestArmy)
	{
		this.hasLargestArmy = hasLargestArmy;
	}

	/**
	 * Increases the score of the player by the given amount.
	 * @param scoreAmount is the amount of score that will be added to the player.
	 */
	public void increaseScore( int scoreAmount)
	{
		score += scoreAmount;
	}

	/**
	 * Gets the player's structures.
	 * @return player's structures as ArrayList.
	 */
	public ArrayList<StructureTile> getStructures(){
		return this.structures;
	}

	/**
	 * Add given structure to the structures ArrayList
	 * @param structure Given structure that is wanted to add.
	 */
	public void addStructure( StructureTile structure){
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
	public void setColor( javafx.scene.paint.Color color) {
		this.color = color;
	}

	public int[] getResources() {
		return resources;
	}

	public ArrayList<Card> getCards() {return cards;}

	/**
	 * Returns the total settlement count of the player.
	 * @return the settlement count.
	 */
	public int getSettlementCount() {
		return settlementCount;
	}

	/**
	 * Returns the total road count of the player.
	 * @return the road count.
	 */
	public int getRoadCount() {
		return roadCount;
	}

	/**
	 * Returns the total city count of the player.
	 * @return the city count.
	 */
	public int getCityCount() {
		return cityCount;
	}
	
	/**
	 * Add a new port to the player's ports ArrayList
	 * @param port is the portType wanted to add
	 */
	public void addHarbor( Harbor.HarborType port){
		ports.add( new Harbor(port));
	}

	/**
	 * Return player's port list
	 * @return ports which is player's port list
	 */
	public boolean hasHarbor( Harbor.HarborType port)
	{
		boolean result = false;
		for( Harbor harbor : ports)
		{
			if( harbor.getHarborType() == port) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Return the total resource count of the player.
	 * @return int - the number of total resource count.
	 */
	public int getTotalResCount()
	{
		return this.totResources;
	}
}
