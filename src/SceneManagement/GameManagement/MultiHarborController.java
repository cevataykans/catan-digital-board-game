package SceneManagement.GameManagement;

import GameBoard.Harbor;
import GameFlow.FlowManager;
import GameFlow.ResourceManager;
import GameFlow.Response;
import Player.Player;
import SceneManagement.MultiGameController;
import SceneManagement.SingleGameController;
import ServerCommunication.ServerHandler;
import ServerCommunication.ServerInformation;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class MultiHarborController
{
    // Game Properties
    private Button tradeGameButton;
    private Scene gameScene;
    private MultiGameController controller;

    // Popup properties special for this controller
    private Parent harborRoot;

    private Button trade4;
    private Button trade3;
    private Button tradeLumber2;
    private Button tradeWool2;
    private Button tradeGrain2;
    private Button tradeBrick2;
    private Button tradeOre2;
    private Button close;

    private ArrayList<ImageView> imageGive = new ArrayList<>();
    private ArrayList<ImageView> imageTake = new ArrayList<>();

    private ChoiceBox toGive;
    private ChoiceBox toTake;

    // Constructor
    public MultiHarborController( Scene scene, MultiGameController controller)
    {
        this.gameScene = scene;
        this.controller = controller;
        initialize();
    }

    /**
     * Initializes the harbor UI elements and gives them functionality.
     */
    private void initialize()
    {
        // Get UI elements
        this.tradeGameButton = (Button) this.gameScene.lookup("#tradeGameButton");

        try
        {
            this.harborRoot = FXMLLoader.load( getClass().getResource("/UI/HarborPopup.fxml"));
            this.harborRoot.getStylesheets().add(getClass().getResource("/UI/HarborPopup.css").toExternalForm());

            Scene harborScene = new Scene( this.harborRoot);

            this.trade4 = (Button) harborScene.lookup("#trade4");
            this.trade3 = (Button) harborScene.lookup("#trade3");
            this.tradeLumber2 = (Button) harborScene.lookup("#tradeLumber2");
            this.tradeWool2 = (Button) harborScene.lookup("#tradeWool2");
            this.tradeGrain2 = (Button) harborScene.lookup("#tradeGrain2");
            this.tradeBrick2 = (Button) harborScene.lookup("#tradeBrick2");
            this.tradeOre2 = (Button) harborScene.lookup("#tradeOre2");
            this.close = (Button) harborScene.lookup("#cancel");

            // Get all the left hand side images and collect them
            for ( int i = 0; i < 2; i++)
            {
                this.imageGive.add( (ImageView) harborScene.lookup("#imgGive" + i) );
            }

            // Get all the right hand side images and collect them
            for ( int i = 0; i < 7; i++)
            {
                this.imageTake.add( (ImageView) harborScene.lookup("#imgTake" + i) );
            }

            this.toGive = (ChoiceBox) harborScene.lookup("#offeringChoice");
            this.toTake = (ChoiceBox) harborScene.lookup("#gainingChoice");
        }
        catch ( IOException e)
        {
            e.printStackTrace();
        }

        // Setup all the functionalities
        this.setupHarborTrade();
    }

    /**
     *
     */
    private void setupHarborTrade()
    {
        FlowManager flowManager = new FlowManager();
            // Setup the trade button so that harbor trade pop up will open. Player harbors are also checked here
            this.tradeGameButton.setOnMouseClicked(mouseEvent ->
            {
                if ( controller.getLocalPlayer() == flowManager.getCurrentPlayer() && flowManager.checkMust() == Response.MUST_FREE_TURN) {
                    FlowManager flow = new FlowManager();
                    Player curPlayer = flow.getCurrentPlayer();

                    // Check player harbors, if player has that one, make it functional, else make it not clickable

                    if (curPlayer.hasHarbor(Harbor.HarborType.THREE_TO_ONE)) {
                        this.trade3.setVisible(true);
                    } else {
                        this.trade3.setVisible(false);
                    }

                    if (curPlayer.hasHarbor(Harbor.HarborType.TWO_TO_ONE_LUMBER)) {
                        this.tradeLumber2.setVisible(true);
                    } else {
                        this.tradeLumber2.setVisible(false);
                    }

                    if (curPlayer.hasHarbor(Harbor.HarborType.TWO_TO_ONE_WOOL)) {
                        this.tradeWool2.setVisible(true);
                    } else {
                        this.tradeWool2.setVisible(false);
                    }

                    if (curPlayer.hasHarbor(Harbor.HarborType.TWO_TO_ONE_GRAIN)) {
                        this.tradeGrain2.setVisible(true);
                    } else {
                        this.tradeGrain2.setVisible(false);
                    }

                    if (curPlayer.hasHarbor(Harbor.HarborType.TWO_TO_ONE_BRICK)) {
                        this.tradeBrick2.setVisible(true);
                    } else {
                        this.tradeBrick2.setVisible(false);
                    }

                    if (curPlayer.hasHarbor(Harbor.HarborType.TWO_TO_ONE_ORE)) {
                        this.tradeOre2.setVisible(true);
                    } else {
                        this.tradeOre2.setVisible(false);
                    }

                    // Show Trade
                    showTradePopup(tradeGameButton);
                }
                else
                {
                    controller.getStatusController().informStatus(flowManager.checkMust());
                }
            });

            // Setup the trade button functionalities
            setupTradeButtons();

            // Setup choice box functionalities so images change when user changes resource
            setupChoiceBox();
    }

    /**
     * Shows a beautiful pop up for material trading.
     */
    private void showTradePopup( Node owner)
    {
        PopOver harborPopup = new PopOver( harborRoot);
        harborPopup.setTitle( "Trade with Game");

        harborPopup.setArrowLocation( PopOver.ArrowLocation.LEFT_CENTER );
        harborPopup.setArrowSize( 20);
        harborPopup.setAnchorLocation( PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        harborPopup.show( owner);

        // Close the pop up by clicking button
        this.close.setOnMouseClicked( mouseEvent ->
        {
            if ( harborPopup.isShowing() )
            {
                harborPopup.hide( Duration.seconds( 0.3) );
            }
        });
    }

    /**
     * Set up the functionalities of trade buttons in HarborPopup.fxml for player to trade resources.
     */
    private void setupTradeButtons()
    {
        ResourceManager resMan = new ResourceManager();
        FlowManager flow = new FlowManager();

		/*
			Below here for each button, depending on the type, selected resource and wanted resource indexes are found
			Then, player is checked if they have enough resources.
			Lastly, if they have enough, resource manager performs trading. Else, error is displayed on UI.
		 */

        this.trade4.setOnMouseClicked( mouseEvent ->
        {
            int giveResIndex = this.getSelectedResourceIndex( ( String) this.toGive.getValue() );
            int takeResIndex = this.getSelectedResourceIndex( ( String) this.toTake.getValue() );

            int[] resToCheck = {0, 0, 0, 0, 0};
            resToCheck[ giveResIndex] = 4;
            if ( resMan.hasEnoughResources( flow.getCurrentPlayer(), resToCheck ) )
            {
                System.out.println( "Checking");
                resMan.tradeUsing4( giveResIndex, takeResIndex);
                this.controller.getInfoController().setupCurrentPlayer();
                ServerHandler.getInstance().harborTrade(6, giveResIndex, takeResIndex);
            }
            else
            {
                this.controller.getStatusController().informStatus( Response.ERROR_NOT_ENOGUH_TRADING_MATERIAL);
            }

        });

        this.trade3.setOnMouseClicked( mouseEvent ->
        {
            int giveResIndex = this.getSelectedResourceIndex( ( String) this.toGive.getValue() );
            int takeResIndex = this.getSelectedResourceIndex( ( String) this.toTake.getValue() );

            int[] resToCheck = {0, 0, 0, 0, 0};
            resToCheck[ giveResIndex] = 3;
            if ( resMan.hasEnoughResources( flow.getCurrentPlayer(), resToCheck ) )
            {
                resMan.tradeWithHarbor( Harbor.HarborType.THREE_TO_ONE, giveResIndex, takeResIndex);
                this.controller.getInfoController().setupCurrentPlayer();
                Harbor.HarborType type = Harbor.HarborType.THREE_TO_ONE;
                ServerHandler.getInstance().harborTrade(0, giveResIndex, takeResIndex);
            }
            else
            {
                this.controller.getStatusController().informStatus( Response.ERROR_NOT_ENOGUH_TRADING_MATERIAL);
            }
        });

        this.tradeLumber2.setOnMouseClicked( mouseEvent ->
        {
            int takeResIndex = this.getSelectedResourceIndex( ( String) this.toTake.getValue() );

            int[] resToCheck = {0, 0, 0, 0, 0};
            resToCheck[ ResourceManager.LUMBER] = 2;
            if ( resMan.hasEnoughResources( flow.getCurrentPlayer(), resToCheck ) )
            {
                resMan.tradeWithHarbor( Harbor.HarborType.TWO_TO_ONE_LUMBER, ResourceManager.LUMBER, takeResIndex);
                this.controller.getInfoController().setupCurrentPlayer();
                ServerHandler.getInstance().harborTrade(1, ResourceManager.LUMBER, takeResIndex);
            }
            else
            {
                this.controller.getStatusController().informStatus( Response.ERROR_NOT_ENOGUH_TRADING_MATERIAL);
            }
        });

        this.tradeWool2.setOnMouseClicked( mouseEvent ->
        {
            int takeResIndex = this.getSelectedResourceIndex( ( String) this.toTake.getValue() );

            int[] resToCheck = {0, 0, 0, 0, 0};
            resToCheck[ ResourceManager.WOOL] = 2;
            if ( resMan.hasEnoughResources( flow.getCurrentPlayer(), resToCheck ) )
            {
                resMan.tradeWithHarbor( Harbor.HarborType.TWO_TO_ONE_WOOL, ResourceManager.WOOL, takeResIndex);
                this.controller.getInfoController().setupCurrentPlayer();
                ServerHandler.getInstance().harborTrade(2, ResourceManager.WOOL, takeResIndex);
            }
            else
            {
                this.controller.getStatusController().informStatus( Response.ERROR_NOT_ENOGUH_TRADING_MATERIAL);
            }
        });

        this.tradeGrain2.setOnMouseClicked( mouseEvent ->
        {
            int takeResIndex = this.getSelectedResourceIndex( ( String) this.toTake.getValue() );

            int[] resToCheck = {0, 0, 0, 0, 0};
            resToCheck[ ResourceManager.GRAIN] = 2;
            if ( resMan.hasEnoughResources( flow.getCurrentPlayer(), resToCheck ) )
            {
                resMan.tradeWithHarbor( Harbor.HarborType.TWO_TO_ONE_GRAIN, ResourceManager.GRAIN, takeResIndex);
                this.controller.getInfoController().setupCurrentPlayer();
                ServerHandler.getInstance().harborTrade(3, ResourceManager.GRAIN, takeResIndex);
            }
            else
            {
                this.controller.getStatusController().informStatus( Response.ERROR_NOT_ENOGUH_TRADING_MATERIAL);
            }
        });

        this.tradeBrick2.setOnMouseClicked( mouseEvent ->
        {
            int takeResIndex = this.getSelectedResourceIndex( ( String) this.toTake.getValue() );

            int[] resToCheck = {0, 0, 0, 0, 0};
            resToCheck[ ResourceManager.BRICK] = 2;
            if ( resMan.hasEnoughResources( flow.getCurrentPlayer(), resToCheck ) )
            {
                resMan.tradeWithHarbor( Harbor.HarborType.TWO_TO_ONE_BRICK, ResourceManager.BRICK, takeResIndex);
                this.controller.getInfoController().setupCurrentPlayer();
                ServerHandler.getInstance().harborTrade(4, ResourceManager.BRICK, takeResIndex);
            }
            else
            {
                this.controller.getStatusController().informStatus( Response.ERROR_NOT_ENOGUH_TRADING_MATERIAL);
            }
        });

        this.tradeOre2.setOnMouseClicked( mouseEvent ->
        {
            int takeResIndex = this.getSelectedResourceIndex( ( String) this.toTake.getValue() );

            int[] resToCheck = {0, 0, 0, 0, 0};
            resToCheck[ ResourceManager.ORE] = 2;
            if ( resMan.hasEnoughResources( flow.getCurrentPlayer(), resToCheck ) )
            {
                resMan.tradeWithHarbor( Harbor.HarborType.TWO_TO_ONE_ORE, ResourceManager.ORE, takeResIndex);
                this.controller.getInfoController().setupCurrentPlayer();
                ServerHandler.getInstance().harborTrade(5, ResourceManager.ORE, takeResIndex);
            }
            else
            {
                this.controller.getStatusController().informStatus( Response.ERROR_NOT_ENOGUH_TRADING_MATERIAL);
            }
        });
    }

    public void receiveHarborTradeMessage()
    {
        ResourceManager resMan = new ResourceManager();
        JSONObject obj = ServerInformation.getInstance().getInformation();
        try {
            int harborType = obj.getInt("harborType");
            int giveResIndex = obj.getInt("giveResIndex");
            int takeResIndex = obj.getInt("takeResIndex");
            System.out.println(" Type: " + harborType + " GiveIndex: " + giveResIndex + " TakeIndex: " + takeResIndex);

            switch (harborType)
            {
                case 0:
                    resMan.tradeWithHarbor(Harbor.HarborType.THREE_TO_ONE, giveResIndex, takeResIndex);
                    break;
                case 1:
                    resMan.tradeWithHarbor(Harbor.HarborType.TWO_TO_ONE_LUMBER, ResourceManager.LUMBER, takeResIndex);
                    break;
                case 2:
                    resMan.tradeWithHarbor(Harbor.HarborType.TWO_TO_ONE_WOOL, ResourceManager.WOOL, takeResIndex);
                    break;
                case 3:
                    resMan.tradeWithHarbor(Harbor.HarborType.TWO_TO_ONE_GRAIN, ResourceManager.GRAIN, takeResIndex);
                    break;
                case 4:
                    resMan.tradeWithHarbor(Harbor.HarborType.TWO_TO_ONE_BRICK, ResourceManager.BRICK, takeResIndex);
                    break;
                case 5:
                    resMan.tradeWithHarbor(Harbor.HarborType.TWO_TO_ONE_ORE, ResourceManager.ORE, takeResIndex);
                    break;
                case 6:
                    resMan.tradeUsing4(giveResIndex, takeResIndex);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Setups choice boxes so that when player changes the resource type, images change too!
     */
    private void setupChoiceBox()
    {
		/*
			Below, this is done:

			First, depending on change in the regarding choice box, get the value of selected resource
			Second, get the url of the image for selected resource.
			Last, set all the images to this newly selected resource.
		 */

        this.toGive.setOnAction( actionEvent ->
        {
            String newImg = ( String) toGive.getValue();
            newImg = newImg.toLowerCase();
            newImg = "images/" + newImg + "Icon.jpg";

            for ( ImageView img : imageGive)
            {
                img.setImage( new Image( newImg) );
            }
        });

        this.toTake.setOnAction( actionEvent ->
        {
            String newImg = ( String) toTake.getValue();
            newImg = newImg.toLowerCase();
            newImg = "images/" + newImg + "Icon.jpg";

            for ( ImageView img : imageTake)
            {
                img.setImage( new Image( newImg) );
            }
        });
    }

    /**
     * Returns the index of the selected resource depending on its name
     * @param resourceName is the String name of the resource: Lumber, Wool, Grain, Brick or Ore.
     * @return the index of the resource in the resource array.
     */
    private int getSelectedResourceIndex( String resourceName)
    {
        switch (resourceName) {
            case "Lumber":
                return ResourceManager.LUMBER;
            case "Wool":
                return ResourceManager.WOOL;
            case "Grain":
                return ResourceManager.GRAIN;
            case "Brick":
                return ResourceManager.BRICK;
            case "Ore":
                return ResourceManager.ORE;
            default:
                return -1;
        }
    }
}
