<<<<<<< HEAD
import java.util.ArrayList;

=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa
public class Tile{

	// Attributes
	private boolean isGameTile;
	private Structure structure;
	private boolean isStartPoint;
	private int diceNumber;
	private int resource;
<<<<<<< HEAD
	private ArrayList<Tile> startPoints; // List of tiles that are start points of hexagons that includes this tile
=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa


	// Constructor
	public Tile(){
		this.isGameTile = false;
		this.structure = null;
		this.isStartPoint = false;
		this.diceNumber = -1;
		this.resource = -1;
<<<<<<< HEAD
		this.startPoints = new ArrayList<Tile>();
=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa
	}


	// Getter and setter methods
<<<<<<< HEAD


	// isGameTile methods

=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa
	public boolean isItGameTile(){
		return this.isGameTile;
	}

	public void setGameTile(){
		this.isGameTile = true;
	}

<<<<<<< HEAD

	// structure methods

=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa
	public Structure getStructure(){
		return this.structure;
	}

<<<<<<< HEAD
	public boolean isEmpty(){
		return this.structure == null;
	}

=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa
	public void setStructure(Structure structure){
		this.structure = structure;
	}

<<<<<<< HEAD

	// isStartPoint methods

=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa
	public boolean isItStartPoint(){
		return this.isStartPoint;
	}

	public void setStartPoint(){
		this.isStartPoint = true;
	}

<<<<<<< HEAD
	public void addStartPoint(Tile tile){
		startPoints.add(tile);
	}

	// diceNumber methods

=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa
	public int getDiceNumber(){
		return this.diceNumber;
	}

	public void setDiceNumber(int diceNumber){
		this.diceNumber = diceNumber;
	}

<<<<<<< HEAD
	// resource methods

=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa
	public int getResource(){
		return this.resource;
	}

	public void setResource(int resource){
		this.resource = resource;
	}
<<<<<<< HEAD
	
=======
>>>>>>> b8ca6d4417c5ab836635a4b59e6c5cedb398efaa

}