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
