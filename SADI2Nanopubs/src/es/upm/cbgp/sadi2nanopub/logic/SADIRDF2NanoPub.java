package es.upm.cbgp.sadi2nanopub.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NsIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.tdb.TDBFactory;

/**
 * 
 * @author Alejandro Rodríguez González Centre of Biotechnology and Plant
 *         Genomics Polytechnic University of Madrid
 * 
 */
public class SADIRDF2NanoPub {
	/*
	 * Models
	 */
	private Model modelInput;
	private Model modelOutput;
	private String fileInput;
	private String fileOutput;

	/*
	 * Nano pub nodes
	 */

	private Node nanoPubSchemaCollectionNode;
	private Node nanoPubSchemaAssertionNode;
	private Node nanoPubSchemaHasAssertionNode;
	private Node nanoPubSchemaHasProvenanceNode;
	private Node nanoPubSchemaHasPublicationInfoNode;
	private Node nanoPubSchemaNanoPublicationNode;
	private Node nanoPubSchemaProvenanceNode;
	private Node nanoPubSchemaPublicationInfoNode;

	/*
	 * RDF nodes
	 */
	private Node rdfTypeNode;
	private Node rdfSubGraphOf;

	/*
	 * Purl nodes
	 */
	private Node purlCoverageNode;
	private Node purlDescriptionNode;
	private Node purlIdentifierNode;
	private Node purlPublisherNode;
	private Node purlSourceNode;
	private Node purlTitleNode;
	private Node purlCreatedNode;
	private Node purlCreatorNode;

	/*
	 * Swan nodes
	 */

	private Node swanCreatedByNode;

	/*
	 * Quad nodes
	 */
	private Node nanoPubCollectionNode;

	private Node provenanceNode;
	private Node publicationInfoNode;

	private SADIServiceObjectData sadiServiceObjectData;
	private ProvenanceObjectData provenanceObjectData;
	private ArrayList<Quad> quads;

	private final String CREATOR_INFO = "Java SADI->NanoPub library";
	private final String ASSERTION = "/assertion/";
	private final String NANOPUBLICATION = "/nanopublication/";
	private final String NANOPUBLICATION_COLLECTION = "/nanopub_collection/";
	private final String PROVENANCE = "/provenance/";
	private final String PUBLICATION_INFO = "/publication_info/";

	/**
	 * Constructor.
	 * 
	 * @param fi
	 *            Receives the file with the input data (in RDF/XML).
	 * @param fo
	 *            Receives the file with the output data (in RDF/XML).
	 * @param ssod
	 *            Receives the SADI Service Object Data
	 * @param pd
	 *            Receives the Provenance Object Data.
	 */
	public SADIRDF2NanoPub(Model modelInput, Model modelOutput, SADIServiceObjectData ssod,
			ProvenanceObjectData pod) {
		this.createNodes();
		this.modelInput = modelInput;
		this.modelOutput = modelOutput;
		this.sadiServiceObjectData = ssod;
		this.provenanceObjectData = pod;
	}

	/**
	 * Method to create the nodes.
	 */
	private void createNodes() {
		this.createPurlNodes();
		this.createNanoPubsNodes();
		this.createRDFNodes();
		this.createSwanNodes();
	}

	/**
	 * Method to create SWAN nodes.
	 */
	private void createSwanNodes() {
		this.swanCreatedByNode = NodeFactory
				.createURI("http://swan.mindinformatics.org/ontologies/1.2/pav/createdBy");
	}

	/**
	 * Method to create RDF nodes.
	 */
	private void createRDFNodes() {
		this.rdfTypeNode = NodeFactory
				.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		this.rdfSubGraphOf = NodeFactory
				.createURI("http://www.w3.org/2004/03/trix/rdfg-1/subGraphOf");
	}

	/**
	 * Public method to generate the nanopub.
	 * 
	 * @throws Exception
	 *             It can throw an exception.
	 */
	public void createNanoPub() throws Exception {
		//this.createModel();
		String hashContext = createHashContext("");
		this.createNanoPub(hashContext);
	}

	/**
	 * Method to create nano pubs nodes.
	 */
	private void createNanoPubsNodes() {
		this.nanoPubSchemaAssertionNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#Assertion");
		this.nanoPubSchemaHasAssertionNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#hasAssertion");
		this.nanoPubSchemaHasProvenanceNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#hasProvenance");
		this.nanoPubSchemaHasPublicationInfoNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#hasPublicationInfo");
		this.nanoPubSchemaNanoPublicationNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#Nanopublication");
		this.nanoPubSchemaProvenanceNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#Provenance");
		this.nanoPubSchemaPublicationInfoNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#PublicationInfo");
		this.nanoPubSchemaCollectionNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#NanopublicationCollection");
		this.nanoPubSchemaNanoPublicationNode = NodeFactory
				.createURI("http://www.nanopub.org/nschema#Nanopublication");
	}

	/**
	 * Method to create purl nodes.
	 */
	private void createPurlNodes() {
		this.purlCoverageNode = NodeFactory
				.createURI("http://purl.org/dc/elements/1.1/coverage");
		this.purlDescriptionNode = NodeFactory
				.createURI("http://purl.org/dc/elements/1.1/description");
		this.purlIdentifierNode = NodeFactory
				.createURI("http://purl.org/dc/elements/1.1/identifier");
		this.purlPublisherNode = NodeFactory
				.createURI("http://purl.org/dc/elements/1.1/publisher");
		this.purlSourceNode = NodeFactory
				.createURI("http://purl.org/dc/elements/1.1/source");
		this.purlTitleNode = NodeFactory
				.createURI("http://purl.org/dc/elements/1.1/title");
		this.purlCreatedNode = NodeFactory
				.createURI("http://purl.org/dc/terms/created");
		this.purlCreatorNode = NodeFactory
				.createURI("http://purl.org/dc/terms/creator");
	}

	/**
	 * Method to create the models.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void createModel() throws Exception {
		//this.modelInput = ModelFactory.createOntologyModel();
		//this.modelInput.read(fileInput);
		//this.modelOutput = ModelFactory.createDefaultModel();
		//this.modelOutput.read(fileOutput);
	}

	/**
	 * Method to create the nanopub.
	 * 
	 * @param hash
	 *            Receive the context hash.
	 * @throws Exception
	 *             It can throws an exception.
	 */
	private void createNanoPub(String hash) throws Exception {
		String uniqueCode = ""
				+ (System.currentTimeMillis() + new java.util.Random(
						System.currentTimeMillis() * (hash).length())
						.nextInt(123456789));
		String nanoPublicationCollectionData = this.sadiServiceObjectData
				.getServiceName() + NANOPUBLICATION_COLLECTION + uniqueCode;
		String provenance = this.sadiServiceObjectData.getServiceName()
				+ PROVENANCE + uniqueCode;
		String pubInfo = this.sadiServiceObjectData.getServiceName()
				+ PUBLICATION_INFO + uniqueCode;

		this.nanoPubCollectionNode = NodeFactory
				.createURI(nanoPublicationCollectionData);
		this.provenanceNode = NodeFactory.createURI(provenance);
		this.publicationInfoNode = NodeFactory.createURI(pubInfo);
		this.createQuads();
	}

	/**
	 * Method to save the nano pub.
	 * 
	 * @throws Exception
	 *             It can throws an exception.
	 */
	public void saveNanoPub(String f) throws Exception {
		if (quads == null) {
			throw new Exception(
					"Quad list is null. Do you call createNanoPub() first?");
		}
		if (quads.size() == 0) {
			throw new Exception("Quad list is empty.");
		}
		Dataset ds = TDBFactory.createDataset();
		DatasetGraph dsg = ds.asDatasetGraph();
		for (int i = 0; i < quads.size(); i++) {
			dsg.add(quads.get(i));
		}
		RDFDataMgr.write(new FileOutputStream(new File(f)), dsg,
				RDFFormat.NQUADS);
	}

	/**
	 * Method to create the quads.
	 * 
	 * @return Return the array with the quads.
	 */
	private void createQuads() throws Exception {
		this.quads = new ArrayList<Quad>();
		createNanoPublicationCollectionQuad();
		createProvenanceQuads();
		createPublicationInfoQuads();
		createServiceResultQuads();
	}

	/**
	 * Method to create the quads of the service results.
	 */
	private void createServiceResultQuads() {
		/* We obtain the statements of the model. */
		StmtIterator res = modelOutput.listStatements();
		/* We create an array of SADIServiceResult objects. */
		ArrayList<SADIServiceResult> ssrs = new ArrayList<SADIServiceResult>();
		SADIServiceResult current = null;
		while (res.hasNext()) {
			Statement st = res.next();
			printSt(st);
			/*
			 * We get current statement. We get the subject. If we already have
			 * a SADIServiceResult object with this subject, we get it. If not,
			 * we create a new SADIServiceResultObject with this subject.
			 * 
			 * Once we have the SADIServiceResult object, we obtain predicate
			 * and object and add them to the service result.
			 * 
			 * A SADIServiceResult is identified by a subject (and the
			 * corresponding created hash). Each SADIServiceResult can have
			 * several Predicate-Object pairs associated.
			 */

			Node sub = getURIFromString(st.getSubject().getURI());
			current = getServiceResult(sub, ssrs);
			if (current == null) {
				current = new SADIServiceResult(sub,
						createHashContext(sub.toString()));
				ssrs.add(current);
			}
			Resource pred = st.getPredicate();
			RDFNode ob = st.getObject();
			Node p, o = null;
			if (ob.isResource()) {
				p = getURIFromString(pred.getURI());
				o = getURIFromString(ob.toString());
			} else {
				p = getURIFromString(pred.getURI());
				o = getLiteralFromString(ob.asLiteral().toString());
			}
			current.addPredicateAndObject(new PredicateAndObject(p, o));
		}
		/*
		 * Once all the statements had been verified, we create the quads for
		 * each combination.
		 */
		for (int i = 0; i < ssrs.size(); i++) {
			SADIServiceResult ssr = ssrs.get(i);
			for (int j = 0; j < ssr.getPredicatesAndObjects().size(); j++) {
				PredicateAndObject pao = ssr.getPredicatesAndObjects().get(j);
				createQuad(ssr.getSubject(), pao.getPredicate(),
						pao.getObject(), ssr.getHash());
			}
		}
	}

	private void printSt(Statement st) {
		System.out.println("-----");
		System.out.println("S: " + st.getSubject());
		System.out.println("P :" + st.getPredicate());
		System.out.println("O: " + st.getObject());
		System.out.println("-----");
		
	}

	/**
	 * Method to get the service result from the arraylist.
	 * 
	 * @param sub
	 *            Receives the subject.
	 * @param ssrs
	 *            Receives the arraylist.
	 * @return Returns true or false.
	 */
	private SADIServiceResult getServiceResult(Node sub,
			ArrayList<SADIServiceResult> ssrs) {
		for (int i = 0; i < ssrs.size(); i++) {
			if (ssrs.get(i).getSubject().getURI().toString()
					.equals(sub.getURI().toString()))
				return ssrs.get(i);
		}
		return null;
	}

	/**
	 * Method to create a quad with a given hash.
	 * 
	 * @param s
	 *            Receives the subject.
	 * @param p
	 *            Receives the predicate.
	 * @param o
	 *            Receives the object.
	 * @param hash
	 *            Receives the hash.
	 */
	private void createQuad(Node s, Node p, Node o, String hash) {
		String assertion = this.sadiServiceObjectData.getServiceName()
				+ ASSERTION + hash;
		String nanopub = this.sadiServiceObjectData.getServiceName()
				+ NANOPUBLICATION + hash;
		Node assertionNode = NodeFactory.createURI(assertion);
		Node nanoPubNode = NodeFactory.createURI(nanopub);
		quads.add(createQuad(nanoPubNode, this.rdfTypeNode,
				this.nanoPubSchemaNanoPublicationNode, nanoPubNode));
		quads.add(createQuad(assertionNode, this.rdfTypeNode,
				this.nanoPubSchemaAssertionNode, nanoPubNode));
		quads.add(createQuad(nanoPubNode, this.nanoPubSchemaHasAssertionNode,
				assertionNode, nanoPubNode));
		quads.add(createQuad(nanoPubNode, this.nanoPubSchemaHasProvenanceNode,
				this.provenanceNode, nanoPubNode));
		quads.add(createQuad(nanoPubNode,
				this.nanoPubSchemaHasPublicationInfoNode,
				this.publicationInfoNode, nanoPubNode));
		quads.add(createQuad(nanoPubNode, this.rdfSubGraphOf,
				this.nanoPubCollectionNode, nanoPubNode));
		quads.add(createQuad(this.provenanceNode, this.rdfTypeNode,
				this.nanoPubSchemaProvenanceNode, nanoPubNode));
		quads.add(createQuad(this.publicationInfoNode, this.rdfTypeNode,
				this.nanoPubSchemaPublicationInfoNode, nanoPubNode));
		quads.add(createQuad(s, p, o, assertionNode));
	}

	/**
	 * Method to create the nano publication quads.
	 */
	private void createNanoPublicationCollectionQuad() {
		quads.add(createQuad(this.nanoPubCollectionNode, this.rdfTypeNode,
				this.nanoPubSchemaCollectionNode, this.nanoPubCollectionNode));
	}

	/**
	 * Method to create the publication info quads.
	 */
	private void createPublicationInfoQuads() throws Exception {
		quads.add(createQuad(this.nanoPubCollectionNode, this.purlCreatedNode,
				getTypedLiteralFromString(getTimestamp()),
				this.publicationInfoNode));

		quads.add(createQuad(this.nanoPubCollectionNode, this.purlCreatorNode,
				getTypedLiteralFromString(CREATOR_INFO),
				this.publicationInfoNode));

		quads.add(createQuad(this.nanoPubCollectionNode,
				this.swanCreatedByNode,
				getURIFromString(this.sadiServiceObjectData.getServiceName()),
				this.publicationInfoNode));
	}

	/**
	 * Method to create the provenance quads.
	 */
	private void createProvenanceQuads() throws Exception {
		quads.add(createQuad(this.nanoPubCollectionNode, this.purlCoverageNode,
				getLiteralFromStringWithLanguage(this.sadiServiceObjectData
						.getCoverageContent()), this.provenanceNode));

		quads.add(createQuad(this.nanoPubCollectionNode,
				this.purlDescriptionNode,
				getLiteralFromStringWithLanguage(this.sadiServiceObjectData
						.getServiceDescription()), this.provenanceNode));

		quads.add(createQuad(this.nanoPubCollectionNode,
				this.purlIdentifierNode,
				getTypedLiteralFromString(this.sadiServiceObjectData
						.getServiceName()), this.provenanceNode));

		quads.add(createQuad(this.nanoPubCollectionNode,
				this.purlPublisherNode,
				getTypedLiteralFromString(this.provenanceObjectData
						.getPublisher()), this.provenanceNode));

		quads.add(createQuad(this.nanoPubCollectionNode, this.purlSourceNode,
				getURIFromString(this.sadiServiceObjectData.getServiceName()),
				this.provenanceNode));

		quads.add(createQuad(
				this.nanoPubCollectionNode,
				this.purlTitleNode,
				getTypedLiteralFromString(this.provenanceObjectData.getTitle()),
				this.provenanceNode));

		quads.add(createQuad(this.nanoPubCollectionNode, this.purlCreatedNode,
				getTypedLiteralFromString(getTimestamp()), this.provenanceNode));

		quads.add(createQuad(this.nanoPubCollectionNode, this.purlCreatorNode,
				getTypedLiteralFromString(this.provenanceObjectData
						.getPublisher()), this.provenanceNode));
	}

	/**
	 * Method to obtain timestamp in format: year-month-dayThour:min:sec
	 * 
	 * @return Returns the value.
	 */
	private Calendar getTimestamp() throws Exception {
		SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String date = dtf.format(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtf.parse(date));
		return cal;
	}

	/**
	 * Method to create the quad based on the output desired.
	 * 
	 * @param c
	 *            Receives the context.
	 * @param s
	 *            Receives the subject.
	 * @param p
	 *            Receives the predicate.
	 * @param o
	 *            Receives the object.
	 * @return Returns the quad.
	 */
	private Quad createQuad(Node c, Node s, Node p, Node o) {
		return new Quad(o, c, s, p);
	}

	/**
	 * Method to obtain an URI node from String.
	 * 
	 * @param s
	 *            Receives the String.
	 * @return Returns the node.
	 */
	private Node getURIFromString(String s) {
		return NodeFactory.createURI(s);
	}

	/**
	 * Method to obtain a Literal node from String.
	 * 
	 * @param s
	 *            Receives the String.
	 * @return Returns the node.
	 */
	private Node getLiteralFromString(String s) {
		return NodeFactory.createLiteral(s);
	}

	/**
	 * Method to obtain a Literal with language from String.
	 * 
	 * @param s
	 *            Receives the string in format "string@language".
	 * @return Returns the literal node.
	 */
	private Node getLiteralFromStringWithLanguage(String s) throws Exception {
		String parts[] = s.split("@");
		if (parts.length == 2) {
			return NodeFactory.createLiteral(parts[0], parts[1], false);
		} else {
			throw new Exception("Error getting literal and language from: " + s);
		}
	}

	/**
	 * Method to obtain a typed literal node from String.
	 * 
	 * @param s
	 *            Receives the String.
	 * @return Return the node.
	 */
	private Node getTypedLiteralFromString(Object s) {
		return this.modelInput.createTypedLiteral(s).asNode();
	}

	/**
	 * Method to create the context hash from the input URIs.
	 * 
	 * @return Return the hash.
	 */
	public String createHashContext(String st) {
		String stringContextToHash = st;
		stringContextToHash += Long.toString(System.currentTimeMillis());
		NsIterator nss = this.modelInput.listNameSpaces();
		while (nss.hasNext()) {
			String ns = nss.next();
			stringContextToHash += ns;
		}

		System.out.println("st ="+ st);
		System.out.println("stctH ="+ stringContextToHash);
		System.out.println("RESULT DATASET ="+ DigestUtils.md5Hex(""));
		System.out.println("RESULT DATASET ="+ DigestUtils.md5Hex(stringContextToHash));
		System.out.println("RESULT DATASET ="+ DigestUtils.md5Hex(""));
		
		return DigestUtils.md5Hex(stringContextToHash);
	}
}
