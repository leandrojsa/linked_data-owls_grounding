package br.ufba.dcc.linked_data.impl;

import impl.owl.WrappedIndividual;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;


import br.ufba.dcc.linked_data.LinkedDataSparqlPrefixes;
import br.ufba.dcc.linked_data.OWLSLinkedDataVocabulary;

public class LinkedDataSparqlPrefixesImpl extends WrappedIndividual implements LinkedDataSparqlPrefixes {
	
	public LinkedDataSparqlPrefixesImpl(final OWLIndividual ind) {
		super(ind);
	}
	

	public void setPrefixName(final URI op) {
		setProperty(OWLSLinkedDataVocabulary.PrefixName, op);
	}

	public URI getPrefixName() {
		return getPropertyAsURI(OWLSLinkedDataVocabulary.PrefixName);
	}
	
	public void setPrefixUri(final URI op) {
		setProperty(OWLSLinkedDataVocabulary.PrefixUri, op);
	}

	public URI getPrefixUri() {
		return getPropertyAsURI(OWLSLinkedDataVocabulary.PrefixUri);
	}
 
	public void removePrefixName() {
		if (hasProperty(OWLSLinkedDataVocabulary.PrefixName))
			removeProperty(OWLSLinkedDataVocabulary.PrefixName, null);
	}

	public void removePrefixUri() {
		if (hasProperty(OWLSLinkedDataVocabulary.PrefixUri))
			removeProperty(OWLSLinkedDataVocabulary.PrefixUri, null);
	}
	@Override
	public void delete() {
		removePrefixName();
		removePrefixUri();
		super.delete();
	}

}
