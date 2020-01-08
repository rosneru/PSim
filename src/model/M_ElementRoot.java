package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Abstrakte Basisklasse für die Elemente (also Stellen und Transitionen) in der Logik.
 * Sie kann selbst nicht instanziiert werden, stellt aber einige Standardfunktionen zur Verfügung,
 * die für alle erbenden Klassen gleich sind und daher gut hierher passen. Die Klassen M_PNInput, M_PNOutput
 * und M_PNStorage (Stellen) sowie die Klasse für Transitionen, M_PNTransition (Transitionen), 
 * erben von dieser Klasse.
 * @author Uwe Rosner
 *
 */
public abstract class M_ElementRoot extends Observable implements MI_Element, Serializable {

    private static final long serialVersionUID = 1L;

    ME_ElementIdent elementIdent;   // Art des Elements (konkret)
    ME_ElementType elementType;     // Typ des Elements (allgemein, also Transition oder Stelle)

    ArrayList<M_Pin> pinsIn = new ArrayList<M_Pin>();   // Eingänge
    ArrayList<M_Pin> pinsOut = new ArrayList<M_Pin>();  // Ausgänge

    ArrayList<Float> wx = new ArrayList<Float>();       // Gewichte der Eingänge
    ArrayList<Float> wy = new ArrayList<Float>();       // Gewichte der Ausgänge

//    ArrayList<Boolean> rx = new ArrayList<Boolean>();     // Verklemmungsstatus der Eingänge
//    ArrayList<Boolean> ry = new ArrayList<Boolean>();     // Verklemmungsstatus der Ausgänge

    String[] weightNames;    // für die namen der Gewichte (also z.B. "Input 1", usw.)

    /**
     * gibt die Art des Elementes zurück
     */
    public ME_ElementIdent getElementIdent() {
        return elementIdent;
    }

    /**
     * gibt den Typ des Elementes zurück
     */
    public ME_ElementType getElementType() {
        return elementType;
    }

    /**
     * Testet, ob das Element teilweise angeschlossen ist.
     * @return true, wenn mindestens ein Pin des Elementes angeschlossen ist, false sonst.
     */
    public boolean isConnectedPartial() {
        for(int i = 0; i < pinsIn.size(); i++) {
            if(pinsIn.get(i).isConnected() == true) {
                return true;
            }
        }

        for(int i = 0; i < pinsOut.size(); i++) {
            if(pinsOut.get(i).isConnected() == true) {
                return true;
            }
        }

        return false;
    }

    /**
     * Testet, ob das Element komplett angeschlossen ist.
     * @return true, wenn alle Pins des Elements angeschlossen sind, false sonst.
     */
    public boolean isConnectedComplete() {
        for(int i = 0; i < pinsIn.size(); i++) {
            if(pinsIn.get(i).isConnected() == false) {
                return false;
            }
        }

        for(int i = 0; i < pinsOut.size(); i++) {
            if(pinsOut.get(i).isConnected() == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gibt ein Array mit allen Pins des Elements zurück. 
     */
    public M_Pin[] getAllPins() {
        M_Pin[] pins = new M_Pin[pinsIn.size() + pinsOut.size()];

        for(int i = 0; i < pinsIn.size(); i++) {
            pins[i] = pinsIn.get(i);
        }

        for(int i = pinsIn.size(); i < (pinsIn.size() + pinsOut.size()); i++) {
            pins[i] = pinsOut.get(i - pinsIn.size());
        }

        return pins;
    }

}
