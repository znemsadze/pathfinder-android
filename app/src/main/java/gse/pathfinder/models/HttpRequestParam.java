package gse.pathfinder.models;

import java.io.Serializable;

public class HttpRequestParam implements Serializable {
	private static final long serialVersionUID = 8536338826117943604L;
	private int id;
	private String name;
	private String value;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
