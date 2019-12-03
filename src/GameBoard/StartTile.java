package GameBoard;

/**
 * Represents a start tile
 * @author Yusuf Nevzat Şengün
 * @version 25.11.2019
 */

public class StartTile extends BuildingTile {
    //properties
    private int diceNumber;
    private int resource;

    //constructor
    public StartTile( BuildingTile.BuildingType type, int diceNumber, int resource, int x, int y){
        super( type, x, y);
        this.diceNumber = diceNumber;
        this.resource = resource;
    }

    /**
     * get dice number
     * @return dice number
     */
    public int getDiceNumber(){
        return diceNumber;
    }

    /**
     * get resource
     * @return resource
     */
    public int getResource(){
        return resource;
    }

}
