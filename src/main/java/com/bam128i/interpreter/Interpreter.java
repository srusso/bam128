package com.bam128i.interpreter;

import javax.swing.JTextArea;
import com.bam128i.data.Data;

/**
 * Manipulates the data. Receives instructions by the Parser class.
 * @author Simone Russo
 *
 */
public class Interpreter {
	private final Data data;
	
	public Interpreter(JTextArea outputPanel){
		data = new Data(outputPanel);
	}
	
	public int getMatrixValue(){
		return data.getMatrixValue();
	}
	
	/**
	 * Starts operating on the other position register.
	 * Does NOT switch the values.
	 * Corresponds to the command '/'.
	 */
	public void switchC(){
		data.switchC();
	}
	
	/**
	 * Switches the values of the two position registers.
	 * After a call to this function, you are still working on the same position register.
	 * Corresponds to the command '!'.
	 */
	public void switchCValues(){
		data.switchCValues();
	}

	/**
	 * Increments by 2^exp the value of the current position register.
	 * If the new value is >127, restarts from 0. Example: 126+3=1.
	 * Corresponds to the command '+n'.
	 * 
	 * @param exp = must be 0<=exp<=9
	 */
	public void incrementPR(int exp){
		data.incrementPR(exp);
	}
	
	/**
	 * Increments by 2^exp the value of current matrix element.
	 * If the new value is >127, restarts from 0. Example: 126+3=1.
	 * Corresponds to the command '*n'.
	 * 
	 * @param exp = must be 0<=exp<=9
	 */
	public void incrementMatrixValue(int exp){
		data.incrementMatrixValue(exp);
	}
	
	/**
	 * Prints on the screen the ASCII character corresponding to the current matrix element.
	 * Corresponds to the command 'p'.
	 */
	public void printMatrixValue(){
		data.printMatrixValue();
	}
	
	/**
	 * Sums the current matrix element to the element 2^exp positions ahead.
	 * If the end of the row is met, the count is continued to the next row.
	 * If it was the last row, the count is continued from the first element of the first row.
	 * Corresponds to the command '>n'.
	 */
	public void moveWithSum(int exp){
		data.moveWithSum(exp);
	}
	
	public void inputValue(int value){
		data.inputValue(value);
	}

	public void printMatrixValueAsNumber() {
		data.printMatrixValueAsNumber();
	}
}
