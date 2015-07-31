package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;

public interface SparqlTriples extends OWLIndividual{
	
	public void setTripleSubject(final String op);

	public String getTripleSubject();
	
	public void setTriplePredicate(final String op);

	public String getTriplePredicate();
	
	public void setTripleObject(final String op);

	public String getTripleObject();

}
