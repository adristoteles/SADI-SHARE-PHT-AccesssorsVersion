package es.upm.cbgp.sadi2nanopub.logic;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class Main {

	
	public Main() throws Exception {
		Model modelInput = ModelFactory.createOntologyModel();
		Model modelOutput = ModelFactory.createDefaultModel();
			
		String rdfout = "data/rdfoutput_sync.rdf";
		String rdfin = "data/sampledata.rdf";
		modelInput.read(rdfin);
		modelOutput.read(rdfout);
		
		String svName = "pr";
		//String svName = "http://biordf.org:8080/cgi-bin/services/Dragon/getAlleleDescription.pl";
		String svDesc = "Service Description: whatever@en";
		String coverageContent = "Output from SADI Service@en";
		String publisher = "wilkinsonlab.info";
		String title = "getAlleleDescription (Syncronous)";
		SADIServiceObjectData ssod = new SADIServiceObjectData(svName,svDesc,coverageContent);
		ProvenanceObjectData pod = new ProvenanceObjectData(publisher, title);
		SADIRDF2NanoPub sr2np = new SADIRDF2NanoPub((OntModel) modelInput, modelOutput, ssod, pod);
		sr2np.createNanoPub();
		sr2np.saveNanoPub("nquadgen_sync.nq");
	}
	public static void main(String[] args) {
		try {
			new Main();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
