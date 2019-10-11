public class Tile{

	// Attributes
	private boolean isGameTile;
	private Structure structure;
	private boolean isStartPoint;
	private int diceNumber;
	private int resource;


	// Constructor
	public Tile(){
		this.isGameTile = false;
		this.structure = null;
		this.isStartPoint = false;
		this.diceNumber = -1;
		this.resource = -1;
	}


	// Getter and setter methods
	public boolean isItGameTile(){
		return this.isGameTile;
	}

	public void setGameTile(){
		this.isGameTile = true;
	}

	public Structure getStructure(){
		return this.structure;
	}

	public void setStructure(Structure structure){
		this.structure = structure;
	}

	public boolean isItStartPoint(){
		return this.isStartPoint;
	}

	public void setStartPoint(){
		this.isStartPoint = true;
	}

	public int getDiceNumber(){
		return this.diceNumber;
	}

	public void setDiceNumber(int diceNumber){
		this.diceNumber = diceNumber;
	}

	public int getResource(){
		return this.resource;
	}

	public void setResource(int resource){
		this.resource = resource;
	}

}