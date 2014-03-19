/*
	Steps through a XML file to create a batch from the individual lines
	of the file
*/

package batch;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import command.*;

/*
 * Class that parses the XML file passed in, creates a batch based on the underlying information,
 * and runs the batch created
 */
public class BatchParser {

	private File inputFile;
	private Batch batch;
	
	/*
	 * Constructor
	 */
	public BatchParser(String fileName) throws BatchException{
		
		batch = new Batch();	
		inputFile = new File(fileName);
		
		if(inputFile.exists()){
			try {
				Parse();
			}
			catch(BatchException ex){
				throw ex;
			}
		}
		else{
			throw new BatchException("File: "+ fileName +" was not found");
		}
			
	}
	
	/*
	 * Parses the XML file of the BatchProcessor into commands and stores them in the batch
	 */
	private void Parse() throws BatchException {
		try{
			Command tempCommand = null;
			
			// Creates a document from the XML file provided in order to extract command data
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			
			// Retrieve batch commands from document
			Element parentNode = doc.getDocumentElement();
			NodeList nodes = parentNode.getChildNodes();
			
			/* For each node in the element, if the element contains a valid command,
			 * parse the command and add it to the batch
			 * 
			 * Throw an exception if an unsupported command is encountered
			 */
			
			for(int i = 0; i < nodes.getLength(); i++){
				Node node = nodes.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					Element elem = (Element) node;
					
					switch(elem.getNodeName()){
						case "wd":
							System.out.println("Parsing wd command");
							tempCommand = new wdCommand();
							tempCommand.ParseCommand(elem);
							batch.AddCommand(tempCommand);
							break;
						case "cmd":
							System.out.println("Parsing cmd command");
							tempCommand = new cmdCommand();
							tempCommand.ParseCommand(elem);
							batch.AddCommand(tempCommand);
							break;
						case "file":
							System.out.println("Parsing file command");
							tempCommand = new fileCommand();
							tempCommand.ParseCommand(elem);
							batch.AddCommand(tempCommand);
							break;
						case "pipe":
							System.out.println("Parsing pipe command");
							tempCommand = new pipeCommand();
							tempCommand.ParseCommand(elem);
							batch.AddCommand(tempCommand);
							break;
						default:
							throw new BatchException("Attempt to parse unsupported command: "+ elem.getNodeName());
							
					}
				}
			}
		}
		// Exception handling
		catch(IOException ex){
			throw new BatchException("Error occurred while parsing XML file", ex);
		}
		catch (ParserConfigurationException e){
			throw new BatchException("ParserConfigurationException occured", e);
		}
		catch (SAXException e){
			throw new BatchException("SAXException occured", e);
		}
	}
	
	/*
	 * Runs the batch created by the Parse() method
	 */
	public void RunBatch() throws BatchException{
		
		CommandBucket localBucket = batch.GetCommands();	// Retrieve batch CommandBucket
		CommandBucket.CommandNode tempHolder[], nodePointer;
		Command tempCommand;
		File tempFile;
		
		// List of processes created
		ArrayList<ProcessBuilder> processes = new ArrayList<ProcessBuilder>();
		
		// Retrieve the array of CommandNodes from the batch CommandBucket
		tempHolder = localBucket.GetCommandList();
		
		/* 
		 * Set working directory
		 * Starts at first element of CommandNode array, which stores a wdCommand
		 * Retrieves the command from the node and uses it as an argument to SetWorkingDir()
		 */
		nodePointer = tempHolder[0];
				
		while(nodePointer.GetNextNode() != null)	// Move to last element in list
			nodePointer = nodePointer.GetNextNode();
		
		tempCommand = nodePointer.GetCommand();
		batch.SetWorkingDir((wdCommand)tempCommand);
		
		/*
		 *  Create files that do not exist
		 *  Starts at second element of CommandNode array, which stores a linked list of fileCommands
		 *  For each fileCommand in the list, if the underlying file does not exist, it is created in
		 *  the working directory
		 *  
		 *  If it cannot be created, an exception is thrown
		 */
		nodePointer = tempHolder[1];
		while(nodePointer.GetNextNode() != null){
			nodePointer = nodePointer.GetNextNode();
			tempCommand = nodePointer.GetCommand();
			System.out.printf("File command on %s\n", tempCommand.GetPath());
			if(!(tempFile = new File(batch.GetWorkingDir() + "/" + tempCommand.GetPath())).exists()){
				try{
					tempFile.createNewFile();
				}
				catch(IOException ex){
					throw new BatchException("Could not create file: " + batch.GetWorkingDir() + "/" + tempCommand.GetPath());
					
				}
			}
		}
		
		/*
		 * Makes processes depending on whether the batch contains a pipe command
		 */
		if(!batch.GetPipeFlag()){	// If batch does not contain a pipe command
			
			//Start at third element of CommandNode array, which contains a linked list of cmdCommands			 
			nodePointer = tempHolder[2];
			while(nodePointer.GetNextNode() != null){	// While there is another node in the list
				nodePointer = nodePointer.GetNextNode();
				tempCommand = nodePointer.GetCommand();	// Get command information
				
				System.out.printf("Command on %s: %s\n", tempCommand.GetID(), ((cmdCommand)tempCommand));
				
				// Create array containing command name and arguments for ProcessBuilder
				String[] cmdArguments = new String[((cmdCommand) tempCommand).GetArgs().length + 1];
				cmdArguments[0] = tempCommand.GetPath();
				System.arraycopy(((cmdCommand) tempCommand).GetArgs(), 0, cmdArguments, 1, ((cmdCommand) tempCommand).GetArgs().length);
				
				/*
				 *  Create a ProcessBuilder with the batch working directory, and
				 *  specified command and arguments, and input and output file
				 */
				ProcessBuilder newProcessBuilder = new ProcessBuilder();
				newProcessBuilder.directory(new File(batch.GetWorkingDir()));
				newProcessBuilder.command(cmdArguments);
				
				// Locate input file, and redirect process input if needed
				if(!(((cmdCommand) tempCommand).GetInput().equals("")||((cmdCommand) tempCommand).GetInput().equalsIgnoreCase("pipe"))){
					String infile = ((cmdCommand) tempCommand).GetInput();
					
					if(batch.FindFile(infile)==null){	// Throw exception if file ID is not in batch
						throw new BatchException("Could not locate file ID: " + infile);
					}
						
					newProcessBuilder.redirectInput(new File(batch.GetWorkingDir()+"/"+batch.FindFile(infile)));
				}
				
				// Locate output file, and redirect process output if needed
				if(!(((cmdCommand) tempCommand).GetOutput().equals("")||((cmdCommand) tempCommand).GetOutput().equalsIgnoreCase("pipe"))){
					String outfile = ((cmdCommand) tempCommand).GetOutput();
					
					if(batch.FindFile(outfile)==null){	// Throw exception if file ID is not in batch
						throw new BatchException("Could not locate file ID: " + outfile);
					}
					
					newProcessBuilder.redirectOutput(new File(batch.GetWorkingDir()+"/"+batch.FindFile(outfile)));
				}
				
				// Add new ProcessBuilder to list of processes
				processes.add(newProcessBuilder);
			}
		}
		else{	// If there is a pipe command in the batch
			
			// Start at fourth element of CommandNode array, which contains a linked list of pipeCommands
			nodePointer = tempHolder[3];
			while(nodePointer.GetNextNode()!= null)
				nodePointer = nodePointer.GetNextNode();
			
			// Retrieve command data
			pipeCommand pipeInfo = (pipeCommand) nodePointer.GetCommand();
			System.out.printf("Pipe Command: %s; Execution of commands will be deferred\n", pipeInfo.GetID());
			
			// Retrieve first command in pipe, or throw an exception if the ID cannot be found
			cmdCommand command1 = batch.FindCmd(pipeInfo.GetCmd1());
			if(command1 == null){
				//change
				throw new BatchException("Could not locate command ID: " + pipeInfo.GetCmd1());
			}
			
			// Create array containing command name and arguments for ProcessBuilder
			String[] cmd1Args = new String[command1.GetArgs().length +1];
			cmd1Args[0] = command1.GetPath();
			System.arraycopy(command1.GetArgs(), 0, cmd1Args, 1, command1.GetArgs().length);
			
			/*
			 *  Create first ProcessBuilder with the batch working directory, and
			 *  specified command and arguments, and input file
			 */
			ProcessBuilder newProcessBuilder1 = new ProcessBuilder();
			newProcessBuilder1.directory(new File(batch.GetWorkingDir()));
			newProcessBuilder1.command(cmd1Args);
			if(!(command1.GetInput().equals("")||command1.GetInput().equalsIgnoreCase("pipe")))
				newProcessBuilder1.redirectInput(new File(batch.GetWorkingDir() + "/" + batch.FindFile(command1.GetInput())));
			
			processes.add(newProcessBuilder1);	// Add new ProcessBuilder to list
			
			// Retrieve second command in pipe, or throw an exception if the ID cannot be found
			cmdCommand command2 = batch.FindCmd(pipeInfo.GetCmd2());
			if(command2 == null){
				throw new BatchException("Could not locate command ID: " + pipeInfo.GetCmd2());
			}
			
			// Create array containing command name and arguments for ProcessBuilder
			String[] cmd2Args = new String[command2.GetArgs().length +1];
			cmd2Args[0] = command2.GetPath();
			System.arraycopy(command2.GetArgs(), 0, cmd2Args, 1, command2.GetArgs().length);
			
			/*
			 *  Create second ProcessBuilder with the batch working directory, and
			 *  specified command and arguments, and output file
			 */
			ProcessBuilder newProcessBuilder2 = new ProcessBuilder();
			newProcessBuilder2.directory(new File(batch.GetWorkingDir()));
			newProcessBuilder2.command(cmd2Args);
			if(!(command2.GetOutput().equals("")||command2.GetOutput().equalsIgnoreCase("pipe")))
				newProcessBuilder2.redirectOutput(new File(batch.GetWorkingDir() + "/" + batch.FindFile(command2.GetOutput())));
			
			processes.add(newProcessBuilder2);	// Add new ProcessBuilder to list
			
		}
		
		// Run processes
		if(!batch.GetPipeFlag()){	// If batch does not contain a pipe command
			
			for(ProcessBuilder pb: processes){	// For each ProcessBuilder in the list of ProcessBuilders
				
				// Create the command name for display
				String commandName = pb.command().get(0);
				for(int i = 1; i < pb.command().size(); i++)
					commandName = commandName + " " + pb.command().get(i);
				
				// Execute the process and wait for it to finish
				try{
					Process runningProcess = pb.start();
					System.out.printf("Waiting for Command %s to exit\n", commandName);
					runningProcess.waitFor();
					System.out.printf("Command %s has exited\n", commandName);
				}
				// Exception handling
				catch(IOException ex){
					throw new BatchException("IOException occurred while creating process: " + commandName, ex);
				}
				catch(InterruptedException ex){
					throw new BatchException("InterruptedException occurred while waiting for process: "+ commandName, ex);
				}
			}
		}
		else{	// If batch contains a pipe command
			
			Process process1, process2;
			
			// Prepare process data streams
			BufferedInputStream inFromProc1 = null;
			BufferedOutputStream outToProc2 = null;
			
			String command1Name="", command2Name="";
			
			// Create first process name for display
			for(int i = 0; i < processes.get(0).command().size(); i++)
				command1Name += " " + processes.get(0).command().get(i);
			
			// Create second process name for display
			for(int i = 0; i < processes.get(1).command().size(); i++)
				command2Name += " " + processes.get(1).command().get(i);
			
			int byteRead;
			
			try{
				// Start first process and capture the input stream that reads from the process
				process1 = processes.get(0).start();
				inFromProc1 = new BufferedInputStream(process1.getInputStream());
				
				// Start second process and capture output stream that feeds to the process
				process2 = processes.get(1).start();
				outToProc2 = new BufferedOutputStream(process2.getOutputStream());
				
				// While there is data being written out by first process, write that data to the second process
				while((byteRead = inFromProc1.read()) != -1){
					outToProc2.write(byteRead);
				}
				
				// Wait for first process to be completely finished
				System.out.printf("Waiting for Command %s to exit\n", command1Name);
				process1.waitFor();
				System.out.printf("Command %s has exited\n", command1Name);
				
				// Close process 1 end of pipe
				inFromProc1.close();
				
				// Flush and close process 2 end of pipe
				outToProc2.flush();
				outToProc2.close();
				
				// Wait for second process to complete
				System.out.printf("Waiting for Command %s to exit\n", command2Name);
				process2.waitFor();
				System.out.printf("Command %s has exited\n", command2Name);
								
			}
			// Exception handling
			catch(IOException ex){
				throw new BatchException("IOException occurred while creating pipe commands", ex);
			}
			catch(InterruptedException ex){
				throw new BatchException("InterruptedException occurred while waiting for pipe command", ex);
			}
			// Attempt to close data streams that may have been left open during an error
			finally{
				if(inFromProc1 != null)
					try{
						inFromProc1.close();
					}
					catch(IOException ex){
						throw new BatchException("IOException occured while closing pipe", ex);
					}
				if(outToProc2 != null){
					try{
						outToProc2.flush();
						outToProc2.close();
					}
					catch(IOException ex){
						throw new BatchException("IOException occured while closing pipe", ex);
					}
				}
			}
		}
		
		System.out.printf("Batch finished\n");
	}
}
