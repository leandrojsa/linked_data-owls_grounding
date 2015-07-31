package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owls.grounding.AtomicGrounding;
import org.mindswap.owls.grounding.MessageMap;


public interface SparqlAtomicGrounding extends AtomicGrounding<String>{
	
	public void setSparqlEndPoint(URI uri);
	
	public void addPrefix(OWLIndividual value);
	
	public void setInputMessage(final MessageMap<String> map);
	
	public void addTriple(OWLIndividual value);

}
