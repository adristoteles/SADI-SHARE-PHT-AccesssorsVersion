package es.upm.cbgp.sadi2nanopub.logic;

public class ProvenanceObjectData {

	private String publisher = "Publisher not defined.";
	private String title = "Title not defined.";

	public ProvenanceObjectData(String p, String t) {
		this.publisher = p;
		this.title = t;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
