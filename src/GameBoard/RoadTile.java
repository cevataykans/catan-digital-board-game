package GameBoard;

/**
 * Represents a road tile
 * @author Yusuf Nevzat Şengün
 * @version 25.11.2019
 */

public class RoadTile extends StructureTile {

    public enum RotationType{
        UPPER_RIGHT_VERTICAL, //  ->  /
        UPPER_LEFT_VERTICAL,  //  ->  \
        HORIZONTAL            //  ->  -
    }

    //properties
    private RoadTile.RotationType rotation;

    //constructor
    public RoadTile( RoadTile.RotationType rotation, int x, int y){
        super( x, y);
        this.rotation = rotation;
    }

    /**
     * get rotation
     * @return rotation
     */
    public RoadTile.RotationType getRotation(){
        return rotation;
    }

}
