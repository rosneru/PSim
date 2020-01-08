package control;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Diese Klasse stellt eine Statuszeile zur Verfügung, die beim Petrinet-Simulatot am unteren
 * Bildschirmrand positioniert wird. Sie ermöglicht wahlweise die Ausgabe von normalem Text (Farbe schwarz)
 * oder hervorgehobenem Text (Farbe rot)
 * @author Uwe Rosner
 *
 */

public class GUI_Statebar extends JPanel {
    static final long serialVersionUID = 1L;    // umgeht die Warnung "The serializable class ..."
                                                // unterscheidet verschiedene Versionen der Klasse

    JLabel    label;    //    Das Label für den anzuzeigenden Text

    /**
     * Das ist der Konstrunktor der Statusleiste.
     */
    public GUI_Statebar() {
        // Statusleiste bauen und initialisieren
        setLayout(new GridLayout(1,1));
        label = new JLabel("Petri net simulator ready.");
        label.setBorder(new EtchedBorder());
        add(label);
    }

    /**
     * Stellt den übergebenen Text normal dar.
     * @param t -  Anzuzeigender Text
     */
    public void setText(String t){
        label.setForeground(Color.BLACK);
        label.setText(t);
    }

    /**
     * Stellt den übergebenen Text hervorgehoben dar.
     * @param t -  Anzuzeigender Text
     */
    public void setTextHighlighted(String t){
        label.setForeground(Color.RED);
        label.setText(t);
    }

}
