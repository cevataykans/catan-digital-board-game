package SceneManagement.GameManagement;

import SceneManagement.MultiGameController;
import SceneManagement.SceneController;
import ServerCommunication.ServerHandler;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * This controller manages all the chat interactions. It has association with the MultiGameController.
 * @author Talha Şen
 * @version 29.11.2019
 */

public class ChatController {
    // Properties
    MultiGameController controller;
    Scene scene;
    AnchorPane chatBox;
    ImageView chatHover;
    TextField chatInput;
    TextArea chatMessages;

    private double chatBoxHideLocation;
    private double chatBoxShownLocation;

    // Constructor
    public ChatController(Scene scene, SceneController controller)
    {
        this.scene = scene;
        this.controller = (MultiGameController) controller;
        initialize();
    }

    // Methods
    /**
     * This method initializes the UI components (taken from the game's fxml) related to the status and it
     * adds the logic as a listener to the components.
     */
    private void initialize()
    {
        chatBox = (AnchorPane) scene.lookup("#chatBox");
        chatHover = (ImageView) scene.lookup("#chatHover");
        chatInput = (TextField) scene.lookup("#chatInput");
        chatMessages = (TextArea) scene.lookup("#chatMessages");

        chatBoxHideLocation = chatBox.getTranslateY();
        chatBoxShownLocation = chatBoxHideLocation + 175;
        setupChat();
    }

    /**
     * This method adds the logic and listeners to all of the chat components.
     */
    private void setupChat()
    {
        // Adds the listener to the chat hover image so that when hovered, chat box is shown.
        chatHover.setOnMouseEntered(event ->
        {
            // If current player hovers of the "Chat" part of the box in UI, show the chat container to user.
            if ( chatBox.getTranslateY() == chatBoxHideLocation) {
                TranslateTransition hoverTT = new TranslateTransition(Duration.millis(500), chatHover);
                TranslateTransition boxTT = new TranslateTransition(Duration.millis(500), chatBox);
                hoverTT.setByY(175);
                boxTT.setByY(175);
                hoverTT.play();
                boxTT.play();
                boxTT.setOnFinished(event1 ->
                {
                    chatBox.setTranslateY(175);
                });
            }
        });
        // When chat box is exited, it is hidden.
        chatBox.setOnMouseExited(event2 ->
        {
            // If the container is already shown and player's mouse exits the container, hide the container.
            if ( chatBox.getTranslateY() == chatBoxShownLocation) {
                TranslateTransition hoverTT = new TranslateTransition(Duration.millis(500), chatHover);
                TranslateTransition boxTT = new TranslateTransition(Duration.millis(500), chatBox);
                hoverTT.setByY(-175);
                boxTT.setByY(-175);
                hoverTT.play();
                boxTT.play();
                boxTT.setOnFinished(event1 ->
                {
                    chatBox.setTranslateY(0);
                });
            }
        });
        // When chat is sent via "Enter", send it to other players.
        chatInput.setOnKeyPressed(event3 ->
        {
            switch (event3.getCode()) {
                case ENTER:
                    sendChatMessage();
                    chatInput.clear();
                    break;
            }
        });
        // Whenever a new chat messages gets a new message, scroll it to the bottom.
        chatMessages.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                chatMessages.setScrollTop(Double.MAX_VALUE); // scrolls to the bottom.
            }
        });
    }

    /**
     * This method sends the message of the player they inputted to chat to other players.
     */
    public void sendChatMessage()
    {
        // Get the message from the input.
        String message = chatInput.getText();

        if ( message.length() > 0) {
            // Add it to the local user.
            chatMessages.setText(chatMessages.getText() + controller.getLocalPlayer().getName() + ": " + message + "\n");
            chatMessages.appendText(""); // This is used to trigger the textProperty listener.

            // Send it to the other players.
            ServerHandler.getInstance().sendMessage(controller.getLocalPlayer().getName(), message);
        }
    }

    /**
     * This method gets the chat message of another player from server and adds it to the chat box.
     * @param message is the chat messaage another player sent.
     */
    public void getChatMessage(String userId, String message)
    {
        chatMessages.setText(chatMessages.getText() + userId + ": " + message + "\n");
        chatMessages.appendText(""); // This is used to trigger the textProperty listener.
    }
}
