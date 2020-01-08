package view;

import java.awt.Graphics;
import java.awt.Point;

import model.MI_Link;

/**
 * Interface für Kanten.
 * Deklariert notwendigen Funktionen.
 * 
 * @author Uwe Rosner
 */

public interface VI_Link {
    boolean containsPoint(Point mousePosition);
    void setPosition(Point position);
    void paintOn(Graphics g);
    int nearestPoint(Point pnt);
    void setAssociatedLogicLink(MI_Link associatedLink);
    MI_Link getAssociatedLogicLink();
}
