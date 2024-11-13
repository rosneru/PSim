package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

import model.ME_ElementType;
import model.MI_Element;
import model.MI_Link;
import model.MI_Transition;
import model.M_ElementRoot;
import model.M_Link;
import model.M_PNPInput;
import model.M_PNPOutput;
import model.M_PNPStorage;
import model.M_PNTransition;
import model.M_Pin;


/**
 * Klasse für die zentrale Logikverwaltung. Hier werden dem Netz
 * Elemente, Kanten und Pins hinzugefügt (oder geladen) und
 * Tests durchgeführt. Weiterhin steht mit der Methode makeStep() das Ausführen des Netzes im Mittelpunkt. Eine Reihe
 * von Service-Funktionen vervollständigt schließlich die für die Logik geforderte Funktionalität.
 * @author Uwe Rosner
 *
 */
public class Logic extends Observable implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<MI_Element> vElementsTransition;        // Liste für alle Transitionen
    private ArrayList<MI_Element> vElementsPlaceStorage;      // Liste für alle Allgemeinen Stellen
    private ArrayList<MI_Element> vElementsPlaceInput;        // Liste für alle Eingabestellen
    private ArrayList<MI_Element> vElementsPlaceOutput;       // Liste für alle Ausgabestellen
    private ArrayList<M_Link> vLinks;                         // Liste für alle Kanten

    private int stepCounter;                                  // zum Zählen der Schritte
    private boolean atLeastOneTransitionWorked;               // zum Testen, ob Netz verklemmt ist

    private ActionListener act;                               // der ActionListener

    /**
     * Erzeugt eine neue Logik-Instanz. An den zu übergebenen
     * ActionListener werden Ereignisse gesendet, die von ihm
     * ausgewertet werden sollten.
     * @param act ActionListener, der die gesendeten Ereignisse
     * auswertet.
     */
    public Logic(ActionListener act) {

        this.act = act;

        vElementsTransition = new ArrayList<MI_Element>();
        vElementsPlaceStorage = new ArrayList<MI_Element>();
        vElementsPlaceInput = new ArrayList<MI_Element>();
        vElementsPlaceOutput = new ArrayList<MI_Element>();
        vLinks = new ArrayList<M_Link>();

        stepCounter = 0;
    }

    /**
     * Gibt eine Liste aller Transitionen zurück.
     * @return Liste aller Transitionen
     */
    public ArrayList<MI_Element>  getElementsTransition(){
        return vElementsTransition;
    }

    /**
     * Gibt eine Liste aller Allgemeinen Stellen zurück.
     * @return Liste aller Allgemeinen Stellen
     */
    public ArrayList<MI_Element>  getElementsStorage(){
        return vElementsPlaceStorage;
    }

    /**
     * Gibt eine Liste aller Eingabestellen zurück.
     * @return Liste aller Eingabestellen
     */
    public ArrayList<MI_Element>  getElementsInput(){
        return vElementsPlaceInput;
    }

    /**
     * Gibt eine Liste aller Ausgabestellen zurück.
     * @return Liste aller Ausgabestellen
     */
    public ArrayList<MI_Element>  getElementsOutput(){
        return vElementsPlaceOutput;
    }

    /**
     * Gibt eine Liste aller Kanten zurück.
     * @return Liste aller Kanten
     */
    public ArrayList<M_Link> getLinks() {
        return vLinks;
    }

    /**
     * Fügt die übergebene Transition in die Liste der Transitionen ein
     * @param element einzufügende Transition
     */
    public void loadTransition(M_ElementRoot element){
        vElementsTransition.add(element);
    }

    /**
     * Fügt die übergebene Allgemeine Stelle in die Liste der
     * Allgemeinen Stellen ein
     * @param element einzufügende Allgemeine Stelle
     */
    public void loadPlaceStorage(M_ElementRoot element){
        vElementsPlaceStorage.add(element);
    }

    /**
     * Fügt die übergebene Eingabestelle in die Liste der Eingabestellen
     * ein
     * @param element einzufügende Eingabestelle
     */
    public void loadPlaceInput(M_ElementRoot element){
        vElementsPlaceInput.add(element);
    }

    /**
     * Fügt die übergebene Ausgabestelle in die Liste der Ausgabestellen
     * ein
     * @param element einzufügende Ausgabestelle
     */
    public void loadPlaceOutput(M_ElementRoot element){
        vElementsPlaceOutput.add(element);
    }

    /**
     * Fügt die übergebene Kante in die Liste der Kanten ein
     * @param link einzufügende Kante
     */
    public void loadLink(M_Link link){
        vLinks.add(link);
    }

    /**
     * Löschgt alle Elemente des Netzes in der Logik.
     */
    public void clear() {
        this.reset();
        vElementsTransition.clear();
        vElementsPlaceStorage.clear();
        vElementsPlaceInput.clear();
        vElementsPlaceOutput.clear();
        vLinks.clear();
    }

    /**
     * Gibt die Anzahl der durchgeführten Schritte zurück.
     * @return Anzahl durchgeführter Schritte
     */
    public String getStepCount() {
        return Integer.toString(stepCounter);
    }

    /**
     * Setzt alle Elemente auf ihre Ausgangswerte zurück.
     */
    public void reset() {

        stepCounter = 0;

        for(int i = 0; i < vElementsTransition.size(); i++) {
            vElementsTransition.get(i).reset();
        }

        for(int i = 0; i < vElementsPlaceStorage.size(); i++) {
            vElementsPlaceStorage.get(i).reset();
        }

        for(int i = 0; i < vElementsPlaceInput.size(); i++) {
            vElementsPlaceInput.get(i).reset();
        }

        for(int i = 0; i < vElementsPlaceOutput.size(); i++) {
            vElementsPlaceOutput.get(i).reset();
        }

    }

    /**
     * Fügt dem Netz neue Transition hinzu.
     */
    public void addTransition() {
        M_PNTransition element = new M_PNTransition(2, 2);
        vElementsTransition.add(element);
        this.setChanged();
        this.notifyObservers(element);
    }

    /**
     * Fügt dem Netz neue Allgemeine Stelle hinzu.
     */
    public void addPlaceStorage() {
        M_PNPStorage element = new M_PNPStorage(50);
        vElementsPlaceStorage.add(element);
        this.setChanged();
        this.notifyObservers(element);
    }

    /**
     * Fügt dem Netz neue Eingabestelle hinzu.
     */
    public void addPlaceInput() {
        M_PNPInput element = new M_PNPInput(100);
        vElementsPlaceInput.add(element);
        this.setChanged();
        this.notifyObservers(element);
    }

    /**
     * Fügt dem Netz neue Ausgabestelle hinzu.
     */
    public void addPlaceOutput() {
        M_PNPOutput element = new M_PNPOutput(1000);
        vElementsPlaceOutput.add(element);
        this.setChanged();
        this.notifyObservers(element);
    }

    /**
     * Entfernt das übergebene Element aus dem Petrinetz.
     * @param element zu entfernendes Element
     */
    public void deleteElement(M_ElementRoot element) {
        if(vElementsTransition.remove(element) == true) {
            ActionEvent ev = new ActionEvent(this, 1, "Msg_Deleted_Transition");
            act.actionPerformed(ev);
        }

        if(vElementsPlaceStorage.remove(element) == true) {
            ActionEvent ev = new ActionEvent(this, 1, "Msg_Deleted_PlaceStorage");
            act.actionPerformed(ev);
        }

        if(vElementsPlaceInput.remove(element) == true) {
            ActionEvent ev = new ActionEvent(this, 1, "Msg_Deleted_PlaceInput");
            act.actionPerformed(ev);
        }
        
        if(vElementsPlaceOutput.remove(element) == true) {
            ActionEvent ev = new ActionEvent(this, 1, "Msg_Deleted_PlaceOutput");
            act.actionPerformed(ev);
        }
    }

    /**
     * Verbindet zwei Pins mit einer Kante. Die Kante wird im
     * Erfolgsfall neu erstellt und der Liste der Kanten hinzugefügt.
     * @param source Quellpin
     * @param destination Zielpin
     * @return true, wenn Anschließen erfolgreich, sonst false.
     */
    public boolean connectElements(M_Pin source, M_Pin destination) {
        // raus & Event werfen, wenn Zielpin bereits angeschlossen ist
        // (bei Quellpins unnötig, wenn man auf einen angeschlossenen
        // klickt, wird der ja abgeklemmt.)
        if(destination.isConnected() == true && destination.getAssignedElement().getElementType() == ME_ElementType.TRANSITION) {
            ActionEvent ev = new ActionEvent(this, 1, "Error_Connect_DstPinAlreadyConnected");
            act.actionPerformed(ev);
            return false;
        }

        // raus & Event werfen, wenn auf eine Stelle keine Transition
        // folgt 
        if(source.getAssignedElement().getElementType() == ME_ElementType.PLACE) {
            if(destination.getAssignedElement().getElementType() != ME_ElementType.TRANSITION) {
                ActionEvent ev = new ActionEvent(this, 1, "Error_Connect_PlaceNoTransition");
                act.actionPerformed(ev);
                return false;
            }
        }

        // raus & Event werfen, wenn auf eine Transition keine Stelle
        // folgt
        if(source.getAssignedElement().getElementType() == ME_ElementType.TRANSITION) {
            if(destination.getAssignedElement().getElementType() != ME_ElementType.PLACE) {
                ActionEvent ev = new ActionEvent(this, 1, "Error_Connect_TransitionNoPlace");
                act.actionPerformed(ev);
                return false;
            }
        }

        // Anschließen, also entsprechende Kante erstellen
        M_Link link = new M_Link();
        if(link.setLink(source, destination) == true) {
            vLinks.add(link);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Entfernt eine Kante aus der Liste der Kanten und damit aus dem
     * Petrinetz.
     * @param link zu entfernende Kante
     */
    public void deleteLink(MI_Link link) {
        MI_Link delLink = vLinks.get(vLinks.indexOf(link));
        delLink.unsetLink();
        vLinks.remove(delLink);
    }

    /**
     * Testet, ob das Petrinetz konsistent ist. Es wird geprüft, ob
     * mindestens eine Transition und eine Ausgabestelle vorhanden sind
     * und ob alle Transitionen vollständig angeschlossen sind.
     * @return true, wenn Netz konsistent ist, sonst false.
     */
    public boolean isNetConsistent() {

        // Sind alle Transitionen vollständig angeschlossen?

        for(int i = 0; i < vElementsTransition.size(); i++) {
            if(vElementsTransition.get(i).isConnectedComplete() == false)
                return false;
        }

        // Kommen alle Element-Typen, d.h. Transition und Ausgabestellen
        // vor? Das ist das Minimum, sonst macht das Berechnungen keinen
        // Sinn. Grund: Ein Rechen-Schritt gilt als ausgeführt, wenn an
        // mindestens einer Ausgabestelle etwas ankommt. Ein
        // Rechen-Schritt wird abgebrochen, wenn keine Transition
        // rechnen konnte.

        if(vElementsTransition.size() == 0) {
            return false;
        }

        if(vElementsPlaceOutput.size() == 0) {
            return false;
        }

        return true;
    }

    /**
     * Testet, ob im letzten Schritt mindestens eine Transition arbeiten
     * konnte.
     * @return true, wenn mindestens eine Transition gearbeitet hat,
     * sonst false.
     */
    public boolean hasAtLeastOneTransitionWorked() {
        return atLeastOneTransitionWorked;
    }

    /**
     * Versucht, das Netz einen Schritt ausführen zu lassen.
     * @return true, wenn Schritt ausgeführt werden konnte, sonst false.
     */
    public boolean makeStep() {
        boolean someCameOut = false;

        // Erstmal alle Output-Elemente zurücksetzen
        for(int i = 0; i < vElementsPlaceOutput.size(); i++) {
            ((M_PNPOutput)vElementsPlaceOutput.get(i)).resetAfterStep();
        }

        do {

            // Alle Transitionen versuchen, einen Schritt zu machen
            for(int i = 0; i < vElementsTransition.size(); i++) {
                ((MI_Transition) vElementsTransition.get(i)).computeStep();
            }

            // Ist an mindestens einer Ausgabestelle etwas angekommen?
            for(int i = 0; i < vElementsPlaceOutput.size(); i++) {
                if( ((M_PNPOutput)vElementsPlaceOutput.get(i)).hasChanged() == true) {
                    someCameOut = true;
                }
            }

            // Konnte mindestens eine Transition einen Schritt machen?
            atLeastOneTransitionWorked = false;
            for(int i = 0; i < vElementsTransition.size(); i++) {
                if( ((MI_Transition) vElementsTransition.get(i)).hasWorked_KillFlag() == true ) {
                    atLeastOneTransitionWorked = true;
                }
            }

            if(atLeastOneTransitionWorked == false) {
                return false;
            }

        }
        while (someCameOut == false);

        stepCounter++;

        // Schritt erledigt

        return true;
    }
}
