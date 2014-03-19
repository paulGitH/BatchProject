package command;

import org.w3c.dom.Element;

/*
 * Class representing pipe commands in batch files
 */
public class pipeCommand extends Command {
	
	// Stores command IDs in pipeCommand
	private String cmd1;
	private String cmd2;
	
	// Constructors
	public pipeCommand() {
		super((String) null, (String) null);
		this.cmd1 = null;
		this.cmd2 = null;
	}

	public pipeCommand(String ID, String CMD1, String CMD2) {
		super(ID, (String) null);
		this.cmd1 = CMD1;
		this.cmd2 = CMD2;		
	}
	
	// Extracts pipeCommand data from Document element
	public void ParseCommand(Element elem){
		this.id = elem.getAttribute("id");
		this.path = elem.getAttribute("path");
		this.cmd1 = elem.getAttribute("cmd1");
		this.cmd2 = elem.getAttribute("cmd2");
	}
	
	// Set/Get functions
	public void SetID(String arg){
		super.SetID(arg);
	}
	
	public String GetID(){
		return super.GetID();
	}
	
	public void SetCmd1(String arg){
		this.cmd1 = arg;
	}
	
	public String GetCmd1(){
		return this.cmd1;
	}
	
	public void SetCmd2(String arg){
		this.cmd2 = arg;
	}
	
	public String GetCmd2(){
		return this.cmd2;
	}
}
