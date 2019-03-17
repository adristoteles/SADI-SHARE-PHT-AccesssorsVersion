package es.upm.cbgp.sadi2nanopub.logic;

import java.util.ArrayList;

import org.apache.jena.graph.Node;

/**
 * Class to store the result of a service.
 * 
 * It contains a set of predicate and object results for each service output (identified by the subject)
 * @author Alejandro Rodríguez González Centre of Biotechnology and Plant
 *         Genomics Polytechnic University of Madrid
 *
 */
public class SADIServiceResult {

	private Node subject;
	private ArrayList<PredicateAndObject> predsAndObjects;
	private String hash;

	/**
	 * Constructor. Receives the subject of the triple and the hash associated to this result.
	 * @param s Receives the subject node.
	 * @param h Receives the hash.
	 */
	public SADIServiceResult(Node s, String h) {
		this.subject = s;
		this.hash = h;
		this.predsAndObjects = new ArrayList<PredicateAndObject>();
	}

	/**
	 * Method to get the subject.
	 * @return Return the node.
	 */
	public Node getSubject() {
		return this.subject;
	}

	/**
	 * Method to get the hash.
	 * @return Return the hash.
	 */
	public String getHash() {
		return this.hash;
	}
	/**
	 * Method to add a pair predicate and object to the service result.
	 * @param pao Receives the object.
	 */
	public void addPredicateAndObject(PredicateAndObject pao) {
		this.predsAndObjects.add(pao);
	}

	/**
	 * Method to get the list of predicate-object pairs associated to the service result.
	 * @return Return the list.
	 */
	public ArrayList<PredicateAndObject> getPredicatesAndObjects() {
		return this.predsAndObjects;
	}

}
