package control;

import javax.swing.*;

import view.*;
import model.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.ArrayList;

public class StartUpApp extends JFrame  implements ActionListener, Runnable
{

  private static final long serialVersionUID = 1L;
  private Logic model;            // Die Programmlogik.

  // Das auf der Arbeitsoberfläche (drawing_area) markierte Element
  private VI_Element selectedItem = null;

  // Das Fenster zum Einstellen der Elementeigenschaften.
  private GUI_WindowProperties propertyWindow;

  // Das Fenster zum Einstellen der EGewichte.
  private GUI_WindowWeights weightsWindow;

  /**
   * Das Hintergrund-Panel.
   */

  // Das Panel, auf dem die anderen Komponenten platziert werden
  private JPanel  background;

  // Die Arbeitsfläche, auf die Transitionen, Stellen und Kanten
  // platziert werden
  private GUI_DrawingArea drawingArea;

  // Leiste am rechten Bildschirmrand, auf der sich Schaltflächen und
  // Informationen befinden
  private GUI_Sidebar controlPanel;

  // Statuszeile am unteren Rand des Simulators. Zum Anzeigen von
  // Mitteilungen und Fehlern.
  private GUI_Statebar stateBar;

  // Das Popup-Menü, das sich beim Rechtsklicken auf die drawing_area
  // öffnet.
  private GUI_PopupMenu popupMenu;

  // Der Thread, der für die Hintergrundausführung der Funktion
  // "Ausführen bis Verklemmung" benötigt wird.
  transient Thread runUntilDeadlock;
  
  private void initUI()
  {
    setTitle("Petrinetz-Simulator");
    setSize(1280, 720);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }
  
  private void init()
  {
    /*
     * LookAndFeel an die jeweilige Oberfläche (Betriebssystem) anpassen
     */
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e)
    {
      System.out.println("System look and feel could not be set.");
    }

    /*
     * Logik erzeugen
     */
    model = new Logic(this);

    /*
     * View-Komponenten erzeugen
     */
    background = new JPanel();

    // benötigt `model` für Operationen damit und `this`, um Events
    // hierher zu senden
    drawingArea = new GUI_DrawingArea(model, this);

    // benötigt this, um Events hierher zu senden
    controlPanel = new GUI_Sidebar(this);
    stateBar = new GUI_Statebar();

    // Farben einstellen: Zeichenfeld: Blau
    drawingArea.setBackground(new Color(50, 100, 200));

    // Statusleiste: Weiß
    stateBar.setBackground(new Color(255, 255, 255));

    /*
     * Popup-Menü erzeugen, der drawing_area hinzufügen und
     * entsprechenden MouseListener realisieren
     */
    popupMenu = new GUI_PopupMenu(this);
    drawingArea.add(popupMenu);

    // MouseListener für Popup-Menü realisieren
    drawingArea.addMouseListener( new MouseAdapter()  { 
      public void mouseReleased( MouseEvent me ) {

        // Popup-Menü blockieren, wenn Thread für
        // "Ausführen bis Verklemmung" gerade läuft
        if(runUntilDeadlock != null) {
          if(runUntilDeadlock.isAlive() == true) {
            return;
          }
        }

        // Popup-Menü öffnen
        if ( me.isPopupTrigger() ) 
          popupMenu.show( me.getComponent(), me.getX(), me.getY() );
      }
    } );

    // Ein Scrollpane erzeugen und die drawing_area da einbetten.
    //
    // Somit steht der drawing_area eine größere (scrollbare) Fläche zur
    // Verfügung. Hier zunächst: 4000 x 3000 Pixel
    JScrollPane scrollPane = new JScrollPane(drawingArea);
    drawingArea.setPreferredSize(new Dimension(4000, 3000));

    // Layout-Manager für den Hintergrund wählen
    background.setLayout(new BorderLayout());
    
    // Dem Hintergrund an die entsprechenden Stellen die Komponenten hinzufügen
    background.add(scrollPane, BorderLayout.CENTER);
    background.add(controlPanel, BorderLayout.EAST);
    background.add(stateBar, BorderLayout.SOUTH);

    // Dem Applet den Hintergrund hinzufügen und darstellen
    getContentPane().add(background);
    background.setVisible(true);

    repaint();

    /*
     * Damit ist die Initialisierung beendet. Nun wird nur noch auf
     * eintreffende Events gewartet, die in actionPerformed(...)
     * ausgewertet und behandelt werden.
     */ 
  }
  
  public StartUpApp()
  {
    initUI();
    init();
  }

  public static void main(String[] args)
  {

    EventQueue.invokeLater(() -> 
    {
      StartUpApp startupApp = new StartUpApp();
      startupApp.setVisible(true);
    });
  }
  
  
  /**
   * Hier erfolgt die Auswertung aller von den Komponenten erzeugten
   * Events.
   * 
   * @see 
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {

    // Anweisung "repaint" abfragen
    // Quelle: V_PNTransition
    if(e.getActionCommand() == "Stmt_Repaint") {
      drawingArea.repaint();
    }

    // Keine weiteren Aktionen zulassen, solange der Thread für
    // "Ausführen bis Verklemmung" gerade läuft
    if(runUntilDeadlock != null) {
      if(runUntilDeadlock.isAlive() == true) {
        return;
      }
    }

    /*
     * Aktionen bezüglich Fehlern und Mitteilungen
     */

    // Mitteilung "Msg_Connect_OK" abfragen
    // Quelle: GUI_DrawingArea
    if(e.getActionCommand() == "Msg_Connect_OK") {
      stateBar.setText("Pins connected.");
    }

    // Fehler "Error_Connect_SrcPinIsAInputPin" abfragen
    // Quelle: GUI_DrawingArea
    else if(e.getActionCommand() == "Error_Connect_SrcPinIsAInputPin") {
      stateBar.setTextHighlighted("Connecting must start with an output pin.");
    }

    // Fehler "Error_Connect_DstPinIsAOutputPin" abfragen
    // Quelle: GUI_DrawingArea
    else if(e.getActionCommand() == "Error_Connect_DstPinIsAOutputPin") {
      stateBar.setTextHighlighted("The connection must end with an input pin.");
    }

    // Mitteilung "MSG_Deleted_Transition" abfragen
    // Quelle: Logic
    else if(e.getActionCommand() == "Msg_Deleted_Transition") {
      stateBar.setText("Removed a transition.");
    }

    // Mitteilung "MSG_Deleted_PlaceStorage" abfragen
    // Quelle: Logic
    else if(e.getActionCommand() == "Msg_Deleted_PlaceStorage") {
      stateBar.setText("Removed a place (gen.).");
    }

    // Mitteilung "MSG_Deleted_PlaceStorage" abfragen
    // Quelle: Logic
    else if(e.getActionCommand() == "Msg_Deleted_PlaceInput") {
      stateBar.setText("Removed a input place.");
    }

    // Mitteilung "MSG_Deleted_PlaceStorage" abfragen
    // Quelle: Logic
    else if(e.getActionCommand() == "Msg_Deleted_PlaceOutput") {
      stateBar.setText("Removed a output place.");
    }

    // Fehler "Error_PlaceNoTransition" abfragen
    // Quelle: Logic
    else if(e.getActionCommand() == "Error_Connect_PlaceNoTransition") {
      stateBar.setTextHighlighted("A place must be followed by a transition.");
    }

    // Fehler "Error_TransitionNoPlace" abfragen
    // Quelle: Logic
    else if(e.getActionCommand() == "Error_Connect_TransitionNoPlace") {
      stateBar.setTextHighlighted("A transition must be followed by a place.");
    }

    else if(e.getActionCommand() == "Error_Connect_DstPinAlreadyConnected") {
      stateBar.setTextHighlighted("Only one arc may be connected to an input of a transition.");
    }

    /*
     * Aktionen zur Auswertung der befehle zum Öffnen der Fenster "Eigenschaften" und "Gewichte"
     * und zur Verarbeitung von deren Befehle "OK" und "Abbrechen"
     */

    // Befehl "Eigenschaften"
    else if(e.getActionCommand() == "Properties") {
      // markiertes Element holen
      selectedItem = drawingArea.getMarkedElement();

      // wenn kein Element markiert ist --> raus
      if(selectedItem == null) {
        stateBar.setTextHighlighted("Properties: No item selected.");
        return;
      }

      // Wenn schon ein Fenster offen --> raus
      if(propertyWindow != null || weightsWindow != null) {
        stateBar.setTextHighlighted("There's already a window open.");
        return;
      }

      // Wenn versucht wird, Eigenschaften für ein angeschlossenes Transition zu ändern --> raus
      if(selectedItem.getElementLogic().getElementType() == ME_ElementType.TRANSITION) {
        if(selectedItem.getElementLogic().isConnectedPartial() == true) {
          stateBar.setTextHighlighted("The properties of a transition cannot be changed when connected.");
          return;
        }
      }

      // nun Eigenschaften-Fenster öffnen
      stateBar.setText("Edit properties for " + selectedItem.getElementLogic().getPIdentifiers()[0]);
      propertyWindow = new GUI_WindowProperties(this, selectedItem.getElementLogic().getPIdentifiers(), selectedItem.getElementLogic().getProperties());
      propertyWindow.setVisible(true);

    }

    // Abfrage des Buttons "OK" im Fenster "Eigenschaften"
    else if(e.getActionCommand() == "WinModOK") {
      if(selectedItem.getElementLogic().setProperties(propertyWindow.getProperties()) == true) {
        propertyWindow.closeWindow();
        propertyWindow = null;
        stateBar.setText("Editing properties for " + selectedItem.getElementLogic().getPIdentifiers()[0] + " done.");       
      }
      else {
        JOptionPane.showMessageDialog(propertyWindow, "Cannot change properties!\nInvalid values entered?");
      }
    }

    // Abfrage des Buttons "Abbrechen" im Fenster "Eigenschaften"
    else if(e.getActionCommand() == "WinModCANCEL") {
      propertyWindow.closeWindow();
      propertyWindow = null;
      stateBar.setText("Editing properties for " + selectedItem.getElementLogic().getPIdentifiers()[0] + " cancelled.");
    }


    // Befehl "Gewichte"
    else if(e.getActionCommand() == "Weights") {
      // markiertes Element holen
      selectedItem = drawingArea.getMarkedElement();

      // wenn kein Element markiert ist --> raus
      if(selectedItem == null) {
        stateBar.setTextHighlighted("Weights: no item selected.");
        return;
      }

      // Wenn schon ein Fenster offen --> raus
      if(weightsWindow != null || propertyWindow != null) {
        stateBar.setTextHighlighted("There's already a window open.");
        return;
      }

      // Konstruktoraufruf
      else {
        stateBar.setText("Edit weights for " + selectedItem.getElementLogic().getPIdentifiers()[0]);
        weightsWindow = new GUI_WindowWeights(this, selectedItem.getElementLogic().getWIdentifiers(), selectedItem.getElementLogic().getWeights());
        weightsWindow.setVisible(true);
      }
    }

    // Abfrage des Buttons "OK" im Fenster "Gewichte"
    else if(e.getActionCommand() == "WinWeightsOK") {
      if(selectedItem.getElementLogic().setWeights(weightsWindow.getWeights()) == true) {
        weightsWindow.closeWindow();
        weightsWindow = null;
        stateBar.setText("Editing weights for " + selectedItem.getElementLogic().getPIdentifiers()[0] + " done.");        
      }
      else {
        JOptionPane.showMessageDialog(propertyWindow, "Cannot change weights!\nInvalid values entered?");
      }
    }

    // Abfrage des Buttons "Abbrechen" im Fenster "Gewichte"
    else if(e.getActionCommand() == "WinWeightsCANCEL") {
      weightsWindow.closeWindow();
      weightsWindow = null;
      stateBar.setText("Editing weights for " + selectedItem.getElementLogic().getPIdentifiers()[0] + " cancelled.");
    }

    /*
     * Aktionen zur Auswertung der restlichen Befehle des Popup-Menüs (GUI_PopupMenu) und
     * von den Buttons auf der rechten Seite (GUI_Sidebar)
     */

    // Befehl "Entfernen"
    else if(e.getActionCommand() == "Remove") {
      selectedItem = drawingArea.getMarkedElement();

      if(selectedItem == null) {
        // kein Element ausgewählt
        stateBar.setText("To remove please select an item first.");
        return;
      }

      if(selectedItem.getElementLogic().isConnectedPartial() == false) {
        // Element entfernen
        model.deleteElement((M_ElementRoot)selectedItem.getElementLogic());
        drawingArea.deleteElement(selectedItem);
      }
      else {
        stateBar.setTextHighlighted("Can't remove a connected item.");
      }
    }

    // Befehl "Vollständig Abklemmen"
    else if(e.getActionCommand() == "Disconnect completely") {
      stateBar.setText("Disconnect completely: No implemented, yet.");
    }

    // Befehl "..."
    else if(e.getActionCommand() == "...") {
      JOptionPane.showMessageDialog(this, "Petri net simulator.\nDeveloped by Uwe Rosner during a project seminar at the HTW-Dresden.\nDevelopment period: 10/2007 - 01/2008",
          "About", JOptionPane.INFORMATION_MESSAGE);
    }

    // Befehl "Neu"
    else if(e.getActionCommand() == "New") {

      // raus, wenn Sicherheitsabfrage nicht mit "ja" beantwortet
      if(continueQuestion("New Petri net") == false) {
        return;
      }

      // Alles zurücksetzen
      drawingArea.clear();
      model.clear();
      model.reset();
      controlPanel.setSteps(model.getStepCount());
      drawingArea.repaint();
      
    }

    // Befehl "Laden"
    else if(e.getActionCommand() == "Load") {

      // raus, wenn Sicherheitsabfrage nicht mit "ja" beantwortet
      if(continueQuestion("Load Petri net") == false) {
        return;
      }

      if(loadPetrinet() == true) {
        stateBar.setText("Petri net loaded successfully.");
      }
      else {
        stateBar.setTextHighlighted("Petri net not loaded.");
      }
    }

    // Befehl "Speichern"
    else if(e.getActionCommand() == "Save") {
      if(savePetrinet() == true) {
        stateBar.setText("Petri net saved successfully.");
      }
      else {
        stateBar.setTextHighlighted("Petri net not saved.");
      }
    }

    // Befehl "Einzelschritt"
    else if(e.getActionCommand() == "Single step") {
      if(model.isNetConsistent() == true) {
        if(model.makeStep() == true) {
          controlPanel.setSteps(model.getStepCount());
          stateBar.setText("Step done");
        }
        else {
          stateBar.setTextHighlighted("Step *not* done; no transition could work.");
        }
      }
      else {
        stateBar.setTextHighlighted("Can't start running; net isn't consistent.");
      }
    }

    // Befehl "Ausführen bis Verklemmung"
    else if(e.getActionCommand() == "Run until deadlock") {
      runUntilDeadlock = new Thread(this);
      runUntilDeadlock.start();
    }

    // Befehl "Reset"
    else if(e.getActionCommand() == "Reset") {
      model.reset();
      controlPanel.setSteps(model.getStepCount());
      stateBar.setText("Net reset.");
      drawingArea.repaint();
    }

    // Befehle zum Hinzufügen der Elemente
    else if(e.getActionCommand() == "Input place") {
      model.addPlaceInput();
      stateBar.setText("Input place created.");
    }
    else if(e.getActionCommand() == "Output place") {
      model.addPlaceOutput();
      stateBar.setText("Output place created");
    }
    else if(e.getActionCommand() == "Place (gen.)") {
      model.addPlaceStorage();
      stateBar.setText("Place (gen.) created.");
    }
    else if(e.getActionCommand() == "Transition") {
      model.addTransition();
      stateBar.setText("Transition created.");
    }

    drawingArea.repaint();
  }

  /*
   * Fragt, ob das aktuelle Netz verworfen werden soll.
   * @param title Der Titel des Fensters
   * @return true Wenn Frage mit ja beantwortet wurde, false sonst
   */
  private boolean continueQuestion(String title) {
    // Wenn brereits ein Netz existiert
    if(drawingArea.getElements().size() > 0) {
      // Sicherheitsabfrage
      if(JOptionPane.showConfirmDialog(
          this,
          "Discard current net?",
          title,
          JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        // Aussteigen, wenn nicht 'ja' geklickt
        return false;
      }
    }
    return true;
  }

  /**
   * Speichert das aktuelle Petrinetz. Verwendet dazu die einfache Serialisierung.
   * @return true, wenn Petrinetz erfolgreich gespeichert wurde, false sonst
   */
  private boolean savePetrinet() {

    /*
     * Dateirequester öffnen
     */

//    JFileChooser fileChooser = new JFileChooser();
//    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//    fileChooser.setMultiSelectionEnabled(false);
//
//    // aktuelles Verzeichnis vorwählen
//    fileChooser.setCurrentDirectory(new File(System.getProperties().getProperty("user.dir")));
//
//    // raus, wenn keine Datei ausgewählt
//    if(fileChooser.showDialog(this, "Petrinetz speichern...") != JFileChooser.APPROVE_OPTION) {
//      return false;
//    }
    
    File outputFile = new File("testtest.pnet");

    /*
     * Versuchen, das ausgewählte Petrinetz zu speichern
     */

    try {
//      FileOutputStream fout = new FileOutputStream(fileChooser.getSelectedFile());
      FileOutputStream fout = new FileOutputStream(outputFile);
      ObjectOutputStream oout = new ObjectOutputStream(fout);

      ArrayList<MI_Element> logicElementsTransitions = model.getElementsTransition();
      ArrayList<MI_Element> logicElementsPlaces = model.getElementsStorage();
      ArrayList<MI_Element> logicElementsPlacesInput = model.getElementsInput();
      ArrayList<MI_Element> logicElementsPlacesOutput = model.getElementsOutput();
      ArrayList<M_Link> logicLinks = model.getLinks();

      ArrayList<V_Link> viewLinks = drawingArea.getLinks();
      ArrayList<V_ElementRoot> viewElements = drawingArea.getElements();

      /*
       * Logik speichern
       */
      // Transitionen
      oout.writeInt(logicElementsTransitions.size());
      for(int i = 0; i < logicElementsTransitions.size(); i++) {
        oout.writeObject(logicElementsTransitions.get(i));
      }

      // allgemeine Stellen
      oout.writeInt(logicElementsPlaces.size());
      for(int i = 0; i < logicElementsPlaces.size(); i++) {
        oout.writeObject(logicElementsPlaces.get(i));
      }

      // Eingabestellen
      oout.writeInt(logicElementsPlacesInput.size());
      for(int i = 0; i < logicElementsPlacesInput.size(); i++) {
        oout.writeObject(logicElementsPlacesInput.get(i));
      }

      // Ausgabestellen
      oout.writeInt(logicElementsPlacesOutput.size());
      for(int i = 0; i < logicElementsPlacesOutput.size(); i++) {
        oout.writeObject(logicElementsPlacesOutput.get(i));
      }

      // Kanten
      oout.writeInt(logicLinks.size());
      for(int i = 0; i < logicLinks.size(); i++) {
        oout.writeObject(logicLinks.get(i));
      }

      /*
       * View speichern
       */

      // Elemente
      oout.writeInt(viewElements.size());
      for(int i = 0; i < viewElements.size(); i++) {
        V_ElementRoot viewItem = viewElements.get(i); 
        
        // eventuell markiertes oder verklemmtes Element wieder in
        // Normalzustand versetzen
        viewItem.setColorNormal();
        
        
        oout.writeObject(viewItem);
      }

      // Kanten
      oout.writeInt(viewLinks.size());
      for(int i = 0; i < viewLinks.size(); i++) {
        oout.writeObject(viewLinks.get(i));
      }

      oout.close();
      fout.close();

      return true;
      
    }
    catch(Exception ioe) {
      System.out.println(ioe);
      return false;
    }
  }

  /**
   * Lädt ein Petrinetz. Verwendet dazu die einfache Deserialisierung.
   * @return true, wenn Petrinetz erfolgreich geladen wurde, false sonst
   */
  private boolean loadPetrinet() {
    /*
     * altes Netz löschen
     */
    drawingArea.clear();
    model.clear();

    /*
     * Dateirequester öffnen
     */

//    JFileChooser fileChooser = new JFileChooser();
//    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//    fileChooser.setMultiSelectionEnabled(false);
//
//    // aktuelles Verzeichnis vorwählen
//    fileChooser.setCurrentDirectory(new File(System.getProperties().getProperty("user.dir")));
//
//    // raus, wenn keine Datei ausgewählt
//    if(fileChooser.showDialog(this, "Petrinetz laden...") != JFileChooser.APPROVE_OPTION) {
//      return false;
//    }
    
    File inputFile = new File("testtest.pnet");

    /*
     * Versuchen, das ausgewählte Petrinetz zu laden
     */

    try {
//      FileInputStream fin = new FileInputStream(fileChooser.getSelectedFile());
      FileInputStream fin = new FileInputStream(inputFile);
      ObjectInputStream oin = new ObjectInputStream(fin);

      M_ElementRoot logicElement;
      M_Link logicLink;

      V_ElementRoot viewElement;
      V_Link viewLink;

      /*
       * Logik laden
       */

      // Transitionen
      int size = oin.readInt();
      for(int i = 0; i < size; i++) {
        logicElement = (M_ElementRoot)oin.readObject();
        model.loadTransition(logicElement);
      }

      // Stellen (allgemein)
      size = oin.readInt();
      for(int i = 0; i < size; i++) {
        logicElement = (M_ElementRoot)oin.readObject();
        model.loadPlaceStorage(logicElement);
      }

      // Stellen (input)
      size = oin.readInt();
      for(int i = 0; i < size; i++) {
        logicElement = (M_ElementRoot)oin.readObject();
        model.loadPlaceInput(logicElement);
      }

      // Stellen (output)
      size = oin.readInt();
      for(int i = 0; i < size; i++) {
        logicElement = (M_ElementRoot)oin.readObject();
        model.loadPlaceOutput(logicElement);
      }

      // Kanten
      size = oin.readInt();
      for(int i = 0; i < size; i++) {
        logicLink = (M_Link)oin.readObject();
        model.loadLink(logicLink);
      }

      /*
       * View laden
       */

      // Elemente
// TODO
      size = oin.readInt();
      for(int i = 0; i < size; i++) {
        viewElement = (V_ElementRoot)oin.readObject();
        viewElement.reloadObverver();
        drawingArea.loadElement(viewElement);
      }

      // Kanten
      size = oin.readInt();
      for(int i = 0; i < size; i++) {
        viewLink = (V_Link)oin.readObject();
        drawingArea.loadLink(viewLink);
      }

      oin.close();
      fin.close();

      controlPanel.setSteps(model.getStepCount());
      drawingArea.repaint();

      return true;
    }
    catch(Exception ioe) {
      System.out.println("Loading failed:\n" + ioe);
      return false;
    }
  }

  /**
   * Wird beim Aufruf des Threads ausgeführt. Enthält den Code der
   * Funktion "Ausführen bis Verklemmung", die nicht blockieren darf.
   */
  public void run() {
    if(model.isNetConsistent() == true) {

      // drawing_area für Bearbeitung sperren
      drawingArea.lockEditing();

      do {
        model.makeStep();
        controlPanel.setSteps(model.getStepCount());
        try {
          Thread.sleep(120);
        }
        catch (InterruptedException exc) {
          System.out.println("Internal error in thread \"Run until deadlock\":\n" + exc);
        }
      }
      while(model.hasAtLeastOneTransitionWorked() == true);

      // Bearbeitung für drawing_area wieder erlauben
      drawingArea.unlockEditing();

      stateBar.setText("Run until deadlock. Currently no transition can work.");
    }
    else {
      stateBar.setTextHighlighted("Run failed; net isn't consistent.");
    }
  }
}
