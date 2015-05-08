/**
 * Vytvoreno Davidem Rejdlem
 * Tato trida slouzi pro ovladani prehravace, nastaveni hlasitosti
 * Tato trida bylo optimalizovana pomoci IntelliJ Idea Analyzator
 * trida je package-local, proto chybi "public" pred "class"
 */

package sample;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

class prehravac {;
    private Media media = new Media(new File("src/sample/cink.mp3").toURI().toString());    // na tohle nejsem hrdy, ale nevyresil jsem jak to zavest bez souboru a uz se kratil cas, tak jsem pouzil tuhle konstrukci a doufam ze bude fungovat na vsech OS
    private MediaPlayer player = new MediaPlayer(media);
    private final vyber vyber = new vyber();
    private final ArrayList<File> songs = new ArrayList<>();
    private final dialogy dialog = new dialogy();

    private Boolean prehravani = false;                                                 // promena typu pravda/nepravda
    private Boolean autoPlayNext = true;                                                // promena typu pravda/nepravda, urcuje zdali ma prehravac po dohrani pokracovat v seznamu
    private Boolean repeat = false;                                                     // promena typu pravda/nepravda, urcuje zdali ma prehravac opakovat playlist
    private int i = 0;                                                                  // slouzi pro orientaci v seznamu
    private Boolean nahoda = false;                                                     // slozi pro urceni toho, jestli ma prehravac prehravat pisnicky nahodne nebo ne


    // funkce na ovladani prehravace, pusti nebo pozastavi pisnicku
    public void hraj(){
        try {
            if (!prehravani) {                                                          // jestli se neprehrava, pust pisnicku
                if (songs.isEmpty()){                                                   // ale prvni zaved soubor do databaze
                    dialog.chyba("Chyba", "Nejprve zvol soubor");                        // chybova hlaska
                } else {
                    if (songs.get(i).toURI().toString().equals(media.getSource())){     // zjisti aktualne prehravany soubor a pokud se shoduje se souborem s databaze, pokracuje v prehravani pisnicky
                        player.play();                                                  // pust jiz zavedenou pisnicku
                        prehravani = true;                                              // nastav prehravani na true
                    } else {
                        media = new Media(songs.get(i).toURI().toString());             // zavedeni noveho souboru do Media
                        player = new MediaPlayer(media);                                // zavedeni noveho MediaPlayeru s novym Mediem
                        player.play();                                                  // zacne prehravat pisnicku
                        prehravani = true;                                              // nastavi prehravani na true
                    }
                }
            } else {
                player.pause();
                prehravani = false;
            }
        } catch (Exception e) {
            dialog.chyba("Chyba", "Neco se pokazilo\nAplikace bude pokracovat v provozu");
        }
    }

    //prehozeni pisnicky, znici se player a zavede znovu s novym souborem, funkce je Runnable kvuli player.setOnEndOfMedia() ktera je pro Runnable
    public Runnable Next(){
        if (nahoda){                                                                            // jestli je nahoda true
            Random nahoda = new Random();                                                       // udela se nahodne cislo
            player.dispose();                                                                   // znici se MediaPlayer
            media = new Media(songs.get(nahoda.nextInt(songs.size())).toURI().toString());      // zavola se nahodna pisnicka ze seznamu
            player = new MediaPlayer(media);                                                    // zavede se nove Medium
            player.play();                                                                      // zavede se novy MediaPlayer i s novym Media
        } else {
            i++;
            if (i >= songs.size()){                                                             // jestli je i > songs.size()
                dialog.chyba("Chyba", "Jste mimo pole");                                         // vyhodi se vyjimka
                i--;                                                                            // a nastavi se nazpet cislo pred zavolanim funkce
            } else {                                                                            // ale jestli je vsechno v poradku
                player.dispose();                                                               // znici se predchozi player
                media = new Media(songs.get(i).toURI().toString());                             // zavede se nove Medium s novou pisnickou ze seznamu
                player = new MediaPlayer(media);                                                // zavede se novy prehravac s novym Mediem
                player.play();                                                                  // a zacne hrat
            }
        }
        return null;                                                                            // nic nevraci, proto je tu null
    }

    //prehozeni pisnicky, znici se player a zavede znovu s novym souborem
    public Runnable Prev(){
        if (nahoda){                                                                            // jestli je nahoda true
            Random nahoda = new Random();                                                       // udela se nahodne cislo
            player.dispose();                                                                   // znici se MediaPlayer
            media = new Media(songs.get(nahoda.nextInt(songs.size())).toURI().toString());      // zavola se nahodna pisnicka ze seznamu
            player = new MediaPlayer(media);                                                    // zavede se nove Medium
            player.play();                                                                      // zavede se novy MediaPlayer i s novym Media
        } else {
            i--;
            if (i < 0){                                                                         // jestli je i < 0 (List stejne mena ensi index a vzdy zacina 0)
                dialog.chyba("Chyba", "Jste mimo pole");                                         // vyhodi se vyjimka
                i++;                                                                            // a nastavi se nazpet cislo pred zavolanim funkce
            } else {                                                                            // ale jestli je vsechno v poradku
                player.dispose();                                                               // znici se predchozi player
                media = new Media(songs.get(i).toURI().toString());                             // zavede se nove Medium s novou pisnickou ze seznamu
                player = new MediaPlayer(media);                                                // zavede se novy prehravac s novym Mediem
                player.play();                                                                  // a zacne hrat
            }
        }
        return null;                                                                            // nic nevraci, proto je tu null
    }

    // funkce na nastaveni hlasitosti
    public void hlasitost(Double uroven){
        player.setVolume(uroven);
    }

    // funkce pro nastaveni aktualniho casu pres posuvnik, prevadi ziskanou hodnotu z Double -> Duration v millis
    public void setCas (Double cas){
        player.seek(Duration.millis(cas));
    }

    // funkce na pridani pisnicek do ArrayListu, po vykonani vola metodu hraj();
    public void vyberPisnicky(){
        songs.addAll(vyber.vyberSong());
    }

    // funkce nastaveni pisnicky, slouzi pro nastaveni dalsi/predesle pisnicky
    public void nastaveniPisnicky(Integer i){
        player.dispose();                                               // "zniceni playeru
        media = new Media(songs.get(i).toURI().toString());             // zavedeni noveho media s pozadovanym souborem
        player = new MediaPlayer(media);                                // zavedeni noveho prehravace s novym mediem
        player.play();                                                  // Player zacne hrat novou pisnicku
        this.i = i;                                                     // kolikaty song se prave prehrava
    }

    // Gettovani ArrayListu songs
    public ArrayList<File> getSongs() {
        return songs;
    }

    // Gettovani MediaPlayeru
    public MediaPlayer getPlayer() {
        return player;
    }

    // Gettovani Media, kuprikladu na cteni metadat
    public Media getMedia() {
        return media;
    }

    public void setAutoPlayNext(Boolean autoPlayNext) {
        this.autoPlayNext = autoPlayNext;
    }

    public void setRepeat(Boolean repeat) {
        this.repeat = repeat;
    }

    public void setNahoda (Boolean nahoda){
        this.nahoda = nahoda;
    }
}
