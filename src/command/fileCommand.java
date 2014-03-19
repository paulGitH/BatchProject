package command;

import org.w3c.dom.Element;

/*
 * Class representing file commands in batch files
 */
public class fileCommand extends Command {

	// Constructors
	public fileCommand() {
		super((String) null, (String) null);
	}

	public fileCommand(String ID, String PATH) {
		super(ID, PATH);
	}
	
	// Extracts fileCommand data from Document element
	public void ParseCommand(Element elem){
		this.id = elem.getAttribute("id");
		this.path = elem.getAttribute("path");
	}
		
	// Set/Get functions
	public void SetID(String arg){
		super.SetID(arg);
	}
	
	public String GetID(){
		return super.GetID();
	}
	
	public void SetPath(String arg){
		super.SetPath(arg);
	}
	
	public String GetPath(){
		return super.GetPath();
	}
}
