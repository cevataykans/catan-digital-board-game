package SceneManagement;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller is the parent class of all controllers. It has the shared scene and root properties and the required
 * abstract initialize method.
 * @author Talha Şen
 * @version 29.11.2019
 */

public abstract class SceneController {
    // Properties
    Scene scene;
    Parent root;

    // Constructor
    public SceneController()
    {
        //Initializations are done in the respective controller classes.
    }

    // Methods

    /**
     * This method will initialize the controller's component from their respective scenes and it will add their logic.
     * @param stage is the primary stage that will take the controller's scene.
     * @throws IOException is the file-not-found exception.
     */
    public abstract  void initialize(Stage stage) throws IOException;
}
