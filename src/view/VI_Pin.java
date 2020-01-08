package view;

/**
 * Interface für Anschlüsse.
 * Deklariert die notwendigen Funktionen.
 * 
 * @author Uwe Rosner
 */

public interface VI_Pin {
    public void setColorHighlighted();
    public void setColorNormal();
    public int getPointX();
    public int getPointY();
    public String getOrientation();
}
