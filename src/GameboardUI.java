import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;

import javafx.stage.StageStyle;

import java.util.Optional;

public class GameboardUI extends Application {

	// Static messages for display dialog!


	// Properties for UI
	Group root;
	Canvas canvas; // to print gameboard, there should be other canvasses for printing cards, player or such
	Label statusBar; // status bar to display user events

	// Properties for logic
	GameController controller; // GameController logic
	Tile[][] board;


	@Override
	public void start(Stage primaryStage) throws Exception {

		//this.root = new Group(); //FXMLLoader.load(getClass().getResource("sample.fxml"));

		// Temp layout for display
		BorderPane layout = new BorderPane();

		this.canvas = new Canvas( 900, 900);
		layout.setCenter( this.canvas);
		Scene scene = new Scene( layout,  1920, 1080); // scene should be full screen
		scene.setFill( new ImagePattern( new Image( "/images/grain.png") ) );
		//this.root.getChildren().add( this.canvas);

		this.statusBar = new Label( "Welcome to the Catan!");
		layout.setBottom( this.statusBar);
		//this.root.getChildren().add( this.statusBar);

		GraphicsContext gc = canvas.getGraphicsContext2D();


		this.canvas.setOnMouseClicked( new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {

				int x = processX( mouseEvent.getX() );
				int y = processY( mouseEvent.getY() );

				System.out.print( "X is: " + mouseEvent.getX() + " | Y is: " + mouseEvent.getY());
				System.out.println( " X' is: " + x + " | Y' is: " + y );

				createDialog( primaryStage, controller.checkStructure( x, y ), x, y );
			}
		} );


		primaryStage.setTitle("Hello World");
		primaryStage.setScene(scene); // x, y
		primaryStage.setResizable(false);
		drawGameBoard( gc);

		primaryStage.show();
	}


	/**
	 * Draws the game board on the canvas with its gc property.
	 * @param gc is the canvas property which performs the drawing operation on its belonging canvas.
	 */
	private void drawGameBoard(GraphicsContext gc) {

		// Initialize the controller
		this.controller = new GameController();
		controller.initializeGame();
		this.board = controller.getGameBoard(); // get the game board for display

		Image myImg;
		// For each Tile in the game board ( this method could be optimized with increased i, j index increment)
		for ( int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				/*
				 *      0-index = saman
				 *      1-index = odun
				 *      2-index = mermer
				 *      3-index = kaya
				 *      4-index = koyun
				 *      çöl -> will be assigned automatically when dice is 7
				 */
				if ( board[ i][ j].isItStartPoint() )
				{
					int diceNum = board[i][j].getDiceNumber();
					int resource = board[ i][ j].getResource();

					String resourceStr;
					if ( resource  == 0 )
					{
						resourceStr = "grain";
					}
					else if ( resource == 1)
					{
						resourceStr = "lumber";
					}
					else if ( resource == 2 )
					{
						resourceStr = "ore";
					}
					else if ( resource == 3)
					{
						resourceStr = "stone";
					}
					else if ( resource == 4)
					{
						resourceStr = "wool";
					}
					else
					{
						resourceStr = "";
						System.out.println( "Oh no! Exception at drawGameBoard( gc) or the resource is desert!");
					}

					// Create the img path for image drawing
					String imgPath;
					if ( diceNum != 7 )
					{
						imgPath = "/images/" + resourceStr + diceNum + "-removebg-preview.png";
					}
					else
					{
						imgPath = "/images/desert.png";
					}
					myImg = new Image( imgPath);
					gc.drawImage( myImg, (j - 2) * 30, i * 30); // draw the image at the stated
				}
			}
		}

	}

	/**
	 * Processes the mouse click event for the x coordinate of game board
	 * @param x is the x coordinate given by the mouse event
	 * @return an integer index, the processed result corresponding the x index for the game board
	 */
	private int processX( double x)
	{
		return ((int) x / 30);
	}

	/**
	 * Processes the mouse click event for the y coordinate of game board
	 * @param y is the y coordinate given by the mouse event
	 * @return an integer index, the processed result corresponding the y index for the game board
	 */
	private int processY( double y)
	{
		return (int) y / 30;
	}

	/**
	 * The function to handle user intereaction for the game board.
	 * @param stage is usuless right now, why did that put it i dont know?
	 * @param resultCode is the code returned from the game controller
	 * @param x is the x corrdinate in the game board
	 * @param y is the y coordinate in the game board
	 */
	private void createDialog( Stage stage, int resultCode, int x, int y )
	{

		// If the controller returns minus integer, there is an error!
		if ( resultCode < 0 )
		{
			// handle error
			informError( resultCode);
		}
		else
		{
			// The clicked tile is game tile, inform the user about the event regarding the resultCode gotten from controller
			Alert alert = new Alert( Alert.AlertType.CONFIRMATION);
			alert.initStyle( StageStyle.UTILITY);

			// User icon could be used
			ImageView icon = new ImageView( "/images/hakan.jpg");
			icon.setFitHeight(48);
			icon.setFitWidth(48);
			alert.getDialogPane().setGraphic( icon);

			// Handle the intended user action could have a dedicated function for it!
			informResult( alert, resultCode, x, y);
		}
	}

	/**
	 * Inform error function updates the status bar to display user error, this function could be evolved into pop up
	 * and a sound may be played regarding the error.
	 * @param resultCode is the error code given by the game controller
	 */
	private void informError( int resultCode )
	{
		if ( resultCode == -1 )
		{
			this.statusBar.setText( "There is no connection for road to build for player:" +
									this.controller.getCurrentPlayer().getName() );
		}
		else if ( resultCode == -2 )
		{
			this.statusBar.setText( "There is no connection for city to build for player:" +
									this.controller.getCurrentPlayer().getName() );
		}
		else if ( resultCode == -3 )
		{
			this.statusBar.setText( "There is a building near for player:" +
									this.controller.getCurrentPlayer().getName() );
		}
		else if ( resultCode == -4 )
		{
			this.statusBar.setText( "This spot is occupied by other players for player: " +
									this.controller.getCurrentPlayer().getName() );
		}
		else
		{
			this.statusBar.setText( "Inform Error function could not detect the error??");
		}
	}

	/**
	 * Creates the dialog corresponding to the user action on the game board.
	 * @param alert is the dialog to display the user event
	 * @param resultCode is the positive result gotten from the controller
	 * @param x is the x coordinate of to perform action on the game board
	 * @param y is the y coordinate of to perform action on the game board
	 */
	private void informResult( Alert alert, int resultCode, int x, int y )
	{
		if ( resultCode == 0 )
		{
			buildRoad( alert, x, y);
		}
		else if ( resultCode == 1)
		{
			buildSettlement( alert, x ,y);
		}
		else if ( resultCode == 2)
		{
			buildCity( alert, x, y);
		}
	}

	/**
	 * Prompts to ask if the user really wants to build a settlement.
	 * @param alert is the dialog prompting confirmation of building the settlement
	 * @param x is the x coordinate in the game board
	 * @param y is the y coordinate in the game board
	 */
	private void buildSettlement( Alert alert, int x, int y)
	{
		alert.setHeaderText("Look, a Confirmation Dialog");
		alert.setContentText("Build settlement?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			System.out.println( "Building Setlement...");
			this.controller.buildSettlement( x, y);
		}
	}

	/**
	 * A custom dialog to confirmation of road building
	 * @param alert is the dialog prompting confirmation of building the road
	 * @param x is the x coordinate in the game board
	 * @param y is the y coordinate in the game board
	 */
	private void buildRoad( Alert alert, int x, int y)
	{
		alert.setHeaderText("Look, a Confirmation Dialog");
		alert.setContentText("Build road?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			System.out.println( "Building road...");
			this.controller.buildRoad( false, x, y);

		}
	}

	/**
	 * A custom dialog to confirmation of city upgrading
	 * @param alert is the dialog prompting confirmation of upgrading to the city
	 * @param x is the x coordinate in the game board
	 * @param y is the y coordinate in the game board
	 */
	private void buildCity( Alert alert, int x, int y)
	{
		alert.setHeaderText("");
		alert.setContentText("Upgrade city?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			System.out.println( "Upgrading to city");
			this.controller.buildCity( x, y);
		}
	}

	// Lol so small code
	public static void main(String[] args) {
		launch(args);
	}
}

// listen to the resize event

		/*
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				drawSimpleLine(line, scene);
			}
		});
		scene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
				drawSimpleLine(line, scene);
			}
		});
		*/