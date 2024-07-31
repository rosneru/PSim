package view;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import model.MI_Element;
import model.M_PNTransition;


/**
 * Klasse für Transitionen in der View. Abgeleitet von V_ElementRoot.
 * Ist mit der zugehörigen Transition in der Logik verknüpft und
 * beobachtet (implements Observer) diese.
 * @author Uwe Rosner
 *
 */
public class V_PNTransition extends V_ElementRoot implements Observer, Runnable {

    private static final long serialVersionUID = 1L;

    int numberIn;    // Anzahl der Eingänge
    int numberOut;    // Anzahl der Ausgänge
    int portion;    // Abstand der Anschlüsse (in Pixeln)

    M_PNTransition model;

    // Zum Blinken-Lassen der Transition wird folgendes gebraucht:
    ActionListener act;
    transient Thread flashThread;   // `transient` bedeutet: der Thread 
                                    // soll nicht mit gespeichert
                                    // (serialisiert) werden. Denn das
                                    // würde eine Exception auslösen, da
                                    // ein Thread die Klasse
                                    // Serializable nicht implementiert.

    /**
     * Erzeugt eine neue Transition in der View. Als besonderer
     * Parameter taucht hier ein ActionListener auf, der beim
     * threadgesteuerten Blinken der Transition (wenn sie erfolgreich
     * gerechnet hat) aufgerufen wird, um die Anzeige für jeden
     * (Blink-)Schritt aktualisieren zu lassen.
     * @param cx x - Koordinate, an der das Element platziert werden
     * soll.
     * @param cy y - Koordinate, an der das Element platziert werden
     * soll.
     * @param model - Logik-Represäntation des zu erzeugenden
     * View-Elementes.
     * @param act ActionListener, dem die Nachricht zum Neuzeichnen der
     * Anzeige (View) gesendet werden kann.
     */
    public V_PNTransition(int cx, int cy, Object model, ActionListener act) {
        flashThread = null;
        rx=64; ry=32;

        this.act = act;

        // Modell initialisieren
        this.model = (M_PNTransition)model;
        this.model.addObserver(this);

        x = cx;            // x-Position setzen
        y = cy;            // y-Position setzen

        // Anzahl der Pins einstellen, davon abhängig Elementebreite festlegen
        initTransition();

        // Mittelpunktskoordinaten midX und midY neu berechnen
        calcMidPoint();
    }

    /**
     * Initialisiert die Transition.
     */
    private void initTransition() {

        // Elementbreite mittels Multiplikator w entsprechend Anschlussanzahl einstellen.
        w = (Math.max(numberIn, numberOut) + 2) / 3;

        // horizontalen Abstand der Anschlüsse festlegen
        portion = 20;

        // Falls sich im Modell Anzahl der Ein- oder Ausgänge geändert hat
        if(numberIn != model.getNumberOfInputs() || numberOut != model.getNumberOfOutputs()) {

            numberIn = model.getNumberOfInputs();    // Anzahl der Eingänge setzen
            numberOut = model.getNumberOfOutputs();    // Anzahl der Ausgänge setzen
    
            // Elementbreite mittels Multiplikator w entsprechend Anschlussanzahl einstellen.
            w = (Math.max(numberIn, numberOut) + 2) / 3;
    
            // Anschluss-Objekte anlegen
            pins.clear();
            pins = new ArrayList<V_Pin>();
    
            for (int i = 0; i < numberIn; i++) {
                pins.add(new V_Pin("top", this.model.getAllPins()[i]));
            }
    
            for (int i = 0; i < numberOut; i++) {
                pins.add(new V_Pin("down", this.model.getAllPins()[numberIn + i]));
            }

        }

    }

    /**
     * true, wenn übergebener Punkt innerhalb des Elements ist
     */
    public boolean containsPoint(Point pnt){
    /*
     * Gibt true zurück, wenn Element (x, y, w, h) den Punkt pnt enthält
     * (hier: true, wenn in Element geklickt wurde.)
     */
        if(pnt.x>=x && pnt.x<=x+w*rx && pnt.y>=y && pnt.y<=y+h*ry) {
            // pnt liegt innerhalb Element
            xdiff = pnt.x-x;    // Mausposition innerhalb des Elements beibehalten 
            ydiff = pnt.y-y;    // ---------------------- " ----------------------
            return true;
        }
        else {
            // pnt liegt nicht innerhalb Element
            xdiff = 0;
            ydiff = 0;
            return false;
        }
    }

    /**
     * Setzt die Position des Elements auf einen neuen Wert.
     */
    public void setPosition(Point pos){
        if(xrast > 0) {
            if ((pos.x-xdiff) % xrast == 0) {
                x = pos.x-xdiff;
            }
        }
        else {
            x = pos.x-xdiff;
        }

        if(yrast > 0) {
            if((pos.y-ydiff) % yrast == 0) {
                y =    pos.y-ydiff;
            }
        }
        else {
            y =    pos.y-ydiff;
        }

        calcMidPoint();
    }

    /**
     * Zeichnet das Element an seiner Position.
     */
    public void paintOn(Graphics g){
        /*
         * Zeichnet das Element an seiner Position.
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(colSelected); // Farbe für Beschriftung / Symbol / Rahmen

        int startXin = midX - portion * (numberIn - 1) / 2 - 1;
        int startXout = midX - portion * (numberOut - 1) / 2 - 1;

        g2d.drawRect(x, y, (int)(w * rx), (int)(h * ry));        // Rahmen

        // Anschlüsse zeichnen
        // (D.h., die bereits im Konstruktor angelegten Pins positionieren)
        g2d.setColor(colPinNormal); // Farbe für Anschlüsse
        V_Pin d;

        // oben
        for(int i=1; i<=numberIn; i++) {
            d = (V_Pin)pins.get(i-1);
            d.setPosition((startXin + (i-1) * portion) - 2, y);
            d.setPoint(startXin + (i-1) * portion, y);
            d.paintOn(g);
        }

        // unten
        for(int i=1; i<=numberOut; i++) {
            d = (V_Pin)pins.get(i + numberIn - 1);
            d.setPosition((startXout + (i-1) * portion) - 2, (int)(y + h * ry - 2));
            d.setPoint(startXout + (i-1) * portion, (int)(y + h * ry));
            d.paintOn(g);
        }
    }

    /**
     * Berechnet die Mittelpunktskoordinaten des Elements.
     */
    protected void calcMidPoint() {
        midX = (int)(x + w * rx / 2);
        midY = (int)(y + h * ry / 2);
    }

    /**
     * Gibt das Logik-Pendant dieses Elements zurück.
     */
    public MI_Element getElementLogic() {
        return model;
    }

    /**
     * Weist das Logik-Pendant dieser Stelle, sich von diesem (this) Element beobachten zu lassen.
     */
    public void reloadObverver() {
        this.model.addObserver(this);
    }

    /**
     * Wird aufgerufen, wenn sich im Logik-Pendat dieses Elementes etwas geändert hat.
     * Übernimmt diese Änderungen in die View.
     */
    public void update(Observable arg0, Object arg1) {
        if(model.hasWorked_NotKillFlag() == true) {
            // Transition hat einen Schritt gemacht --> aufblinken lassen

            // aber erst testen, ob sie nicht vielleicht sogar noch blinkt (vom vorherigen Aufruf)
            if(flashThread != null) {
                // Es existiert schon ein Thread
                try {
                    // Warten bis der beendet ist.
                    flashThread.join();
                } catch (InterruptedException e) {
                    System.out.println("Fehler bei flashThread.join in V_PNTransition.update(...). Grund:\n" + e);
                }                
            }

            flashThread = new Thread(this);
            flashThread.start();
    
        }
        else {
            // Eigenschaften der Transition wurden verändert --> neu initialisieren
            initTransition();
            calcMidPoint();
        }
    }

    /** 
     * Thread zum Blinken-Lassen der Transition.
     */
    public void run() {
        Color colBackup = new Color(colSelected.getRed(), colSelected.getGreen(),colSelected.getBlue());
        Color destCol = new Color(200, 75, 50);

        int steps = 10;

        int diffRed = (destCol.getRed() - colSelected.getRed()) / 10;
        int diffGreen = (destCol.getGreen() - colSelected.getGreen()) / 10;
        int diffBlue = (destCol.getBlue() - colSelected.getBlue()) / 10;

        int i = 0;

        // 10 Schritte hin zur Zielfarbe
        do {
            i++;
            colSelected = new Color(colSelected.getRed() + diffRed, colSelected.getGreen() + diffGreen, colSelected.getBlue() + diffBlue);

            // Anweisung zum Neuzeichnen der drawing_area geben
            ActionEvent ev = new ActionEvent(this, 1, "Stmt_Repaint");
            act.actionPerformed(ev);

            try {
                Thread.sleep(16, 667);
            }
            catch(Exception e) {
                System.out.println("Fehler bei Thread.sleep() in V_PNTransition.run().");
            }
        }
        while(i < steps+1);

        i = 0;

        // 10 Schritte zurück zur Ausgangsfarbe
        do {
            i++;
            colSelected = new Color(colSelected.getRed() - diffRed, colSelected.getGreen() - diffGreen, colSelected.getBlue() - diffBlue);

            // Anweisung zum Neuzeichnen der drawing_area geben
            ActionEvent ev = new ActionEvent(this, 1, "Stmt_Repaint");
            act.actionPerformed(ev);

            try {
                Thread.sleep(16, 667);
            }
            catch(Exception e) {
                System.out.println("Fehler bei Thread.sleep() in V_PNTransition.run().");
            }
        }
        while(i < steps+1);

        colSelected = new Color(colBackup.getRed(), colBackup.getGreen(),colBackup.getBlue());
    }

    /**
     * Weist dieser Transition einen ActionListener zu.
     * @param act zu verwendender ActionListener.
     */
    public void setActionListener(ActionListener act) {
        this.act = act;
    }

}
