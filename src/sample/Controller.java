/**
 * Vytvoreno Davidem Rejdlem
 * Tato trida slouzi pro ovladani prehravace, nastaveni hlasitosti
 * Tato trida bylo optimalizovana pomoci IntelliJ Idea Analyzator
 */

package sample;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    //*zavedeni GUI
    @FXML
    private Slider seekbar;
    @FXML
    private Button play;
    @FXML
    private Button next;
    @FXML
    private Button prev;
    @FXML
    private Label start;
    @FXML
    private Label end;
    @FXML
    private Label artist;
    @FXML
    private Label song;
    @FXML
    private Label album;
    @FXML
    private ListView<String> list;

    //* zavedeni trid
    private final prehravac prehravac = new prehravac();                                // trida prehravace
    private final SimpleDateFormat sdf = new SimpleDateFormat("m:ss");                  // prevedeni Duration na normalni format
    private final dialogy dialog = new dialogy();
    private final ObservableList<String> items = FXCollections.observableArrayList();   // zavede novy observableList, protoze ListView pracuje pouze s nim
    private Boolean isRepeating = false;                                                // promena typu pravda/nepravda, slouti ke zjisteni jestli se prehravac opakuje nebo ne
    private Boolean isNahoda = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {                           //odposlech "double-clicku"
                    if (mouseEvent.getClickCount() == 2) {                                          // jestli se 2x stisklo vychozi tlacitko, vykonec tuto akci
                        prehravac.nastaveniPisnicky(list.getSelectionModel().getSelectedIndex());   // posila index vybraneho prvku tride prehravac
                        handleLabels(new ActionEvent());
                    }
                }
            }
        });
        // handler pro seekbar, pouzit level 8 - lamda, prepisuje vychozi handler
        seekbar.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleSeekbar();
            prehravac.setCas((Double) newValue);
            handleLabels(new ActionEvent());
        });
    }
    // funkce pro ovladani Play/Pause a volani zjistovani casu
    public void handlePlayStop (ActionEvent event){
        prehravac.hraj();
        handleLabels(event);
    }


    // ovladani vsech labelu, tato funkce je na nastavovani casu v labelech a vola funkci na metadata
    void handleLabels(ActionEvent event){
        prehravac.getPlayer().currentTimeProperty().addListener(observable -> {
            end.setText(sdf.format(prehravac.getPlayer().getTotalDuration().toMillis()));
            start.setText(sdf.format(prehravac.getPlayer().getCurrentTime().toMillis()));
        });
        handleMetadata(event);
    }

    void handleSeekbar(){
        seekbar.setMax(prehravac.getPlayer().getTotalDuration().toMillis());
        seekbar.setMin(0.0);
        seekbar.setBlockIncrement(0.1);
    }

    // funkce na obstarani metadat, nastaveni textu v labelu Artist, Song, Album
    void handleMetadata(ActionEvent event){
        ObservableMap<String,Object> meta_data=prehravac.getMedia().getMetadata();
        meta_data.addListener((MapChangeListener<String, Object>) ch -> {       // vlastni handler, prevedeno na Lambda level language
            if (ch.wasAdded()) {                                                // jestli-ze byla zmenena pisnicka, updatuj labely
                String key = ch.getKey();
                Object value = ch.getValueAdded();
                if (ch.getValueAdded().toString().isEmpty()) {                                         // jestli ze nejsou k dispozici metadata, nastav text uvedeny nize
                    album.setText("Album: ");
                    artist.setText("Artist: ");
                    song.setText("Title: ");
                } else {                                                        // ale jestli jsou, nastav hodnoty ziskane z metadat, jako Artist, Album nebo Title
                    switch (key) {
                        case "album":
                            album.setText("Album: " + value.toString());
                            break;
                        case "artist":
                            artist.setText("Artist: " + value.toString());
                            break;
                        case "title":
                            song.setText("Title: " + value.toString());
                            break;
                    }
                }
            }
        });
    }

    // handle pro zaobstaravani playlistu, precte cestu k souborum z Arraye "songs" a dosadi
    // je do ObservableList items, pote vytiskne do ListView list
    void handlePlaylist(ActionEvent event){
        try {
            items.clear();                                                  // nejprve vycisti databazi
            list.setItems(items);                                           // pote vycisti listview
            for (int i = 0; i <= prehravac.getSongs().size(); i++) {        // a cyklem for ji znovu naplni
                items.add(i, prehravac.getSongs().get(i).toString());
            }
            list.setItems(items);                                           // a nakonec je zase vypise do listview
        }catch (IndexOutOfBoundsException e){
            System.out.println();                                           // nevypsani chyby (ono to funguje, ale porad to pise IndexOutOfBounderies, ikdyz se to tam naimportuje spravne)
        }
    }

    // handle na vymzani databaze a listview
    public void handleDelete (ActionEvent event){
        dialog.potvrd("Počkat", "Opravdu chcete smazat playlist?");
        prehravac.getPlayer().dispose();
        prehravac.getSongs().clear();
        items.clear();
        list.setItems(items);
        handleMetadata(event);
    }

    // handle na prehozeni pisnicky na nasledujici
    public void handleNext (ActionEvent event){
        prehravac.Next();
        handleLabels(event);
    }

    // handle na prehozeni pisnicky za predchozi
    public void handlePrev (ActionEvent event){
        prehravac.Prev();
        handleLabels(event);
    }

    // handle na nastaveni repeatu na true/false
    public void handleRepeat (ActionEvent event){
        if (!isRepeating) {
            prehravac.setRepeat(true);
            isRepeating = true;
        }
        else {
            prehravac.setRepeat(false);
            isRepeating = false;
        }
    }

    // handle na nastaveni nahody na true/false
    public void handleNahoda (ActionEvent event){
        if (!isNahoda){
            prehravac.setNahoda(true);
        } else {
            prehravac.setNahoda(false);
        }
    }

    // handle na zavolani vyberu souboru a aktualizaci Playlistu
    public void handleVyber (ActionEvent event){
        prehravac.vyberPisnicky();
        handlePlaylist(event);
    }

    //handle pro ukonceni programu
    public void handleKonec (ActionEvent event){
        System.exit(0);
    }

}
