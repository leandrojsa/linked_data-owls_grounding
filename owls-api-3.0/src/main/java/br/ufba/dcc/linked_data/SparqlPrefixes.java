package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;

public interface SparqlPrefixes extends OWLIndividual {
	
	

	public void setPrefixName(final String op);

	public String getPrefixName();
	
	public void setPrefixUri(final URI op);

	public URI getPrefixUri();
	
	public void removePrefixName();
	
	public void removePrefixUri();

}
