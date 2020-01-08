package model;

/**
 * Interface mit Funktionsdeklarationen, die von (Logik-)Kanten implementiert werden müssen.
 * @author Uwe Rosner
 *
 */
public interface MI_Link {
    public boolean setLink(M_Pin source, M_Pin destination);    // stellt die zur Kante gehörigen Pins ein, d.h.,
                                                                // die Kante wird angeschlossen oder umgeklemmt

    public void unsetLink();                                    // Löst die Kante von ihren Pins, d.h., klemmt die Kante ab

    MI_Pin getSourcePin();      // Liefert Quellpin der Kante
    MI_Pin getDestinationPin(); // Liefert Zielpin der Kante
}
