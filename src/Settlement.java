
public class Settlement extends Structure {

	protected Settlement(Player owner, int x, int y) {
		super(owner);
		this.victoryPoints = VICTORY_POINTS_FOR_SETTLEMENT;

		this.x = x;
		this.y = y;
		
		this.information = "Settlement info"; // It will be written
		
	}

	
}
