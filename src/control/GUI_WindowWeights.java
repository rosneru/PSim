package control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Diese Klasse stellt ein Fenster zum Eingeben von Gewichten zur Verfügung.
 * @author Uwe Rosner
 *
 */
public class GUI_WindowWeights extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField[] textFields = null;
    ButtonGroup buttonGroup = new ButtonGroup();
    private JPanel panel = new JPanel();
    private JButton buttOK;
    private JButton buttCancel;

    /**
     * Konstruktor zum Bereitstellen eines Fensters zur numerischen Eingabe von Gewichten.
     * @param act ActionListener an den die Events der Schaltflächen "OK" und "Abbrechen" gesendet werden
     * @param identifiers Die Bezeichnungen der Gewichte.
     * @param weights Die Gewichte.
     */
    public GUI_WindowWeights(ActionListener act, String[] identifiers, String[] weights) {
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setMinimumSize(new Dimension(480, 50));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setTitle("Gewichte für '" + identifiers[0] + "'.");

        // auskommentiert: verursacht bei Applets eine Exception
        // this.setAlwaysOnTop(true);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        buttOK = new JButton("Ok");
        buttOK.setActionCommand("WinWeightsOK");
        buttOK.addActionListener(act);
        buttCancel = new JButton("Cancel");
        buttCancel.setActionCommand("WinWeightsCANCEL");
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

        if(identifiers.length < 2) {    // Es gibt keine veränderbaren Gewichte
            p[0] = new JPanel();
            p[0].add(new JLabel("No wieghts can be set for this item. Weights only can be set for transitions."));
        }
        else {
            textFields = new JTextField[p.length];
            for(int i = 0; i < p.length; i++) {
                p[i] = new JPanel();
                p[i].setLayout(new BoxLayout(p[i], BoxLayout.X_AXIS));
                p[i].add(Box.createHorizontalStrut(10));
                p[i].add(new JLabel(identifiers[i + 1]));
                p[i].add(Box.createHorizontalStrut(10));
                textFields[i] = new JTextField(weights[i]);
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
     * Übergibt die im Gewichtefenster einegebenen Gewichte.
     * @return Die eingegebenen Eigenschaften
     */
    public String[] getWeights() {
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

    /**
     * Schließt das Gewichtefenster
     */
    public void closeWindow() {
        dispose();
    }
}
