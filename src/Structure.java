public abstract class Structure {
	//properties
	protected String name;
	protected String information;
	protected int pointValue;
	protected Player owner;
	protected int[] requirements; //lumber, wool, grain, brick, ore
	
	protected Structure( String name, Player owner ) {
		this.name = name;
		this.owner = owner;
	}
			
}
