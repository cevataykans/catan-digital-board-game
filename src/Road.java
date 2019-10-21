
public class Road extends Structure {

	protected Road(Player owner, int x, int y) {
		super(owner);
		this.victoryPoints = VICTORY_POINTS_FOR_ROAD;

		this.x = x;
		this.y = y;

		this.information = "Road info"; // It will be written
		
	}

}
