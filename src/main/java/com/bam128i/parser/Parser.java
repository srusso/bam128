package com.bam128i.parser;

import java.util.Stack;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.bam128i.data.Data;
import com.bam128i.gui.MainScreen;
import com.bam128i.interpreter.Interpreter;

/**
 * Parses the program and gives instructions to the Interpreter class.
 * @author Simone Russo
 *
 */
public class Parser {
	
	private final int WAITING_FOR_INSTRUCTION=0;
	private final int WAITING_FOR_ARGUMENT=1;
	private final int INSIDE_COMMENT=2;
	private final int STOP=3;	
	private int STATE;
	
	private final int PAR_MISMATCH=1;
	private final int ARG_EXPECTED=2;
	private final int ARG_WITH_NO_COMMAND=3;
	private final int WRONG_NUMBER_OF_PARENTHESES=4;
	private final int WRONG_INPUT=5;
	
	private Interpreter interpreter;
	
	private char pendingCommand=0;
	private int pos;
	private int previousState;
	
	private final MainScreen context;
	private final JTextArea outputPanel;
	private char[]code;
	
	private Stack<ParenthesesPair> cycleStack;
	
	public Parser(MainScreen c, JTextArea outputPanel){
		STATE = WAITING_FOR_INSTRUCTION;
		context = c;
		this.outputPanel=outputPanel;
	}
	
	public void startAnalysis(char []code){
		int length=code.length;
		this.code=code;
		interpreter = new Interpreter(outputPanel);
		
		STATE = WAITING_FOR_INSTRUCTION;
		cycleStack = new Stack<ParenthesesPair>();
		
		if(checkParentheses()){
			for(pos=0;pos<length && STATE!=STOP;pos++){
				checkChar(code[pos]);
			}
		} else endExecution(WRONG_NUMBER_OF_PARENTHESES);

		
		STATE = WAITING_FOR_INSTRUCTION;
	}
	
	private boolean checkParentheses() {
		int open=0, close=0;
		
		for(int i=0;i<code.length;i++){
			if(code[i]=='(')
				open++;
			else if(code[i]==')')
				close++;
		}
		
		return open==close;
	}

	private void checkChar(char c) {
		if(STATE == WAITING_FOR_INSTRUCTION){
			if(c=='/' || c=='p' || c=='!' || c=='i' || c=='n'){
				executeCommand(c);
			} else if(c=='*' || c=='+' || c=='>'){
				pendingCommand=c;
				STATE=WAITING_FOR_ARGUMENT;
			} else if(c=='('){
				if(interpreter.getMatrixValue()==0){
					pos=getMatchingParenthesis();
					if(pos==-1 || pos==code.length)
						endExecution(PAR_MISMATCH);
				} else {
					cycleStack.push(new ParenthesesPair(pos, getMatchingParenthesis()));
				}
			} else if(c==')'){
				if(!cycleStack.isEmpty()){
					if(interpreter.getMatrixValue()==0){
						cycleStack.pop();
					}
					else {
						pos=cycleStack.peek().start;
					}
				}
				else
					endExecution(PAR_MISMATCH);
			} else if(c=='.'){
				previousState = STATE;
				STATE = INSIDE_COMMENT;
			}
		} else if(STATE == WAITING_FOR_ARGUMENT){
			if(c=='.'){
				previousState = STATE;
				STATE = INSIDE_COMMENT;
			}
			else if(!Character.isDigit(c))
				endExecution(ARG_EXPECTED);
			else{
				executeCommand(c);
				STATE = WAITING_FOR_INSTRUCTION;
			}
		} else if(STATE == INSIDE_COMMENT){
			if(c=='.')
				STATE = previousState;
		}
		
	}
	
	private int getMatchingParenthesis() {
		boolean inComment=false;
		int count=1;
		for(int i=pos+1;i<code.length;i++){
			if(inComment){
				if(code[i]=='.'){
					inComment=false;
				}
			} else {
				if(code[i]=='.'){
					inComment=true;
				} else if(code[i]==')'){
					count--;
				} else if(code[i]=='('){
					count++;
				}
			}
			
			if(count==0)
				return i;
			
		}
		
		return -1;
	}

	private void executeCommand(char c) {
		if(c=='n'){
			interpreter.printMatrixValueAsNumber();
		} else if(c=='i'){
			int x=-1;
			String str = JOptionPane.showInputDialog("Input a number");
			if(str==null)
				endExecution(WRONG_INPUT);
			else {
				try{
					x = Integer.parseInt(str);
					interpreter.inputValue(x);
				} catch(NumberFormatException e){
					endExecution(WRONG_INPUT);
				}
			}
		} else if(c=='!'){			
			interpreter.switchCValues();
		} else if (c=='/'){			
			interpreter.switchC();
		} else if(c=='p'){			
			interpreter.printMatrixValue();
		} else if(Character.isDigit(c)){
			if(pendingCommand==0){
				endExecution(ARG_WITH_NO_COMMAND);
				return;
			}
				
			if(pendingCommand=='+'){
				interpreter.incrementPR(Character.getNumericValue(c));
			} else if(pendingCommand=='>'){
				interpreter.moveWithSum(Character.getNumericValue(c));
			} else if(pendingCommand=='*'){
				interpreter.incrementMatrixValue(Character.getNumericValue(c));
			}
				
			pendingCommand=0;
			
		}
	}
	
	public void stopExecution(){
		STATE = STOP;
	}
	
	private String getRowCol(){		
        int row = 1, column=0;
		int lastNewline=-1;
		String text = new String(code).replaceAll("\r", "");
		
		for(int i=0;i<pos;i++){
			if(text.charAt(i)==10){
				row++;
				lastNewline=i;
			}
		}
		
		column=pos-lastNewline;
		
		return "[row="+row+", col="+column+"]";
	}
	
	private void endExecution(int errorID) {
		
		switch(errorID){
			
		case PAR_MISMATCH:
			JOptionPane.showMessageDialog(context, "Parentheses mismatch at "+getRowCol()+".", "Error" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case ARG_EXPECTED:
			JOptionPane.showMessageDialog(context, "Digit argument expected after command '"+code[pos]+ "' but not found. At "+getRowCol()+".", "Error" , JOptionPane.ERROR_MESSAGE);
			break;
		case ARG_WITH_NO_COMMAND:
			JOptionPane.showMessageDialog(context, "Digit argument not expected at "+getRowCol()+".", "Error" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case WRONG_NUMBER_OF_PARENTHESES:
			JOptionPane.showMessageDialog(context, "Number of '(' occurrences is different from ')'.", "Error" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case WRONG_INPUT:
			JOptionPane.showMessageDialog(context, "You must insert a numeric value! Aborting.", "Error" , JOptionPane.ERROR_MESSAGE);
			break;
			
		default:
			JOptionPane.showMessageDialog(context, "Syntax error at position "+pos+".", "Error" , JOptionPane.ERROR_MESSAGE);
		}
		
		STATE = STOP;
	}

	public int[][] getMatrix() {
		return Data.getMatrix();
	}
}
