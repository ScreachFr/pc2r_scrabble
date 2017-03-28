package core;


public class Message {

	private String domaine;
	private String[] args;
	private String dest;
		
	
	public Message(String domaine, String[] args) {
		this.domaine = domaine;
		this.args = args;
	}
	public Message(String domaine, String dest, String[] args) {
		this.domaine = domaine;
		this.args = args;
		this.dest = dest;
	}
	
	public String generateMessage() {
		String chaine = String.join("/", args);
		return domaine + "/" + chaine + "/\n";
	}

	public String getDomaine() {
		return domaine;
	}

	public String[] getArgs() {
		return args;
	}
	
	public boolean isPrivate() {
		return dest != null;
	}
	public String getDest() {
		return dest;
	}
	
}
