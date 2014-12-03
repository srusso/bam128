package com.bam128i.data;

import javax.swing.JTextArea;

/**
 * This class contains the matrix and the positions registers.
 * Receives instructions by the Interpreter class.
 * @author Simone Russo
 *
 */
public class Data {
	private final boolean debug=false;
	
	public static final int MATRIX_DIM=128, PR_MAX=127;
	
	/**
	 * Position registers. Range: 0...(MATRIX_DIM-1) included.
	 */
	private int[] pr;
	
	/**
	 * The square matrix that holds the values that can be printed on the screen.
	 */
	private static int[][]matrix = new int[MATRIX_DIM][MATRIX_DIM];
	
	/**
	 * Tells on which position register we are currently working.
	 */
	private int CURRENT_PR;
	
	
	private final JTextArea outputPanel;
	
	public Data(JTextArea outputPanel){
		pr = new int[2];
		resetMatrix();
		CURRENT_PR = 0;
		this.outputPanel = outputPanel;
		
		outputPanel.setText("");
	}
	
	public void resetMatrix() {
		
		for(int i=0;i<MATRIX_DIM;i++)
			for(int j=0;j<MATRIX_DIM;j++)
				matrix[i][j]=0;
	}

	/**
	 * Starts operating on the other position register.
	 * Does NOT switch the values.
	 * Corresponds to the command '/'.
	 */
	public void switchC(){
		CURRENT_PR = 1 - CURRENT_PR;
		if(debug)
			System.out.println("Cambio pr corrente");
	}
	
	/**
	 * Switches the values of the two position registers.
	 * After a call to this function, you are still working on the same position register.
	 * Corresponds to the command '!'.
	 */
	public void switchCValues(){
		if(debug)
			System.out.println("Swticho i valori pr");
		int temp=pr[0];
		pr[0]=pr[1];
		pr[1]=temp;
	}
	
	/**
	 * Increments by 2^exp the value of the current position register.
	 * If the new value is >127, restarts from 0. Example: 126+3=1.
	 * Corresponds to the command '+n'.
	 * 
	 * @param exp = must be 0<=exp<=9
	 */
	public void incrementPR(int exp){
		int amount = power(2, exp);
		
		if(debug)
			System.out.println("Aumento di 2^"+exp+" il pr." + CURRENT_PR);
		
		pr[CURRENT_PR]+=amount;
		pr[CURRENT_PR]=pr[CURRENT_PR]%MATRIX_DIM;
	}
	
	/**
	 * Increments by 2^exp the value of current matrix element.
	 * If the new value is >127, restarts from 0. Example: 126+3=1.
	 * Corresponds to the command '*n'.
	 * 
	 * @param exp = must be 0<=exp<=9
	 */
	public void incrementMatrixValue(int exp){
		int amount = power(2, exp);
		
		matrix[pr[0]][pr[1]]+=amount;
		matrix[pr[0]][pr[1]]=matrix[pr[0]][pr[1]]%MATRIX_DIM;
		
		if(debug)
			System.out.println("Incremento di 2^"+exp+". Nuovo valore: " + matrix[pr[0]][pr[1]]);
	}
	
	/**
	 * Prints on the screen the ASCII character corresponding to the current matrix element.
	 * Corresponds to the command 'p'.
	 */
	public void printMatrixValue(){
		outputPanel.append(""+(char)matrix[pr[0]][pr[1]]);
	}
	
	
	public void printMatrixValueAsNumber() {
		outputPanel.append(""+matrix[pr[0]][pr[1]]);
	}
	
	/**
	 * Sums the current matrix element to the element 2^exp positions ahead.
	 * If the end of the row is met, the count is continued to the next row.
	 * If it was the last row, the count is continued from the first element of the first row.
	 * Corresponds to the command '>n'.
	 */
	public void moveWithSum(int exp){
		int amount = power(2, exp);
		
		int pos1=pr[0], pos2=pr[1];
		
		if(debug)
			System.out.println("Muovo avanti di "+amount+" posizioni");
		
		for(int i=0;i<amount;i++){
			pos2++;
			if(pos2>PR_MAX){
				pos2=0;
				pos1++;
				if(pos1>PR_MAX){
					pos1=0;
				}
			}
		}
		
		matrix[pos1][pos2]+=matrix[pr[0]][pr[1]];
		
		if(debug)
			System.out.println("Nuovo valore: " + matrix[pos1][pos2]);
		
	}
	
	private int power(int base, int exp){
		int tot=1;
		
		for(int i=0;i<exp;i++)
			tot*=base;
		
		return tot;
	}
	
	public int getMatrixValue(){
		return matrix[pr[0]][pr[1]];
	}

	public void inputValue(int value) {
		if(value>=0)
			matrix[pr[0]][pr[1]] = value%128;
		else matrix[pr[0]][pr[1]] = (-value)%128;
	}

	public static int[][] getMatrix() {
		return matrix;
	}
}
