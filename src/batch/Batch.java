package batch;

import command.*;

public class Batch {
	
	String workingDir;
	boolean pipeCmdFlag;
	CommandBucket commands;
	
	Batch(){
		workingDir = null;
		pipeCmdFlag = false; 
		commands = new CommandBucket();
	}
	
	public void AddCommand (Command arg){
		commands.addCommand(arg);
					
		if(arg instanceof pipeCommand){
			pipeCmdFlag = true;
		}
	}
	
	void SetWorkingDir(Command arg){
		workingDir = arg.GetPath();
		System.out.printf("Working directory will be set to: %s\n", workingDir);
	}
	
	public String GetWorkingDir(){
		return workingDir;
	}
	
	public boolean GetPipeFlag(){
		return pipeCmdFlag;
	}
	
	public CommandBucket GetCommands(){
		return commands;
	}
}
