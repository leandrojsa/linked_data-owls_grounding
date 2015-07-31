package br.ufba.dcc.linked_data.impl;

import impl.owl.WrappedIndividual;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;


import br.ufba.dcc.linked_data.SparqlTriples;
import br.ufba.dcc.linked_data.OWLSSparqlVocabulary;

public class SparqlTriplesImpl extends WrappedIndividual implements SparqlTriples{

	public SparqlTriplesImpl(final OWLIndividual ind) {
		super(ind);
	}
	

	public void setTripleSubject(final String op) {
		setProperty(OWLSSparqlVocabulary.TripleSubject, op);
	}

	public String getTripleSubject() {
		return getPropertyAsString(OWLSSparqlVocabulary.TripleSubject);
	}
	
	public void setTriplePredicate(final String op) {
		setProperty(OWLSSparqlVocabulary.TriplePredicate, op);
	}

	public String getTriplePredicate() {
		return getPropertyAsString(OWLSSparqlVocabulary.TriplePredicate);
	}
	
	public void setTripleObject(final String op) {
		setProperty(OWLSSparqlVocabulary.TripleObject, op);
	}

	public String getTripleObject() {
		return getPropertyAsString(OWLSSparqlVocabulary.TripleObject);
	}
 
	public void removeTripleSubject() {
		if (hasProperty(OWLSSparqlVocabulary.TripleSubject))
			removeProperty(OWLSSparqlVocabulary.TripleSubject, null);
	}

	public void removeTriplePredicate() {
		if (hasProperty(OWLSSparqlVocabulary.TriplePredicate))
			removeProperty(OWLSSparqlVocabulary.TriplePredicate, null);
	}
	
	public void removeTripleObject() {
		if (hasProperty(OWLSSparqlVocabulary.TripleObject))
			removeProperty(OWLSSparqlVocabulary.TripleObject, null);
	}

	@Override
	public void delete() {
		removeTripleSubject();
		removeTriplePredicate();
		removeTripleObject();
		super.delete();
	}

}
