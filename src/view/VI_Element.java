package view;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import model.MI_Element;

/**
 * Interface für Stellen und Transitionen.
 * Deklariert die für diese Elemente notwendigen Funktionen.
 * 
 * @author Uwe Rosner
 */

public interface VI_Element {
    boolean containsPoint(Point mousePosition); // Maus über Element?
    void setPosition(Point position);           // Position des Elemets setzen
    void paintOn(Graphics g);                   // Element an Position zeichnen
    public ArrayList<V_Pin> getAllPins();       // Liste aller Pins des Elements zurückgeben
    public boolean isPoint();

    // Farbverwaltung
    public void setColorNormal();
    public boolean isNormal();

    public void setColorMarked();
    public boolean isMarked();

    public void setColorShaded();
    public boolean isShaded();

    // Verknüpfung Logik <--> View
    public MI_Element getElementLogic();        // gibt das diesem Element zugeordnete Element der Logik zurück

    // Service
    public void reloadObverver();
}
