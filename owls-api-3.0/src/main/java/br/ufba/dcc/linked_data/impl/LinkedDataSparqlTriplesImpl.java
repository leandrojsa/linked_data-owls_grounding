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
 /*
	public void removeOperation() {
		if (hasProperty(OWLS.Grounding.wsdlOperation))
			removeProperty(OWLS.Grounding.wsdlOperation, null);
	}

	public void removePortType() {
		if (hasProperty(OWLS.Grounding.portType))
			removeProperty(OWLS.Grounding.portType, null);
	}
*/
	@Override
	public void delete() {
		//removeOperation();
		//removePortType();
		super.delete();
	}

}
