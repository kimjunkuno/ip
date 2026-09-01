package serina.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import serina.Serina;
import serina.storage.Storage;

/**
 * Tests Serina's JavaFX controls and minimum-size layout.
 */
public class MainWindowTest {
    private static final int SNAPSHOT_HEIGHT = 520;
    private static final int SNAPSHOT_WIDTH = 420;

    @TempDir
    private Path temporaryDirectory;

    @BeforeAll
    public static void startJavaFx() {
        Platform.startup(() -> {
        });
    }

    @AfterAll
    public static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    public void mainWindow_minimumSizeAndUserActions_controlsRemainUsableAndSeparate() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Region root = loader.load();
            MainWindow controller = loader.getController();
            controller.setSerina(new Serina(new Storage(temporaryDirectory.resolve("serina.txt"))));

            Scene scene = new Scene(root, SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            root.resize(SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
            root.applyCss();
            root.layout();

            TextField userInput = (TextField) root.lookup("#userInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            VBox dialogContainer = (VBox) root.lookup("#dialogContainer");

            assertControlsDoNotOverlap(userInput, sendButton, scene);

            userInput.setText("list");
            sendButton.fire();
            assertEquals(3, dialogContainer.getChildren().size());
            assertEquals("", userInput.getText());

            userInput.setText("nonsense");
            Event.fireEvent(userInput, new ActionEvent());
            assertEquals(5, dialogContainer.getChildren().size());

            userInput.setText("   ");
            sendButton.fire();
            assertEquals(5, dialogContainer.getChildren().size());

            userInput.setText("bye");
            sendButton.fire();
            assertEquals(7, dialogContainer.getChildren().size());
            assertTrue(userInput.isDisabled());
            assertTrue(sendButton.isDisabled());

            root.applyCss();
            root.layout();
            saveSnapshot(root);
            return null;
        });
    }

    /**
     * Checks that the text field and send button remain separate and inside the scene.
     */
    private static void assertControlsDoNotOverlap(TextField userInput, Button sendButton, Scene scene) {
        Bounds inputBounds = userInput.localToScene(userInput.getBoundsInLocal());
        Bounds buttonBounds = sendButton.localToScene(sendButton.getBoundsInLocal());

        assertTrue(inputBounds.getMaxX() <= buttonBounds.getMinX());
        assertTrue(buttonBounds.getMaxX() <= scene.getWidth());
        assertFalse(inputBounds.intersects(buttonBounds));
    }

    /**
     * Saves the rendered minimum-size window for visual inspection.
     */
    private static void saveSnapshot(Region root) throws IOException {
        WritableImage image = new WritableImage(SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);
        root.snapshot(null, image);
        PixelReader pixelReader = image.getPixelReader();
        BufferedImage bufferedImage = new BufferedImage(
                SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < SNAPSHOT_HEIGHT; y++) {
            for (int x = 0; x < SNAPSHOT_WIDTH; x++) {
                bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
            }
        }

        Path reportDirectory = Path.of("build", "reports", "gui");
        Files.createDirectories(reportDirectory);
        ImageIO.write(bufferedImage, "png", reportDirectory.resolve("serina-main-window.png").toFile());
    }

    /**
     * Runs an action on the JavaFX Application Thread and returns its result.
     */
    private static <T> T runOnJavaFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
