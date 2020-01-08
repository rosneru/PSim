package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.ArrayList;
import model.M_Pin;

/**
 * Diese Klasse ist für die Verwaltung der Pins in der View verantwortlich.
 * @author Uwe Rosner
 *
 */
public class V_Pin extends V_ElementRoot implements VI_Pin, Serializable{

    private static final long serialVersionUID = 1L;

    boolean vertical;     // Flag, zeigt an, ob Pin vertikal (true) oder horizontal ist
    String orientation;   // enthält die Seite des Elements, an der der Pin platziert ist ("left", "right", ...)
                          // oder "round" für Punkte (V_point)

    int pointX;           // X-Koordinate
    int pointY;           // Y-Koordinate

    M_Pin associatedPin;  // Pendat dieses View-Pins in der Logik

    ArrayList<V_Link> assignedLinks;  // Liste, die alle Kanten enthält, von denen dieser Pin ein Teil ist

    /**
     * Erzeugt einen neuen View-Pin
     * @param orientation Seite des Elements, an der der Pin platziert werden soll ("left", "right", "top", "down")
     * oder "round", wenn der Pin für einen Punkt (V_Point) verwendet werden soll.
     * @param pin Das Logik-Pendant des zu erzeugenden View-Pins.
     */
    public V_Pin(String orientation, M_Pin pin) {
        this.orientation = orientation;
        colSelected = colPinNormal;

        assignedLinks = new ArrayList<V_Link>();

        if(this.orientation == "left") {
            vertical = true;
        }
        else if(this.orientation == "right") {
            vertical = true;
        }
        else if(this.orientation == "top") {
            vertical = false;
        }
        else if(this.orientation == "down") {
            vertical = false;
        }
        else {
            vertical = true;
        }

        associatedPin = pin;
    }

    /**
     * Ordnet dem Pin eine (evtl. weitere) Kante zu.
     * @param assignedLink die zuzuordnende Kante
     */
    public void setAssignedLink(V_Link assignedLink) {
        this.assignedLinks.add(assignedLink);
    }

    /**
     * Entfernt eine zugeordnete Kante von dem Pin.
     * @param assignedLink die zu entfernende Kante
     */
    public void removeAssignedLink(V_Link assignedLink) {
        this.assignedLinks.remove(assignedLink);
    }

    /**
     * Gibt die Anzahl der dem Pin zugeordneten Kanten zurück.
     * @return Anzahl der dem Pin zugeordneten Kanten
     */
    public int getNumberOfAssignedLinks() {
        return assignedLinks.size();
    }

    /**
     * Gibt eine bestimmte dem Pin zugeordnete Kante zurück.
     * @param n Index der zurückzugebenden Kante.
     * @return zugeordnete Kante, entsprechend dem Parameter n
     */
    public V_Link getAssignedLink(int n) {
        int index = 0;
        for(int i = 0; i < assignedLinks.size(); i++) {
            if(assignedLinks.get(i).containsPin(this) == true) {
                index = i;
                i = assignedLinks.size();
            }
        }
        return assignedLinks.get(index);
    }

    /**
     * Zeichnet das Element an seiner Position.
     * 
     */
    public void paintOn(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(colSelected);

        if(orientation.equals("round") == false) {
            if(vertical == true)
                g2d.fillRect(x, y, 3, 5);
            else
                g2d.fillRect(x, y, 5, 3);
        }
        else {
            g2d.fillOval(x, y, 8, 8);
        }

    }

    /**
     * Setzt die Position des Pins.
     * 
     * @param nx die neue x-Position
     * @param ny die neue y-Position
     */
    public void setPosition(int nx, int ny) {
        x = nx;
        y = ny;
    }

    /**
     * Setzt die Position des Andockpunktes im Pin.
     * 
     * @param px die neue x-Position
     * @param py die neue y-Position
     */
    public void setPoint(int px, int py) {
        pointX = px;
        pointY = py;
    }

    /**
     * Gibt x-Wert des Andockpunktes zurück.
     *
     */
    public int getPointX() {
        return pointX;
    }

    /**
     * Gibt y-Wert des Andockpunktes zurück.
     */
    public int getPointY() {
        return pointY;
    }

    /**
     * Stellt ein, dass der Pin zukünftig beim Zeichnen hervorgehoben dargestellt wird.
     * Anwendung z.B. beim Anschließen, wenn Maus über Pin
     */
    public void setColorHighlighted() {
        colSelected = colPinHighlighted;
    }

    /**
     * Stellt ein, dass der Pin zukünftig beim Zeichnen als verklemmt dargestellt wird.
     */
    public void setColorRepressed() {
        flagInhibited = true;
        colSelected = colPinRepressed;
    }

    /**
     * Stellt ein, dass der Pin zukünftig beim Zeichnen normal (nicht hervorgehoben o.ä.) dargestellt wird.
     */
    public void setColorNormal() {
        flagInhibited = false;
        colSelected = colPinNormal;
    }

    /**
     * Testet, ob der übergebene Punkt innerhalb des Pin ist.
     * @param pnt der übergebene Punkt
     * @return true, wenn pnt innerhalb des Pins ist, false sonst.
     */
    public boolean containsPoint(Point pnt) {
        int mx = pnt.x;
        int my = pnt.y;

        if(vertical == true){
            if(mx >= (x-3) && mx <= (x+6) && my >= (y-3) && my <= (y+6))
                return true;
            else
                return false;
            }
        else {
            if(mx >= (x-5) && mx <= (x+10) && my >= (y-3) && my <= (y+3))
                return true;
            else
                return false;
        }
    }

    /**
     * Gibt die Entsprechung dieses Pins der Logic zurück.
     */
    public M_Pin getAssociatedLogicPin() {
        return associatedPin;
    }

    /**
     * Gibt die Orientierung dieses Pins zurück.
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Testet, ob Pin vertikal ist.
     * @return true, wenn Pin vertikal ist, false sonst.
     */
    public boolean isVertical() {
        return vertical;
    }
}
