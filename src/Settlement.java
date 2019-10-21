
public class Settlement extends Structure {

	protected Settlement(String name, Player owner, int x, int y) {
		super(name, owner);
		this.pointValue = 1;
		
		this.requirements[0] = 1;
		this.requirements[1] = 1;
		this.requirements[2] = 1;
		this.requirements[3] = 1;
		this.requirements[4] = 0;

		this.x = x;
		this.y = y;
		
		this.information = "Settlement info"; // It will be written
		
	}

	
}
