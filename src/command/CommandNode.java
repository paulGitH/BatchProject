package command;

public class CommandNode {
	
	Command command;
	CommandNode nextNode;
	
	CommandNode(){
		command = null;
		nextNode = null;
	}
	
	CommandNode(Command arg){
		this();
		command = arg;
	}
	
	public CommandNode GetNextNode(){
		return this.nextNode;
	}
	
	public Command GetCommand(){
		return this.command;
	}
}
