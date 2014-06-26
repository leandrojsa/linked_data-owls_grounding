package br.ufba.dcc.linked_data.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import impl.owls.grounding.MessageMapAtomicGroundingImpl;

import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLObjectProperty;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.grounding.AtomicGrounding;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.grounding.MessageMap;
import org.mindswap.owls.grounding.MessageMap.StringMessageMap;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.query.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufba.dcc.linked_data.LinkedDataAtomicGrounding;
import br.ufba.dcc.linked_data.LinkedDataGrounding;
import br.ufba.dcc.linked_data.OWLSLinkedDataVocabulary;

public class LinkedDataAtomicGroundingImpl extends MessageMapAtomicGroundingImpl<String> implements LinkedDataAtomicGrounding{
	
	private String query;
	private static final Logger logger = LoggerFactory.getLogger(LinkedDataAtomicGroundingImpl.class);
	

	public LinkedDataAtomicGroundingImpl(OWLIndividual ind) {
		super(ind);
	}
	
	public URI getSparqlEndPoint(){
		return getPropertyAsURI(OWLSLinkedDataVocabulary.sparqlEndPoint);
	}
/*	
	public String getQueryPrefixies(){
		String prefixes = "";
		
	}
*/	
	@Override
	public URL getDescriptionURL() {
		try {
			return getSparqlEndPoint().toURL();
		} catch(final MalformedURLException e) {
			return null;
		}
	}

	@Override
	public Grounding<? extends AtomicGrounding<String>, String> getGrounding() {
		return getGrounding(LinkedDataGrounding.class);
	}

	public String getGroundingType() { return AtomicGrounding.GROUNDING_LINKED_DATA; }

	@Override
	public ValueMap<Output, OWLValue> invoke(ValueMap<Input, OWLValue> values) throws ExecutionException {
		return invoke(values, OWLFactory.createKB());
	}

	@Override
	public ValueMap<Output, OWLValue> invoke(ValueMap<Input, OWLValue> inputs,	OWLKnowledgeBase env)
			throws ExecutionException {
		
		System.out.println("\\o/");
		return null;
	}

	@Override
	protected MessageMap<String> createInputMessageMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MessageMap<String> createOutputMessageMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override protected OWLObjectProperty inputMessageMapProperty() { return null;}//OWLSLinkedDataVocabulary.SparqlInputParam; }

	@Override protected OWLObjectProperty outputMessageMapProperty() { return null;}//OWLSLinkedDataVocabulary.SparqlOutputParam; }

	@Override
	protected Class<? extends MessageMap<String>> messageMapType() { return StringMessageMap.class; }


}
