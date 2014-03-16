package command;

import org.w3c.dom.Element;

public class cmdCommand extends Command {

	String[] args;
	String in;
	String out;
	
	public cmdCommand() {
		super((String) null, (String) null);
	}
	
	public void ParseCommand(Element elem){
		this.id = elem.getAttribute("id");
		this.path = elem.getAttribute("path");
		this.args = elem.getAttribute("args").split(" ");
		this.in = elem.getAttribute("in");
		this.out = elem.getAttribute("out");
	}

	public cmdCommand(String ID, String PATH, String ARG, String IN, String OUT) {
		super(ID, PATH);
		this.args = ARG.split(" ");
		this.in = IN;
		this.out = OUT;
		
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
