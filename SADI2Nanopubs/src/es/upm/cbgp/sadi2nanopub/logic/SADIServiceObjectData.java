package es.upm.cbgp.sadi2nanopub.logic;

public class SADIServiceObjectData {

	private String serviceName = "Service name not available.";
	private String serviceDescription = "Service description not available@en";
	private String coverageContent = "Coverage content not defined@en";

	
	public SADIServiceObjectData(String sn, String sd, String cc) {
		this.serviceName = sn;
		this.serviceDescription = sd;
		this.coverageContent = cc;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getCoverageContent() {
		return coverageContent;
	}

	public void setCoverageContent(String coverageContent) {
		this.coverageContent = coverageContent;
	}


}
