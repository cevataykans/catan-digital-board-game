import java.util.ArrayList;

public class Tile{

	// Attributes
	private boolean isGameTile;
	private Structure structure;
	private boolean isStartPoint;
	private int diceNumber;
	private int resource;
	private ArrayList<Tile> startPoints; // List of tiles that are start points of hexagons that includes this tile


	// Constructor
	public Tile(){
		this.isGameTile = false;
		this.structure = null;
		this.isStartPoint = false;
		this.diceNumber = -1;
		this.resource = -1;
		this.startPoints = new ArrayList<Tile>();
	}


	// Getter and setter methods


	// isGameTile methods

	public boolean isItGameTile(){
		return this.isGameTile;
	}

	public void setGameTile(){
		this.isGameTile = true;
	}


	// structure methods

	public Structure getStructure(){
		return this.structure;
	}

	public boolean isEmpty(){
		return this.structure == null;
	}

	public void setStructure(Structure structure){
		this.structure = structure;
	}


	// isStartPoint methods

	public boolean isItStartPoint(){
		return this.isStartPoint;
	}

	public void setStartPoint(){
		this.isStartPoint = true;
	}

	public void addStartPoint(Tile tile){
		startPoints.add(tile);
	}

	// diceNumber methods

	public int getDiceNumber(){
		return this.diceNumber;
	}

	public void setDiceNumber(int diceNumber){
		this.diceNumber = diceNumber;
	}

	// resource methods

	public int getResource(){
		return this.resource;
	}

	public void setResource(int resource){
		this.resource = resource;
	}
	

}