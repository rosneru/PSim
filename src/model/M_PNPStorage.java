package model;

/**
 * Klasse für eine Allgemeine Stelle in der Logik.
 * @author Uwe Rosner
 *
 */
public class M_PNPStorage extends M_ElementRoot implements MI_Place {

    private static final long serialVersionUID = 1L;

    float capacity; // Kapazität
    float value;    // aktueller Wert

    float orgCapacity;  // Originalwert (der, der im Konstruktor übergeben wurde)
                        // bei reset() wird wieder der Originalwert eingestellt

    // String-Array mit Angaben über die Element-Eigenschaften, es enthält:
    // Name des Elements, {Name der Eigenschaft}
    String[] propertyNames = {"Lagerstelle", "Aktueller Wert", "Kapazität"};

    // String-Array mit Angaben über die Element-Gewichte, es enthält:
    // Name des Elements, {Anschlussbezeichnung}
    String[] weightNames = {"Lagerstelle"};

    /**
     * Erzeugt eine Allgemeine Stelle in der Logik.
     * @param capacity Kapazität der Allgemeinen Stelle.
     */
    public M_PNPStorage(float capacity) {
        this.capacity = capacity;
        this.orgCapacity = capacity;

        // Eingang mit 0 initialisieren
        pinsIn.add(new M_Pin(this, ME_PinType.INPUT));
        pinsIn.get(0).setValue(0);

        // Ausgang mit 0 initialisieren
        pinsOut.add(new M_Pin(this, ME_PinType.OUTPUT));
        pinsOut.get(0).setValue(0);

        elementIdent = ME_ElementIdent.PLACE_STORAGE;
        elementType = ME_ElementType.PLACE;

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
        pinsOut.get(0).setValue(0);
        value = 0;
        capacity = orgCapacity;

        // View benachrichtigen
        this.setChanged();
        this.notifyObservers();
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
     * Übergibt dem Element eine bestimmte Anzahl von Teilen.
     * @param n Anzahl an Teilen, die dem Element übergeben werden.
     */
    public void putN(float n) {
        value = value + n;

        // View benachrichtigen
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Entnimmt dem Element eine bestimmte Anzahl von Teilen.
     * @param n Anzahl der teile, die dem Element entnommen werden.
     */
    public void getN(float n) {
        value = value - n;

        // View benachrichtigen
        this.setChanged();
        this.notifyObservers();
    }
}
