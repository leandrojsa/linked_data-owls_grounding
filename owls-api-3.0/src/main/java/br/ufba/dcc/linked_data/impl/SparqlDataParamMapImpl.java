package br.ufba.dcc.linked_data.impl;

import java.net.URI;

import impl.owls.grounding.BaseMessageMap;

import org.mindswap.owl.OWLDataProperty;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLObjectProperty;
import org.mindswap.owls.grounding.MessageMap.StringMessageMap;
import org.mindswap.owls.vocabulary.OWLS;

import br.ufba.dcc.linked_data.OWLSSparqlVocabulary;

public class SparqlDataParamMapImpl extends BaseMessageMap<String> implements StringMessageMap {

	public SparqlDataParamMapImpl(OWLIndividual ind) {
		super(ind);
	}

	/* @see org.mindswap.owls.grounding.MessageMap#getTransformation() */
	
	public String getTransformation()
	{
	    return getPropertyAsString(OWLS.Grounding.xsltTransformationString);
	}
	
	protected OWLDataValue groundingParameterValue(final String value) { 
		return getOntology().createDataValue(URI.create(value)); 
	}

	/* @see org.mindswap.owls.grounding.MessageMap#setTransformation(java.lang.Object) */
	
	public void setTransformation(final String xsltTransformation)
	{
	    setProperty(OWLS.Grounding.xsltTransformationString, xsltTransformation);
	}

	@Override protected OWLDataProperty groundingParameterProperty() { return OWLSSparqlVocabulary.SparqlDataParam; }
	@Override protected OWLObjectProperty owlsParameterProperty() { return OWLS.Grounding.owlsParameter; }

}
