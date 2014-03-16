package command;

import org.w3c.dom.Element;

abstract public class Command {
	String id;
	String path;
	
	Command(){
		this((String)null, (String)null); 
	}

	Command(String ID, String PATH){
		this.id = ID;
		this.path = PATH;
	}
	
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
	
	public abstract void ParseCommand(Element elem);
}
