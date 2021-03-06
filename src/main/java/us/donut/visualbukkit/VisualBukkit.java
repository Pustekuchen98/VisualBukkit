package us.donut.visualbukkit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang.exception.ExceptionUtils;
import us.donut.visualbukkit.blocks.BlockRegistry;
import us.donut.visualbukkit.blocks.UndoManager;
import us.donut.visualbukkit.editor.ProjectManager;
import us.donut.visualbukkit.editor.SelectorPane;
import us.donut.visualbukkit.plugin.PluginBuilder;

import java.io.IOException;
import java.io.InputStream;

public class VisualBukkit extends Application {

    private static VisualBukkit instance;
    private BorderPane rootPane = new BorderPane();
    private SplitPane splitPane = new SplitPane();
    private Scene scene = new Scene(rootPane, 500, 500);
    private Stage primaryStage;
    private SelectorPane selectorPane;
    private int fontSize;

    public VisualBukkit() {
        if (instance != null) {
            throw new IllegalStateException();
        }
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) {
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> Platform.runLater(() -> displayException("An exception occurred", e)));
        this.primaryStage = primaryStage;
        Platform.runLater(this::load);
    }

    private void load() {
        primaryStage.setTitle("Visual Bukkit " + VisualBukkitLauncher.VERSION);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        rootPane.getStylesheets().add("/style.css");

        try (InputStream inputStream = VisualBukkit.class.getResourceAsStream("/icon.png")) {
            primaryStage.getIcons().add(new Image(inputStream));
        } catch (IOException e) {
            displayException("Failed to load icon", e);
        }

        BlockRegistry.registerAll();
        PluginBuilder.init();
        setupMenuBar();
        setupSaving();
        splitPane.getItems().addAll(selectorPane = new SelectorPane(), new Pane(), new Pane());
        rootPane.setCenter(splitPane);
        notifyPreloader(new Preloader.ProgressNotification(1));
        primaryStage.show();
        splitPane.setDividerPositions(0.2, 0.825);
        ProjectManager.loadProjects();
    }

    private void setupMenuBar() {
        MenuItem saveItem = new MenuItem("Save");
        MenuItem helpItem = new MenuItem("Help");
        MenuItem exitItem = new MenuItem("Exit");
        helpItem.setOnAction(e -> getHostServices().showDocument("https://github.com/OfficialDonut/VisualBukkit/wiki"));
        exitItem.setOnAction(e -> primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        saveItem.setOnAction(e -> {
            if (ProjectManager.getCurrentProject() != null) {
                try {
                    ProjectManager.getCurrentProject().save();
                    displayMessage("Successfully saved project");
                } catch (IOException ex) {
                    displayException("Failed to save project", ex);
                }
            }
        });
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(saveItem, helpItem, exitItem);

        MenuItem createItem = new MenuItem("Create");
        MenuItem openItem = new MenuItem("Open");
        MenuItem deleteItem = new MenuItem("Delete");
        createItem.setOnAction(e -> ProjectManager.promptCreateProject(true));
        openItem.setOnAction(e -> ProjectManager.promptOpenProject());
        deleteItem.setOnAction(e -> ProjectManager.promptDeleteProject());
        Menu projectMenu = new Menu("Project");
        projectMenu.getItems().addAll(createItem, openItem, deleteItem);

        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        undoItem.setOnAction(e -> UndoManager.undo());
        redoItem.setOnAction(e -> UndoManager.redo());
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(undoItem, redoItem);

        Menu fontSizeMenu = new Menu("Font Size");
        ToggleGroup toggleGroup = new ToggleGroup();
        fontSize = VisualBukkitLauncher.DATA_FILE.getConfig().getInt("font-size", 14);
        for (int i = 8; i <= 36; i++) {
            RadioMenuItem sizeItem = new RadioMenuItem(String.valueOf(i));
            sizeItem.setOnAction(e -> {
                fontSize = Integer.parseInt(sizeItem.getText());
                rootPane.setStyle("-fx-font-size:" + fontSize + ";");
                VisualBukkitLauncher.DATA_FILE.getConfig().set("font-size", fontSize);
            });
            toggleGroup.getToggles().add(sizeItem);
            fontSizeMenu.getItems().add(sizeItem);
            if (i == fontSize) {
                sizeItem.setSelected(true);
                rootPane.setStyle("-fx-font-size:" + fontSize + ";");
            }
        }
        Menu settingsMenu = new Menu("Settings");
        settingsMenu.getItems().add(fontSizeMenu);

        rootPane.setTop(new MenuBar(fileMenu, projectMenu, editMenu, settingsMenu));
    }

    private void setupSaving() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isShortcutDown() && e.getCode() == KeyCode.S && ProjectManager.getCurrentProject() != null) {
                try {
                    ProjectManager.getCurrentProject().save();
                    VisualBukkitLauncher.DATA_FILE.save();
                    displayMessage("Successfully saved project");
                } catch (IOException ex) {
                    displayException("Failed to save project", ex);
                }
                e.consume();
            }
        });

        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save?");
        saveAlert.setHeaderText(null);
        saveAlert.setGraphic(null);
        ButtonType saveButton = new ButtonType("Save and exit");
        ButtonType noSaveButton = new ButtonType("Exit without saving");
        saveAlert.getButtonTypes().setAll(saveButton, noSaveButton, ButtonType.CANCEL);

        primaryStage.setOnCloseRequest(e -> {
            if (ProjectManager.getCurrentProject() != null) {
                saveAlert.showAndWait().ifPresent(buttonType -> {
                    if (buttonType.equals(saveButton)) {
                        try {
                            ProjectManager.getCurrentProject().save();
                            VisualBukkitLauncher.DATA_FILE.save();
                        } catch (IOException ex) {
                            displayException("Failed to save project", ex);
                        }
                        Platform.exit();
                    } else if (buttonType.equals(noSaveButton)) {
                        Platform.exit();
                    }
                });
            } else {
                Platform.exit();
            }
            e.consume();
        });
    }

    public static void displayMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.showAndWait();
    }

    public static void displayError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.showAndWait();
    }

    public static void displayException(String message, Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, null, ButtonType.CLOSE);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        VBox content = new VBox(5, new Text(message), new TextArea(ExceptionUtils.getStackTrace(e)));
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    public static VisualBukkit getInstance() {
        return instance;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Scene getScene() {
        return scene;
    }

    public BorderPane getRootPane() {
        return rootPane;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public SelectorPane getSelectorPane() {
        return selectorPane;
    }

    public int getFontSize() {
        return fontSize;
    }
}
