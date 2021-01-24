import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.io.File;

public class ChessGUI {
    private JPanel gui = new JPanel(new BorderLayout());
    private ChessButton[][] chessBoardSquares = new ChessButton[8][8];
    private JToolBar tools = new JToolBar();
    private Piece[][] squareToPiece = new Piece[8][8];
    private Image[] chessPieceImages = new Image[12];
    private JPanel chessBoard;
    private JLabel whoseTurn;
    private JButton newGameAction;

    public ChessGUI() {
        initializeGui();
    }

    public void initializeGui() {
        // create the images for the chess pieces
        createImages();
        // set up the main GUI
        gui.setBorder(new EmptyBorder(10, 10, 10, 10));

        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        JButton newGameAction = new JButton("Start New Game");
        newGameAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupNewGame();
            }
        });
        tools.add(newGameAction);
        tools.addSeparator();
        whoseTurn = new JLabel("White's Turn");
        tools.add(whoseTurn);
        
        chessBoard = new JPanel(new GridLayout(8, 8));
        chessBoard.setBorder(new CompoundBorder(new EmptyBorder(8,8,8,8),new LineBorder(Color.BLACK)));
        
        Color skyBlue = new Color(135, 206, 235);
        chessBoard.setBackground(skyBlue); //background set to sky blue

        JPanel boardConstrain = new JPanel(new GridBagLayout());
        boardConstrain.setBackground(skyBlue);
        boardConstrain.add(chessBoard);
        gui.add(boardConstrain);
        // create the chess board squares
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessButton b = new ChessButton(i, j);
                b.setMargin(buttonMargin);
                ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)); //transparent icon
                b.setIcon(icon);
                if (j % 2 == i % 2) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
                chessBoardSquares[i][j] = b;
            }
        }
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {                
                chessBoardSquares[i][j].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {                   
                    selectSquare((ChessButton)e.getSource());
                }
                });
                chessBoard.add(chessBoardSquares[i][j]);               
            }
        }
        
    }

    public JPanel getGui() {
        return gui;
    }

    public void createImages() {
        try {
            for (int i = 0; i < 12; i++) {              
                BufferedImage bi = ImageIO.read(new File("yourImageName"+i+".png"));
                chessPieceImages[i] = bi;                
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Initializes the icons of the initial chess board piece places
     */
    public void setupNewGame() {
        tools.remove(whoseTurn);
        whoseTurn = new JLabel("White's Turn");
        tools.add(whoseTurn);
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                chessBoardSquares[r][c].setIcon(null);
                squareToPiece[r][c] = null;
                colorLastMoved = "black";
            }
        }
        // set up the black pieces
        int[] startingRowInts = {2,3,4,1,0,4,3,2}; //index of each piece in chessPieceImages array
        String[] startingRowStrings = {"rook","knight","bishop","queen","king","bishop","knight","rook"};
        for (int i = 0; i < 8; i++) {
            chessBoardSquares[0][i].setIcon(new ImageIcon(chessPieceImages[startingRowInts[i]]));
            squareToPiece[0][i] = new Piece("black",startingRowStrings[i]);
        }
        for (int i = 0; i < 8; i++) {
            chessBoardSquares[1][i].setIcon(new ImageIcon(chessPieceImages[5]));
            squareToPiece[1][i] = new Piece("black","pawn");
        }
        // set up the white pieces
        for (int i = 0; i < 8; i++) {
            chessBoardSquares[6][i].setIcon(new ImageIcon(chessPieceImages[11]));
            squareToPiece[6][i] = new Piece("white","pawn");
        }
        for (int i = 0; i < 8; i++) {
            chessBoardSquares[7][i].setIcon(new ImageIcon(chessPieceImages[6+startingRowInts[i]]));
            squareToPiece[7][i] = new Piece("white",startingRowStrings[i]);
        }
    }
    String colorLastMoved = "black";
    boolean hasPieceSelection = false;
    int selectedX;
    int selectedY;
    ChessButton selectedCB;
    String selectedColor;
    String selectedType;
    boolean isWhiteInCheck = false;
    boolean isBlackInCheck = false;
    boolean enPassantAvailable = false;
    int enPassantColumn = -1;
    String moveType = "normal";  //normal, capture, en passant, promotion
    Piece tempCaptured = null;
    public void selectSquare(ChessButton b)
    {
        b.setBackground(new Color(250,250,210));
        int positionX = b.getPosX();
        int positionY = b.getPosY();
        if (hasPieceSelection) {
            selectedColor = squareToPiece[selectedX][selectedY].getColor();
            selectedType = squareToPiece[selectedX][selectedY].getType();
            if (isValid(new Piece(selectedColor, selectedType), selectedX, selectedY, positionX, positionY) && selectedColor != colorLastMoved) {  
                if (isMoveCastle(selectedX, selectedY, positionX, positionY) != null 
                    && isMoveCastle(selectedX, selectedY, positionX, positionY).equals("whitekingside")){
                    castle("white","kingside");
                }
                else if (isMoveCastle(selectedX, selectedY, positionX, positionY) != null 
                        && isMoveCastle(selectedX, selectedY, positionX, positionY).equals("whitequeenside")){
                    castle("white","queenside");
                }
                else if (isMoveCastle(selectedX, selectedY, positionX, positionY) != null 
                        && isMoveCastle(selectedX, selectedY, positionX, positionY).equals("blackkingside")){
                    castle("black","kingside");
                }
                else if (isMoveCastle(selectedX, selectedY, positionX, positionY) != null 
                        && isMoveCastle(selectedX, selectedY, positionX, positionY).equals("blackqueenside")){
                    castle("black","queenside");
                } 
                else if (isMoveEnPassant(new Piece(selectedColor, selectedType), selectedX, selectedY, positionX, positionY) && positionY == enPassantColumn){
                    moveType = "en passant";
                    if (selectedColor.equals("white")){
                        tempCaptured = new Piece("black", "pawn");
                    }
                    if (selectedColor.equals("black")){
                        tempCaptured = new Piece("white", "pawn");
                    }
                    enPassant(selectedX, selectedY, positionX, positionY, selectedColor);
                }
                else if (isMovePromotion(new Piece(selectedColor, selectedType), selectedX, selectedY, positionX, positionY)){
                    moveType = "promotion";
                    promote(selectedX, selectedY, positionX, positionY, selectedColor);
                }
                else {
                    moveType = findMoveType(new Piece(selectedColor, selectedType), selectedX, selectedY, positionX, positionY);
                    if (moveType.equals("capture")){
                         tempCaptured = new Piece(squareToPiece[positionX][positionY].getColor(), squareToPiece[positionX][positionY].getType());
                    }
                    chessBoardSquares[selectedX][selectedY].setIcon(null);
                    squareToPiece[selectedX][selectedY] = null;
                    chessBoardSquares[positionX][positionY].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(selectedColor, selectedType)]));
                    squareToPiece[positionX][positionY] = new Piece(selectedColor, selectedType);                       
                }
                if (isInCheck(selectedColor)){
                    if (moveType.equals("capture")){
                        chessBoardSquares[positionX][positionY].setIcon(null);
                        squareToPiece[positionX][positionY] = null;
                        chessBoardSquares[positionX][positionY].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(tempCaptured.getColor(), tempCaptured.getType())]));
                        squareToPiece[positionX][positionY] = tempCaptured;
                        chessBoardSquares[selectedX][selectedY].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(selectedColor, selectedType)]));
                        squareToPiece[selectedX][selectedY] = new Piece(selectedColor, selectedType);
                    }
                    if (moveType.equals("en passant")){
                        if(tempCaptured.getColor().equals("black")){
                            chessBoardSquares[selectedX][selectedY].setIcon(new ImageIcon(chessPieceImages[11]));
                            squareToPiece[selectedX][selectedY] = new Piece(selectedColor, selectedType);
                            chessBoardSquares[positionX+1][positionY].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(tempCaptured.getColor(), tempCaptured.getType())]));
                            squareToPiece[positionX+1][positionY] = tempCaptured;
                            chessBoardSquares[positionX][positionY].setIcon(null);
                            squareToPiece[positionX][positionY] = null;
                        }
                        if(tempCaptured.getColor().equals("white")){
                            chessBoardSquares[selectedX][selectedY].setIcon(new ImageIcon(chessPieceImages[5]));
                            squareToPiece[selectedX][selectedY] = new Piece(selectedColor, selectedType);
                            chessBoardSquares[positionX-1][positionY].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(tempCaptured.getColor(), tempCaptured.getType())]));
                            squareToPiece[positionX-1][positionY] = tempCaptured;
                            chessBoardSquares[positionX][positionY].setIcon(null);
                            squareToPiece[positionX][positionY] = null;
                        }
                    }
                    if (moveType.equals("promotion")){
                        chessBoardSquares[positionX][positionY].setIcon(null);
                        squareToPiece[positionX][positionY] = null;
                        chessBoardSquares[selectedX][selectedY].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(selectedColor, "pawn")]));
                        squareToPiece[selectedX][selectedY] = new Piece(selectedColor, "pawn");
                    }
                    if (moveType.equals("normal")){
                        chessBoardSquares[positionX][positionY].setIcon(null);
                        squareToPiece[positionX][positionY] = null;
                        chessBoardSquares[selectedX][selectedY].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(selectedColor, selectedType)]));
                        squareToPiece[selectedX][selectedY] = new Piece(selectedColor, selectedType);
                    }
                }
                else {   
                    if (colorLastMoved.equals("white")){
                            colorLastMoved = "black";
                            enPassantAvailable = false;
                            tools.remove(whoseTurn);
                            whoseTurn = new JLabel("White's Turn");
                            tools.add(whoseTurn);
                    }
                    else if (colorLastMoved.equals("black")){
                            colorLastMoved = "white";
                            enPassantAvailable = false;
                            tools.remove(whoseTurn);
                            whoseTurn = new JLabel("Black's Turn");
                            tools.add(whoseTurn);
                    }
                    if (selectedColor.equals("white") && selectedType.equals("pawn") && selectedX - positionX == 2 && selectedX == 6 && selectedY - positionY == 0){
                        enPassantColumn = selectedY;
                        enPassantAvailable = true;
                    } else if (selectedColor.equals("black") && selectedType.equals("pawn") && selectedX - positionX == -2 && selectedX == 1 && selectedY - positionY == 0){
                        enPassantColumn = selectedY;
                        enPassantAvailable = true;
                    }
                }
                isWhiteInCheck = isInCheck("white");
                isBlackInCheck = isInCheck("black");
                updateCastlingRights();
                String status1 = isInCheckmate("white");
                String status2 = isInCheckmate("black");
                if (isInCheckmate("white") != null){
                    System.out.println(status1);
                }
                if (isInCheckmate("black") != null){
                    System.out.println(status2);
                }
            }
            hasPieceSelection = false;   
            if (selectedX % 2 == selectedY % 2) {
                selectedCB.setBackground(Color.WHITE);
            } else {
                selectedCB.setBackground(Color.BLACK);
            }
            if (positionX % 2 == positionY % 2) {
                b.setBackground(Color.WHITE);
            } else {
                b.setBackground(Color.BLACK);
            }
        }
        else {
            if (!(squareToPiece[positionX][positionY] == null)){
                hasPieceSelection = true;
                selectedX = positionX;
                selectedY = positionY;
                selectedCB = b;
            }
            else {
                if (positionX % 2 == positionY % 2) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
            }
        }
        paintBoard();
    }
    
    public boolean isPathClear(int row, int col, int nRow, int nCol, String color) //this method is used to determine whether the king is safe
    {
        if(row == nRow)
        {
            int incr = 1;
            if(nCol == col)
            {
                return false;
            }
            else if(nCol < col)
            {
                incr = -1;
            }
            for(int i = col + incr; i != nCol; i += incr)
            {
                if (squareToPiece[row][i] != null && !(squareToPiece[row][i].getType().equals("king") && !squareToPiece[row][i].getColor().equals(color))){
                    return false;
                }
            }
            return true;
        }
        else if (col == nCol)
        {
            int incr = 1;
            if(nRow == row)
            {
                return false;
            }
            else if(nRow < row)
            {
                incr = -1;
            }
            for(int i = row + incr; i != nRow; i += incr)
            {
                if (squareToPiece[i][col] != null && !(squareToPiece[i][col].getType().equals("king") && !squareToPiece[i][col].getColor().equals(color))){
                    return false;
                }
            }
            return true;
        }
        else if (Math.abs(nRow - row) == Math.abs(nCol - col))
        {
            int incrRow = 1;
            int incrCol = 1;
            if (nRow == row || nCol == col){
                return false;
            }
            if (nRow < row && nCol < col){
                incrRow = -1;
                incrCol = -1;
            }
            else if (nRow > row && nCol < col){
                incrCol = -1;
            }
            else if (nRow < row && nCol > col){
                incrRow = -1;
            }
            int j = col + incrCol;
            for(int i = row + incrRow; i != nRow; i += incrRow){
                if (squareToPiece[i][j] != null && !(squareToPiece[i][j].getType().equals("king") && !squareToPiece[i][j].getColor().equals(color))){
                    return false;
                }
                j += incrCol;
            }
            return true;
        }
        return false;
    }
        
    public boolean isValid(Piece p, int row, int col, int nRow, int nCol) // TO DO: ADD PROMOTION
    {
        switch (p.getColor()+" "+p.getType()) {
            case "black king": 
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("white")))
                    && ((nCol == col+1 || nCol == col || nCol == col-1 ) && ( nRow == row+1 || nRow == row || nRow == row-1))
                    && !isSquareControlled(nRow, nCol, "white")){ 
                            return true;                
                } else if (nRow == 0 && nCol == 6 && blackKingsideCastle && !isInCheck("black") && squareToPiece[0][5] == null && squareToPiece[0][6] == null 
                    && !isSquareControlled(0, 5, "white") && !isSquareControlled(0, 6, "white")){
                            return true;
                } else if (nRow == 0 && nCol == 2 && blackQueensideCastle && !isInCheck("black") && squareToPiece[0][3] == null && squareToPiece[0][2] == null 
                    && !isSquareControlled(0, 3, "white") && !isSquareControlled(0, 2, "white")){
                            return true;  
                }
                break;
            case "black queen":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("white")))
                        && ((nCol == col || Math.abs(nRow - row) == Math.abs(nCol - col) || nRow == row)) && isPathClear(row,col,nRow,nCol,"black")){ 
                            return true;
                }
                break;
            case "black rook":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("white")))
                        && ((nCol == col || nRow == row)) && isPathClear(row,col,nRow,nCol,"black")){
                            return true;
                }
                break;
            case "black knight":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("white")))
                        && ((Math.abs(nCol - col) == 1 && Math.abs(nRow - row) == 2) || (Math.abs(nCol - col) == 2 && Math.abs(nRow - row) == 1))){
                            return true;
                }
                break;
            case "black bishop":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("white")))
                        && (Math.abs(nRow - row) == Math.abs(nCol - col)) && isPathClear(row,col,nRow,nCol,"black")){   
                            return true;
                }
                break;
            case "black pawn":
                if ((squareToPiece[nRow][nCol] != null) && squareToPiece[nRow][nCol].getColor().equals("white") && ((nCol == col + 1 || nCol == col - 1) && nRow == row + 1 )){ //row decreases for black pawns         
                            return true;
                } else if ((nRow == row + 1 || (row == 1 && row + 2 == nRow)) && (nCol == col) && squareToPiece[nRow][nCol] == null && isPathClear(row,col,nRow,nCol,"black")){       
                            return true;
                } else if (isEnPassantLegal(row,col,nRow,nCol,"black")){   
                            return true;
                }
                break;
            case "white king":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("black")))
                    && ((nCol == col+1 || nCol == col || nCol == col-1 ) && ( nRow == row+1 || nRow == row || nRow == row-1))
                    && !isSquareControlled(nRow, nCol, "black")){      
                            return true;
                } else if (nRow == 7 && nCol == 6 && whiteKingsideCastle && !isInCheck("white") && squareToPiece[7][5] == null && squareToPiece[7][6] == null 
                    && !isSquareControlled(7, 5, "black") && !isSquareControlled(7, 6, "black")){
                            return true;
                } else if (nRow == 7 && nCol == 2 && whiteQueensideCastle && !isInCheck("white") && squareToPiece[7][3] == null && squareToPiece[7][2] == null 
                    && !isSquareControlled(7, 3, "black") && !isSquareControlled(7, 2, "black")){
                            return true;  
                }
                break;
            case "white queen":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("black")))
                        && ((nCol == col || Math.abs(nRow - row) == Math.abs(nCol - col) || nRow == row)) && isPathClear(row,col,nRow,nCol,"white")){  
                            return true;
                }
                break;
            case "white rook":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("black")))
                            && ((nCol == col || nRow == row)) && isPathClear(row,col,nRow,nCol,"white")){ 
                            return true;
                }
                break;
            case "white knight":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("black")))
                        && ((Math.abs(nCol - col) == 1 && Math.abs(nRow - row) == 2) || (Math.abs(nCol - col) == 2 && Math.abs(nRow - row) == 1))){
                            return true;
                }
                break;
            case "white bishop":
                if (((squareToPiece[nRow][nCol] == null) || (squareToPiece[nRow][nCol].getColor().equals("black")))
                        && (Math.abs(nRow - row) == Math.abs(nCol - col)) && isPathClear(row,col,nRow,nCol,"white")){  
                            return true;
                }
                break;
            case "white pawn":   
                if ((squareToPiece[nRow][nCol] != null) && squareToPiece[nRow][nCol].getColor().equals("black") && ((nCol == col + 1 || nCol == col - 1) && nRow == row - 1 )){ //row increases for white pawns                
                            return true;
                } else if ((nRow == row - 1 || (row == 6 && row - 2 == nRow)) && (nCol == col) && squareToPiece[nRow][nCol] == null && isPathClear(row,col,nRow,nCol,"white")){
                            return true;
                } else if (isEnPassantLegal(row,col,nRow,nCol,"white")){  
                            return true;
                }
                break;
        }
        return false;
    }
    
    public boolean isValid2(Piece p, int row, int col, int nRow, int nCol)
    {
        switch (p.getColor()+" "+p.getType()) {
            case "black king": 
                if ((nCol == col+1 || nCol == col || nCol == col-1 ) && ( nRow == row+1 || nRow == row || nRow == row-1)){ 
                            return true;                
                }
                break;
            case "black queen":
                if (((nCol == col || Math.abs(nRow - row) == Math.abs(nCol - col) || nRow == row)) && isPathClear(row,col,nRow,nCol,"black")){ 
                            return true;
                }
                break;
            case "black rook":
                if (((nCol == col || nRow == row)) && isPathClear(row,col,nRow,nCol,"black")){
                            return true;
                }
                break;
            case "black knight":
                if (((Math.abs(nCol - col) == 1 && Math.abs(nRow - row) == 2) || (Math.abs(nCol - col) == 2 && Math.abs(nRow - row) == 1))){
                            return true;
                }
                break;
            case "black bishop":
                if ((Math.abs(nRow - row) == Math.abs(nCol - col)) && isPathClear(row,col,nRow,nCol,"black")){   
                            return true;
                }
                break;
            //pawns only capture diagonally
            case "white king":
                if (((nCol == col+1 || nCol == col || nCol == col-1 ) && ( nRow == row+1 || nRow == row || nRow == row-1))){      
                            return true;
                }
                break;
            case "white queen":
                if (((nCol == col || Math.abs(nRow - row) == Math.abs(nCol - col) || nRow == row)) && isPathClear(row,col,nRow,nCol,"white")){  
                            return true;
                }
                break;
            case "white rook":
                if (((nCol == col || nRow == row)) && isPathClear(row,col,nRow,nCol,"white")){ 
                            return true;
                }
                break;
            case "white knight":
                if (((Math.abs(nCol - col) == 1 && Math.abs(nRow - row) == 2) || (Math.abs(nCol - col) == 2 && Math.abs(nRow - row) == 1))){
                            return true;
                }
                break;
            case "white bishop":
                if ((Math.abs(nRow - row) == Math.abs(nCol - col)) && isPathClear(row,col,nRow,nCol,"white")){  
                            return true;
                }
                break;
            //pawns only capture diagonally
        }
        return false;
    }
    
    public boolean isInCheck(String color)
    {
        int row = 0;
        int col = 0;
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                if (squareToPiece[r][c] != null && squareToPiece[r][c].getColor().equals(color) && squareToPiece[r][c].getType().equals("king")){
                    row = r;
                    col = c;
                }
            }
        }
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                if (squareToPiece[r][c] != null && !squareToPiece[r][c].getColor().equals(color)){
                    Piece x = squareToPiece[r][c];
                    if (isValid2(x, r, c, row, col)){
                        return true;
                    } else if (x.getColor().equals("white") && x.getType().equals("pawn")){
                        if (r-row == 1 && Math.abs(c-col) == 1){
                            return true;
                        }
                    }
                    else if (x.getColor().equals("black") && x.getType().equals("pawn")){
                        if (r-row == -1 && Math.abs(c-col) == 1){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isSquareControlled(int row, int col, String color)
    {
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                if (squareToPiece[r][c] != null && squareToPiece[r][c].getColor().equals(color)){
                    Piece x = squareToPiece[r][c];
                    if (isValid2(x, r, c, row, col)){
                        return true;
                    }
                    else if (x.getColor().equals("white") && x.getType().equals("pawn")){
                        if (r-row == 1 && Math.abs(c-col) == 1){
                            return true;
                        }
                    }
                    else if (x.getColor().equals("black") && x.getType().equals("pawn")){
                        if (r-row == -1 && Math.abs(c-col) == 1){
                            return true;
                        }
                    }                    
                }
            }
        }
        return false;
    }
    
    public String findMoveType(Piece p, int row, int col, int nRow, int nCol)
    {        
        if (p.getType().equals("pawn") && p.getColor().equals("white") && Math.abs(nCol-col) == 1 && squareToPiece[nRow][nCol] == null && nRow == 2){
            return "en passant";
        }
        if (p.getType().equals("pawn") && p.getColor().equals("black") && Math.abs(nCol-col) == 1 && squareToPiece[nRow][nCol] == null && nRow == 5){
            return "en passant";
        }
        if (squareToPiece[nRow][nCol] != null){
            return "capture";
        }
        if (p.getType().equals("pawn") && p.getColor().equals("white") && nRow == 0){
            return "promotion";
        }
        if (p.getType().equals("pawn") && p.getColor().equals("black") && nRow == 7){
            return "promotion";
        }
        if (squareToPiece[nRow][nCol] == null){
            return "normal";
        }
        return null;
    }
    boolean whiteKingsideCastle = true;
    boolean whiteQueensideCastle = true;
    boolean blackKingsideCastle = true;
    boolean blackQueensideCastle = true;
    public void updateCastlingRights()
    {
        if (whiteKingsideCastle && (squareToPiece[7][4] == null || squareToPiece[7][7] == null || !squareToPiece[7][7].getColor().equals("white"))){
            whiteKingsideCastle = false;
        }
        if (whiteQueensideCastle && (squareToPiece[7][4] == null || squareToPiece[7][0] == null || !squareToPiece[7][0].getColor().equals("white"))){
            whiteQueensideCastle = false;
        }
        if (blackKingsideCastle && (squareToPiece[0][4] == null || squareToPiece[0][7] == null || !squareToPiece[0][7].getColor().equals("black"))){
            blackKingsideCastle = false;
        }
        if (blackQueensideCastle && (squareToPiece[0][4] == null || squareToPiece[0][0] == null || !squareToPiece[0][0].getColor().equals("black"))){
            blackQueensideCastle = false;
        }
    }
    
    public void castle(String color, String side)
    {
        if (color.equals("white") && side.equals("kingside")){
            chessBoardSquares[7][4].setIcon(null);
            squareToPiece[7][4] = null;
            chessBoardSquares[7][7].setIcon(null);
            squareToPiece[7][7] = null;
            chessBoardSquares[7][6].setIcon(new ImageIcon(chessPieceImages[6]));
            squareToPiece[7][6] = new Piece("white","king");
            chessBoardSquares[7][5].setIcon(new ImageIcon(chessPieceImages[8]));
            squareToPiece[7][5] = new Piece("white","rook");
        }
        if (color.equals("white") && side.equals("queenside")){
            chessBoardSquares[7][4].setIcon(null);
            squareToPiece[7][4] = null;
            chessBoardSquares[7][0].setIcon(null);
            squareToPiece[7][0] = null;
            chessBoardSquares[7][2].setIcon(new ImageIcon(chessPieceImages[6]));
            squareToPiece[7][2] = new Piece("white","king");
            chessBoardSquares[7][3].setIcon(new ImageIcon(chessPieceImages[8]));
            squareToPiece[7][3] = new Piece("white","rook");
        }
        if (color.equals("black") && side.equals("kingside")){
            chessBoardSquares[0][4].setIcon(null);
            squareToPiece[0][4] = null;
            chessBoardSquares[0][7].setIcon(null);
            squareToPiece[0][7] = null;
            chessBoardSquares[0][6].setIcon(new ImageIcon(chessPieceImages[0]));
            squareToPiece[0][6] = new Piece("black","king");
            chessBoardSquares[0][5].setIcon(new ImageIcon(chessPieceImages[2]));
            squareToPiece[0][5] = new Piece("black","rook");
        }
        if (color.equals("black") && side.equals("queenside")){
            chessBoardSquares[0][4].setIcon(null);
            squareToPiece[0][4] = null;
            chessBoardSquares[0][0].setIcon(null);
            squareToPiece[0][0] = null;
            chessBoardSquares[0][2].setIcon(new ImageIcon(chessPieceImages[0]));
            squareToPiece[0][2] = new Piece("black","king");
            chessBoardSquares[0][3].setIcon(new ImageIcon(chessPieceImages[2]));
            squareToPiece[0][3] = new Piece("black","rook");
        }
    }
    
    public String isMoveCastle(int row, int col, int nRow, int nCol)
    {
        if(squareToPiece[row][col].getColor().equals("white") && squareToPiece[row][col].getType().equals("king")
            && row == 7 && col == 4 && nRow == 7 && nCol == 6){
                return "whitekingside";
        }
        if(squareToPiece[row][col].getColor().equals("white") && squareToPiece[row][col].getType().equals("king")
            && row == 7 && col == 4 && nRow == 7 && nCol == 2){
                return "whitequeenside";
        }
        if(squareToPiece[row][col].getColor().equals("black") && squareToPiece[row][col].getType().equals("king")
            && row == 0 && col == 4 && nRow == 0 && nCol == 6){
                return "blackkingside";
        }
        if(squareToPiece[row][col].getColor().equals("black") && squareToPiece[row][col].getType().equals("king")
            && row == 0 && col == 4 && nRow == 0 && nCol == 2){
                return "blackqueenside";
        }
        return null;
    }

    public boolean isEnPassantLegal(int row, int col, int nRow, int nCol, String color)
    {
         if (color.equals("white") && Math.abs(nCol-col) == 1 && Math.abs(nRow-row) == 1 && squareToPiece[nRow][nCol] == null && nRow == 2 
            && squareToPiece[nRow+1][nCol] != null && squareToPiece[nRow+1][nCol].getColor().equals("black")
            && squareToPiece[nRow+1][nCol].getType().equals("pawn")){             
                            return true;
         }
         if (color.equals("black") && Math.abs(nCol-col) == 1 && Math.abs(nRow-row) == 1 && squareToPiece[nRow][nCol] == null && nRow == 5 
            && squareToPiece[nRow-1][nCol] != null && squareToPiece[nRow-1][nCol].getColor().equals("white")
            && squareToPiece[nRow-1][nCol].getType().equals("pawn")){             
                            return true;
         }
         return false;
    }
    
    public boolean isMoveEnPassant(Piece p, int row, int col, int nRow, int nCol) //piece p on row,col
    {
        if (p.getType().equals("pawn") && p.getColor().equals("white") && Math.abs(nCol-col) == 1 && squareToPiece[nRow][nCol] == null && nRow == 2){
            return true;
        }
        if (p.getType().equals("pawn") && p.getColor().equals("black") && Math.abs(nCol-col) == 1 && squareToPiece[nRow][nCol] == null && nRow == 5){
            return true;
        }
        return false;
    }
    
    public void enPassant(int row, int col, int nRow, int nCol, String color)
    {
        if(color.equals("white")){
            chessBoardSquares[row][col].setIcon(null);
            squareToPiece[row][col] = null;
            chessBoardSquares[nRow+1][nCol].setIcon(null);
            squareToPiece[nRow+1][nCol] = null;
            chessBoardSquares[nRow][nCol].setIcon(new ImageIcon(chessPieceImages[11]));
            squareToPiece[nRow][nCol] = new Piece("white", "pawn");
        }
        if(color.equals("black")){
            chessBoardSquares[row][col].setIcon(null);
            squareToPiece[row][col] = null;
            chessBoardSquares[nRow-1][nCol].setIcon(null);
            squareToPiece[nRow-1][nCol] = null;
            chessBoardSquares[nRow][nCol].setIcon(new ImageIcon(chessPieceImages[5]));
            squareToPiece[nRow][nCol] = new Piece("black", "pawn");
        }
    }    
    
    public void promote(int row, int col, int nRow, int nCol, String color)
    {
        if (color.equals("white")){
            chessBoardSquares[row][col].setIcon(null);
            squareToPiece[row][col] = null;
            chessBoardSquares[nRow][nCol].setIcon(new ImageIcon(chessPieceImages[7]));
            squareToPiece[nRow][nCol] = new Piece("white","queen");
        }
        if (color.equals("black")){
            chessBoardSquares[row][col].setIcon(null);
            squareToPiece[row][col] = null;
            chessBoardSquares[nRow][nCol].setIcon(new ImageIcon(chessPieceImages[7]));
            squareToPiece[nRow][nCol] = new Piece("black","queen");
        }
    }
    
    public boolean isMovePromotion(Piece p, int row, int col, int nRow, int nCol) //piece p on row,col
    {
        if (p.getType().equals("pawn") && p.getColor().equals("white") && nRow == 0){
            return true;
        }
        if (p.getType().equals("pawn") && p.getColor().equals("black") && nRow == 7){
            return true;
        }
        return false;
    }
    
    public String isInCheckmate(String color)
    {       
        boolean inCheck = false;
        if (isInCheck(color)){
            inCheck = true;
        }
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                if (squareToPiece[r][c] != null && squareToPiece[r][c].getColor().equals(color)){
                    Piece x = squareToPiece[r][c];
                    for (int i = 0; i < 8; i++){
                        for (int j = 0; j < 8; j++){
                            if (isValid(x, r, c, i, j)){   
                                Piece tempCaptured1 = null;
                                String mt = findMoveType(x,r,c,i,j);
                                boolean stillInCheck = true;
                                if (mt.equals("capture")){
                                     tempCaptured1 = new Piece(squareToPiece[i][j].getColor(), squareToPiece[i][j].getType());
                                }
                                if (mt.equals("en passant")){
                                    if (x.getColor().equals("white")){
                                        tempCaptured1 = new Piece("black","pawn");
                                    }
                                    if (x.getColor().equals("black")){
                                        tempCaptured1 = new Piece("white","pawn");
                                    }
                                }
                                chessBoardSquares[r][c].setIcon(null);
                                squareToPiece[r][c] = null;
                                chessBoardSquares[i][j].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(x.getColor(),x.getType())]));
                                squareToPiece[i][j] = new Piece(x.getColor(),x.getType());
                                if (!isInCheck(color)){
                                    stillInCheck = false;
                                }
                                if (mt.equals("capture")){
                                    chessBoardSquares[i][j].setIcon(null);
                                    squareToPiece[i][j] = null;
                                    chessBoardSquares[i][j].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(tempCaptured1.getColor(), tempCaptured1.getType())]));
                                    squareToPiece[i][j] = tempCaptured1;
                                    chessBoardSquares[r][c].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(x.getColor(), x.getType())]));
                                    squareToPiece[r][c] = new Piece(x.getColor(), x.getType());
                                }
                                if (mt.equals("en passant")){
                                    if(tempCaptured1.getColor().equals("black")){
                                        chessBoardSquares[r][c].setIcon(new ImageIcon(chessPieceImages[11]));
                                        squareToPiece[r][c] = new Piece(x.getColor(), x.getType());
                                        chessBoardSquares[i+1][j].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(tempCaptured1.getColor(), tempCaptured1.getType())]));
                                        squareToPiece[i+1][j] = tempCaptured1;
                                        chessBoardSquares[i][j].setIcon(null);
                                        squareToPiece[i][j] = null;
                                    }
                                    if(tempCaptured1.getColor().equals("white")){
                                        chessBoardSquares[r][c].setIcon(new ImageIcon(chessPieceImages[5]));
                                        squareToPiece[r][c] = new Piece(x.getColor(), x.getType());
                                        chessBoardSquares[i-1][j].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(tempCaptured1.getColor(), tempCaptured1.getType())]));
                                        squareToPiece[i-1][j] = tempCaptured1;
                                        chessBoardSquares[i][j].setIcon(null);
                                        squareToPiece[i][j] = null;
                                    }
                                }
                                if (mt.equals("promotion")){
                                    chessBoardSquares[i][j].setIcon(null);
                                    squareToPiece[i][j] = null;
                                    chessBoardSquares[r][c].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(x.getColor(), "pawn")]));
                                    squareToPiece[r][c] = new Piece(x.getColor(), "pawn");
                                }
                                if (mt.equals("normal")){
                                    chessBoardSquares[i][j].setIcon(null);
                                    squareToPiece[i][j] = null;
                                    chessBoardSquares[r][c].setIcon(new ImageIcon(chessPieceImages[findPieceIndex(x.getColor(), x.getType())]));
                                    squareToPiece[r][c] = new Piece(x.getColor(), x.getType());
                                }
                                if (!stillInCheck){
                                    return null;
                                }
                            }
                        }
                    }                        
                }
            }
        }            
        if (inCheck){
            return color+" is in checkmate";   
        }
        else {
            return color+" is in stalemate";   
        }
    }
    
    public int findPieceIndex(String color, String type) 
    {
        int index = 0;
        if (color.equals("white")){
            index += 6;
        }
        //index = index + 0 for king
        if (type.equals("queen")){
            index++;
        }
        if (type.equals("rook")){
            index += 2;
        }
        if (type.equals("knight")){
            index += 3;
        }
        if (type.equals("bishop")){
            index += 4;
        }
        if (type.equals("pawn")){
            index += 5;
        }
        return index;
    }
    
    public void paintBoard()
    {
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (!(squareToPiece[i][j] == null)){
                    int index = 0;
                    String type = squareToPiece[i][j].getType();
                    String color = squareToPiece[i][j].getColor();
                    if (color.equals("white")){
                        index += 6;
                    }
                    if (type.equals("queen")){
                        index++;
                    }
                    if (type.equals("rook")){
                        index += 2;
                    }
                    if (type.equals("knight")){
                        index += 3;
                    }
                    if (type.equals("bishop")){
                        index += 4;
                    }
                    if (type.equals("pawn")){
                        index += 5;
                    }
                    chessBoardSquares[i][j].setIcon(new ImageIcon(chessPieceImages[index]));
                }
            }
        }
    }

    public static void main(String[] args) {        
        ChessGUI chess = new ChessGUI();
        JFrame f = new JFrame("Jonathan's EOY Project");
        f.add(chess.getGui());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setMinimumSize(f.getSize());
        f.setVisible(true);   
    }
}
