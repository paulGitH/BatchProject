package command;

import org.w3c.dom.Element;

public class fileCommand extends Command {

	public fileCommand() {
		super((String) null, (String) null);
	}

	public void ParseCommand(Element elem){
		this.id = elem.getAttribute("id");
		this.path = elem.getAttribute("path");
	}
	
	public fileCommand(String ID, String PATH) {
		super(ID, PATH);
	}
	
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
