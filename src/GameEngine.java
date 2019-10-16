import java.awt.*;

/**
 * GameEngine class combines logic of the game board and players to enable users to play the game through model view
 * controller. Game Engine would constantly communicate with UIEngine to handle player/user events.
 * @author Cevat Aykan Sevinc
 * @version 14.10.2019
 * Class is created to later combine all the logic.
 * Log 16.10.2019 (Talha)
 * -----------------------
 * Created class attributes, constructore and functions.
 * Added function templates.
 * Implemented some functions.
 * -----------------------
 */
public class GameEngine
{
    // Attributes
    UI ui;
    GameBoard board;
    Player[] players;
    int turnNumber;

	// Constructor
    public GameEngine()
    {
        board = new GameBoard();
        players = new Player[4];
        for ( int i = 0; i < 4; i++)
        {
            players[i] = new Player("Placeholder name", Color.BLACK);
        }
        turnNumber = 0;
    }

	// Functions
    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public Player getPlayer(int playerIndex)
    {
        return players[playerIndex];
    }

    public Player getCurrentPlayer()
    {
        return players[turnNumber % 4];
    }

    public int getTurn()
    {
        return turnNumber;
    }

    public void moveThief(int hexagonNumber)
    {
        // To be implemented.
    }

    public void buildRoad(boolean roadBuildingCard)
    {
        // To be implemented.
    }

    public void buildSettlement()
    {
        // To be implemented.
    }

    public void buildCity()
    {
        // To be implemented.
    }
}
