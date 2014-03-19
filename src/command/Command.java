package command;

import org.w3c.dom.Element;
/*
 * Abstract Command class that contains the ID, and path for any command 
 */
abstract public class Command {
	String id;
	String path;
	
	// Constructors
	public Command(){
		this((String)null, (String)null); 
	}

	public Command(String ID, String PATH){
		this.id = ID;
		this.path = PATH;
	}
	
	// Set/Get functions 
	void SetID(String arg){
		this.id = arg;
	}
	
	public String GetID(){
		return id;
	}
	
	void SetPath(String arg){
		this.path = arg;
	}
	
	public String GetPath(){
		return path;
	}
	
	// Abstract method that parses command data from a document element
	public abstract void ParseCommand(Element elem);
}
