package view;

import java.awt.Graphics;
import java.awt.Point;

/**
 * Abstrakte Basisklasse für die Stellen in der View. Sie ist abgeleitet von V_ElementRoot und überschreibt
 * die dort nur formulierten Funktionen containsPoint(...) und setPosition(...). Weiterhin stellt
 * sie mit calcMidPoint(...) eine für die Verwaltung der Stellen benötigte Methode zur Verfügung.
 * 
 * @author Uwe Rosner
 */
public abstract class V_PNPlace extends V_ElementRoot {

    private static final long serialVersionUID = 1L;

    /**
     * Konstruktor. Definiert die Standard-Werte für Höhe und Breite der Stellen.
     */
    public V_PNPlace() {
        rx=32; ry=32;
    }

    /**
     * true, wenn übergebener Punkt innerhalb des Elements ist
     */
    public boolean containsPoint(Point pnt){
        if(((pnt.x - midX) * (pnt.x - midX)) + ((pnt.y - midY) * (pnt.y - midY)) - ((w*rx / 2) * (w*rx / 2)) <= 0) {
            // pnt liegt innerhalb Element
            xdiff = pnt.x-x;    // Mausposition innerhalb des Elements beibehalten 
            ydiff = pnt.y-y;    // ---------------------- " ----------------------
            return true;
        }
        else {
            // pnt liegt nicht innerhalb Element
            xdiff = 0;
            ydiff = 0;
            return false;
        }
    }

    /**
     * Setzt die Position des Elements auf einen neuen Wert.
     */
    public void setPosition(Point pos){
        if(xrast > 0) {
            if ((pos.x-xdiff) % xrast == 0) {
                x = pos.x-xdiff;
            }
        }
        else {
            x = pos.x-xdiff;
        }

        if(yrast > 0) {
            if((pos.y-ydiff) % yrast == 0) {
                y =    pos.y-ydiff;
            }
        }
        else {
            y =    pos.y-ydiff;
        }

        calcMidPoint();
    }

    /**
     * Zeichnet das Element an seiner Position.
     */
    public abstract void paintOn(Graphics g);

    /**
     * Berechnet die Mittelpunktskoordinaten des Elements.
     */
    protected void calcMidPoint() {
        midX = (int)(x + w * rx / 2);
        midY = (int)(y + h * ry / 2);
    }
}
