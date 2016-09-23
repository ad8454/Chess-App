package com.example.mychesstcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TableLayout cBoard;
	private Square squares[][];
	private Square oneTouch;
	private boolean hasOneTouch;
	private boolean myTurn;
	private boolean white;
	private TextView tvTurn;
	
	Boolean firstclick=false,con=false;
	String value;
	String ip="";
	OutputStream os=null;
	 PrintWriter out=null;
	 Socket s=null;
	 ServerSocket ss=null;
	 InputStream is=null;
	 Scanner sc=null;
	 String move;
	 ImageButton ib1,ib2;
	 Thread recv;
		Thread send;
		int flag=0;
		
		EditText et;
		
		 Handler h1=new Handler()
		 {
	         @Override public void handleMessage(Message msg)
	         {

	        	 String temp = msg.obj.toString();
	        	 
	        	 Square oneSq = squares[ 7 - Integer.parseInt(temp.substring(0,1))][ 7 - Integer.parseInt(temp.substring(1, 2))];
	        	 Square twoSq = squares[ 7 - Integer.parseInt(temp.substring(2,3))][ 7 - Integer.parseInt(temp.substring(3, 4))];
	             
	        	 playMove(oneSq, twoSq);
	             
	        	 //=========to check the rows======//
	         }
	         
	 };
		
		
		
		
		
		
		
		
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		tvTurn = (TextView) findViewById(R.id.tvTurn);
		et=(EditText)findViewById(R.id.editText1);
		hasOneTouch = false;
		cBoard = (TableLayout) findViewById(R.id.cBoard);
		//genBoard(true);		//remove
		
		
		
		
		//=============Threads===========//
		
		send= new Thread(new Runnable()
        {
               
                public void run()
                {while(true)
            	{
            		if(flag==1)
            		{
            	 try {
     				os =s.getOutputStream();
     				 out=new PrintWriter(os,true);
     				 
     			} catch (IOException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     			}
     			out.println(value);
     			flag=0;
            	}
            	}
                }
        });
		
		
		
		recv= new Thread(new Runnable()
        {
               
                public void run()
                {
                	
                	
                
                
                	 try {
                		 is = s.getInputStream(); 
                		 sc=new Scanner(is);
         			} catch (IOException e) {
         				// TODO Auto-generated catch block
         				e.printStackTrace();
         			}
         			
         			while(true)
         			{
         				
         				Message myMsg = h1.obtainMessage();
         				myMsg.obj = sc.nextLine();
         				h1.sendMessage(myMsg);
         			}
         			
                
        
                }});
		
		//========================================
		
		
		
		
	
		
		
	}
	
	private void genBoard(final boolean white){
		this.white = white;
		squares = new Square[8][8];
		tvTurn.setText("White to Play");
		
		for(int i=0; i<8; i++){
			TableRow tr = new TableRow(this);
			
			for(int j=0; j<8; j++){
				squares[i][j] = new Square(this);
				squares[i][j].setPadding(0, 0, 0, 0);
				
				if((i+j) % 2 == 0)		
					squares[i][j].setWhite(i, j);
					
				else
					squares[i][j].setBlack(i, j);
				
				if(i == 0 || i == 1){
					if(white)
						setInitialPos(i, j, squares[i][j], "black");
					else
						setInitialPos(i, j, squares[i][j], "white");
				}	
				else if(i == 7 || i== 6){
					if(white)
						setInitialPos(i, j, squares[i][j], "white");
					else
						setInitialPos(i, j, squares[i][j], "black");
				}				
					
				
				final int fi = i, fj =j;
				
				squares[i][j].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(myTurn){
							if(hasOneTouch){
								if(oneTouch.getPieceColor().equals(squares[fi][fj].getPieceColor())){
									oneTouch.onNoTouch();
									oneTouch = squares[fi][fj];
									oneTouch.onMyTouch();
								}									
								else{
									hasOneTouch = false;
									oneTouch.onNoTouch();
								if( ! oneTouch.equals(squares[fi][fj]) )
									playMove(oneTouch, squares[fi][fj]);
								}	
							}
							else if(squares[fi][fj].getPieceID() != 0 && ( (white && squares[fi][fj].getPieceColor().equals("white")) || ( !white && squares[fi][fj].getPieceColor().equals("black")) )){
								hasOneTouch = true;
								oneTouch = squares[fi][fj];
								squares[fi][fj].onMyTouch();
							}
						}
					}
				});
				tr.addView(squares[i][j], new TableRow.LayoutParams(j));
			}
			cBoard.addView(tr);
		}
	}
	
	private void setInitialPos(int i, int j, Square square, String color){
		
		if(!white)
			j = 7 - j;
		
		if(i == 0 || i == 7){
			switch(j){
				case 0: square.setPiece(2, color);
						break;
				case 1: square.setPiece(3, color);
						break;
				case 2: square.setPiece(4, color);
						break;
				case 3: square.setPiece(5, color);
						break;
				case 4: square.setPiece(6, color);
						break;
				case 5: square.setPiece(4, color);
						break;
				case 6: square.setPiece(3, color);
						break;
				case 7: square.setPiece(2, color);
						break;
			}
		}
		else if(i == 1 || i == 6)
			square.setPiece(1, color);
	}

	private void playMove(Square oneSq, Square twoSq){
		
		if(chkMove(oneSq, twoSq) || !myTurn ){
			
			int twoTempID = twoSq.getPieceID();
			String twoTempColor = twoSq.getPieceColor();
			
			twoSq.setPiece(oneSq.getPieceID(), oneSq.getPieceColor());
			oneSq.setPiece(0, "");

			for(int i=0; i<8; i++){
				for(int j=0; j<8; j++){
					if(squares[i][j].getPieceID() == 6 && squares[i][j].getPieceColor().equals(twoSq.getPieceColor())){
						if(chkCheck(squares[i][j])){
							
							oneSq.setPiece(twoSq.getPieceID(), twoSq.getPieceColor());
							twoSq.setPiece(twoTempID, twoTempColor);

							Toast.makeText(getApplicationContext(), "  Invalid Move  ", Toast.LENGTH_LONG).show();
							return;
						}
					}
				}
			}
			
			for(int i=0; i<8; i++){
				for(int j=0; j<8; j++){
					if(squares[i][j].getPieceID() == 6 && ! squares[i][j].getPieceColor().equals(twoSq.getPieceColor())){
						if(chkCheck(squares[i][j])){
							String kingCol = "Black";
							if(squares[i][j].getPieceColor().equals("white"))
								kingCol = "White";							
							Toast.makeText(getApplicationContext(), "  "+kingCol+" King is in Check!  ", Toast.LENGTH_LONG).show();
							
						}
					}
				}
			}
			if(myTurn){
				value=oneSq.getRow()+""+oneSq.getCol()+""+twoSq.getRow()+""+twoSq.getCol();
				flag = 1;
			}
			
			myTurn = ! myTurn;
			
			if(myTurn && white)
				tvTurn.setText("White to Play");
			else if(myTurn && !white)
				tvTurn.setText("Black to Play");
			else if(!myTurn && white)
				tvTurn.setText("Black to Play");
			else if(!myTurn && !white)
				tvTurn.setText("White to Play");
			
		}
	}
	
	private boolean chkMove(Square oneSq, Square twoSq){
		
		boolean legal = false;
		
		switch(oneSq.getPieceID()){
			case 1: legal = movePawn(oneSq, twoSq);
					break;
			case 2: legal = moveRook(oneSq, twoSq);
					break;
			case 3: legal = moveKnight(oneSq, twoSq);
					break;
			case 4: legal = moveBishop(oneSq, twoSq);
					break;
			case 5: legal = moveQueen(oneSq, twoSq);
					break;
			case 6: legal = moveKing(oneSq, twoSq);
					break;
		}
		
		return legal;
	}
	
	private boolean chkCheck(Square chkSq){

		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if(squares[i][j].getPieceID() != 0 && ! squares[i][j].getPieceColor().equals(chkSq.getPieceColor())){
					if(chkMove(squares[i][j], chkSq))
						return true;
				}				
			}
		}
		
		return false;
	}
	
	private boolean movePawn(Square oneSq, Square twoSq){
		
		if(oneSq.getPieceColor().equals("white")){
			if(oneSq.getRow() - twoSq.getRow() == 1 && white ){
				if(oneSq.getCol() == twoSq.getCol() && twoSq.getPieceID() == 0)		//move 1 ahead
					return true;
				else if(Math.abs(oneSq.getCol() - twoSq.getCol()) == 1 && twoSq.getPieceColor().equals("black"))	//capture
					return true;
			}
			else if(twoSq.getRow() - oneSq.getRow() == 1 && !white ){
				if(oneSq.getCol() == twoSq.getCol() && twoSq.getPieceID() == 0)		//move 1 ahead
					return true;
				else if(Math.abs(oneSq.getCol() - twoSq.getCol()) == 1 && twoSq.getPieceColor().equals("black"))	//capture
					return true;
			}
			else if(oneSq.getRow() == 6 && twoSq.getRow() == 4 && twoSq.getPieceID() == 0 && squares[5][oneSq.getCol()].getPieceID() == 0 && oneSq.getCol() == twoSq.getCol() && white )		//move 2 ahead
				return true;
			else if(oneSq.getRow() == 1 && twoSq.getRow() == 3 && twoSq.getPieceID() == 0 && squares[2][oneSq.getCol()].getPieceID() == 0 && oneSq.getCol() == twoSq.getCol() && !white )		//move 2 ahead
				return true;
		}
		else if(oneSq.getPieceColor().equals("black")){
			if(oneSq.getRow() - twoSq.getRow() == 1 && !white ){  
				if(oneSq.getCol() == twoSq.getCol() && twoSq.getPieceID() == 0)		//move 1 ahead
					return true;
				else if(Math.abs(twoSq.getCol() - oneSq.getCol()) == 1 && twoSq.getPieceColor().equals("white"))	//capture
					return true;
			}
			else if(twoSq.getRow() - oneSq.getRow() == 1 && white ){ 
				if(oneSq.getCol() == twoSq.getCol() && twoSq.getPieceID() == 0)		//move 1 ahead
					return true;
				else if(Math.abs(twoSq.getCol() - oneSq.getCol()) == 1 && twoSq.getPieceColor().equals("white"))	//capture
					return true;
			}
			else if(oneSq.getRow() == 1 && twoSq.getRow() == 3 && twoSq.getPieceID() == 0 && squares[2][oneSq.getCol()].getPieceID() == 0 && oneSq.getCol() == twoSq.getCol() && white )		//move 2 ahead
				return true;
			else if(oneSq.getRow() == 6 && twoSq.getRow() == 4 && twoSq.getPieceID() == 0 && squares[5][oneSq.getCol()].getPieceID() == 0 && oneSq.getCol() == twoSq.getCol() && !white )		//move 2 ahead
				return true;
		}
		return false;		
	}
	
	private boolean moveRook(Square oneSq, Square twoSq){

		if(oneSq.getRow() == twoSq.getRow()){
			if(oneSq.getCol() < twoSq.getCol()){
				for(int i=oneSq.getCol()+1; i<twoSq.getCol(); i++){
					if(squares[oneSq.getRow()][i].getPieceID() != 0)
						return false;
				}
				return true;
			}
			else if(oneSq.getCol() > twoSq.getCol()){
				for(int i=oneSq.getCol()-1; i>twoSq.getCol(); i--){
					if(squares[oneSq.getRow()][i].getPieceID() != 0)
						return false;
				}
				return true;
			}
		}
		else if(oneSq.getCol() == twoSq.getCol()){
			if(oneSq.getRow() < twoSq.getRow()){
				for(int i=oneSq.getRow()+1; i<twoSq.getRow(); i++){
					if(squares[i][oneSq.getCol()].getPieceID() != 0)
						return false;
				}
				return true;
			}
			else if(oneSq.getRow() > twoSq.getRow()){
				for(int i=oneSq.getRow()-1; i>twoSq.getRow(); i--){
					if(squares[i][oneSq.getCol()].getPieceID() != 0)
						return false;
				}
				return true;
			}
		}
		return false;		
	}
	
	private boolean moveKnight(Square oneSq, Square twoSq){
		
		if(Math.abs(oneSq.getRow() - twoSq.getRow()) == 2 && Math.abs(oneSq.getCol() - twoSq.getCol()) == 1)
			return true;
		if(Math.abs(oneSq.getCol() - twoSq.getCol()) == 2 && Math.abs(oneSq.getRow() - twoSq.getRow()) == 1)
			return true;
			
		return false;
	}
	
	private boolean moveBishop(Square oneSq, Square twoSq){
		
		if(Math.abs(oneSq.getRow() - twoSq.getRow()) == Math.abs(oneSq.getCol() - twoSq.getCol())){
			if(oneSq.getRow() > twoSq.getRow()){
				if(oneSq.getCol() > twoSq.getCol()){
					for(int i=oneSq.getRow()-1, j=oneSq.getCol()-1; i>twoSq.getRow(); i--, j--){
						if(squares[i][j].getPieceID() != 0)
							return false;
					}
					return true;
				}
				else if(oneSq.getCol() < twoSq.getCol()){
					for(int i=oneSq.getRow()-1, j=oneSq.getCol()+1; i>twoSq.getRow(); i--, j++){
						if(squares[i][j].getPieceID() != 0)
							return false;
					}
					return true;
				}
			}
			else if(oneSq.getRow() < twoSq.getRow()){
				if(oneSq.getCol() > twoSq.getCol()){
					for(int i=oneSq.getRow()+1, j=oneSq.getCol()-1; i<twoSq.getRow(); i++, j--){
						if(squares[i][j].getPieceID() != 0)
							return false;
					}
					return true;
				}
				else if(oneSq.getCol() < twoSq.getCol()){
					for(int i=oneSq.getRow()+1, j=oneSq.getCol()+1; i<twoSq.getRow(); i++, j++){
						if(squares[i][j].getPieceID() != 0)
							return false;
					}
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean moveQueen(Square oneSq, Square twoSq){
		
		return (moveRook(oneSq, twoSq) || moveBishop(oneSq, twoSq));
	}
	
	private boolean moveKing(Square oneSq, Square twoSq){
		
		if(Math.abs(oneSq.getRow() - twoSq.getRow()) < 2 && Math.abs(oneSq.getCol() - twoSq.getCol()) < 2)
			return true;
		
		return false;
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	
	
	
	
	
	
	
	
	
	
	
		 
	
	
	
	
//================Async Tasks=======================================

	public class Atask extends AsyncTask<String, Void, String> {

		 
		
		protected String doInBackground(String... params)
		{
			// TODO Auto-generated method stub
			
			
				try {
				
                    if(con==false)
				
                    {		
                    	ss=new ServerSocket(4002);
				
			      s= ss.accept();
			       con=true;
				     
				  
                    } 
				    }   
				 
			catch(Exception e)
			{
				 e.printStackTrace();
				
			}
			

			return null;
			}
		
	
		
	    protected void onPostExecute(String value) {
	    	
	    	
	  		if(et.getText().toString().equals("")){
	  			myTurn = true;
	  			genBoard(myTurn);
	  		}
	  			
	  		else{
	  			myTurn = false;
	  			genBoard(myTurn);
	  		}
	  			
	  		
	    	 Toast.makeText(getBaseContext(), "connection established", Toast.LENGTH_SHORT).show(); 
	    	
			recv.start();
	    	 send.start();
	}
	}
	
	
	public class Ataskrecv extends AsyncTask<String, Void, String> {

		 
		
		protected String doInBackground(String... value) {
			// TODO Auto-generated method stub
			
			  try {
			   if(con==false)
			   {
			
				 s=new Socket(ip,4002);
				       // os =s.getOutputStream();
				       con=true;
				       //out=new PrintWriter(os,true);
				      
			   }
				    }   
				 
			catch(Exception e)
			{
				 e.printStackTrace();
				
			}
			

			return value[0];
		}
	
		
	    protected void onPostExecute(String value) {
			if(!et.getText().toString().equals("")){
	  			myTurn = true;
	  			genBoard(myTurn);
	  		}
	  			
	  		else{
	  			myTurn = false;
	  			genBoard(myTurn);
	  		}
	    	
	    	  Toast.makeText(MainActivity.this,"sending connection",Toast.LENGTH_SHORT).show();     
	    	 recv.start();
	    	 send.start();
	  		//out.println(value);
	  		
	  		
	   // 	 Ataskrecv atr=new Ataskrecv();
		//	atr.execute("");
	
	}
	}

	
	public void connect(View v)
	{
		if(et.getText().toString().equals(""))
		{
			et.setEnabled(false);
		Atask at=new Atask();at.execute("");
		}
		else if(! et.getText().toString().equals(""))
		{
			ip = et.getText().toString();
			et.setEnabled(false);
			Ataskrecv atr=new Ataskrecv();atr.execute("");
		}
		 
	}

		 
		 }
