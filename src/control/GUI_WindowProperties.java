package control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Diese Klasse stellt ein Fenster zum Eingeben von Eigenschaften zur Verfügung.
 * Dabei wird je nach verwendetem Konstruktor ein Fenster präsentiert, das entweder
 * die numerische Eingabe der mit Bezeichnern versehenen Eigenschaften erlaubt oder
 * das die Auswahl einer bestimmten Eigenschaft per RadioButton anbietet. Näheres dazu
 * in der Beschreibung der entsprechenden Konstruktoren.
 * @author Uwe Rosner
 *
 */
public class GUI_WindowProperties extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField[] textFields = null;                 // Feld von Textfeldern
    private JRadioButton[] radioButtons = null;             // Feld von Radiobuttons
    private ButtonGroup buttonGroup = new ButtonGroup();    // Buttongruppe, in die die Radiobuttons eingebunden
                                                            // werden müssen, damit immer nur ein Button ausgewählt werden kann

    private JButton buttOK;                                 // Schaltfläche "OK"
    private JButton buttCancel;                             // Schaltfläche "Abbrechen"

    private JPanel panel = new JPanel();                    // Das Panel, auf dem das alles platziert wird

    /**
     * Konstruktor, zum Bereitstellen eines Eigenschaften-Fensters zur numerischen Eingabe der Eigenschaften.
     * @param act ActionListener an den die Events der Schaltflächen "OK" und "Abbrechen" gesendet werden
     * @param identifiers Die Bezeichnungen der Eigenschaften. <B>Wichtig:</B> Der erste Identifier sollte
     * den Namen des Elements enthalten, für das die Eigensachaften angezeigt werden, denn als solcher wird
     * er in der Titeleiste des Fensters angezeigt. 
     * @param properties Die Eigenschaften.
     */
    public GUI_WindowProperties(ActionListener act, String[] identifiers, String[] properties) {
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setMinimumSize(new Dimension(480, 50));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setTitle("Properties of '" + identifiers[0] + "'.");

        // auskommentiert: verursacht bei Applets eine Exception
        // this.setAlwaysOnTop(true);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        buttOK = new JButton("Ok");
        buttOK.setActionCommand("WinModOK");
        buttOK.addActionListener(act);
        buttCancel = new JButton("Cancel");
        buttCancel.setActionCommand("WinModCANCEL");
        buttCancel.addActionListener(act);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(buttOK);
        panel.add(Box.createHorizontalGlue());
        panel.add(buttCancel);
        panel.add(Box.createHorizontalStrut(10));

        JPanel[] p;

        if(identifiers.length < 2) {
            p = new JPanel[identifiers.length];
        }
        else {
            p = new JPanel[identifiers.length - 1];
        }

        if(identifiers.length < 2) {    // Es gibt keine veränderbaren Eigenschaften
            p[0] = new JPanel();
            p[0].add(new JLabel("No properties can be set for this item."));
        }
        else {
            textFields = new JTextField[p.length];
            for(int i = 0; i < p.length; i++) {
                p[i] = new JPanel();
                p[i].setLayout(new BoxLayout(p[i], BoxLayout.X_AXIS));
                p[i].add(Box.createHorizontalStrut(10));
                p[i].add(new JLabel(identifiers[i + 1]));
                p[i].add(Box.createHorizontalStrut(10));
                textFields[i] = new JTextField(properties[i]);
                p[i].add(textFields[i]);
                p[i].add(Box.createHorizontalStrut(10));
            }
        }

        for(int i = 0; i < p.length; i++) {
            this.add(Box.createVerticalStrut(10));
            this.getContentPane().add(p[i]);
        }

        this.add(Box.createVerticalStrut(10));
        this.getContentPane().add(panel);
        this.add(Box.createVerticalStrut(10));
        this.pack();
    }

    /**
     * Konstruktor, zum Bereitstellen eines Eigenschaften-Fensters zur Auswahl einer Eigenschaft aus
     * einer übergebenen Menge von Optionen.
     * @param act ActionListener an den die Events der Schaltflächen "OK" und "Abbrechen" gesendet werden
     * @param identifiers Die Bezeichnungen der Eigenschaften. <B>Wichtig:</B> Der erste Identifier sollte
     * den Namen des Elements enthalten, für das die Eigensachaften angezeigt werden, denn als solcher wird
     * er in der Titeleiste des Fensters angezeigt.
     * @param property Die aktuell gewählte Eigenschaft
     * @param options Die zur Verfügung stehenden Eigenschaften
     */
    public GUI_WindowProperties(ActionListener act, String[] identifiers, String property, String[] options) {
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setMinimumSize(new Dimension(480, 50));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setTitle("Eigenschaften für '" + identifiers[0] + "'.");

        final int bpl = 5;    // buttons per line, Buttons pro Zeile

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        buttOK = new JButton("Ok");
        buttOK.setActionCommand("WinModOK");
        buttOK.addActionListener(act);
        buttCancel = new JButton("Cancel");
        buttCancel.setActionCommand("WinModCANCEL");
        buttCancel.addActionListener(act);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(buttOK);
        panel.add(Box.createHorizontalGlue());
        panel.add(buttCancel);
        panel.add(Box.createHorizontalStrut(10));

        radioButtons = new JRadioButton[options.length];

        JPanel pGrid = new JPanel();
        pGrid.setLayout(new GridLayout(options.length / bpl + 1, bpl));

        for(int i = 0; i < options.length; i++) {
            radioButtons[i] = new JRadioButton(options[i]);
            radioButtons[i].setActionCommand(radioButtons[i].getText());
            buttonGroup.add(radioButtons[i]);

            // Wenn aktueller Button die aktuelle Operation enthält: Button selektieren
            if(property.equals(radioButtons[i].getText())) {
                radioButtons[i].setSelected(true);
            }

            // Button zu Panel hinzufügen
            pGrid.add(radioButtons[i]);
        }

        JPanel pTxt = new JPanel();
        pTxt.setLayout(new BoxLayout(pTxt, BoxLayout.X_AXIS));
        pTxt.add(Box.createHorizontalStrut(10));
        pTxt.add(new JLabel("Bitte "+ identifiers[1] + " select."));
        pTxt.add(Box.createHorizontalGlue());

        this.add(Box.createVerticalStrut(10));
        this.add(pTxt);
        this.add(Box.createVerticalStrut(10));
        this.add(pGrid);
        this.add(Box.createVerticalStrut(10));
        this.add(panel);
        this.add(Box.createVerticalStrut(10));

        this.pack();

        
    }

    /**
     * Liest die im Eigenschaftenfenster einegebenen / eingestellten Eigenschaften aus
     * und gibt diese zurück.
     * @return Die eingegebenen / eingestellten Eigenschaften
     */
    public String[] getProperties() {

        if(radioButtons != null) {
            String[] selected = new String[1];
            selected[0] = buttonGroup.getSelection().getActionCommand();
            return selected;
        }
        else {
            // die Werte in allen Textfeldern in einem String-Array zurückgeben
    
            if(textFields == null) {    // Wenn Element keine änderbaren Eigenschaften besitzt
                return null;            // gib leeres String-Array zurück
            }
    
            String[] str = new String[textFields.length];
    
            for(int i = 0; i < textFields.length; i++) {
                str[i] = textFields[i].getText();
            }
    
            return str;
        }
    }

    /**
     * Schließt das Eigenschaften-Fenster
     */
    public void closeWindow() {
        dispose();
    }
}
