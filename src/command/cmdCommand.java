package command;

import org.w3c.dom.Element;

/*
 * Class representing cmd commands in batch files
 */
public class cmdCommand extends Command {

	private String[] args;	// array of command arguments
	private String in;
	private String out;
	
	// Constructors
	public cmdCommand() {
		super((String) null, (String) null);
	}
	
	public cmdCommand(String ID, String PATH, String ARG, String IN, String OUT) {
		super(ID, PATH);
		this.args = ARG.split(" ");
		this.in = IN;
		this.out = OUT;		
	}
	
	// Extracts cmdCommand data from Document element
	public void ParseCommand(Element elem){
		this.id = elem.getAttribute("id");
		this.path = elem.getAttribute("path");
		this.args = elem.getAttribute("args").split(" ");
		this.in = elem.getAttribute("in");
		this.out = elem.getAttribute("out");
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
	
	public String[] GetArgs(){
		return args;
	}
	
	public String GetInput(){
		return in;
	}
	
	public String GetOutput(){
		return out;
	}
}
