package model;

/**
 * Klasse für eine Ausgabestelle in der Logik.
 * @author Uwe Rosner
 *
 */
public class M_PNPOutput extends M_ElementRoot implements MI_Place {

    private static final long serialVersionUID = 1L;

    float capacity;     // Kapazität
    float value;        // Wert

    float orgCapacity;  // Originalwert (der, der im Konstruktor übergeben wurde)
                        // bei reset() wird wieder der Originalwert eingestellt

    boolean charged;    // zum Merken, ob sich Wert dieser Stelle innerhalb eines Schrittes verändert hat

    // String-Array mit Angaben über die Element-Eigenschaften, es enthält:
    // Name des Elements, {Name der Eigenschaft}
    String[] propertyNames = {"Ausgabestelle", "Aktueller Wert", "Kapazität"};

    // String-Array mit Angaben über die Element-Gewichte, es enthält:
    // Name des Elements, {Anschlussbezeichnung}
    String[] weightNames = {"Ausgabestelle"};

    /**
     * Erzeugt eine Ausgabestelle in der Logik.
     * @param capacity Kapazität der Ausgabestelle.
     */
    public M_PNPOutput(float capacity) {
        this.capacity = capacity;
        this.orgCapacity = capacity;

        // Eingang mit 0 initialisieren
        pinsIn.add(new M_Pin(this, ME_PinType.INPUT));
        pinsIn.get(0).setValue(0);

        elementIdent = ME_ElementIdent.PLACE_OUTPUT;
        elementType = ME_ElementType.PLACE;

        charged = false;

        this.value = 0;

        // View benachrichtigen
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Setzt Element zurück.
     */
    public void reset() {
        pinsIn.get(0).setValue(0);
        value = 0;
        capacity = orgCapacity;

        charged = false;

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Speziellle Reset-Funktion, die bei der Ausgabestelle nach jedem Schritt ausgeführt werden muss.
     */
    public void resetAfterStep() {
        charged = false;
    }

    /**
     * Gibt den aktuellen Wert des Elements zurück.
     * @return aktueller Wert
     */
    public float getValue() {
        return value;
    }

    /**
     * Gibt die Kapazität des Elements zurück.
     * @return Kapazität
     */
    public float getCapacity() {
        return capacity;
    }

    // Service (für Eigenschaften)

    /**
     * Gibt ein Array mit den Eigenschaften-Namen dieses Elements zurück
     */
    public String[] getPIdentifiers() {
        return propertyNames;
    }

    /**
     * Setzt die Eigenschaften dieses Elements auf den übergebenen Wert.
     */
    public boolean setProperties(String[] properties) {
        value = new Float((properties[0]));
        capacity = new Float((properties[1]));
        this.setChanged();
        this.notifyObservers();
        return true;
    }

    /**
     * Gibt ein Array mit den Eigenschaften des Elements zurück.
     */
    public String[] getProperties() {
        String[] propertyValues = new String[2];
        propertyValues[0] = Float.toString(value);
        propertyValues[1] = Float.toString(capacity);
        return propertyValues;
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

    // Service (für Berechnung)

    /**
     * Testet, ob der Wert der Ausgabestelle sich innerhalb des aktuellen Schrittes
     * verändert hat.
     * @return treu, wenn Wert sich verändert hat, sonst false.
     */
    public boolean hasChanged() {
        if(charged == true) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Testet, ob dieses Element eine bestimmte Anzahl von Teilen enthält.
     * @param n Anzahl der Teile, die Element enthalten soll.
     * @return true, wenn Element die angegebene Anzahl von Teilen enthält, sonst false.
     */
    public boolean containsN(float n) {
        // Dieser Fall tritt sowieso nie ein.
        return true;
    }

    /**
     * Übergibt dem Element eine bestimmte Anzahl von Teilen.
     * @param n Anzahl an Teilen, die dem Element übergeben werden.
     */
    public void putN(float n) {
        // Ausgabe-Wert setzen (n zum vorherigen Wert addieren)

        value = value + n;

        // Markieren, dass Teile ankamen.
        charged = true;

        // View benachrichtigen
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Testet, ob dieses Element eine bestimmte Anzahl von Teilen aufnehmen kann.
     * @param n Anzahl der Teile, auf deren Aufnahmefähigkeit das Element überprüft werden soll.
     * @return true, wenn Element die übergebene Anzahl von Teilen aufnehmen könnte, sonst false.
     */
    public boolean hasPlaceForN(float n) {
        if( (capacity - value) >= n ) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Entnimmt dem Element eine bestimmte Anzahl von Teilen.
     * @param n Anzahl der teile, die dem Element entnommen werden.
     */
    public void getN(float n) {
        // Dieser Fall tritt sowieso nie ein.
    }

}
