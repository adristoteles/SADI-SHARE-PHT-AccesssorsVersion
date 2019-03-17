package es.upm.cbgp.sadi2nanopub.logic;

import org.apache.jena.graph.Node;

/**
 * Class to store the pair predicate-object of a service result.
 * 
 * @author Alejandro Rodríguez González Centre of Biotechnology and Plant
 *         Genomics Polytechnic University of Madrid
 *
 */

public class PredicateAndObject {

	private Node predicate;
	private Node object;
	
	/**
	 * Constructor.
	 * @param p Receives the predicate.
	 * @param o Receives the object.
	 */
	public PredicateAndObject(Node p, Node o) {
		this.predicate = p;
		this.object = o;
	}

	/**
	 * Method to obtain the predicate.
	 * @return Return the object.
	 */
	public Node getPredicate() {
		return predicate;
	}

	/**
	 * Method to obtain the object.
	 * @return Return the object.
	 */
	public Node getObject() {
		return object;
	}
}
