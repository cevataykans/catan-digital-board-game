
public class Settlement extends Structure {

	protected Settlement(String name, Player owner) {
		super(name, owner);
		this.pointValue = 1;
		
		this.requirements[0] = 1;
		this.requirements[1] = 1;
		this.requirements[2] = 1;
		this.requirements[3] = 1;
		this.requirements[4] = 0;

		this.information = "";
		
	}

	
}
