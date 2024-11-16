package control;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Diese Klasse stellt das Popup-Menü zur Verfügung.
 * Wird im Applet StartUp instanziiert.
 * @author Uwe Rosner
 *
 */
public class GUI_PopupMenu extends JPopupMenu{
    static final long serialVersionUID = 1L;    // umgeht die Warnung "The serializable class ..."
                                                // unterscheidet verschiedene Versionen der Klasse

    JMenu add_elements;
    JMenu places;
    JMenu transitions;
    JMenu tr_1log;
    JMenu tr_2log;
    JMenu edit_elements;
    JMenu petrinet;

    /**
     * Konstruktor des Popup-Menüs.
     * @param act Der ActionListener, an den die Events (bestimmter
     * Menüpunkt gewählt) gesendet werden.
     */
    public GUI_PopupMenu(ActionListener act) {
        // Die folgenden Strings enthalten die Texte für das aufzubauende Menü.
        String[] edStr = {"Properties", "Weights", "Disconnect all", "Remove"};
        String[] pnStr = {"New", "Load", "Save"};
        String[] plStr = {"Place (gen.)", "Input place", "Output place"};

        // Wird temporär für die hinzuzufügenden Menüpunkte benötigt.
        JMenuItem item;

        // Menü "Element hinzufügen"
        add_elements = new JMenu("Add item");

        // Menüpunkt "Transition"
        item = new JMenuItem("Transition");
        item.addActionListener(act);
        add_elements.add(item);

        // Untermenü "Stelle" aufbauen und dem Menü "Element hinzufügen"
        // hinzufügen
        places = new JMenu("Place");
        // Dem Untermenü "Stelle" die Menüpunkte hinzufügen
        for(int i=0; i< plStr.length; i++) {
            item = new JMenuItem(plStr[i]);
            places.add(item);
            item.addActionListener(act);
        }

        // Untermenü "Stelle" dem Menü "Element hinzufügen" hinzufügen
        add_elements.add(places);

        // Dem Popup-Menü das Menü "Element hinzufügen" hinzufügen
        this.add(add_elements);

        // ...

        // Menü "Element bearbeiten"
        edit_elements = new JMenu("Edit item");
        for(int i = 0; i < edStr.length; i++) {
            item = new JMenuItem(edStr[i]);
            item.addActionListener(act);
            edit_elements.add(item);
        }

        this.add(edit_elements);

        this.add(new JSeparator());

        petrinet = new JMenu("Petri net");
        for(int i = 0; i < pnStr.length; i++) {
            item = new JMenuItem(pnStr[i]);
            item.addActionListener(act);
            petrinet.add(item);
        }

        this.add(petrinet);

    }
}
