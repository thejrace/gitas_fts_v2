package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import sample.kahya.Kahya_Scene;

public class Filo_Captcha_Scene extends Application {

    private VBox root;
    private Stage stage;
    private Filo_Captcha_Controller controller;
    @Override
    public void start( Stage primaryStage ) throws Exception{

        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("resources/fxml/login.fxml"));
            Parent root = fxmlLoader.load();
            controller = fxmlLoader.getController();
            primaryStage.setTitle("Gitaş Filo Takip");
            primaryStage.initStyle(StageStyle.DECORATED);
            try {
                Font.loadFont(getClass().getResource("resources/font/montserratbold.otf").toExternalForm().replace("%20", " "), 10);
                Font.loadFont(getClass().getResource("resources/font/montserratsemibold.otf").toExternalForm().replace("%20", " "), 10);
                Font.loadFont(getClass().getResource("resources/font/montserratlight.otf").toExternalForm().replace("%20", " "), 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            primaryStage.setScene(new Scene(root, 500, 250));
            primaryStage.getIcons().add(new Image(getClass().getResource("resources/img/app_ico.png").toExternalForm()));
            primaryStage.show();
            stage = primaryStage;
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    Platform.exit();
                    System.exit(0);
                }
            });

            controller.add_finish_listener(new Refresh_Listener() {
                @Override
                public void on_refresh() {
                    try{
                        stage.close();
                        Takip_Scene main_scene = new Takip_Scene();
                        //Kahya_Scene main_scene = new Kahya_Scene();
                        main_scene.start(new Stage());
                    } catch( Exception e ){
                        e.printStackTrace();
                    }
                }
            });

        } catch( Exception e ){
            e.printStackTrace();
        }

    }






}
