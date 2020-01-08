package view;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;

import model.MI_Element;
import model.M_PNPInput;

/**
 * Klasse für Eingabestellen in der View. Abgeleitet von V_PNPlace. Ist mit der zugehörigen Eingabestelle in der 
 * Logik verknüpft und beobachtet (implements Observer) diese.
 * @author Uwe Rosner
 *
 */
public class V_PNPInput extends V_PNPlace implements Observer {

    private static final long serialVersionUID = 1L;

    // Der Wert der Eingabestelle
    float value;

    // Das Logik-Pendat dieser Eingabestelle
    private M_PNPInput model;

    /**
     * Erzeugt eine neue Eingabestelle in der View.
     * @param cx x - Koordinate, an der das Element platziert werden soll.
     * @param cy y - Koordinate, an der das Element platziert werden soll.
     * @param model - Logik-Represäntation des zu erzeugenden View-Elementes.
     */
    public V_PNPInput(int cx, int cy, Object model) {
        // Modell initialisieren
        this.model = (M_PNPInput) model;
        this.model.addObserver(this);

        value = this.model.getValue();
        x=cx; y=cy;        // Position setzen

        // Anschluss-Objekte anlegen
        pins.add(new V_Pin("down", this.model.getAllPins()[0]));

        calcMidPoint();    // Mittelpunktskoordinaten (midX, midY) berechnen
    }

    /**
     * Zeichnet das Element an seiner Position.
     */
    public void paintOn(Graphics g){

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2d.getFontMetrics();
        int fw, fh, asc;                              // Breite und -höhe der Beschriftung und Ascent
        fw = fm.stringWidth(Float.toString(value));   // Breite der Beschriftung

        // Größenanpassung entsprechend Schriftbreite
        while(fw >= w*rx) {
            w += 0.5f;
            h += 0.5f;
            calcMidPoint();
        }

        fh = fm.getHeight();                          // Höhe der beschriftung (nur vom Font abhängig)
        asc = fm.getAscent();                         // Abstand von Grundlinie bis Obergrenze des Fonts
        g2d.setColor(colSelected);
        g2d.drawOval(x, y, (int)(w*rx), (int)(h*ry));
        g2d.drawString(Float.toString(value), midX - fw / 2, midY - fh / 2 + asc);

        // Anschlüsse zeichnen
        // (D.h., die bereits im Konstruktor angelegten Pins positionieren)

        V_Pin d;

        // unten
        d = (V_Pin) pins.get(0);
        d.setPosition(midX - 2, (int)(y + h * ry - 2));
        d.setPoint(midX, (int)(y + h * ry));
        d.paintOn(g);
    }

    /**
     * Gibt das Logik-Pendant dieses Elements zurück.
     */
    public MI_Element getElementLogic() {
        return model;
    }

    /**
     * Weist das Pendant dieser Stelle in der Logik an, sich von diesem Element beobachten zu lassen.
     */
    public void reloadObverver() {
        this.model.addObserver(this);
    }

    /**
     * Wird aufgerufen, wenn sich im Logik-Pendat dieses Elementes etwas geändert hat.
     * Übernimmt diese Änderungen in die View.
     */
    public void update(Observable arg0, Object arg1) {
        // Elementgröße zurücksetzen
        w = 1;
        h = 1;
        calcMidPoint();

        // aktuellen Wert übernehmen
        value = model.getValue();
    }
}
