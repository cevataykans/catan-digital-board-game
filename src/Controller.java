import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public abstract class Controller {
    // Properties
    Scene scene;
    Parent root;

    // Constructor
    public Controller()
    {
    }

    // Methods
    public abstract  void initialize(Stage stage) throws IOException;
}
