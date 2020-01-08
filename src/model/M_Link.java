package model;

import java.io.Serializable;

/**
 * Klasse für Kanten in der Logik. Kanten dienen zum Verbinden von Elementen,
 * genauer: zum Verbinden der Anschlüsse (Pins) von Elementen.
 * @author Uwe Rosner
 *
 */
public class M_Link implements MI_Link, Serializable {

    private static final long serialVersionUID = 1L;

    private M_Pin source;       // Quell-Pin
    private M_Pin destination;  // Ziel-Pin

    /**
     * Schließt die Kante an, d.h., stellt zugehörigen Quell- und Zielpin ein. Das Anschließen scheitert,
     * wenn Quellpin kein Ausgangspin und Zielpin kein Eingangspin ist. 
     * @param source Quellpin
     * @param destination Zielpin
     * @return true, wenn Anschließen erfolgreich, sonst false.
     */
    public boolean setLink(M_Pin source, M_Pin destination) {
        if(source.getPinType() == ME_PinType.OUTPUT && destination.getPinType() == ME_PinType.INPUT) {
            this.source = source;
            this.destination = destination;
            source.setAssignedLink(this);
            source.markConnected();
            destination.setAssignedLink(this);
            destination.markConnected();

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Löst die Kante von ihren Pins, d.h., die Kante wird abgeklemmt.
     */
    public void unsetLink() {
        source.removeAssignedLink(this);
        source.tryMarkingUnconnectet();
        source = null;

        destination.removeAssignedLink(this);
        destination.tryMarkingUnconnectet();
        destination = null;
    }

    /**
     * Gibt den Zielpin der Kante zurück.
     */
    public MI_Pin getDestinationPin() {
        return source;
    }

    /**
     * Gibt den Quellpin der kante zurück.
     */
    public MI_Pin getSourcePin() {
        return destination;
    }

}
