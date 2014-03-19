/*
 * Authors: Paul Staggs, Alex Biju, Julian Choi
 */

package assignment;

import batch.*;

public class Main {

	/**
	 * @param args name of batch file that will be parsed and executed
	 */
	public static void main(String[] args) throws BatchException {
		try{
			BatchParser batchParser = new BatchParser(args[0]);
		
			batchParser.RunBatch();
		}
		catch(BatchException ex){
			System.err.println("Exception occurred during execution: "+ex.getMessage());
			ex.printStackTrace();
		}
	}

}
