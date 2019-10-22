import java.util.ArrayList;

/**
 * The tile class that is used to represent each part of the gameboard.
 * @author Hakan Sivuk
 * @version 14.10.2019
 * Class is created and functions are implemented. Open to further implementation.
 * --------------
 * Log 20.10.2019 (Hakan)
 * Implemented buyDevelopmentCard() function.
 */

public class Tile{

	// Attributes
	private boolean isGameTile;
	private Structure structure;
	private boolean isStartPoint;
	private int diceNumber;
	private int resource;
	private ArrayList<Tile> startPoints; // List of tiles that are start points of hexagons that includes this tile
	private boolean robber;


	// Constructor
	public Tile(){
		this.isGameTile = false;
		this.structure = null;
		this.isStartPoint = false;
		this.diceNumber = -1;
		this.resource = -1;
		this.startPoints = new ArrayList<Tile>();
		this.robber = false;
	}


	// Getter and setter methods


	// isGameTile methods

	/**
	 * Checks whether this tile is a game tile.
	 * @return true if the tile is a game tile, else false.
	 */
	public boolean isItGameTile(){
		return this.isGameTile;
	}

	/**
	 * Sets the tile as a game tile.
	 */
	public void setGameTile(){
		this.isGameTile = true;
	}


	// structure methods

	/**
	 * Returns the structure that tile has.
	 * @return the structure that tile has, if there is no tile it returns null
	 */
	public Structure getStructure(){
		return this.structure;
	}

	/**
	 * Checks whether the tile has a structure.
	 * @return true if the tile has no structure, else false.
	 */
	public boolean isEmpty(){
		return this.structure == null;
	}

	/**
	 * Sets structure of the tile to the given structure.
	 * @param structure to set the structure instance to this structure.
	 */
	public void setStructure(Structure structure){
		this.structure = structure;
	}


	// isStartPoint methods

	/**
	 * Checks whether the tile is a start point.
	 * @return true if the tile is a start point, else false.
	 */
	public boolean isItStartPoint(){
		return this.isStartPoint;
	}

	/**
	 * Sets the tile as a start point.
	 */
	public void setStartPoint(){
		this.isStartPoint = true;
	}

	/**
	 * Returns start points of hexagons that include this tile
	 * @return startPoints array
	 */
	public ArrayList<Tile> getStartPoints(){
		return startPoints;
	}

	/**
	 * Adds start point to the tile.
	 * @param tile that is wanted to add.
	 */
	public void addStartPoint(Tile tile){
		startPoints.add(tile);
	}

	// diceNumber methods

	/**
	 * Gets dice number of the tile.
	 * @return the dice number.
	 */
	public int getDiceNumber(){
		return this.diceNumber;
	}

	/**
	 * Sets dice number to given integer.
	 * @param diceNumber that is wanted to set to the diceNumber.
	 */
	public void setDiceNumber(int diceNumber){
		this.diceNumber = diceNumber;
	}

	// resource methods

	/**
	 * Returns resource that tile has.
	 * @return the resource that tile has.
	 */
	public int getResource(){
		return this.resource;
	}

	/**
	 * Sets resource to the given resource.
	 * @param resourceToCheck that is wanted to set to the resource.
	 */
	public void setResource(int resource){
		this.resource = resource;
	}

	/**
	 * Sets robber to true
	 */
	public void setRobber(boolean robber){
		this.robber = robber;
	}

	/**
	 * Returns whether there is a robber
	 * @return robber boolean
	 */
	public boolean isThereRobber(){
		return this.robber;
	}

}