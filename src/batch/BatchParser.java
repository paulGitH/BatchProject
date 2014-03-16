/**
	Steps through a XML file to create a batch from the individual lines
	of the file
*/

package batch;

import java.io.*;
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
		// Run processes
	}
}
