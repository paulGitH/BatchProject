package batch;

import command.*;

/*
 * Batch class that stores a list of commands in a batch, the working directory of the batch,
 * and a boolean flag indicating whether or not the batch contains a pipe command *
 */
public class Batch {
	
	private String workingDir;
	private boolean pipeCmdFlag;
	private CommandBucket commands;
	
	// Constructor
	public Batch(){
		workingDir = null;
		pipeCmdFlag = false; 
		commands = new CommandBucket();
	}
	
	/*
	 * Adds the command passed to the batch's list of commands
	 * Alters boolean pipe flag when a pipe command is added
	 * 
	 * @param arg the Command to add
	 */
	public void AddCommand (Command arg){
		commands.addCommand(arg);
					
		if(arg instanceof pipeCommand){
			pipeCmdFlag = true;
		}
	}
	
	/*
	 * Sets the working directory of the batch
	 * 
	 * @param arg string containing working directory
	 */
	void SetWorkingDir(wdCommand arg){
		workingDir = arg.GetPath();
		System.out.printf("Working directory will be set to: %s\n", workingDir);
	}
	
	/*
	 * Returns a string containing the working directory 
	 */
	public String GetWorkingDir(){
		return workingDir;
	}
	
	/*
	 * Returns a boolean indicating whether the batch contains a pipe command
	 */
	public boolean GetPipeFlag(){
		return pipeCmdFlag;
	}
	
	/*
	 * Returns a CommandBucket containing all commands (wd, file, cmd, pipe) in the batch
	 */
	public CommandBucket GetCommands(){
		return commands;
	}
	
	/*
	 * Returns the name(path) of the file command with the ID that matches arg
	 * Returns null if the file ID is not matched
	 */
	public String FindFile(String arg){
		
		CommandBucket.CommandNode tempNode = commands.GetCommandList()[1];
		
		while(tempNode.GetNextNode() != null){
			tempNode = tempNode.GetNextNode();
			if(tempNode.GetCommand().GetID().equals(arg))
				return tempNode.GetCommand().GetPath();
		}
		
		return null;
	}
	
	/*
	 * Returns the cmdCommand with the ID that matches arg
	 * Returns null if the command ID is not matched
	 */
	public cmdCommand FindCmd(String arg){
		CommandBucket.CommandNode tempNode = commands.GetCommandList()[2];
		
		while(tempNode.GetNextNode() != null){
			tempNode = tempNode.GetNextNode();
			if(tempNode.GetCommand().GetID().equals(arg))
				return (cmdCommand) tempNode.GetCommand();
		}
		
		return null;
	}
}
