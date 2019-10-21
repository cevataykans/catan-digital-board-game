public abstract class Structure {

	//constants
	public static final int[] REQUIREMENTS_FOR_ROAD = {1,0,0,1,0};
	public static final int[] REQUIREMENTS_FOR_SETTLEMENT = {1,1,1,1,0};
	public static final int[] REQUIREMENTS_FOR_CITY = {0,0,2,0,3};

	public static int VICTORY_POINTS_FOR_ROAD = 0;
	public static int VICTORY_POINTS_FOR_SETTLEMENT = 1;
	public static int VICTORY_POINTS_FOR_CITY = 2;

	//properties
	protected String information;
	protected int victoryPoints;
	protected Player owner;
	protected int x;
	protected int y;

	protected Structure( Player owner ) {
		this.owner = owner;
	}

	public int getType(){
		return this.victoryPoints;
	}

	public Player getOwner(){
		return this.owner;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

}
