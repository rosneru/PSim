package model;

/**
 * Interface mit Funktionsdeklarationen, die von (Logik-)Stellen implementiert werden müssen.
 * @author Uwe Rosner
 *
 */
public interface MI_Place {
    public boolean containsN(float n);    // true, wenn die Stelle n Teile abgeben kann
    public boolean hasPlaceForN(float n); // true, wenn die Stelle n Teile aufnehmen kann
    public void getN(float n);            // der Stelle n Teile entziehen
    public void putN(float n);            // der Stelle n Teile übergeben
}
