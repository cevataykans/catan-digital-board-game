package GameBoard;

/**
 * Represents an inside tile
 * @author Yusuf Nevzat Şengün
 * @version 25.11.2019
 */

public class InsideTile extends Tile {

    //properties
    private StartTile startTile;

    //constructor
    public InsideTile( StartTile startTile, int x, int y ){
        super( x, y);
        this.startTile = startTile;
    }

    /**
     * get startTile
     * @return startTile
     */
    public StartTile getStartTile(){
        return startTile;
    }
}
