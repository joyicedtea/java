package Test;

import java.io.Serializable;

public class Record implements Serializable{
	private static final long serialVersionUID = 1L;
	Long seize;
	String filename;
	Long position;
	public Long getSeize() {
		return seize;
	}
	public void setSeize(Long seize) {
		this.seize = seize;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public Long getPosition() {
		return position;
	}
	public void setPosition(Long position) {
		this.position = position;
	}
	public Record(Long seize, String filename, Long position) {
		super();
		this.seize = seize;
		this.filename = filename;
		this.position = position;
	}
	
	
	

}
