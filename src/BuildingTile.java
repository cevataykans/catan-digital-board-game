import java.util.ArrayList;

/**
 * Represents a building tile
 * @author Yusuf Nevzat Şengün
 * @version 25.11.2019
 */

public class BuildingTile extends StructureTile {

    enum BuildingType{
        SETTLEMENT, CITY
    }

    //properties
    private BuildingTile.BuildingType type;
    private Port.PortType port;
    private ArrayList<StartTile> startTiles;

    //constructor
    public BuildingTile( BuildingTile.BuildingType type, int x, int y){
        super( x, y);
        this.type = type;
        this.port = null;
        this.startTiles = new ArrayList<>();
    }

    /**
     * get building type
     * @return building type
     */
    public BuildingTile.BuildingType getType(){
        return type;
    }

    /**
     * changes the type
     */
    public void upgradeToCity(){
        this.type = BuildingType.CITY;
    }

    /**
     * get port type
     * @return port type
     */
    public Port.PortType getPort(){
        return port;
    }

    /**
     * sets the port type of this hexagon
     * @param port port type
     */
    public void setPort( Port.PortType port){
        this.port = port;
    }

    /**
     * get start tiles
     * @return start tile
     */
    public ArrayList<StartTile> getStartTiles(){
        return startTiles;
    }

    /**
     * add a new start tile to this tile
     * @param startTile a new start tile
     */
    public void addStartTile( StartTile startTile){
        startTiles.add( startTile);
    }
}
