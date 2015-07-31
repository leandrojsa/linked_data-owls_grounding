package br.ufba.dcc.linked_data.impl;

import impl.owl.WrappedIndividual;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;


import br.ufba.dcc.linked_data.SparqlPrefixes;
import br.ufba.dcc.linked_data.OWLSSparqlVocabulary;

public class SparqlPrefixesImpl extends WrappedIndividual implements SparqlPrefixes {
	
	public SparqlPrefixesImpl(final OWLIndividual ind) {
		super(ind);
	}
	

	public void setPrefixName(final String op) {
		setProperty(OWLSSparqlVocabulary.PrefixName, op);
	}

	public  String getPrefixName() {
		return getPropertyAsString(OWLSSparqlVocabulary.PrefixName);
	}
	
	public void setPrefixUri(final URI op) {
		setProperty(OWLSSparqlVocabulary.PrefixUri, op);
	}

	public URI getPrefixUri() {
		return getPropertyAsURI(OWLSSparqlVocabulary.PrefixUri);
	}
 
	public void removePrefixName() {
		if (hasProperty(OWLSSparqlVocabulary.PrefixName))
			removeProperty(OWLSSparqlVocabulary.PrefixName, null);
	}

	public void removePrefixUri() {
		if (hasProperty(OWLSSparqlVocabulary.PrefixUri))
			removeProperty(OWLSSparqlVocabulary.PrefixUri, null);
	}
	@Override
	public void delete() {
		removePrefixName();
		removePrefixUri();
		super.delete();
	}

}
