
public class City extends Structure {

	protected City(String name, Player owner, int x, int y) {
		super(name, owner);
		this.pointValue = 2;
		
		this.requirements[0] = 0;
		this.requirements[1] = 0;
		this.requirements[2] = 2;
		this.requirements[3] = 0;
		this.requirements[4] = 3;

		this.x = x;
		this.y = y;

		this.information = "City info"; // It will be written
		
	}

	
}
