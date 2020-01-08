package model;

/**
 * Interface mit Funktionsdeklarationen, die von (Logik-)Transitionen implementiert werden müssen.
 * @author Uwe Rosner
 *
 */
public interface MI_Transition {
    void computeStep();                     // die Transition soll versuchen, einen Schritt auszuführen

    public boolean hasWorked_NotKillFlag(); // Testet, ob Transition innerhalb des letzten Schrittes arbeiten
                                            // konnte, belässt das zugehörige Flag

    public boolean hasWorked_KillFlag();    // Testet, ob Transition innerhalb des letzten Schrittes arbeiten
                                            // konnte, LOESCHT das zugehörige Flag
}
