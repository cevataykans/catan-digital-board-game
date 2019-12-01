import animatefx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class PlayerInfoController {
    // Properties
    SingleGameController controller;
    Scene scene;

    ArrayList<Circle> otherPlayers;
    ArrayList<AnchorPane> playerAnchors;
    ArrayList<Label> playerNames;
    ArrayList<Label> currPlayerResources;
    ArrayList<ProgressIndicator> playerScores;
    ArrayList<ImageView> longestRoads;
    ArrayList<ImageView> largestArmies;
    boolean other1Shown = false;
    boolean other2Shown = false;
    boolean other3Shown = false;

    // Constructor
    public PlayerInfoController(Scene scene, SingleGameController controller)
    {
        this.scene = scene;
        this.controller = controller;
        initialize();
    }

    // Methods
    private void initialize() {
        Circle other1 = (Circle) scene.lookup("#other1Info");
        Circle other2 = (Circle) scene.lookup("#other2Info");
        Circle other3 = (Circle) scene.lookup("#other3Info");

        other1.setOnMouseEntered(event ->
        {
            if ( !other1Shown)
            {
                other1Shown = true;
                showPlayer(1);
            }
        });
        other1.setOnMouseExited(event ->
        {
            if ( other1Shown)
            {
                hidePlayer(1);
            }
        });

        other2.setOnMouseEntered(event ->
        {
            if ( !other2Shown)
            {
                other2Shown = true;
                showPlayer(2);
            }
        });
        other2.setOnMouseExited(event ->
        {
            hidePlayer(2);
        });

        other3.setOnMouseEntered(event ->
        {
            if ( !other3Shown)
            {
                other3Shown = true;
                showPlayer(3);
            }
        });
        other3.setOnMouseExited(event ->
        {
            hidePlayer(3);
        });

        otherPlayers = new ArrayList<>();
        otherPlayers.add(other1);
        otherPlayers.add(other2);
        otherPlayers.add(other3);

        AnchorPane currentPlayerBox = (AnchorPane) scene.lookup("#currentPlayerInf");
        AnchorPane otherPlayer1Box = (AnchorPane) scene.lookup("#other1Box");
        AnchorPane otherPlayer2Box = (AnchorPane) scene.lookup("#other2Box");
        AnchorPane otherPlayer3Box = (AnchorPane) scene.lookup("#other3Box");
        playerAnchors = new ArrayList<>();
        playerAnchors.add(currentPlayerBox);
        playerAnchors.add(otherPlayer1Box);
        playerAnchors.add(otherPlayer2Box);
        playerAnchors.add(otherPlayer3Box);

        Label currentPlayer = (Label) scene.lookup("#currentName");
        currentPlayer.setAlignment(Pos.CENTER);
        Label otherPlayer1 = (Label) scene.lookup("#other1Name");
        Label otherPlayer2 = (Label) scene.lookup("#other2Name");
        Label otherPlayer3 = (Label) scene.lookup("#other3Name");
        playerNames = new ArrayList<>();
        playerNames.add(currentPlayer);
        playerNames.add(otherPlayer1);
        playerNames.add(otherPlayer2);
        playerNames.add(otherPlayer3);

        ProgressIndicator currentPlayerIndicator = (ProgressIndicator) scene.lookup("#currentScore");
        ProgressIndicator otherPlayer1Progress = (ProgressIndicator) scene.lookup("#other1Score");
        ProgressIndicator otherPlayer2Progress = (ProgressIndicator) scene.lookup("#other2Score");
        ProgressIndicator otherPlayer3Progress = (ProgressIndicator) scene.lookup("#other3Score");
        playerScores = new ArrayList<>();
        playerScores.add(currentPlayerIndicator);
        playerScores.add(otherPlayer1Progress);
        playerScores.add(otherPlayer2Progress);
        playerScores.add(otherPlayer3Progress);

        Label lumberCount = (Label) scene.lookup("#lumberCount");
        Label woolCount = (Label) scene.lookup("#woolCount");
        Label grainCount = (Label) scene.lookup("#grainCount");
        Label brickCount = (Label) scene.lookup("#brickCount");
        Label oreCount = (Label) scene.lookup("#oreCount");
        currPlayerResources = new ArrayList<>();
        currPlayerResources.add(lumberCount);
        currPlayerResources.add(woolCount);
        currPlayerResources.add(grainCount);
        currPlayerResources.add(brickCount);
        currPlayerResources.add(oreCount);

        ImageView currentLR = (ImageView) scene.lookup("#currentLR");
        ImageView other1LR = (ImageView) scene.lookup("#other1LR");
        ImageView other2LR = (ImageView) scene.lookup("#other2LR");
        ImageView other3LR = (ImageView) scene.lookup("#other3LR");
        longestRoads = new ArrayList<>();
        longestRoads.add(currentLR);
        longestRoads.add(other1LR);
        longestRoads.add(other2LR);
        longestRoads.add(other3LR);

        ImageView currentLA = (ImageView) scene.lookup("#currentLA");
        ImageView other1LA = (ImageView) scene.lookup("#other1LA");
        ImageView other2LA = (ImageView) scene.lookup("#other2LA");
        ImageView other3LA = (ImageView) scene.lookup("#other3LA");
        largestArmies = new ArrayList<>();
        largestArmies.add(currentLA);
        largestArmies.add(other1LA);
        largestArmies.add(other2LA);
        largestArmies.add(other3LA);

        setupOtherPlayers();
        setupCurrentPlayer();
        setupLongestRoad();
        setupLargestArmy();
    }

    public void setupOtherPlayers() {
        FadeOut animation1 = new FadeOut(otherPlayers.get(0));
        FadeOut animation2 = new FadeOut(otherPlayers.get(1));
        FadeOut animation3 = new FadeOut(otherPlayers.get(2));
        animation1.setSpeed(3);
        animation2.setSpeed(3);
        animation3.setSpeed(3);
        animation1.play();
        animation2.play();
        animation3.play();

        animation1.setOnFinished(event ->
        {
            FadeIn animation1In = new FadeIn(otherPlayers.get(0));
            otherPlayers.get(0).getStyleClass().clear();
            otherPlayers.get(0).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4)
                    .getColor().toString().substring(1) + "Circle");
            playerAnchors.get(1).getStyleClass().clear();
            playerAnchors.get(1).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            playerNames.get(1).setText(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4).getName());
            playerScores.get(1).setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 1) % 4).getScore() * 1.0 / 10);
            animation1In.setSpeed(3);
            animation1In.play();
        });
        animation2.setOnFinished(event ->
        {
            FadeIn animation2In = new FadeIn(otherPlayers.get(1));
            otherPlayers.get(1).getStyleClass().clear();
            otherPlayers.get(1).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4)
                    .getColor().toString().substring(1) + "Circle");
            playerAnchors.get(2).getStyleClass().clear();
            playerAnchors.get(2).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            playerNames.get(2).setText(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4).getName());
            playerScores.get(2).setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 2) % 4).getScore() * 1.0 / 10);
            animation2In.setSpeed(3);
            animation2In.play();
        });
        animation3.setOnFinished(event ->
        {
            FadeIn animation3In = new FadeIn(otherPlayers.get(2));
            otherPlayers.get(2).getStyleClass().clear();
            otherPlayers.get(2).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4)
                    .getColor().toString().substring(1) + "Circle");
            playerAnchors.get(3).getStyleClass().clear();
            playerAnchors.get(3).getStyleClass().add(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4)
                    .getColor().toString().substring(1) + "PlayerBox");
            playerNames.get(3).setText(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4).getName());
            playerScores.get(3).setProgress(controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + 3) % 4).getScore() * 1.0/ 10);
            animation3In.setSpeed(3);
            animation3In.play();
        });
    }

    public void setupCurrentPlayer() {
        FadeOut infoOut = new FadeOut(playerAnchors.get(0));
        infoOut.setSpeed(3);
        infoOut.setOnFinished(event ->
        {
            FadeIn infoIn = new FadeIn(playerAnchors.get(0));
            playerAnchors.get(0).getStyleClass().clear();
            playerAnchors.get(0).getStyleClass().add(controller.getGame().getCurrentPlayer().getColor().toString().substring(1) + "PlayerBox");
            playerNames.get(0).setText(controller.getGame().getCurrentPlayer().getName());
            infoIn.setSpeed(3);

            playerScores.get(0).setProgress(controller.getGame().getCurrentPlayer().getScore() * 1.0 / 10);
            int playercurrPlayerResources[] = controller.getGame().getCurrentPlayer().getResources();

            for ( int i = 0; i < currPlayerResources.size(); i++)
            {
                currPlayerResources.get(i).setText("" + playercurrPlayerResources[i]);
            }

            infoIn.play();
        });
        infoOut.play();
    }

    private void showPlayer(int otherIndex)
    {
        playerAnchors.get(otherIndex).setVisible(true);
        ZoomIn showAnim = new ZoomIn(playerAnchors.get(otherIndex));
        showAnim.play();
    }

    private void hidePlayer(int otherIndex)
    {
        ZoomOut hideAnim = new ZoomOut(playerAnchors.get(otherIndex));
        hideAnim.setOnFinished(event ->
        {
            playerAnchors.get(otherIndex).setVisible(false);
            switch (otherIndex)
            {
                case 1: other1Shown = false; break;
                case 2: other2Shown = false; break;
                case 3: other3Shown = false; break;
            }
        });
        hideAnim.play();
    }

    public void setupLongestRoad() {
        for ( int i = 0; i < 4; i++)
        {
            if ( controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + i) % 4) == controller.getGame().getLongestRoadPlayer())
            {
                longestRoads.get(i).setVisible(true);
                FadeIn laIn = new FadeIn(longestRoads.get(i));
                laIn.setSpeed(3);
                laIn.play();
            }
            else
            {
                FadeOut laOut = new FadeOut(longestRoads.get(i));
                laOut.setSpeed(3);
                int finalI = i;
                laOut.setOnFinished(event ->
                {
                    longestRoads.get(finalI).setVisible(false);
                });
                laOut.play();
            }
        }
        setupOtherPlayers();
    }

    public void setupLargestArmy() {
        for ( int i = 0; i < 4; i++)
        {
            if ( controller.getGame().getPlayer((controller.getGame().getCurrentPlayerIndex() + i) % 4) == controller.getGame().getLargestArmyPlayer())
            {
                largestArmies.get(i).setVisible(true);
                FadeIn laIn = new FadeIn(largestArmies.get(i));
                laIn.setSpeed(3);
                laIn.play();
            }
            else
            {
                FadeOut laOut = new FadeOut(largestArmies.get(i));
                laOut.setSpeed(3);
                int finalI = i;
                laOut.setOnFinished(event ->
                {
                    largestArmies.get(finalI).setVisible(false);
                });
                laOut.play();
            }
        }
        setupOtherPlayers();
    }
}
