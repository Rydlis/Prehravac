/**
 * Vytvořeno Davidem Rejdlem
 * Třída slouží pro vyber pisnicky na disku pomoci FileChooser
 * Tato trida bylo optimalizovana pomoci IntelliJ Idea Analyzator
 * trida je package-local, proto chybi "public" pred "class"
 */
package sample;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

class vyber {

    // zavedeni tridy FileChooseru
    private final FileChooser fileChooser = new FileChooser();

    // fce na vyber pisnicky s filtrem na podporovane formaty (podle dokumentace javy)
    public List<File> vyberSong(){
            fileChooser.setTitle("Otevři soubory");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Audio soubory", "*.wav", "*.mp3", "*.aac")
            );
        return fileChooser.showOpenMultipleDialog(new Stage()); //vraceni souboru
    }
}
