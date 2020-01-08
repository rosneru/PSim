package view;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;

import model.MI_Link;

/**
 * Realisiert die Kanten in der View. Eine Kante verbindet zwei Elemente und zwar konkret deren Pins.
 * Eine Kante benötigt zunächst mindestene einen Pin (den Anfangspin), dieser muss im
 * Konstruktor übergeben werden. Weitere Pins können mittels der addPin(...) - Funktionen hinzugefügt werden.
 * Das letzte Element in der Liste der Pins wird als Endpunkt betrachtet.
 * @author Uwe Rosner
 *
 */
public class V_Link extends V_ElementRoot implements VI_Link, Serializable {

    private static final long serialVersionUID = 1L;

    MI_Link associatedLink;            // Pendat dieser View-Kante in der Logik

    /**
     * Erstellt eine neue Kante.
     * @param p Anfangspin der Kante
     */
    public V_Link(V_Pin p) {
        pins.add(p);
    }

    /**
     * Fügt der Kante einen Punkt hinzu.
     * @param p Hinzuzufügender Pin
     */
    public void addPin(V_Pin p) {
        pins.add(p);
    }

    /**
     * Fügt der Kante einen Pin an einer bestimmten Position (ausgehend vom Startpin) hinzu.
     * @param p Hinzuzufügender Pin
     * @param position Position des Pins innerhalb aller Pins der Kante
     */
    public void addPin(V_Pin p, int position) {
        pins.add(position, p);
    }

    /**
     * Löscht alle Pins der Kante
     */
    public void clearPoints() {
        pins.clear();
    }

    /**
     * Entfernt einen Pin von der Kante
     * @param pin der zu entfernende Pin
     */
    public void removePin(V_Pin pin) {
        pins.remove(pin);
    }

    /**
     * Es wird der Index (innerhalb der Pins der Kante) desjenigen Pins der kante zurückgeliefert,
     * nach dem der übergebene Punkt der kante nahe liegt. Gibt es keinen solchen
     * Pin, wird -1 zurückgegeben.
     *  @param pnt Punkt, dessen Nähe zur Kante geprüft werden soll
     *  @return Index des Pins der Kante, nach dem der Kantenabschnitt beginnt, dem der übergebene
     *  Punkt nahe liegt.
     */
    public int nearestPoint(Point pnt) {
        Line2D line = new Line2D.Double();
        Point2D point = new Point2D.Double();

        point.setLocation(pnt.getX(), pnt.getY());

        for(int i = 1; i < pins.size(); i++) {
            line.setLine(pins.get(i-1).getPointX(), pins.get(i-1).getPointY(), pins.get(i).getPointX(), pins.get(i).getPointY());
            if(line.ptLineDist(point) < 1.57) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Testet, ob der übergebene Pin Teil dieser Kante ist.
     * @param pin Der zu testende Pin.
     * @return true, wenn Pin Teil der Kante ist, false sonst.
     */
    public boolean containsPin(V_Pin pin) {
        if(pins.contains(pin) == true) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Zeichnet die Kante.
     */
    public void paintOn(Graphics g) {

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        VI_Pin temp = null;

        // nun Link zeichnen
        if(pins.size() > 0) {
            temp = pins.get(0);
        }

        g2d.setColor(colPinNormal);

        // Punkte verbinden
        for(int i = 1; i < pins.size(); i++) {
            g2d.drawLine(temp.getPointX(), temp.getPointY(), pins.get(i).getPointX(), pins.get(i).getPointY());
            temp = pins.get(i);
        }

        if(pins.size() > 1) { // Wenn mindestens zwei Pins vorhanden sind, Pfeil an Zielpunkt zeichnen
            Point pSrc = new Point(pins.get(pins.size()-2).getPointX(), pins.get(pins.size()-2).getPointY());
            Point pDst = new Point(pins.get(pins.size()-1).getPointX(), pins.get(pins.size()-1).getPointY());

            double beta = Math.PI / 10;    // Öffnungswinkel der Pfeilspitze
            double rp = 10;                // Länge der Pfeilspitze

            double alpha;

            if(pDst.x - pSrc.x == 0) {
                // Berechnung von alpha bei gleichen x-Werten von Quell- und Zielpunkt
                if(pDst.y - pSrc.y < 0) {
                    alpha = -Math.PI / 2;
                }
                else if(pDst.y - pSrc.y > 0) {
                    alpha = Math.PI / 2;
                }
                else {
                    // Quell- und Zielpunkt gleich: kein Pfeil
                    return;
                }
                
            }
            else {
                // Berechnung von alpha mit arctan
                alpha = Math.atan((pDst.getY() - pSrc.getY()) / (pDst.getX() - pSrc.getX()));
            }

            double x = pDst.getX() - pSrc.getX();
            double y = pDst.getY() - pSrc.getY();

            if (x < 0 && y >= 0){
                alpha = alpha + Math.PI;
            }
            else if ((x < 0) && (y < 0)) {
                alpha = alpha - Math.PI;
            }

            double gamma = Math.PI / 2 - alpha - beta;
            double delta = alpha - beta;

            double l = rp * Math.sin(gamma);
            double m = rp * Math.cos(gamma);
            
            double o = rp * Math.sin(delta);
            double p = rp * Math.cos(delta);

            Point p1 = new Point();
            p1.setLocation(pDst.getX() - l, pDst.getY() - m);

            Point p2 = new Point();
            p2.setLocation(pDst.getX() - p, pDst.getY() - o);

            int[] xPoints = {p1.x, pDst.x, p2.x};
            int[] yPoints = {p1.y, pDst.y, p2.y};

            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }

    /**
     * Ordnet der View-Kante ihr Pendant der Logik zu
     * @param associatedLink Entsprechung dieser View-Kante in der Logik. 
     */
    public void setAssociatedLogicLink(MI_Link associatedLink) {
        this.associatedLink = associatedLink;
    }

    /**
     * Gibt das Logik-Pendant dieser View-Kante zurück.
     * @return Die dieser View-Kante ensprechende Kante in der Logik.
     */
    public MI_Link getAssociatedLogicLink() {
        return associatedLink;
    }
}

