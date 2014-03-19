package command;

/*
 * Class to store commands in an array of linked lists
 * Each element of the array is used for a specific command list
 * 1 - wdCommands
 * 2 - fileCommands
 * 3 - cmdCommands
 * 4 - pipeCommands
 */
public class CommandBucket {
	
	/*
	 * Class to represent a CommandNode in a linked list
	 */
	public class CommandNode {
		
		Command command;
		CommandNode nextNode;
		
		// Constructors
		private CommandNode(){
			command = null;
			nextNode = null;
		}
		
		private CommandNode(Command arg){
			this();
			command = arg;
		}
		
		// Get functions
		public CommandNode GetNextNode(){
			return this.nextNode;
		}
		
		public Command GetCommand(){
			return this.command;
		}
	}
	
	private CommandNode[] commandList;
	
	// Constructor
	public CommandBucket(){
		commandList = new CommandNode[4];
		for(int i = 0; i < 4; i++){
			commandList[i] = new CommandNode();
		}
	}
	
	// Get function
	public CommandNode[] GetCommandList(){
		return commandList;
	}
	
	/* 
	 * Adds a command to the CommandBucket
	 * Does not allow duplicate commands, as determined by command ID
	 * Returns a boolean value indicating whether or not the command was added
	 */
	public boolean addCommand(Command comm){
		CommandNode temp;
		CommandNode newNode = new CommandNode(comm);
		
		// Adds the command "comm" to the end of the correct list based on type
		if(comm instanceof wdCommand){
			temp = commandList[0];
			while(temp.nextNode != null){
				temp = temp.nextNode;
				if(temp.command.GetID().equals(comm.GetID()))	// Checks for duplicate ID
					return false;
			}
				
			temp.nextNode = newNode;
			return true;
		}
		else if(comm instanceof fileCommand){
			temp = commandList[1];
			while(temp.nextNode != null){
				temp = temp.nextNode;
				if(temp.command.GetID().equals(comm.GetID()))	// Checks for duplicate ID
					return false;
			}
				
			temp.nextNode = newNode;
			return true;
		}
		else if(comm instanceof cmdCommand){
			temp = commandList[2];
			while(temp.nextNode != null){
				temp = temp.nextNode;
				if(temp.command.GetID().equals(comm.GetID()))	// Checks for duplicate ID
					return false;
			}
				
			temp.nextNode = newNode;
			return true;
		}
		else if(comm instanceof pipeCommand){
			temp = commandList[3];
			while(temp.nextNode != null){
				temp = temp.nextNode;
				if(temp.command.GetID().equals(comm.GetID()))	// Checks for duplicate ID
					return false;
			}
				
			temp.nextNode = newNode;
			return true;
		}
		else
			return false;
	}

}
