package sample;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;


/**
 * Created by Obarey on 09.02.2017.
 */
public class Obarey_Popup {

    private String header, id;
    private Popup popup;
    private Node parent;
    private boolean on = false;
    private AnchorPane content_container;

    public Obarey_Popup( String header, Node parent_root ){
        this.header = header;
        this.popup = new Popup();
        this.parent = parent_root;
    }

    public void set_id( String id ){
        this.id = id;
    }
    public String get_id(){
        return id;
    }

    public void init( boolean draggable ){

        content_container = new AnchorPane();
        content_container.getStyleClass().add("popup-container");
        content_container.getStylesheets().add(Obarey_Popup.class.getResource("resources/css/common.css").toExternalForm());
        AnchorPane popup_header = new AnchorPane();
        popup_header.getStyleClass().add("popup-header");

        AnchorPane.setTopAnchor( popup_header, 7.0 );
        AnchorPane.setLeftAnchor( popup_header, 10.0 );
        AnchorPane.setRightAnchor( popup_header, 10.0 );

        Label header_label = new Label( this.header );
        header_label.getStyleClass().addAll("fbold", "fs12", "cbeyaz");
        ImageView kapat_btn = new ImageView( new Image(getClass().getResource("resources/img/ico_popup_off_beyaz.png").toExternalForm() ) );
        kapat_btn.getStyleClass().add("cursor-hand");

        AnchorPane.setLeftAnchor(header_label, 5.0);
        AnchorPane.setRightAnchor(kapat_btn, 0.0);
        AnchorPane.setTopAnchor(kapat_btn, 0.0);

        popup_header.getChildren().addAll( header_label, kapat_btn );

        content_container.getChildren().add( popup_header );

        kapat_btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                hide();
                event.consume();
            }
        });
        if( draggable ) Common.make_draggable( content_container );


    }

    public void set_content( Node content ){
        // ilk acilista 1. index olmadigi icin
        if( content_container.getChildren().size() > 1 ) content_container.getChildren().remove(1);

        AnchorPane.setTopAnchor( content, 45.0);
        AnchorPane.setLeftAnchor( content, 10.0 );
        AnchorPane.setRightAnchor( content, 10.0 );
        AnchorPane.setBottomAnchor( content, 10.0 );

        // bir onceki veriyi sil
        content_container.getChildren().add( content );
        this.popup.getContent().clear();

        this.popup.getContent().add( content_container );

    }


    public void show( double x, double y ){
        if( !this.on ){
            this.popup.show( this.parent, x, y );
            this.on = true;
        }

    }

    public void hide(){
        try {
            this.popup.getContent().clear();
            this.popup.hide();
            this.on = false;
        } catch( Exception e ){
            e.printStackTrace();
        }

    }

    public boolean ison(){
        return this.on;
    }

    public void set_auto_hide( boolean flag ){
        this.popup.setAutoHide(flag);
    }

}
