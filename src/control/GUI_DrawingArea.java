package control;

import java.awt.event.*;
import java.awt.*;

import javax.swing.border.*;
import javax.swing.*;

import view.*;
import model.*;

import java.util.*;

/**
 * Diese Klasse steuert die View. Sie realisiert das Erzeugen,
 * Platzieren, Verschieben und Markieren der Elemente. Außerdem ist sie
 * für das Anschließen, also das Erzeugen von Kanten (Links) in der View
 * verantwortlich. Sie beobachtet (implements Observer) die Klasse Logik
 * und erfährt so, ob in der Logik ein neues Element hinzugefügt wurde
 * und macht das dann auch in der View ( in update(...) ).
 *
 * @author Uwe Rosner
 *
 */
public class GUI_DrawingArea extends JPanel implements MouseListener, MouseMotionListener, Observer {
    static final long serialVersionUID = 1L;

    // Das Modell, Änderungen im Modell werden beobachtet (siehe Member
    // 'update')
    private Logic model;

    private ArrayList<V_ElementRoot> elements = new ArrayList<V_ElementRoot>();
    private ArrayList<V_Link> links = new ArrayList<V_Link>();
    private VI_Element draggedElement = null;
    private VI_Element markedElement = null;

    // Damit kann das Bearbeiten der drawing_area gesperrt werden
    private boolean editingLock;

    // für die Mauskoordinaten
    private int mouseX;
    private int mouseY;

    // zum Anschließen
    private V_Pin sourcePin = null;             // Quell-Pin beim Anschließen
    private V_Pin firstPin = null;              // erster Pin beim Anschließen
    private VI_Element sourceElement = null;    // Quell-Element beim Anschließen
    private V_Link linkInWork = null;           // Enthält während des Anschließens die neue (anzuschließende) Kante

    // zum Hinzufügen neuer Elemente
    private V_ElementRoot elementInWork = null;    // Enthält das Element, das beim Erstellen "am Mauspfeil hängt"

    // Events dieser Klasse werden an diesen ActionListener gesendet
    private ActionListener act;

    /**
     * Initialisiert die View.
     * @param model die Instanz der Klasse "Logik". Notwendig, um die
     * Logik beobachten zu können (wenn dort neue Elemente erstellt
     * werden) 
     * @param act
     */
    public GUI_DrawingArea(Logic model, ActionListener act) {
        // Rand für GUI_DrawingArea einstellen
        setBorder(new EtchedBorder());

        // auf Maus-Aktionen reagieren
        addMouseListener(this);
        addMouseMotionListener(this);

        // übergebenen ActionListe
        this.act = act;

        // Übergebenes Modell übernehmen und beobachten
        this.model = model;
        this.model.addObserver(this);
    }

    /**
     * Testet, ob die Arbeitsfläche für das Bearbeiten gesperrt ist.
     * @return true wenn gesperrt, false sonst.
     */
    public boolean isEditingLocked() {
        return editingLock;
    }

    /**
     * Sperrt die Arbeitsfläche. Auf ihr kann fortan nicht mehr
     * gearbeitet werden.
     */
    public void lockEditing() {
        editingLock = true;
    }

    /**
     * Entsperrt die Arbeitsfläche.
     */
    public void unlockEditing() {
        editingLock = false;
    }

    /**
     * Gibt das auf der Arbeitsfläche markierte Element gerade
     * markiert ist.
     * @return markiertes Element
     */
    public VI_Element getMarkedElement() {
        if (markedElement != null && markedElement.isPoint() == true) {
            // Punkt gilt nicht als richtiges Element
            // und soll nicht zurückgegeben werden.
            return null;
        } else {
            // markiertes Element zurückgeben
            return markedElement;
        }
    }

    /**
     * Diese Funktion wird immer aufgerufen, wenn
     * GUI_DrawingArea.repaint() aufgerufen wird.
     */
    public void paintComponent(Graphics g) {
        // Arbeitsfläche löschen
        super.paintComponent(g);

        VI_Element d;
        VI_Link l;

        // Alle Elemente zeichnen
        for (int i = 0; i < elements.size(); i++) { 
            d = (VI_Element) elements.get(i);
            d.paintOn(g); 
        }

        // Alle Links (Kanten) zeichnen
        for (int i = 0; i < links.size(); i++) {
            l = (V_Link) links.get(i);
            l.paintOn(g);
        }

        // Das gerade zu erstellende Element (das "am Mauspfeil hängt")
        // zeichnen
        if (elementInWork != null) {
            elementInWork.setPosition(new Point(mouseX, mouseY));
            elementInWork.paintOn(g);
        }

        // Auch die gerade zu erstellende Kante zeichnen
        if (linkInWork != null) {
            linkInWork.paintOn(g);
        }
    }

    /**
     * Markiert das Element unter dem Mauspfeil zum Verschieben oder
     * schließt Pins an (Kanten erstellen)
     */
    public void mousePressed(MouseEvent e) {

        // Aussteigen, wenn Bearbeiten verboten ist
        if(isEditingLocked() == true) {
            return;
        }

        // Aussteigen, wenn nicht die linke Maustaste gedrückt wurde
        if (e.getButton() != MouseEvent.BUTTON1) {
            draggedElement = null;
            return;
        }

        /*
         * Wenn ein neues Element am Mauspfeil "hängt" und die Maustaste
         * gedrückt wurde, wird es nun in die Liste der Elemente
         * aufgenommen und platziert. Anschließend: Ausstieg.
         */
        if (elementInWork != null) {
            elementInWork.setColorNormal();
            elements.add(elementInWork);
            elementInWork = null;
            repaint();
            return;
        }

        /*
         * Variablen zurücksetzen
         */
        boolean clickedInElement = false;
        draggedElement = null; // alte Auswahl löschen
        VI_Element d;
        V_Pin highlight, destinationPin = null;
        ArrayList pins;

        /*
         * Das Herz der View. Aufgaben:
         *   - Entscheidung, ob ein Element verschoben oder
         *     angeschlossen werden werden soll (Maus nur über Element
         *     oder auch über Pin). Dann:
         *
         *       - Beim Verschieben: Setzen des Flags: zu verschiebendes
         *         Element wird in Variable draggedElement abgelegt. Das
         *         eigentliche Verschieben erfolgt dann in Funktion
         *         mouseDragged().
         *       - Beim Anschließen: Funktionalität siehe Kommentare
         */

        // Verschieben oder Anschliessen?
        for (int i = 0; i < elements.size(); i++) {
            d = (V_ElementRoot) elements.get(i);

            // Wenn in Element geklickt wurde:
            if (d.containsPoint(e.getPoint()) == true) {
                clickedInElement = true;

                // Alle Pins prüfen
                pins = d.getAllPins();
                for (int j = 0; j < pins.size(); j++) {
                    highlight = (V_Pin) pins.get(j);

                    // Maus über Pin --> Anschließen
                    if (d.isPoint() == false && highlight.containsPoint(e.getPoint()) == true) {
                        // Wenn noch kein Link in Bearbeitung:
                        if (linkInWork == null) {
                            // Falls Pin schon angeschlossen ist UND
                            // nur, wenn es eine Transition ist -->
                            // Abklemmen, zugehörigen Link löschen
                            if (highlight.getAssociatedLogicPin().isConnected() == true 
                            && highlight.getAssociatedLogicPin()
                                        .getAssignedElement()
                                        .getElementType() == ME_ElementType.TRANSITION) {

                                // Link im Modell löschen
                                model.deleteLink(highlight.getAssociatedLogicPin().getAssignedLink(0));
                                V_Link delLink = links.get(links.indexOf(highlight.getAssignedLink(0)));

                                // Referenzen des Links in den beiden
                                // zugehörigen Pins in der View löschen
                                ArrayList<V_Pin> allpins = highlight.getAssignedLink(0).getAllPins();
                                for (int k = 0; k < allpins.size(); k++) {
                                    allpins.get(k).removeAssignedLink(delLink);
                                }

                                // die zum Link gehörenden Punkte löschen
                                for (int k = 0; k < elements.size(); k++) {
                                    if (elements.get(k).isPoint() == true) {
                                        V_Point pnt = (V_Point) elements.get(k);
                                        if (pnt.getAssignedLink().equals(delLink)) {
                                            elements.remove(k);
                                            // k--, da durch remove() alle
                                            // weiteren Elemente eine
                                            // Position nach oben gerutscht
                                            // sind, und sonst
                                            // das folgende übersprungen
                                            // würde
                                            k--;
                                        }
                                    }
                                }

                                // Link in der View (aus der Link-Liste)
                                // löschen
                                links.remove(delLink);
                                highlight = null;
                                allpins = null;
                                repaint();

                                return;

                            }

                            // Wenn Output-Pin geklickt: angeklickter Pin wird Quell-Pin
                            if (highlight.getAssociatedLogicPin().getPinType() == ME_PinType.OUTPUT) {
                                sourcePin = highlight;
                                sourceElement = d;
                                firstPin = highlight;
                                linkInWork = new V_Link(sourcePin);
                            } else {
                                // Fehler "Anschliessen muss mit einem
                                // Ausgangs-Pin beginnen" erzeugen
                                // Das erzeugte ActionEvent wird in der Klasse
                                // StartUp() ausgewertet.
                                ActionEvent ev = new ActionEvent(this, 1, "Error_Connect_SrcPinIsAInputPin");
                                act.actionPerformed(ev);
                            }
                        }
                        else { // sonst
                            destinationPin = highlight;    // angeklickter Pin wird Ziel-Pin
                        }

                        // Wenn Quell- und Ziel-Pin schon bestimmt sind:
                        if (destinationPin != null) {

                            // Das Anschließen auch im Modell realisieren
                            if (model.connectElements(firstPin.getAssociatedLogicPin(), destinationPin.getAssociatedLogicPin()) == true) {

                                firstPin.setAssignedLink(linkInWork);
                                destinationPin.setAssignedLink(linkInWork);

                                // View-Link mit Model-Link assoziieren.
                                linkInWork.setAssociatedLogicLink(firstPin.getAssociatedLogicPin().getAssignedLink(0));

                                // Link vervollständigen
                                linkInWork.addPin(destinationPin);

                                // Link permanent hinzufügen (d.h. in Liste
                                // aufnehmen)
                                links.add(linkInWork);

                                // Anschlussvorgang beenden, Variablen freigeben, Pins normal darstellen
                                linkInWork = null;
                                sourcePin.setColorNormal();            // Hervorhebung löschen
                                destinationPin.setColorNormal();    // Hervorhebung löschen

                                sourcePin = null;         // Source- und
                                destinationPin = null;    // Destination-Pin zurücksetzten

                                // Mitteilung "Pins verbunden" erzeugen
                                // Das erzeugte ActionEvent wird in der Klasse
                                // StartUp() ausgewertet.
                                ActionEvent ev = new ActionEvent(this, 1, "Msg_Connect_OK");
                                act.actionPerformed(ev);

                                repaint(); // Alles neuzeichnen
                            }
                            // Wenn Anschließen im Modell fehlschlug
                            else {
                                sourcePin.setColorNormal();
                                destinationPin.setColorNormal();
                                linkInWork.clearPoints();

                                // die zum Link gehörenden Punkte löschen
                                for (int k = 0; k < elements.size(); k++) {
                                    if (elements.get(k).isPoint() == true) {
                                        V_Point pnt = (V_Point) elements.get(k);
                                        if (pnt.getAssignedLink().equals(
                                                linkInWork)) {
                                            elements.remove(k);
                                            // k--, da durch remove() alle
                                            // weiteren Elemente eine
                                            // Position nach oben gerutscht
                                            // sind, und sonst
                                            // das folgende übersprungen würde
                                            k--;
                                        }
                                    }
                                }

                                if(highlight.getAssociatedLogicPin().getPinType() != ME_PinType.INPUT) {
                                    // Fehler "Anschliessen muss mit einem Eingangs-Pin enden" erzeugen
                                    // Das erzeugte ActionEvent wird in der Klasse StartUp() ausgewertet.
                                    ActionEvent ev = new ActionEvent(this, 1, "Error_Connect_DstPinIsAOutputPin");
                                    act.actionPerformed(ev);
                                }

                                linkInWork = null;
                                sourcePin = null;
                                destinationPin = null;

                                repaint();
                            }
                        }
                    } else { // Maus über Element aber nicht über Pin --> Verschieben, Markieren

                        draggedElement = d; // aktuelles Element als zu verschiebendes markieren
                        // (eigentliche Verschiebung wird dann in mouseDragged()
                        // realisiert)

                        if (markedElement != null) {
                            markedElement.setColorNormal(); // Markierung
                                                            // löschen
                        }
                        markedElement = d; // angeklicktes Element merken
                        markedElement.setColorMarked(); // angeklicktes Element
                                                        // markieren
                        repaint();
                    }
                }
            }
        }

        // Wenn nicht in ein Element geklickt wurde
        if (clickedInElement == false) { 

            // (1) Immer: eventuelle Markierung eines Elements aufheben
            if (markedElement != null) {
                markedElement.setColorNormal();
            }
            markedElement = null;

            // (2) Beim Anschließen: Erzeugen eines neuen Punktes (während Kantenerstellung)
            if (linkInWork != null) {
                elements.add(new V_Point(e.getPoint().x, e.getPoint().y, linkInWork));
                pins = ((V_Point) elements.get(elements.size() - 1)).getAllPins();
                destinationPin = (V_Pin) pins.get(0);
                linkInWork.addPin(destinationPin);
                sourcePin = (V_Pin) pins.get(0);
                highlight = (V_Pin) pins.get(0);
            }

            // (3) Sonst: Wenn nahe einer Leitung geklickt wurde, einen Punkt hinzufügen
            else {
                V_Link lnk;
                for (int i = 0; i < links.size(); i++) {     // Alle Links nacheinander durchgehen...
                    lnk = (V_Link) links.get(i);             // ... auswählen ...
                    int position = lnk.nearestPoint(e.getPoint());

                    // wenn in die Nähe dieser Kante geklickt wurde
                    if (position > 0) {
                        // Punkt als Element hinzufügen
                        elements.add(new V_Point(e.getPoint().x, e.getPoint().y, lnk));

                        // Zielpunkt besorgen
                        pins = ((V_Point) elements.get(elements.size() - 1)).getAllPins();    
                        destinationPin = (V_Pin) pins.get(0);

                        // Zielpunkt an entsprechende Position in Kante hinzufügen
                        lnk.addPin(destinationPin, position);

                        // aus Schleife aussteigen
                        i = links.size();
                    }
                }
            }
            repaint();
        }

        clickedInElement = false;

    }

    /**
     * Folgende Funktionen müssen wegen der Mouse(...)Listener implementiert werden,
     * werden aber nicht benötigt
     */
    public void mouseReleased(MouseEvent e) {
        // von: MouseListener
    }

    public void mouseEntered(MouseEvent e) {
        // von: MouseListener
    }

    public void mouseClicked(MouseEvent e) {
        // von: MouseListener
    }

    public void mouseExited(MouseEvent e) {
        // von: MouseListener
    }

    /**
     * Realisiert das Verschieben von Elementen. Bedingung: linke
     * Maustaste muss über einem Element gedrückt werden und während des
     * Verschiebens der Maus weiter gedrückt bleiben. Realisierung:
     * Anwenden der Methode setPosition() auf das draggedElement (falls
     * es existiert), dabei als neue Position  auf aktuelle Mausposition
     * setzen.
     */
    public void mouseDragged(MouseEvent e) {

        /*
         * Aussteigen, wenn Bearbeiten verboten ist
         */
        if(isEditingLocked() == true) {
            return;
        }

        if (draggedElement != null) { // Wenn ein Element verschoben wird:
            draggedElement.setPosition(e.getPoint()); // seine (neue) Position setzten

            // Wenn dabei über linken Fenster-Rand hinaus geschoben wird
            if (e.getXOnScreen() < 1) {

                // und nur wenn das Element vom Typ Punkt ist
                if (draggedElement.isPoint() == true) {

                    // als Punkt behandeln
                    V_Point pnt = (V_Point) draggedElement;

                    // den zum Punkt zugehörigen Link holen
                    V_Link lnk = pnt.getAssignedLink();

                    // Alle Pins des Punktes besorgen (naja, ein Punkt hat nur
                    // einen...)
                    ArrayList<V_Pin> pins = pnt.getAllPins();

                    // Im zugehörigen Link den Pin des Punktes löschen
                    lnk.removePin(pins.get(0));

                    // den Punkt selbst löschen
                    elements.remove(draggedElement);
                }
            }

            repaint(); // und alles neuzeichnen
        }
    }

    public void mouseMoved(MouseEvent e) {

        mouseX = e.getX();
        mouseY = e.getY();

        V_ElementRoot d;
        for (int i = 0; i < elements.size(); i++) { // Alle Elemente nacheinander durchgehen ...
            d = elements.get(i);                     // ... und auswählen
            ArrayList<V_Pin> list = d.getAllPins(); // Alle Pins des gewählten Elements holen
            V_Pin pin;

            for (int j = 0; j < list.size(); j++) { // und pinweise durchgehen
                pin = list.get(j);

                // wenn Maus über Pin
                if (d.isPoint() == false && pin.containsPoint(e.getPoint()) == true) {
                    pin.setColorHighlighted(); // Pin hervorheben
                }
                else {
                    pin.setColorNormal(); // andernfalls Pin normal darstellen
                }
            }
            if (sourcePin != null) {
                sourcePin.setColorHighlighted(); // Quellpin (beim Anschließen) immer hervorheben
            }
        }

        repaint();
    }

    /**
     * Diese Funktion wird aufgerufen, wenn im beobachteten Modell
     * (Klasse: Logik) ein neues Element erzeugt wird. Sie sorgt dafür,
     * dass das neue Element auch in der View dargestellt wird, und dass
     * es zunächst "schattiert" am Mauspfeil klebt.
     */
    public void update(Observable obs, Object obj) {

        MI_Element element = (MI_Element) obj;

        switch (element.getElementIdent()) {

        case PLACE_INPUT:
            elementInWork = new V_PNPInput(mouseX, mouseY, obj);
            elementInWork.setColorShaded();
            repaint();
            break;

        case PLACE_OUTPUT:
            elementInWork = new V_PNPOutput(mouseX, mouseY, obj);
            elementInWork.setColorShaded();
            repaint();
            break;

        case PLACE_STORAGE:
            elementInWork = new V_PNPStorage(mouseX, mouseY, obj);
            elementInWork.setColorShaded();
            repaint();
            break;

        case TRANSITION:
            elementInWork = new V_PNTransition(mouseX, mouseY, obj, act);
            elementInWork.setColorShaded();
            repaint();
            break;
        }

    }

    /**
     * Löscht ein Element aus der Elementeliste.
     * @param element das zu löschende View-Element
     */
    public void deleteElement(VI_Element element) {
        elements.remove(element);
    }

    /**
     * Gibt eine Liste mit Allen View-Elementen zurück
     * @return elements ArrayList mit allen existierenden View-Elementen
     */
    public ArrayList<V_ElementRoot> getElements() {
        return elements;
    }

    /**
     * Gibt eine Liste mit einen View-Kanten zurück.
     * @return links ArrayList mit allen View-Lanten
     */
    public ArrayList<V_Link> getLinks() {
        return links;
    }

    /**
     * Löscht alle Elemente und Kanten aus der View.
     */
    public void clear() {
        elements.clear();
        links.clear();
    }

    /**
     * Nimmt das übergebene Element in die Liste der View-Elemente auf.
     * @param element
     */
    public void loadElement(V_ElementRoot element) {

        // Nur wenn es kein Punkt (--> V_Point) ist (Es bleiben also
        // Transitionen und Stellen übrig). Punkte werden übersprungen,
        // weil später (im inneren if()-Zweig) auf
        // element.getElementLogic() zugegriffen wird; das ist für
        // Elemente vom Typ V_Point nicht definiert und würde zu einer
        // NullPointerException führen.
        if(element.isPoint() == false) {
            // Und nur wenn es eine Transition ist: Diese muss nach dem
            // Landen noch den ActionListener zugewiesen bekommen (den
            // braucht sie für das Aufblinken-Lassen bei erfolgreicher
            // Berechnung zum repaint()
            if(element.getElementLogic().getElementType() == ME_ElementType.TRANSITION) {
                ( (V_PNTransition)element ).setActionListener(act);
            }
        }

        elements.add(element);
    }

    /**
     * Nimmt die übergebene Kante in die Liste der View-Kanten auf.
     * @param link
     */
    public void loadLink(V_Link link) {
        links.add(link);
    }
}
