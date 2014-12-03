package com.bam128i.parser;

public class ParserTask implements Runnable{
	
	private final Parser parser;
	private final char[] code;
	
	public ParserTask(Parser p, char[] cs){
		parser = p;
		code   = cs;
	}

	public void run() {
		parser.stopExecution();
		parser.startAnalysis(code);
		
	}
	
	public void stopParsing(){
		parser.stopExecution();
	}

}
