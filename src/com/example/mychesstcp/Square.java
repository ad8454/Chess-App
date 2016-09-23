package com.example.mychesstcp;

import android.content.Context;
import android.widget.ImageButton;

public class Square extends ImageButton {
	
	private Context context;
	private boolean isBlack;
	private boolean isWhite;
	private int row;
	private int col;
	private int pieceID;
	private String pieceColor;

	public Square(Context context) {
		super(context);
		this.context = context;
	}
	
	public void setWhite(int i, int j){
		this.setBackgroundResource(R.drawable.white);
		isWhite = true;
		isBlack = false;
		pieceID = 0; pieceColor = "";
		row = i; col = j;
	}
	
	public void setBlack(int i, int j){
		this.setBackgroundResource(R.drawable.black);
		isBlack = true;
		isWhite = false;
		pieceID = 0; pieceColor = "";
		row = i; col = j;
	}
	
	public void setPiece(int id, String color){
		pieceID = id;
		pieceColor = color;
		String resid="";
		
		if(color.equals("black"))
			resid+="b";
		else if(color.equals("white"))
			resid+="w";
		resid+=""+id;
		
		if(id == 0){
			this.setImageResource(android.R.color.transparent);
		}
		else
			this.setImageResource(getResources().getIdentifier(resid, "drawable", context.getPackageName()));
		
	}
	
	public boolean isBlack(){
		return isBlack;
	}
	
	public boolean isWhite(){
		return isWhite;
	}
	
	public int getPieceID(){
		return pieceID;
	}
	
	public String getPieceColor(){
		return pieceColor;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	public void onMyTouch(){
		this.setBackgroundResource(R.drawable.stest);
	}
	
	public void onNoTouch(){
		if(isWhite)
			this.setBackgroundResource(R.drawable.white);
		else
			this.setBackgroundResource(R.drawable.black);
	}

}
