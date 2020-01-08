package model;

import java.io.Serializable;
import java.util.Vector;

/**
 * Klasse für Pins (Anschlüsse) in der Logik. Pins gehören zu Elementen und dienen dazu,
 * diese mittels Kanten (Links) zu verbinden. Weiterhin haben Pins einen Wert, der die Menge
 * an Teilen, die an dem entsprechenden Pin "anliegt" repräsentiert.
 * @author Uwe Rosner
 *
 */
public class M_Pin implements MI_Pin, Serializable {

    private static final long serialVersionUID = 1L;

    float value;
    ME_PinType type;

    boolean connected;

    MI_Element assignedElement;
    Vector<MI_Link> assignedLinks;

    /**
     * Erzeugt einen neuen Anschluss
     * @param assignedElement Element, zu dem der Anschluss gehören soll
     * @param type Anschlusstyp (Eingang / Ausgang)
     */
    public M_Pin(MI_Element assignedElement, ME_PinType type) {
        this.type = type;
        this.assignedElement = assignedElement;
        this.connected = false;
        value = 0;
        assignedLinks = new Vector<MI_Link>();
    }

    /**
     * Ordnet dem Anschluss eine Kante zu. Mit dieser Methode können
     * einem Anschluss mehrere Kanten zugeordnet werden.
     * @param assignedLink Kante, die dem Anschluss hinzugefügt werden soll
     */
    public void setAssignedLink(MI_Link assignedLink) {
        this.assignedLinks.add(assignedLink);
    }

    /**
     * Die übergebene Kante wird von dem Anschluss entfernt.
     * @param link zu entfernende Kante
     */
    public void removeAssignedLink(M_Link link) {
        assignedLinks.remove(link);
    }

    /**
     * Liefert die dem Anschluss zugeordnete Kante entsprechend dem übergebenen index.
     */
    public MI_Link getAssignedLink(int n) {
        return assignedLinks.get(n);
    }

    /**
     * Gibt den aktuellen Wert des Anschlusses aus.
     * @return aktueller Wert des Anschlusses
     */
    public float getValue() {
        return value;
    }

    /**
     * Stellt den Wert des Anschlusses ein.
     * @param value neuer Wert des Anschlusses
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Gibt das Element zurück, zu dem der Anschluss gehört.
     */
    public MI_Element getAssignedElement() {
        return assignedElement;
    }

    /**
     * Liefert den Anschluss-Typ dieses Anschlusses.
     */
    public ME_PinType getPinType() {
        return type;
    }

    /**
     * Liefert denjenigen Anschluss, der durch diesen Anschluss mit einer Kante verbunden ist.
     */
    public MI_Pin getComplementPin() {
        if(assignedLinks.get(0).getSourcePin().getPinType() == type) {
            return assignedLinks.get(0).getDestinationPin();
        }
        else {

            return assignedLinks.get(0).getSourcePin();
        }
    }

    /**
     * Testet, ob dieser Anschluss mit einer mindestens Kante verbunden ist.
     * @return treu, wenn Anschluss verbunden ist, false sonst.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Markiert diesen Anschluss als "angeschlossen".
     */
    public void markConnected() {
        connected = true;
        
    }

    /**
     * Markiert diesen Anschluss, wenn er wirklich mit keiner Kante mehr verbunden ist, als
     * "unangeschlossen".
     */
    public void tryMarkingUnconnectet() {
        if(assignedLinks.size() == 0) {
            connected = false;
        }
    }

}
