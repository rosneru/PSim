package model;

/**
 * Klasse für eine Eingabestelle in der Logik.
 * @author Uwe Rosner
 *
 */
public class M_PNPInput extends M_ElementRoot implements MI_Place{

    private static final long serialVersionUID = 1L;

    float value;    // aktueller Wert
    float orgValue; // Originalwert (der, der im Konstruktor übergeben wurde)
                    // bei reset() wird wieder der Originalwert eingestellt

    // String-Array mit Angaben über die Element-Eigenschaften, es enthält:
    // Name des Elements, {Name der Eigenschaft}
    String[] propertyNames = {"Constant place", "Value"};

    // String-Array mit Angaben über die Element-Gewichte, es enthält:
    // Name des Elements, {Anschlussbezeichnung}
    String[] weightNames = {"Constant place"};

    /**
     * Erzeugt eine Eingabestelle in der Logik.
     * @param value Wert der Eingabestelle.
     */
    public M_PNPInput(float value) {
        this.value = value;
        this.orgValue = value;
        pinsOut.add(new M_Pin(this, ME_PinType.OUTPUT));
        pinsOut.get(0).setValue(value);

        elementIdent = ME_ElementIdent.PLACE_INPUT;
        elementType = ME_ElementType.PLACE;

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Setzt Element zurück.
     */
    public void reset() {
        value = orgValue;
        pinsOut.get(0).setValue(orgValue);

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Liefert den aktuellen Wert des Elements.
     * @return aktuelle Wert
     */
    public float getValue() {
        return value;
    }

    // Service (für Eigenschaften)

    /**
     * Gibt ein Array mit den Eigenschaften-Namen dieses Elements zurück
     */
    public String[] getPIdentifiers() {
        return propertyNames;
    }

    /**
     * Gibt ein Array mit den Eigenschaften des Elements zurück.
     */
    public String[] getProperties() {
        String[] propertyValues = new String[1];
        propertyValues[0] = Float.toString(value);
        return propertyValues;
    }

    /**
     * Setzt die Eigenschaften dieses Elements auf den übergebenen Wert.
     */
    public boolean setProperties(String[] properties) {
        value = new Float((properties[0])).floatValue();
        pinsOut.get(0).setValue(value);
        this.setChanged();
        this.notifyObservers();
        return true;
    }

    // Service (für Gewichte)

    /**
     * Gibt ein Array mit den Namen der Gewichte dieses Elements zurück
     */
    public String[] getWIdentifiers() {
        return weightNames;
    }

    /**
     * Gibt ein Array mit den Gewichten des Elements zurück.
     */
    public String[] getWeights() {
        return null;
    }

    /**
     * Setzt die Gewichte dieses Elements auf den übergebenen Wert.
     */
    public boolean setWeights(String[] weights) {
        return true;
    }

    // Funktionen für das Ausführen

    /**
     * Testet, ob dieses Element eine bestimmte Anzahl von Teilen enthält.
     * @param n Anzahl der Teile, die Element enthalten soll.
     * @return true, wenn Element die angegebene Anzahl von Teilen enthält, sonst false.
     */
    public boolean containsN(float n) {
        if(value >= n) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Übergibt dem Element eine bestimmte Anzahl von Teilen.
     * @param n Anzahl an Teilen, die dem Element übergeben werden.
     */
    public void putN(float n) {
        // Diese Situation wird sowieso nie auftreten.
        
    }

    /**
     * Testet, ob dieses Element eine bestimmte Anzahl von Teilen aufnehmen kann.
     * @param n Anzahl der Teile, auf deren Aufnahmefähigkeit das Element überprüft werden soll.
     * @return true, wenn Element die übergebene Anzahl von Teilen aufnehmen könnte, sonst false.
     */
    public boolean hasPlaceForN(float n) {
        // Diese Situation wird sowieso nie auftreten.
        return true;
    }

    /**
     * Entnimmt dem Element eine bestimmte Anzahl von Teilen.
     * @param n Anzahl der teile, die dem Element entnommen werden.
     */
    public void getN(float n) {
        value = value - n;
        pinsOut.get(0).setValue(value);

        // View benachrichtigen
        this.setChanged();
        this.notifyObservers();
    }

}
