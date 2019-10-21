
public class City extends Structure {

	protected City( Player owner, int x, int y) {
		super(owner);
		this.victoryPoints = VICTORY_POINTS_FOR_CITY;

		this.x = x;
		this.y = y;

		this.information = "City info"; // It will be written
		
	}

	
}
