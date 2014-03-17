/**
	Steps through a XML file to create a batch from the individual lines
	of the file
*/

package batch;

import java.io.*;
import java.util.*;

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
				if(!(((cmdCommand) tempCommand).GetInput().equals("")||((cmdCommand) tempCommand).GetInput().equalsIgnoreCase("pipe"))){
					String infile = ((cmdCommand) tempCommand).GetInput();
					if(batch.findFile(infile)==null);
						//throw ; // TODO throw process exception
					newProcessBuilder.redirectInput(new File(batch.GetWorkingDir()+"/"+batch.findFile(infile)));
				}
				
				if(!(((cmdCommand) tempCommand).GetOutput().equals("")||((cmdCommand) tempCommand).GetOutput().equalsIgnoreCase("pipe"))){
					String outfile = ((cmdCommand) tempCommand).GetOutput();
					if(batch.findFile(outfile)==null);
					//throw ; // TODO throw process exception
					newProcessBuilder.redirectOutput(new File(batch.GetWorkingDir()+"/"+batch.findFile(outfile)));
				}
				
				processes.add(newProcessBuilder);
			}
		}
		else{
			nodePointer = tempHolder[3];
			while(nodePointer.GetNextNode()!= null) nodePointer = nodePointer.GetNextNode();
			pipeCommand pipeInfo = (pipeCommand) nodePointer.GetCommand();
			System.out.printf("Pipe Command: %s; Execution of commands will be deferred\n", pipeInfo.GetID());
			
			cmdCommand command1 = batch.FindCmd(pipeInfo.GetCmd1());
			if(command1 == null);
				// TODO throw process exception
			String[] cmd1Args = new String[command1.GetArgs().length +1];
			cmd1Args[0] = command1.GetPath();
			System.arraycopy(command1.GetArgs(), 0, cmd1Args, 1, command1.GetArgs().length);
			
			ProcessBuilder newProcessBuilder1 = new ProcessBuilder();
			newProcessBuilder1.directory(new File(batch.GetWorkingDir()));
			newProcessBuilder1.command(cmd1Args);
			if(!(command1.GetInput().equals("")||command1.GetInput().equalsIgnoreCase("pipe")))
				newProcessBuilder1.redirectInput(new File(batch.GetWorkingDir() + "/" + batch.findFile(command1.GetInput())));
			
			processes.add(newProcessBuilder1);
			
			
			cmdCommand command2 = batch.FindCmd(pipeInfo.GetCmd2());
			if(command2 == null);
				// TODO throw process exception
			String[] cmd2Args = new String[command2.GetArgs().length +1];
			cmd2Args[0] = command2.GetPath();
			System.arraycopy(command2.GetArgs(), 0, cmd2Args, 1, command2.GetArgs().length);
			
			ProcessBuilder newProcessBuilder2 = new ProcessBuilder();
			newProcessBuilder2.directory(new File(batch.GetWorkingDir()));
			newProcessBuilder2.command(cmd2Args);
			if(!(command2.GetOutput().equals("")||command2.GetOutput().equalsIgnoreCase("pipe")))
				newProcessBuilder2.redirectOutput(new File(batch.GetWorkingDir() + "/" + batch.findFile(command2.GetOutput())));
			
			processes.add(newProcessBuilder2);
			
		}
		// Run processes
		if(!batch.GetPipeFlag()){
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
		else{
			
			Process process1, process2;
			
			BufferedInputStream inFromProc1;
			BufferedOutputStream outToProc2;
			
			String command1Name="", command2Name="";
			
			for(int i = 0; i < processes.get(0).command().size(); i++)
				command1Name += " " + processes.get(0).command().get(i);
			
			for(int i = 0; i < processes.get(1).command().size(); i++)
				command2Name += " " + processes.get(1).command().get(i);
			
			int byteRead;
			
			try{
				process1 = processes.get(0).start();
				inFromProc1 = new BufferedInputStream(process1.getInputStream());
				
				process2 = processes.get(1).start();
				outToProc2 = new BufferedOutputStream(process2.getOutputStream());
				
				while((byteRead = inFromProc1.read()) != -1){
					outToProc2.write(byteRead);
				}
				
				System.out.printf("Waiting for Command %s to exit\n", command1Name);
				process1.waitFor();
				System.out.printf("Command %s has exited\n", command1Name);
				
				inFromProc1.close();
				outToProc2.flush();
				outToProc2.close();
				
				System.out.printf("Waiting for Command %s to exit\n", command2Name);
				process2.waitFor();
				System.out.printf("Command %s has exited\n", command2Name);
								
			}
			catch(IOException ex){
				// TODO handle exception
			}
			catch(InterruptedException ex){
				// TODO handle exception
			}
		}
		
		System.out.printf("Batch finished\n");
	}
}
