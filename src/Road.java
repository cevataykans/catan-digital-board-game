
public class Road extends Structure {

	protected Road(String name, Player owner) {
		super(name, owner);
		this.pointValue = 0;
		
		this.requirements[0] = 1;
		this.requirements[1] = 0;
		this.requirements[2] = 0;
		this.requirements[3] = 1;
		this.requirements[4] = 0;

		this.information = "";
		
	}

	
}
