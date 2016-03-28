/*Brandon Neff
 *Project 3 - Bluetooth Tic Tac Toe
 *COSC 4735 or 4010
 */
package edu.cs4730.tictactoe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class TTTView extends View {
    Paint black, clickColor;
    Bitmap X, O;
    boolean Xwin, Owin;
    public int incr, size=3;
    int	XorO=0, Xwins = 0, Owins = 0;
    int mheight =0, mwidth =0;
    int leftside, rightside, boardwidth;
    final int[][] btboard = new int[][]{{1,2,3},{4,5,6},{7,8,9}};
    public int board[][];
    public Rect myXRect = new Rect();
    public Rect myORect = new Rect();
    public Context myContext;
    TTT_Fragment TTTfrag = new TTT_Fragment();
    String Xplayer = "X player's";
    String Oplayer = "O player's";
    
    // default constructor
    public TTTView(Context context) {
        super(context);
        myContext = context;
        //loads X picture and draws it onto the screen.
        X = BitmapFactory.decodeResource(getResources(), R.drawable.tictactoe_x);
        //loads O picture and draws it onto the screen.
        O = BitmapFactory.decodeResource(getResources(), R.drawable.tictactoe_o);
        setup();
    }
    
    //constructor that is being called
    public TTTView(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
        //loads X picture and draws it onto the screen.
        X = BitmapFactory.decodeResource(getResources(), R.drawable.tictactoe_x);
        //loads O picture and draws it onto the screen.
        O = BitmapFactory.decodeResource(getResources(), R.drawable.tictactoe_o);
        setup();
    }
    
    //3rd constructor used in draw demo
    public TTTView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        myContext = context;
        //loads X picture and draws it onto the screen.
        X = BitmapFactory.decodeResource(getResources(), R.drawable.tictactoe_x);
        //loads O picture and draws it onto the screen.
        O = BitmapFactory.decodeResource(getResources(), R.drawable.tictactoe_o);
        setup();
    }
    
    /*
     * Setups all the default variables.
     */
    public void setup() {
        black = new Paint();
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);
        
        clickColor = new Paint();
        clickColor.setStyle(Paint.Style.FILL);
        
        if (board != null) { board = null; }
        board = new int[size][size];
        
        for(int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                board[i][j] =Color.BLACK;
            }
        }
        
        if (mheight >0) setsizes();  //in case not on screen.
    }
    
    /*
     * Setups up the default sizes of the screen so the board fits
     */
    public void setsizes() {
        incr = (mwidth /(size +2));  //give a margin.
        leftside = incr -1;
        rightside = incr*9;
        boardwidth = incr * size;
        Log.i("setsizes", "incr is " + incr);
    }
    
    /*
     * clears the board and then has the view redraw for new game
     */
    void clearBoard() {
        for(int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                board[i][j] = 0;
            }
        }
        XorO = 0;
        Xwin = false;
        Owin = false;
        invalidate();
    }
    
    void changeX() {
        AlertDialog.Builder alert = new AlertDialog.Builder(myContext);
        alert.setMessage("Do you want X to be the Eagles' logo?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //loads X picture and draws it onto the screen.
                X = BitmapFactory.decodeResource(getResources(), R.drawable.eagles);
                Xplayer = "   Eagles'";
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                
            }
        });
        alert.show();
    }
    void changeO() {
        AlertDialog.Builder alert = new AlertDialog.Builder(myContext);
        alert.setMessage("Do you want O to be the Redskins' logo?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //loads O picture and draws it onto the screen.
                O = BitmapFactory.decodeResource(getResources(), R.drawable.redskins);
                Oplayer = " Redskins'";
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                
            }
        });
        alert.show();
    }
    
    void winner() {
        Xwin = false;
        Owin = false;
        
        for (int i = 0; i<size; i++) {
            //checks for wins horizontally
            if (board[0][i] == 1 && board[1][i] == 1 && board[2][i] == 1) {
                Xwin = true;
            } else if (board[0][i] == 2 && board[1][i] == 2 && board[2][i] == 2) {
                Owin = true;
            }
            //checks for wins vertically
            if (board[i][0] == 1 && board[i][1] == 1 && board[i][2] == 1) {
                Xwin = true;
            } else if (board[i][0] == 2 && board[i][1] == 2 && board[i][2] == 2){
                Owin = true;
            }
            //checks for wins diagonally
            if (board[0][0] == 1 && board[1][1] == 1 && board[2][2] == 1) {
                Xwin = true;
            } else if (board[0][0] == 2 && board[1][1] == 2 && board[2][2] == 2){
                Owin = true;
            }
            if (board[2][0] == 1 && board[1][1] == 1 && board[0][2] == 1) {
                Xwin = true;
            } else if (board[2][0] == 2 && board[1][1] == 2 && board[0][2] == 2) {
                Owin = true;
            }
        }
        
        if (Xwin == true || Owin == true) {
            AlertDialog.Builder alert = new AlertDialog.Builder(myContext);
            if (Xwin == true) {
                TTTfrag.setWin(1);
                Xwins++;
                if (Xplayer == "X player's")
                    alert.setMessage("X wins!");
                else
                    alert.setMessage("Eagles win!");
            }
            else {
                TTTfrag.setWin(2);
                Owins++;
                if (Oplayer == "O player's")
                    alert.setMessage("O wins!");
                else
                    alert.setMessage("Redskins win!");
            }
            alert.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    clearBoard();
                }
            });
            alert.setNegativeButton("See board", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Gets rid of dialog box if cancel is pressed
                }
            });
            alert.show();
        }
    }
    
    /*
     * Draws X or O for players depending on the turn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int x = incr;
        int y = incr + 175;
        
        canvas.drawColor(Color.WHITE);
        
        //draws squares across, then down
        for (int yi = 0; yi<size; yi++) {
            for (int xi =0; xi<size; xi++) {
                canvas.drawRect(x, y, x + incr, y + incr, black);  //draw black box.
                //draws x
                if (board[xi][yi] == 1) {
                    myXRect.set(x, y, x + incr, y + incr);
                    Log.i("onDraw", "Drawing X");
                    Log.i("Set X Placement", xi + ", " + yi);
                    canvas.drawBitmap(X, null, myXRect, black);
                }
                //draws o
                else if (board[xi][yi] == 2) {
                    myORect.set(x, y, x + incr, y + incr);
                    Log.i("onDraw", "Drawing O");
                    Log.i("Set O Placement", xi + ", " + yi);
                    canvas.drawBitmap(O, null, myORect, black);
                }
                x+=incr; //move to next square across
            }
            x = incr;
            y += incr;
        }
        if (XorO == 9 && Xwin == false && Owin == false){
            TTTfrag.setWin(3);
            AlertDialog.Builder alert = new AlertDialog.Builder(myContext);
            alert.setMessage("CAT game! The game ended in a draw.");
            alert.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {clearBoard();
                }
            });
            alert.setNegativeButton("See board", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    
                }
            });
            alert.show();
        }
    }
    
    /*
     * places opponent character in correct location
     */
    void btTTT(int placeXO, String whichXO) {
        for (int y = 0; y<size; y++){
            for (int x = 0; x<size; x++) {
                if (board[y][x] != 1 && board[y][x] != 2) {
                    Log.i("place given: " + placeXO, "Bluetooth board: " + btboard[x][y]);
                    if (placeXO == btboard[x][y] && whichXO.equals("X")) {
                        board[y][x] = 1;
                        XorO++;
                    }
                    else if (placeXO == btboard[x][y] && whichXO.equals("O")) {
                        board[y][x] = 2;
                        XorO++;
                    }
                }
            }
        }
    }
    
    /*
     * used by the ontouch event to figure out which box (if any) was "touched"
     */
    boolean where(int x, int y) {
        int cx, cy;
        if ((y>= leftside && y<rightside && x>= leftside && x<rightside)){
            y-=(incr + 175); x-=incr; //simplifies the math here.
            cx = x/incr;
            cy = y/incr;
            if (cx <size && cy <size) {
                if (board[cx][cy] != 1 && board[cx][cy] != 2) {
                    Log.i("onDraw", "Board = " + cx + ", " + cy);
                    if (Xwin == false && Owin == false) {
                        TTTfrag.setPlacement(cx,cy);
                    }
                }
                else
                    XorO--;
            }
            else {
                Log.i("where", "Error in Where, cx="+cx+" cy="+cy);
                return false;
            }
            return true;
        }
        return false;
    }
    
    /*
     * sets location of touch
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        // Gets new x and y touch positions
        int x = (int) event.getX();
        int y = (int) event.getY();
        
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("onTouchEvent", "Action_down");
                where(x, y);
                break;
                
        }
        return true;
    }
    
    /*
     * Gets/sets the size of the view.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        Log.i("MSW", ""+getMeasuredWidth());
        Log.i("MSH", ""+getMeasuredHeight());
        mwidth = getMeasuredWidth();
        mheight = getMeasuredHeight();
        if (mheight >0 && mwidth > mheight ) {
            mwidth = mheight;
        } else if (mheight ==0) {
            mheight = mwidth;
        }
        setsizes();
        setMeasuredDimension(mwidth, mheight);
    }
}
