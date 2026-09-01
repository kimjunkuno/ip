package serina.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import serina.Serina;

/**
 * Displays Serina's graphical user interface.
 */
public class Main extends Application {
    private static final double INITIAL_HEIGHT = 720;
    private static final double INITIAL_WIDTH = 560;
    private static final double MINIMUM_HEIGHT = 520;
    private static final double MINIMUM_WIDTH = 420;

    private final Serina serina = new Serina();

    /**
     * Creates Serina's JavaFX application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowResource = Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "MainWindow.fxml is missing");
        URL stylesheetResource = Objects.requireNonNull(
                Main.class.getResource("/css/main.css"),
                "main.css is missing");

        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        scene.getStylesheets().add(stylesheetResource.toExternalForm());

        MainWindow controller = fxmlLoader.getController();
        controller.setSerina(serina);

        stage.setTitle("Serina");
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setScene(scene);
        stage.show();
    }
}
