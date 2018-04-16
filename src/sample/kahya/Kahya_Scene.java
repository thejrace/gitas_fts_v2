package sample.kahya;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;


public class Kahya_Scene extends Application{
    private static AnchorPane root;
    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("../resources/fxml/test.fxml"));
        primaryStage.setTitle("Gitaş FTS Sunucu Versiyon");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();


        Kahya kahya = new Kahya("C-1812");
        kahya.init();

    }



    public static void main(String[] args) {
        launch(args);
    }

    private static void guzergah_tayin_test(){
        ArrayList<String> karisik_hat = new ArrayList<>();
        karisik_hat.add("15C_G_D2163");
        karisik_hat.add("15C_G_D2163");
        karisik_hat.add("15C_G_D2163");
        karisik_hat.add("15C_G_D2162");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D0");
        karisik_hat.add("15C_G_D0");
        karisik_hat.add("15C_D_D2181");

        ArrayList<String> normal_hat = new ArrayList<>();
        normal_hat.add("15BK_D_D2846");
        normal_hat.add("15BK_G_D2845");
        normal_hat.add("15BK_D_D2846");
        normal_hat.add("15BK_G_D2845");
        normal_hat.add("15BK_D_D2846");
        normal_hat.add("15BK_G_D2845");
        normal_hat.add("15BK_D_D2846");
        normal_hat.add("15BK_G_D2845");
        normal_hat.add("15BK_D_D2846");

        ArrayList<String> ring_hat_son_sefer_donus = new ArrayList<>();
        ring_hat_son_sefer_donus.add("11ÜS_G_D2278");
        ring_hat_son_sefer_donus.add("11ÜS_G_D2278");
        ring_hat_son_sefer_donus.add("11ÜS_G_D2278");
        ring_hat_son_sefer_donus.add("11ÜS_G_D2032");
        ring_hat_son_sefer_donus.add("11ÜS_D_D2033");

        ArrayList<String> ring_hat_ilk_sefer_gidis = new ArrayList<>();
        ring_hat_ilk_sefer_gidis.add("11ÜS_G_D2278");
        ring_hat_ilk_sefer_gidis.add("11ÜS_D_D2278");
        ring_hat_ilk_sefer_gidis.add("11ÜS_D_D2278");
        ring_hat_ilk_sefer_gidis.add("11ÜS_D_D2032");
        ring_hat_ilk_sefer_gidis.add("11ÜS_D_D2033");

        String hat = "11ÜS";
        Yon_Tayini.yap( hat, 2, ring_hat_son_sefer_donus );
    }

}

