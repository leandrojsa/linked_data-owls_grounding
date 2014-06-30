package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;

public interface SparqlTriples extends OWLIndividual{
	
	public void setTripleSubject(final URI op);

	public String getTripleSubject();
	
	public void setTriplePredicate(final URI op);

	public String getTriplePredicate();
	
	public void setTripleObject(final URI op);

	public String getTripleObject();

}
