/**
 * Vytvořeno David Rejdlem za ucelem zprehledneni kodu
 * navic, komu by se to chtelo porad psat
 * Tato trida bylo optimalizovana pomoci IntelliJ Idea Analyzator
 * trida je package-local, proto chybi "public" pred "class"
 */
package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

class dialogy {

    // zavedeni tridy Alert
    private final Alert alert = new Alert(Alert.AlertType.ERROR);

    // funkce na tisk alertu, nechtelo se mi psat dalsi 3 radky kodu ke kazdemu try/catch, navic tohle zprehlednuje kod
    public void chyba(String nadpis, String popis){
        alert.setHeaderText(nadpis);                // nastaveni nadpisu
        alert.setContentText(popis);                // nastaveni popisu chyby
        alert.showAndWait();                        // zobrazeni dialogu s nasim textem
    }

    // funkce na potvrzovaci dialog
    public Optional<ButtonType> potvrd(String nadpis, String dotaz){
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(nadpis);
        alert.setContentText(dotaz);
        return alert.showAndWait();
    }

}
