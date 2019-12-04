package GameBoard;

/**
 * GameBoard.Tile class combines all type of class, tile class is used for polymorphism
 * @author Yusuf Nevzat Şengün
 * @version 25.11.2019
 */

public abstract class Tile {
    //properties
    private int x;
    private int y;

    //constructor
    public Tile( int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * get x
     * @return x
     */
    public int getX(){
        return x;
    }

    /**
     * get y
     * @return y
     */
    public int getY(){
        return y;
    }
}
