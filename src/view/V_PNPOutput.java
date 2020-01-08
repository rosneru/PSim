package view;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;

import model.MI_Element;
import model.M_PNPOutput;

/**
 * Klasse für Ausgabestellen in der View. Abgeleitet von V_PNPlace. Ist mit der zugehörigen Ausgabestelle in der 
 * Logik verknüpft und beobachtet (implements Observer) diese.
 * @author Uwe Rosner
 *
 */
public class V_PNPOutput extends V_PNPlace implements Observer{


    private static final long serialVersionUID = 1L;

    float capacity;        // Kapazität
    float value;        // aktueller Wert

    // Das Logik-Pendat dieser Ausgabestelle
    private M_PNPOutput model;

    /**
     * Erzeugt eine neue Ausgabestelle in der View.
     * @param cx x - Koordinate, an der das Element platziert werden soll.
     * @param cy y - Koordinate, an der das Element platziert werden soll.
     * @param model - Logik-Represäntation des zu erzeugenden View-Elementes.
     */
    public V_PNPOutput(int cx, int cy, Object model) {
        // Modell initialisieren
        this.model = (M_PNPOutput) model;
        this.model.addObserver(this);

        this.value = this.model.getValue();
        this.capacity = this.model.getCapacity();

        x=cx; y=cy;       // Position setzen

        // Anschluss-Objekte anlegen
        pins.add(new V_Pin("top", this.model.getAllPins()[0]));


        w = 1.5f;         // Skalierung der Elementbreite einestellen
        h = 1.5f;         // Skalierung der Elementhöhe einstellen

        calcMidPoint();   // Mittelpunktskoordinaten (midX, midY) berechnen

    }

    /**
     * Zeichnet das Element an seiner Position.
     */
    public void paintOn(Graphics g){

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2d.getFontMetrics();
        int fw1, fw2, fh, asc;                          // Breite und Höhe der Beschriftung und Ascent
        fw1 = fm.stringWidth(Float.toString(value));    // Breite der 'value' - Beschriftung
        fw2 = fm.stringWidth(Float.toString(capacity)); // Breite der 'capacity' - Beschriftung

        // Größenanpassung entsprechend Schriftbreite
        while((fw1 >= w*rx - rx/2) || (fw2 >= w*rx - rx/2)) {
            w += 0.5f;
            h += 0.5f;
            calcMidPoint();
        }

        fh = fm.getHeight();                            // Höhe der beschriftung (nur vom Font abhängig)
        asc = fm.getAscent();                           // Abstand von Grundlinie bis Obergrenze des Fonts
        g2d.setColor(colSelected);
        g2d.drawOval(x, y, (int)(w*rx), (int)(h*ry));
        g2d.drawLine(x + 4, midY, (int)(x + w * rx - 4), midY);
        g2d.drawString(Float.toString(value), midX - fw1 / 2, (midY - (midY - y) / 2) - fh / 2 + asc + 2);
        g2d.drawString(Float.toString(capacity), midX - fw2 / 2, (midY + (midY - y) / 2) - fh / 2 + asc - 2);

        // Anschluss zeichnen
        // (D.h., den bereits im Konstruktor angelegten Pin positionieren)

        V_Pin d;

        // oben
        d = (V_Pin) pins.get(0);
        d.setPosition(midX - 2, y);
        d.setPoint(midX, y);
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
        w = 1.5f;
        h = 1.5f;
        calcMidPoint();

        // Werte übernehmen
        value = model.getValue();
        capacity = model.getCapacity();
    }
}
