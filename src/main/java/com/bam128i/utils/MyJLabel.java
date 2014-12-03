package com.bam128i.utils;

import javax.swing.JLabel;

public class MyJLabel extends JLabel{
	private static final long serialVersionUID = 1L;
	
	public int row, column;
	
	public MyJLabel(String x, int r, int c){
		super(x);
		row=r;
		column=c;
	}

}
