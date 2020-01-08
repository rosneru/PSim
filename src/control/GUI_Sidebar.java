package control;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

/**
 * Stellt einen rechten Seitenbereich mit Schaltflächen (zum Bearbeiten und
 * zur Simaulation) und einem Schrittzähler zur Verfügung.
 * @author Uwe Rosner
 *
 */
public class GUI_Sidebar extends Box{
    static final long serialVersionUID = 1L;    // umgeht die Warnung "The serializable class ..."
                                                // unterscheidet verschiedene Versionen der Klasse

    JPanel p1;        // Panel für die Schaltflächen zum Bearbeiten
    JPanel p2;        // Panel für die Schaltflächen zur Simulation

    JButton[] edtButt;    // Schalflächen-Feld für die Befehle zum Bearbeiten
    JButton[] simButt;    // Schalflächen-Feld für die Befehle zur Simulation

    JLabel stepcount;    // für die Anzeige des Schrittzählers

    /**
     * Konstruktor des rechten Seitenbereichs.
     * @param act Der ActionListener, an den die Events (auf bestimmte Schaltfläche geklickt) gesendet werden.
     */
    public GUI_Sidebar(ActionListener act) {
        // Grundsätzliche layout-Einstellungen
        super(BoxLayout.Y_AXIS);
        this.setBorder(new EtchedBorder());
        this.setAlignmentY(0.5f);
        EtchedBorder etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);

        // Strings für die Schaltflächenbezeichnungen
        String[] edtStr = {"Properties", "Weights"};
        String[] simStr = {"Single step", "Run until deadlock", "Reset"};

        // Label für die Anzeige des Schrittzählers
        stepcount = new JLabel("Steps: 0");

        edtButt = new JButton[edtStr.length];
        simButt = new JButton[simStr.length];

        // Buttons erzeugen
        for(int i = 0; i < edtStr.length; i++) {
            edtButt[i] = new JButton(edtStr[i]);
            edtButt[i].addActionListener(act);
        }

        for(int i = 0; i < simStr.length; i++) {
            simButt[i] = new JButton(simStr[i]);
            simButt[i].addActionListener(act);
        }

        JButton infoButt = new JButton("...");
        infoButt.addActionListener(act);

        // Bearbeiten-Panel erzeugen
        p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.setBorder(new TitledBorder(etchedBorder, "Edit", 0, TitledBorder.CENTER));
        for(int i = 0; i < edtButt.length; i++) {
            p1.add(edtButt[i]);
            p1.add(createVerticalStrut(8));
        }

        // Simulation-Panel erzeugen
        p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setBorder(new TitledBorder(etchedBorder, "Simulating", 0, TitledBorder.CENTER));
        for(int i = 0; i < (simButt.length - 1); i++) {
            p2.add(simButt[i]);
            p2.add(createVerticalStrut(8));
        }
        p2.add(createVerticalStrut(64));
        p2.add(simButt[simButt.length - 1]);
        p2.add(createVerticalStrut(8));
        p2.add(stepcount);
        p2.add(createVerticalStrut(8));

        // Panels und Info-Button hinzufügen
        this.add(p1);
        this.add(createVerticalGlue());
        this.add(infoButt);
        this.add(createVerticalGlue());
        this.add(p2);

    }

    /**
     * Setzt die Anzeige des Schrittzählers auf einen neuen Wert.
     * @param steps - Der neue Wert des Schrittzählers.
     */
    public void setSteps(String steps){
        stepcount.setText("Steps: " + steps);
    }

}
