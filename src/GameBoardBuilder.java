import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Cevat Aykan Sevinc
 * @version 25.11.2019
 */
public class GameBoardBuilder
{
	//constants
	final private int FIELDPEREDGE = 3;
	final private int WIDTH = FIELDPEREDGE * 8 - 1;
	final private int HEIGHT = FIELDPEREDGE * 8 - 3;

	// Attributes
	private Tile[][] board;
	private StartTile robber;
	private ArrayList<Integer> diceNumbers;
	private ArrayList<Integer> resources;
	private ArrayList<Port.PortType> ports;

	public GameBoardBuilder()
	{
		this.board = new Tile[HEIGHT][WIDTH];
		/*
		for( int i = 0 ; i < HEIGHT ; i++ ){
			for( int j = 0 ; j < WIDTH ; j++ ){
				board[i][j] = new Tile();
			}
		}
		*/
		this.robber = null;
		this.diceNumbers = new ArrayList<>();
		this.resources = new ArrayList<>();
		this.ports = new ArrayList<>();
	}

	public void configurate()
	{
		this.addDiceNumbers();
		this.addResources();
		this.setUpGameBoard();
		this.setPorts();
	}

	/**
	 * After decide counts of dice numbers add them to an arraylist and shuffles.
	 * diceCounts are filled through 2 to 12
	 */
	private void addDiceNumbers(){
		int[] diceCounts = {1,2,2,2,2,1,2,2,2,2,1};

		for( int i = 2 ; i <= 12 ; i++ )
			for( int j = 0 ; j < diceCounts[i - 2] ; j++ )
				diceNumbers.add(i);

		Collections.shuffle(diceNumbers);
	}

	/**
	 * After decide counts of resources add them to an arraylist and shuffles
	 * for the resourceCount array:
	 *      0-index = saman
	 *      1-index = odun
	 *      2-index = mermer
	 *      3-index = kaya
	 *      4-index = koyun
	 *      çöl -> will be assigned automatically when dice is 7
	 */
	private void addResources(){
		int[] resourceCounts = {4,4,3,3,4};

		for( int i = 0 ; i <= 4 ; i++ )
			for( int j = 0 ; j < resourceCounts[i] ; j++ )
				resources.add(i);

		Collections.shuffle(resources);
	}

	/**
	 * distrubutes ports to exact positions randomly
	 */
	private void setPorts(){
		int[][] positions={ // (x,y,x,y)
				{10,0,12,0},{4,4,6,2},{0,10,2,8},{0,14,2,16},{6,18,8,18},{14,18,16,18},{20,16,22,14},{20,8,22,10},{16,2,18,4}
		};
		int[] portTypes={4,1,1,1,1,1};

		int ii = 0;
		for( Port.PortType pt : Port.PortType.values() ){
			for( int j = 0 ; j < portTypes[ii] ; j++ ){
				ports.add(pt);
			}
			ii++;
		}

		Collections.shuffle(ports);

		for( int i = 0 ; i < ports.size(); i++ )
		{
			System.out.println( i);
			((BuildingTile)board[positions[i][1]][positions[i][0]]).setPort(ports.get(i));
			((BuildingTile)board[positions[i][3]][positions[i][2]]).setPort(ports.get(i));
		}
	}

	/**
	 * Determine the most upper-left game tile and start the process from there, downward
	 */
	private void setUpGameBoard(){
		int x = WIDTH / 2 - 1;
		int y = 0;

		for( int i = 1 ; y < HEIGHT ; y += 4, i++ )
			setUpByTraversingHexagon( x, y, i);
	}

	/**
	 * Starting from the given x and y traverse toward left and right, traverse only the start points of the hexagons,
	 * other tiles of the hexagons are filled in another method
	 * @param x x-coordinate of the start point
	 * @param y y-coordinate of the start point
	 * @param numberOfHexagon number of the field from the top, with this param we can know the boundaries of game board
	 */
	private void setUpByTraversingHexagon( int x, int y, int numberOfHexagon ){
		setUpLeftHexagon( x, y, numberOfHexagon ); //center hexagon is traversed here
		setUpRightHexagon( x + 4, y + 2, numberOfHexagon );
	}

	/**
	 * traverse toward left, traverse only the starting point of the hexagons
	 * @param x x-coordinate of the start point
	 * @param y y-coordinate of the start point
	 * @param numberOfHexagon number of the field from the top, with this param we can know the boundaries of game board
	 */
	private void setUpLeftHexagon( int x, int y, int numberOfHexagon ){
		int iterationNum = (numberOfHexagon<=FIELDPEREDGE ? FIELDPEREDGE: FIELDPEREDGE * 2 - numberOfHexagon);

		int dice, resource;
		for( int i = 1 ; i <= iterationNum ; x -= 4, y += 2, i++ ){
			dice = diceNumbers.get(diceNumbers.size() - 1);
			diceNumbers.remove(diceNumbers.size()-1);

			if( dice != 7 ) {
				resource = resources.get(resources.size() - 1);
				resources.remove( resources.size() - 1);
			}
			else{ // if dice is 7 then this hexagon will be desert
				resource = 5;
			}
			fillHexagon( x, y, dice, resource);

			if(dice == 7)
				this.robber = (StartTile)board[y][x];
		}
	}

	/**
	 * traverse toward right, traverse only the starting point of the hexagons
	 * @param x x-coordinate of the start point
	 * @param y y-coordinate of the start point
	 * @param numberOfHexagon number of the field from the top, with this param we can know the boundaries of game board
	 */
	private void setUpRightHexagon( int x, int y, int numberOfHexagon){
		int iterationNum = (numberOfHexagon<=FIELDPEREDGE ? FIELDPEREDGE : FIELDPEREDGE * 2 - numberOfHexagon ) - 1;

		int dice, resource;
		for( int i = 1 ; i <= iterationNum ; x += 4, y += 2, i++ ){
			dice = diceNumbers.get(diceNumbers.size() - 1);
			diceNumbers.remove(diceNumbers.size()-1);
			if( dice != 7 ) {
				resource = resources.get(resources.size() - 1);
				resources.remove( resources.size() - 1);
			}
			else{ // if dice is 7 then this hexagon will be desert
				resource = 5;
			}
			fillHexagon( x, y, dice, resource);

			if(dice == 7)
				this.robber = (StartTile)board[y][x];
		}
	}

	/**
	 * traverse all tiles of the current hexagon
	 * resource and dice info are kept on start tile of each hexagon,
	 * and all other tiles will know their all starting points as a list(1 tile can belong to more than one hexagon)
	 * @param x x-coordinate of the start point of this hexagon
	 * @param y y-coordinate of the start point of this hexagon
	 * @param dice determined dice number for this hexagon
	 * @param resource determined resource for this hexagon
	 */
	private void fillHexagon( int x, int y, int dice, int resource){
		// keeps the distance from the last tile as x, y
		int[][] changeNext = {
				{-1,1}, {-1,1},
				{1,1}, {1,1},
				{1,0}, {1,0},
				{1,-1}, {1,-1},
				{-1,-1}, {-1,-1},
				{-1,0}
		};

		// keeps the start point of this hexagon
		StartTile startTile = null;

		for( int i = 0 ; i < 12 ; i++ ){
			if( i == 0 ){
				board[y][x] = new StartTile( BuildingTile.BuildingType.SETTLEMENT, dice, resource, x, y );
				startTile = (StartTile) board[y][x];
				((StartTile)board[y][x]).addStartTile( startTile);
			}
			else if( i % 6 == 1 )
				board[y][x] = new RoadTile( RoadTile.RotationType.UPPER_RIGHT_VERTICAL, x, y );
			else if( i % 6 == 3 )
				board[y][x] =  new RoadTile(RoadTile.RotationType.UPPER_LEFT_VERTICAL, x, y);
			else if( i % 6 == 5 )
				board[y][x] = new RoadTile(RoadTile.RotationType.HORIZONTAL, x, y);
			else {
				board[y][x] = new BuildingTile( BuildingTile.BuildingType.SETTLEMENT, x, y);
				((BuildingTile)board[y][x]).addStartTile( startTile);
			}


			if(i < 11){
				x += changeNext[i][0];
				y += changeNext[i][1];
			}
		}

		fillInsideWithStartPoint( startTile);
	}

	/**
	 * fill inside of the given hexagon with their starting point by using bfs
	 * @param startTile start point of the hexagon
	 */
	private void fillInsideWithStartPoint( StartTile startTile){
		int x = startTile.getX();
		int y = startTile.getY();
		int[][] tiles = { // (x,y)
				{-1,2},{0,1},{0,2},{0,3},{1,1},{1,2},{1,3},{2,1},{2,2},{2,3},{3,2}
		};

		for( int i = 0 ; i < 11 ; i++){
			int targetX = x + tiles[i][0];
			int targetY = y + tiles[i][1];

			board[targetY][targetX] = new InsideTile( startTile, x, y);
		}
	}

	/**
	 * Returns the configurated robber to to gameboard.
	 * @return Tile robber for gameboard.
	 */
	public StartTile getRobber()
	{
		return this.robber;
	}

	/**
	 * Returns configurated board to the GameBoard
	 * @return 2D tile array board for gameboard to use.
	 */
	public Tile[][] getBoard()
	{
		return this.board;
	}


}
