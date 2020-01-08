package model;

/**
 * Interface mit Funktionsdeklarationen, die von (Logik-)Pins implementiert werden müssen.
 * @author Uwe Rosner
 *
 */
public interface MI_Pin {
    MI_Element getAssignedElement();                    // Liefert das zum Link gehörende Element
    MI_Pin getComplementPin();                          // Liefert den mit diesem Pin durch eine Kante verbundenen Pin
    ME_PinType getPinType();                            // Liefert den Typ des Pins (also: Input oder Output)

    public void setAssignedLink(MI_Link assignedLink);  // stellt die zum Pin gehörende Kante ein
    public MI_Link getAssignedLink(int n);              // Liefert die zum Pin gehörende Kante

    void markConnected();                               // Markiert den Pin als "angeschlossen"
    void tryMarkingUnconnectet();                       // markiert den Pin als "abgeklemmt", wenn er nicht etwa noch
                                                        // mit einer kante verbunden ist.

    boolean isConnected();                              // Testet, ob Pin angeschlossen ist.

    public float getValue();                            // Liefert die aktuell am Pin "anliegende" Anzahl von Teilen
    public void setValue(float value);                  // Stellt eine neue Anzahl von teilen ein, die am Pin anliegen
}
