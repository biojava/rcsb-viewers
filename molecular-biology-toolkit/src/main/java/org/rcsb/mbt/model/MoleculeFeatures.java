package org.rcsb.mbt.model;

public class MoleculeFeatures {
	private String prdId = "";
	private String name = "";
	private String type = "";
	private String prdClass = "";
	private String details = "";

	public String getPrdId() {
		return prdId;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getPrdClass() {
		return prdClass;
	}

	public String getDetails() {
		return details;
	}

	public void setPrdId(String prdId) {
		this.prdId = prdId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPrdClass(String prdClass) {
		this.prdClass = prdClass;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
