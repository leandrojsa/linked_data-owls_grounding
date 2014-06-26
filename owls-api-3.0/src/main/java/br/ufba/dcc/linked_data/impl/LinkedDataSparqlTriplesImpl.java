package br.ufba.dcc.linked_data.impl;

import impl.owl.WrappedIndividual;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;


import br.ufba.dcc.linked_data.LinkedDataSparqlTriples;
import br.ufba.dcc.linked_data.OWLSLinkedDataVocabulary;

public class LinkedDataSparqlTriplesImpl extends WrappedIndividual implements LinkedDataSparqlTriples{
	
	public LinkedDataSparqlTriplesImpl(final OWLIndividual ind) {
		super(ind);
	}
	

	public void setTripleSubject(final URI op) {
		setProperty(OWLSLinkedDataVocabulary.TripleSubject, op);
	}

	public URI getTripleSubject() {
		return getPropertyAsURI(OWLSLinkedDataVocabulary.TripleSubject);
	}
	
	public void setTriplePredicate(final URI op) {
		setProperty(OWLSLinkedDataVocabulary.TriplePredicate, op);
	}

	public URI getTriplePredicate() {
		return getPropertyAsURI(OWLSLinkedDataVocabulary.TriplePredicate);
	}
	
	public void setTripleObject(final URI op) {
		setProperty(OWLSLinkedDataVocabulary.TripleObject, op);
	}

	public URI getTripleObject() {
		return getPropertyAsURI(OWLSLinkedDataVocabulary.TripleObject);
	}
 
	public void removeTripleSubject() {
		if (hasProperty(OWLSLinkedDataVocabulary.TripleSubject))
			removeProperty(OWLSLinkedDataVocabulary.TripleSubject, null);
	}

	public void removeTriplePredicate() {
		if (hasProperty(OWLSLinkedDataVocabulary.TriplePredicate))
			removeProperty(OWLSLinkedDataVocabulary.TriplePredicate, null);
	}
	
	public void removeTripleObject() {
		if (hasProperty(OWLSLinkedDataVocabulary.TripleObject))
			removeProperty(OWLSLinkedDataVocabulary.TripleObject, null);
	}

	@Override
	public void delete() {
		removeTripleSubject();
		removeTriplePredicate();
		removeTripleObject();
		super.delete();
	}

}
