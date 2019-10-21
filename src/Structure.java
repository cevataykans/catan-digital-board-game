public abstract class Structure {
	//properties
	protected String name;
	protected String information;
	protected int pointValue;
	protected Player owner;
	protected int x;
	protected int y;
	protected int[] requirements; //lumber, wool, grain, brick, ore
	
	protected Structure( String name, Player owner ) {
		this.name = name;
		this.owner = owner;
		this.requirements = new int[5];
	}

	public int getType(){
		return this.pointValue;
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
