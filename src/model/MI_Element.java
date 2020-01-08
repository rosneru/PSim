package model;

/**
 * Interface mit Funktionsdeklarationen, die alle (Logik-)Elemente implementieren müssen.
 * @author Uwe Rosner
 *
 */
public interface MI_Element {
        // Berechnen
        void reset();                               // setzt die Berechnung zurück

        boolean isConnectedPartial();               // true, wenn Element zum Teil angeschlossen ist
        boolean isConnectedComplete();              // true, wenn Element vollständig angeschlossen ist

        // Service (für Eigenschaften-Fenster)
        String[] getPIdentifiers();                 // gibt die Namen der Eigenschaften des Elementes zurück (Index 0 = Elementname)
        String[] getProperties();                   // gibt die Werte der Eigenschaften zurück
        boolean setProperties(String[] properties); // setzt die Elementeigenschaften entsprechend den übergebenen Werten

        // Service (für Gewichte-Fenster)
        String[] getWIdentifiers();                 // Gibt die namen der Gewichte des Elements zurück
        String[] getWeights();                      // Gibt die Gewichte des Elements zurück
        boolean setWeights(String[] weights);       // setzt die Gewichte des Elements entsprechend den übergebenen Werten

        // Service (wird zur Steuerung benötigt)
        ME_ElementIdent getElementIdent();          // gibt Enum-Wert zur Elementidentifikation zurück (also Transition, Eingabestelle, ...)
        ME_ElementType getElementType();            // gibt den Elementtyp per Enum zurück (also: Transition oder Stelle)

        public M_Pin[] getAllPins();                // Gibt ein Array aller Pins des Elements zurück.

}
