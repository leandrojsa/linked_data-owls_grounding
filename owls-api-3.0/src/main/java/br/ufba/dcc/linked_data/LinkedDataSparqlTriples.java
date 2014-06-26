package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;

public interface LinkedDataSparqlTriples extends OWLIndividual{
	
	public void setTripleSubject(final URI op);

	public URI getTripleSubject();
	
	public void setTriplePredicate(final URI op);

	public URI getTriplePredicate();
	
	public void setTripleObject(final URI op);

	public URI getTripleObject();

}
