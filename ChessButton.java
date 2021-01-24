import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * Write a description of class ChessButton here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class ChessButton extends JButton //implements Accessible
{
    // instance variables - replace the example below with your own
    private int x;
    private int y;

    /**
     * Constructor for objects of class ChessButton
     */
    public ChessButton(int i, int j)
    {
        x = i;
        y = j;
    }
    
    public void setPosXY(int i, int j)
    {
        x = i;
        y = j;
    }
    
    public int getPosX()
    {
        return x;
    }
    
    public int getPosY()
    {
        return y;
    }
}
