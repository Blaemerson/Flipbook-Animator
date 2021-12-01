package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.FileData;
import model.SQLite;

import java.util.LinkedList;
import java.util.List;

public class StartScreenController {

    @FXML
    private MenuBar menuBarStartScreen;

    @FXML
    private MenuItem newFileStartScreenID;

    @FXML
    private BorderPane pane;

    @FXML
    private AnchorPane startScreenPane;

    @FXML
    private HBox startscreenBox;

    @FXML
    private ScrollPane startscreenScrollPane;

    @FXML
    void newFileStartScreen(ActionEvent event) {
        // get a handle to the stage
        Stage stage = (Stage) menuBarStartScreen.getScene().getWindow();

        stage.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Studio.class.getResource("resources/window-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 480, 360);
            stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void onDeleteChosen(ActionEvent event) {

    }

    @FXML
    void onInsertFrame(ActionEvent event) {

    }

    @FXML
    void onOpenFileChosen(ActionEvent event) {

    }

    @FXML
    void onSaveFileChosen(ActionEvent event) {

    }

    /*@FXML
    void populateStartScreen(ActionEvent event) {
        Stage window = new Stage();
        startscreenBox.setSpacing(2);
        startscreenBox.getChildren().clear();
        List<FileData> recentFiles = new LinkedList<>();
        SQLite.fileList(recentFiles);

        Group root = new Group();
        Scene scene = new Scene(root);
        scene.setFill(Color.BLACK);
        HBox box = new HBox();
        //root.getChildren().add(box);
        window.setTitle("ImageView");
        window.setWidth(415);
        window.setHeight(200);
        window.setScene(scene);
        window.sizeToScene();


        //TODO: populate recentFiles by reading sqldb

        for (int i = 0, recentFilesSize = recentFiles.size(); i < recentFilesSize; i++) {
            FileData curRecentFile = recentFiles.get(i);
            Image image = new Image(curRecentFile.getImgString());

            ImageView thumb = new ImageView();
            thumb.setImage(image);

            thumb.setPreserveRatio(true);
            thumb.setFitHeight(100);
            Tooltip.install(thumb, new Tooltip(curRecentFile.getName()));
            thumb.setOnMousePressed((MouseEvent e) -> {
                WindowController.open(curRecentFile.getFilePath());
            });
            box.getChildren().add(thumb);


                startscreenBox.getChildren().add(thumb);
                Tooltip.install(box.getChildren().get(i),
                        new Tooltip(curRecentFile.getName()));



                startscreenBox.getChildren().get(i).setOnMousePressed((MouseEvent e) -> {
                    //addThumbnails(this.flipbook.getCurFrameNum());
                    open(curRecentFile.getFilePath());
                });

        }
        root.getChildren().add(box);
        window.show();

    }*/

    @FXML
    void toggleOnionSkinning(ActionEvent event) {

    }

}
