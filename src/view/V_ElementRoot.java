package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import model.MI_Element;

/**
 * Abstrakte Basisklasse für die Elemente (also Stellen und Transitionen) in der View.
 * Sie kann selbst nicht instanziiert werden, stellt aber einige Standardfunktionen zur Verfügung,
 * die bei Bedarf von den erbenden Klassen überschrieben werden. Die Klassen V_PNPLace (Stellen) und
 * V_PNTransition (Transitionen) erben von dieser Klasse.
 * @author Uwe Rosner
 *
 */
public abstract class V_ElementRoot implements VI_Element, Serializable {

    private static final long serialVersionUID = 1L;

    Color colElementNormal;     // Farbe für Beschriftung / Symbol und Rahmen
    Color colElementShaded;     // Farbe für Beschriftung / Symbol und Rahmen SCHATTIERT
    Color colElementRepressed;  // Farbe für Symbolrahmen und -beschriftung, wenn Element VERKLEMMT
    Color colElementMarked;     // Farbe für Beschriftung / Symbol und Rahmen HERVORGEHOBEN
    Color colPinNormal;         // Farbe für Anschlüsse und deren Beschriftung
    Color colPinRepressed;      // Farbe für verklemmte Anschlüsse
    Color colPinHighlighted;    // Farbe für hervorgehobene Anschlüsse

    Color colSelected;          // aktuell für Element gewählte Farbe (colElementHighlighted oder colElementNormal oder ...) 

    int x;      // x-Position des Elements
    int y;      // y-Position des Elements
    float w;    // Skalierungsfaktor für Breite des Elements
    float h;    // Skalierungsfaktor für Höhe des Elements
    int rx;     // Breite des Elements
    int ry;     // Höhe des Elements
    int xdiff;  // für Maus-x-Position innerhalb Element
    int ydiff;  // für Maus-y-Position innerhalb Element
    int xrast;  // Raster in x-Richtung
    int yrast;  // Raster in y-Richtung
    int midX;   // x-Koordinate des Elementmittelpunktes
    int midY;   // y-Koordinate des Elementmittelpunktes

    // jeweils zum markieren, ob Element
    // schattiert, markiert oder verklemmt ist (wegen Farbänderung)
    boolean flagShaded = false;
    boolean flagMarked = false;
    boolean flagInhibited = false;

    // Liste der Pins des Elementes
    protected ArrayList<V_Pin> pins = new ArrayList<V_Pin>();

    /**
     * Konstrunktor. Definiert Farben, Raster (das allerdings aktuell nicht verwendet wird)
     * und Standardgröße der Elemente. Wird auch beim Instanziieren der erbenden Klassen ausgeführt.
     */
    public V_ElementRoot() {
        colElementNormal = new Color(222, 222, 222);
        colElementShaded = new Color(84, 126, 212);
        colElementRepressed = new Color(200, 75, 50);
        colElementMarked = new Color(75, 50, 200);
        colPinNormal = new Color(33, 33, 33);
        colPinRepressed = new Color(200, 75, 50);
        colPinHighlighted = new Color(33, 222, 33);

        // Elemente standardmäßig normal (nicht hervorgehoben) zeichnen
        colSelected = new Color(colElementNormal.getRed(), colElementNormal.getGreen(), colElementNormal.getBlue());

        xrast = 0;
        yrast = 0;
        w=1; h=1;
        xdiff = 0; ydiff = 0;

    }

    /**
     * Gibt die Liste der Pins zurück.
     */
    public ArrayList<V_Pin> getAllPins() {
        return pins;
    }

    /**
     * true, wenn übergebener Punkt innerhalb Element ist
     */
    public boolean containsPoint(Point mousePosition) {
        return false;
    }

    /**
     * Zeichnet das Element an seiner Position.
     */
    public abstract void paintOn(Graphics g);

    /**
     * Setzt die Position des Elements auf einen neuen Wert.
     */
    public void setPosition(Point position) {
        
    }

    /**
     * true, wenn das Element ein Punkt (Instanz der Klasse V_Point) ist.
     */
    public boolean isPoint() {
        return false;
    }

    /**
     * Setzt Zeichenfarbe des Elements auf "normal".
     */
    public void setColorNormal() {
        colSelected = new Color(colElementNormal.getRed(), colElementNormal.getGreen(), colElementNormal.getBlue());
        flagMarked = false;
        flagShaded = false;
        flagInhibited = false;
    }

    /**
     * true, wenn Elementfarbe auf "normal" eingestellt ist.
     */
    public boolean isNormal() {
        if(flagMarked || flagShaded || flagInhibited == true) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Setzt Zeichenfarbe des Elements auf "markiert".
     */
    public void setColorMarked() {
        colSelected = new Color(colElementMarked.getRed(), colElementMarked.getGreen(), colElementMarked.getBlue());
        flagMarked = true;
        flagShaded = false;
        flagInhibited = false;
    }

    /**
     * true, wenn Elementfarbe auf "markiert" eingestellt ist.
     */
    public boolean isMarked() {
        return flagMarked;
    }

    /**
     * Setzt Zeichenfarbe des Elements auf "schattiert".
     */
    public void setColorShaded() {
        colSelected = new Color(colElementShaded.getRed(), colElementShaded.getGreen(), colElementShaded.getBlue());
        flagMarked = false;
        flagShaded = true;
        flagInhibited = false;
    }

    /**
     * true, wenn Elementfarbe auf "schattiert" eingestellt ist.
     */
    public boolean isShaded() {
        return flagShaded;
    }

    /**
     * gibt das dem View-Element entsprechende Logik-Element zurück
     */
    public MI_Element getElementLogic() {
        return null;
    }

    /**
     * Muss implementiert werden, da die View-Elemente die Schnittstelle Observer implementieren
     * und diese das verlangt. Funktionalität hier keine, da nicht benötigt. Dass diese Funktion
     * hier in der Basisklasse der View-Elemente implementiert wird, spart ein implementieren derselben
     * in jedem einzelnen Element
     */
    public void reloadObverver() {
        
    }

}
