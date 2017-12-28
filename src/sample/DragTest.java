package sample;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

/** Blend a coke can and a pepsi can to find the difference. */
public class DragTest extends Application {
    @Override
    public void start(Stage stage) {


        File newfile = new File("C://Users//Jeppe-PC//Desktop//a.png");
        File newfile2 = new File("C://Users//Jeppe-PC//Desktop//a2.png");
        try {
            Image pepsi = new Image(
                    newfile.toURI().toURL().toExternalForm()
            );
            Image coke = new Image(
                    newfile2.toURI().toURL().toExternalForm()
            );

            ImageView bottom = new ImageView(coke);
            ImageView top = new ImageView(pepsi);
            top.setBlendMode(BlendMode.GREEN);

            Group blend = new Group(
                    bottom,
                    top
            );

            HBox layout = new HBox(10);
            layout.getChildren().addAll(
                    new ImageView(coke),
                    blend,
                    new ImageView(pepsi)
            );
            layout.setPadding(new Insets(10));
            stage.setScene(new Scene(layout));
            stage.show();

        } catch( MalformedURLException e ){
            e.printStackTrace();
        }




    }

    public static void main(String[] args) {
        launch();
    }
}