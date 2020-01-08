package model;

import java.util.ArrayList;

/**
 * Klasse für Transitionen in der Logik.
 * @author Uwe Rosner
 *
 */
public class M_PNTransition extends M_ElementRoot implements MI_Transition {

    private static final long serialVersionUID = 1L;

    int numberIn;   // Anzahl der Eingänge
    int numberOut;  // Anzahl der Ausgänge

    boolean worked; // true, wenn Fork innerhalb eines Schrittes schon berechnet wurde
    int counter;    // zählt, wie oft Fork innerhalb eines Schrittes schon ausgewertet wurde

    // String-Array mit Angaben über die Element-Eigenschaften, es enthält:
    // Name des Elements, {Name der Eigenschaft}
    String[] propertyNames = {"Transition", "Anzahl der Eingänge", "Anzahl der Ausgänge"};

    // String-Array mit Angaben über die Element-Gewichte, es enthält:
    // Name des Elements, {Allgemeine Informationen}
    String[] weightNames;


    /**
     * Erzeugt einen neue Transition in der Logik.
     * @param numberIn Anzahl an Eingängen
     * @param numberOut Anzahl an Ausgängen
     */
    public M_PNTransition(int numberIn, int numberOut) {
        this.numberIn = numberIn;
        this.numberOut = numberOut;

        elementIdent = ME_ElementIdent.TRANSITION;
        elementType = ME_ElementType.TRANSITION;

        initTransition();

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Initialisiert die Transition. Ist bei jeder Änderung von gewichten oder
     * der Anzahl der Ein- und Ausgänge auszuführen.
     */
    private void initTransition() {
        // String-Array der Anschlussbezeichnungen aufbauen
        weightNames = new String[numberIn + numberOut + 1];

        weightNames[0] = "Transition";

        for(int i = 0; i < numberIn; i++) {
            weightNames[i+1] = "Input " + (i + 1);
        }

        for(int i = 0; i < numberOut; i++) {
            weightNames[i + numberIn + 1] = "Output " + (i + 1);
        }

        // Pins erzeugen
        pinsIn.clear();
        pinsOut.clear();

        pinsIn = new ArrayList<M_Pin>();    // Eingänge
        pinsOut = new ArrayList<M_Pin>();    // Ausgänge

        for(int i = 0; i < numberIn; i++) {
            pinsIn.add(new M_Pin(this, ME_PinType.INPUT));
            pinsIn.get(i).setValue(0);
        }

        for(int i = 0; i < numberOut; i++) {
            pinsOut.add(new M_Pin(this, ME_PinType.OUTPUT));
            pinsOut.get(i).setValue(0);
        }

        // Gewichte initialisieren

        wx = new ArrayList<Float>();    // Gewichte der Eingänge
        wy = new ArrayList<Float>();    // Gewichte der Ausgänge

        for(int i = 0; i < numberIn; i++) {
            wx.add(new Float(1.0));
        }

        for(int i = 0; i < numberOut; i++) {
            wy.add(new Float(1.0));
        }

        worked = false;
        counter = 0;
    }

    /**
     * Setzt die Transition zurück.
     */
    public void reset() {
        pinsIn.get(0).setValue(0);

        for(int i = 0; i < pinsOut.size(); i++) {
            pinsOut.get(i).setValue(0);
        }

        worked = false;
        counter = 0;

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Gibt Anzahl der Eingänge zurück.
     * @return Anzahl der Eingänge.
     */
    public int getNumberOfInputs() {
        return numberIn;
    }

    /**
     * Gibt Anzahl der Ausgänge zurück.
     * @return Anzahl der Ausgänge.
     */
    public int getNumberOfOutputs() {
        return numberOut;
    }

    /**
     * Versucht, einen Schritt auszuführen.
     * 
     */
    public void computeStep() {

        // prüfen, ob alle vorhergehenden Stellen genug Teile liefern
        for(int i=0; i<pinsIn.size(); i++) {
            MI_Place previous = (MI_Place) pinsIn.get(i).getComplementPin().getAssignedElement();

            // raus, wenn Stelle an diesem Pin nicht genug Teile liefern kann
            if( previous.containsN(wx.get(i)) == false) {
                return;
            }
        }

        // prüfen, ob alle nachfolgenden Stellen genug Teile aufnehmen können 
        for(int i=0; i<pinsOut.size(); i++) {
            MI_Place next = (MI_Place) pinsOut.get(i).getComplementPin().getAssignedElement();

            // raus, wenn Stelle an diesem Pin nicht genug Teile aufnehmen kann
            if( next.hasPlaceForN(wy.get(i)) == false) {
                return;
            }
        }

        // Ok, Feuerungsregel ist damit erfüllt. --> Feuern!

        // den an den Eingängen angeschlossenen Stellen Teile (entsprechend den Gewichten) entziehen
        for(int i=0; i<pinsIn.size(); i++) {
            MI_Place previous = (MI_Place) pinsIn.get(i).getComplementPin().getAssignedElement();
            previous.getN(wx.get(i));
        }

        // den an den Ausgängen angeschlossenen Stellen Teile (entsprechend den Gewichten) hinzufügen
        for(int i=0; i<pinsOut.size(); i++) {
            MI_Place next = (MI_Place) pinsOut.get(i).getComplementPin().getAssignedElement();
            next.putN(wy.get(i));
        }

        // Erfolgreiches Berechnen vermerken
        worked = true;

        // View benachrichtigen
        this.setChanged();
        this.notifyObservers();

        return;
    }

    /**
     * Testet, ob die Transition innerhalb des aktuellen Schrittes  arbeiten konnte.
     * <b>ACHTUNG:</b> Im positiven Fall wird das ensprechende interne Flag gelöscht,
     * so dass eine erneute Anfrage (innerhalb desselben Schrittes) kein positives Ergebnis mehr bringen würde.
     * @return true, wenn Transition innerhalb dieses Schrittes arbeiten konnte, false sonst.
     */
    public boolean hasWorked_KillFlag() {
        if(worked == true) {
            worked = false;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Testet, ob die Transition innerhalb des aktuellen Schrittes arbeiten konnte.
     * @return true, wenn Transition innerhalb dieses Schrittes arbeiten konnte, false sonst.
     */
    public boolean hasWorked_NotKillFlag() {
        if(worked == true) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Gibt ein Array mit den Namen der Eigenschaften zurück.
     */
    public String[] getPIdentifiers() {
        return propertyNames;
    }

    /**
     * Setzt die Eigenschaften entsprechend den Werten im übergebenen Array.
     */
    public boolean setProperties(String[] properties) {
        if(new Integer(properties[0]) < 1) {
            return false;
        }

        numberIn = new Integer((properties[0]));
        numberOut = new Integer((properties[1]));

        initTransition();

        this.setChanged();
        this.notifyObservers();
        return true;
    }

    /**
     * Gibt ein Array mit den Eigenschaften zurück.
     */
    public String[] getProperties() {
        String[] propertyValues = new String[propertyNames.length - 1];
        propertyValues[0] = Integer.toString(numberIn);
        propertyValues[1] = Integer.toString(numberOut);
        return propertyValues;
    }

    // Service (für Gewichte)

    /**
     * Gibt ein Array mit den Namen der Eigenschaften zurück.
     */
    public String[] getWIdentifiers() {
        return weightNames;
    }

    /**
     * Gibt ein Array mit den Gewichten zurück.
     */
    public String[] getWeights() {
        String[] weights = new String[(numberIn + numberOut)];

        for(int i = 0; i < wx.size(); i++) {
            weights[i] = wx.get(i).toString();
        }

        for(int i = 0; i < wy.size(); i++) {
            weights[i + wx.size()] = wy.get(i).toString();
        }

        return weights;
    }

    /**
     * Setzt die Gewichte entsprechend den Werten im übergebenen Array
     */
    public boolean setWeights(String[] weights) {
        for(int i = 0; i < wx.size(); i++) {
            wx.set(i, Float.parseFloat(weights[i]));
        }

        for(int i = 0; i < wy.size(); i++) {
            wy.set(i, Float.parseFloat(weights[i+wx.size()]));
        }

        return true;
    }
}
