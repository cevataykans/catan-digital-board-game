
public class Road extends Structure {

	enum RotationType{
		UPPER_LEFT_VERTICAL, UPPER_RIGHT_VERTICAL, HORIZONTAL
	}

	private Road.RotationType rotation;

	protected Road(Player owner, int x, int y) {
		super(owner);
		this.victoryPoints = VICTORY_POINTS_FOR_ROAD;

		this.x = x;
		this.y = y;

		this.information = "Road info"; // It will be written

		this.type = Type.ROAD;
	}

	public Road.RotationType getRotation(){
		return rotation;
	}

	public void setRotation( Road.RotationType rotation ){
		this.rotation = rotation;
	}

}
