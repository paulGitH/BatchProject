/*
 * Authors: Paul Staggs, Alex Biju, Julian Choi
 */

package assignment;

import batch.*;

public class Main {

	/*
	 * Parse a XML batch file provided as a command-line argument and executes the underlying batch
	 * 
	 * @param args name of batch file that will be parsed and executed
	 */
	public static void main(String[] args) throws BatchException {
		try{
			// Create a new BatchParser from provided XML file
			BatchParser batchParser = new BatchParser(args[0]);
			
			// Run batch created from XML file
			batchParser.RunBatch();
		}
		catch(BatchException ex){
			System.err.println("Exception occurred during execution: "+ex.getMessage());
			ex.printStackTrace();
		}
	}

}
