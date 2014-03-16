package command;

public class CommandBucket {
	
	CommandNode[] commandList;
	
	public CommandBucket(){
		commandList = new CommandNode[4];
		for(int i = 0; i < 4; i++){
			commandList[i] = new CommandNode();
		}
	}
	
	public CommandNode[] GetCommandList(){
		return commandList;
	}
	
	public boolean addCommand(Command comm){
		CommandNode temp;
		CommandNode newNode = new CommandNode(comm);
		
		if(comm instanceof wdCommand){
			temp = commandList[0];
			while(temp.nextNode != null){
				temp = temp.nextNode;
				if(temp.command.GetID().equals(comm.GetID()))
					return false;
			}
				
			temp.nextNode = newNode;
			return true;
		}
		else if(comm instanceof fileCommand){
			temp = commandList[1];
			while(temp.nextNode != null){
				temp = temp.nextNode;
				if(temp.command.GetID().equals(comm.GetID()))
					return false;
			}
				
			temp.nextNode = newNode;
			return true;
		}
		else if(comm instanceof cmdCommand){
			temp = commandList[2];
			while(temp.nextNode != null){
				temp = temp.nextNode;
				if(temp.command.GetID().equals(comm.GetID()))
					return false;
			}
				
			temp.nextNode = newNode;
			return true;
		}
		else if(comm instanceof pipeCommand){
			temp = commandList[3];
			while(temp.nextNode != null){
				temp = temp.nextNode;
				if(temp.command.GetID().equals(comm.GetID()))
					return false;
			}
				
			temp.nextNode = newNode;
			return true;
		}
		else
			return false;// TODO throw fault?
	}

}
