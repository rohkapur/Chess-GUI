
/**
 * Write a description of class Piece here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Piece
{
    // instance variables - replace the example below with your own
    private String color;
    private String type;

    /**
     * Constructor for objects of class Piece
     */
    public Piece(String color, String type)
    {
        this.color = color;
        this.type = type;
    }

    public String getColor()
    {
        return color;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String newType)
    {
        type = newType;
    }
}
