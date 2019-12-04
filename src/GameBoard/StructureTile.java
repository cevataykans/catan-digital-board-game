package GameBoard;
import GameFlow.Player;

/**
 * Represents a structure tile
 * @author Yusuf Nevzat Şengün
 * @version 25.11.2019
 */

public abstract class StructureTile extends Tile {

    //constants
    public static final int[] REQUIREMENTS_FOR_ROAD = {1,0,0,1,0};
    public static final int[] REQUIREMENTS_FOR_SETTLEMENT = {1,1,1,1,0};
    public static final int[] REQUIREMENTS_FOR_CITY = {0,0,2,0,3};

    public static int VICTORY_POINTS_FOR_ROAD = 0;
    public static int VICTORY_POINTS_FOR_SETTLEMENT = 1;
    public static int VICTORY_POINTS_FOR_CITY = 2;

    //properties
    private boolean availability;
    private Player owner;

    //constructor
    public StructureTile( int x, int y){
        super( x, y);
        availability = false;
        owner = null;
    }

    /**
     * get owner
     * @return owner
     */
    public Player getOwner(){
        return owner;
    }

    public void setOwner( Player owner ){
        this.owner = owner;
    }

    /**
     * get availability
     * @return availability
     */
    public boolean getAvailability(){
        return availability;
    }

    /**
     * sets the availability true
     */
    public void setAvailability(){
        this.availability = true;
    }
}
