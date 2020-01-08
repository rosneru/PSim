package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Klasse für das View-Element "Punkt".
 * @author Uwe Rosner
 *
 */
public class V_Point extends V_PNPlace implements VI_Element {

    private static final long serialVersionUID = 1L;

    V_Link assignedLink;

    /**
     * Erzeugt einen neuen Punkt.
     * @param x x-Position des zu erzeugenden Punktes
     * @param y y-Position des zu erzeugenden Punktes
     * @param assigedLink (View-)Kante, zu der der Punkt gehört
     */
    public V_Point(int x, int y, V_Link assigedLink) {
        rx=8; ry=8;
        
        this.x = x; this.y = y;        // Position setzen
        this.assignedLink = assigedLink;

        // Anschluss-Objekte anlegen
        pins.add(new V_Pin("round", null));

        calcMidPoint();

    }

    /**
     * true, wenn das Element ein Punkt (Instanz der Klasse V_Point) ist.
     */
    public boolean isPoint() {
        return true;
    }

    /**
     * Gibt die zugehörige (View-)Kante des Links zurück.
     * @return Kante, zu der der Punkt gehört.
     */
    public V_Link getAssignedLink() {
        return assignedLink;
    }

    /**
     * Ordnet dem Punkt eine Kante zu.
     * @param assignedLink (View-)Kante, der der Punkt zugeordnet werden soll.
     */
    public void setAssignedLink(V_Link assignedLink) {
        this.assignedLink = assignedLink;
    }

    /**
     * Zeichnet das Element an seiner Position.
     */
    public void paintOn(Graphics g){

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Farbe für Anschlüsse und Anschlussbeschriftung
        g2d.setColor(colPinNormal);

        // Anschluss zeichnen
        V_Pin d;

        d = (V_Pin) pins.get(0);
        d.setPosition(midX - 4, midY - 4);
        d.setPoint(midX, midY);
        d.paintOn(g2d);
    }
}
