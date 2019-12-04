package SceneManagement;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Get the Catan font from the fonts file and initialize it for the game.
            final Font font1 = Font.loadFont(new FileInputStream(new File("").getAbsolutePath()
                    .concat("/src/fonts/MinionPro-Bold.otf")), 40);
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e);
        }
        GameEngine.getInstance().initializePrimaryStage(primaryStage);
    }
}
