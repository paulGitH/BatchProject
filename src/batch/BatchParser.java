/**
	Steps through a XML file to create a batch from the individual lines
	of the file
*/

package batch;

import java.io.*;
import java.util.*;
import java.lang.ProcessBuilder.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import command.*;

public class BatchParser {

	File inputFile;
	Batch batch;
	
	public BatchParser(String fileName){
		
		batch = new Batch();	
		inputFile = new File(fileName);
		
		if(inputFile.exists()){
			Parse();
		}
		else
			;//TODO throw file not found exception
	}
	
	private void Parse(){
		try{
			Command tempCommand = null;
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			
			Element parentNode = doc.getDocumentElement();
			NodeList nodes = parentNode.getChildNodes();
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
							// TODO throw exception for unexpected command type
							break;
					}
					
				}
			}
		}
		catch(Exception ex){
			// TODO throw various exceptions for IOException and others
		}
	}
	
	public void RunBatch(){
		//TODO Fix this method to run the batch created (i.e. run process)
		CommandBucket localBucket = batch.GetCommands();
		CommandNode tempHolder[], nodePointer;
		Command tempCommand;
		File tempFile;
		
		ArrayList<ProcessBuilder> processes = new ArrayList<ProcessBuilder>();
		
		tempHolder = localBucket.GetCommandList();
		
		// Set working directory
		nodePointer = tempHolder[0];
		while(nodePointer.GetNextNode() != null) nodePointer = nodePointer.GetNextNode();
		tempCommand = nodePointer.GetCommand();
		batch.SetWorkingDir(tempCommand);
		
		// Create files that do not exist
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
					// TODO handle exception
				}
			}
		}
		
		// Make processes
		if(!batch.GetPipeFlag()){
			nodePointer = tempHolder[2];
			while(nodePointer.GetNextNode() != null){
				nodePointer = nodePointer.GetNextNode();
				tempCommand = nodePointer.GetCommand();
				
				System.out.printf("Command on %s: %s\n", tempCommand.GetID(), ((cmdCommand)tempCommand));
				String[] cmdArguments = new String[((cmdCommand) tempCommand).GetArgs().length + 1];
				cmdArguments[0] = tempCommand.GetPath();
				System.arraycopy(((cmdCommand) tempCommand).GetArgs(), 0, cmdArguments, 1, ((cmdCommand) tempCommand).GetArgs().length);
				
				ProcessBuilder newProcessBuilder = new ProcessBuilder();
				newProcessBuilder.directory(new File(batch.GetWorkingDir()));
				newProcessBuilder.command(cmdArguments);
				if(!((cmdCommand) tempCommand).GetInput().equals("")){
					String infile = ((cmdCommand) tempCommand).GetInput();
					if(batch.findFile(infile)==null);
						//throw ; // TODO throw process exception
					newProcessBuilder.redirectInput(new File(batch.GetWorkingDir()+"/"+batch.findFile(infile)));
				}
				else
					newProcessBuilder.redirectInput(Redirect.INHERIT);
				
				if(!((cmdCommand) tempCommand).GetOutput().equals("")){
					String outfile = ((cmdCommand) tempCommand).GetOutput();
					if(batch.findFile(outfile)==null);
					//throw ; // TODO throw process exception
					newProcessBuilder.redirectOutput(new File(batch.GetWorkingDir()+"/"+batch.findFile(outfile)));
				}
				else
					newProcessBuilder.redirectOutput(Redirect.INHERIT);
				
				processes.add(newProcessBuilder);
			}
		}
		else{	// TODO Pipe processes
			
		}
		// Run processes
		for(ProcessBuilder pb: processes){
			
			String commandName = pb.command().get(0);
			for(int i = 1; i < pb.command().size(); i++)
				commandName = commandName + " " + pb.command().get(i);
			
			try{
				Process runningProcess = pb.start();
				System.out.printf("Waiting for Command %s to exit\n", commandName);
				runningProcess.waitFor();
				System.out.printf("Command %s has exited\n", commandName);
			}
			catch(Exception ex){
				// TODO exception handling
			}
		}
	}
}
