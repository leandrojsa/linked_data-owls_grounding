package br.ufba.dcc.linked_data.impl;

import impl.owl.GenericOWLConverter;

import java.net.URI;

import org.mindswap.owl.OWLClass;
import org.mindswap.owl.OWLModel;
import org.mindswap.owl.OWLObjectConverter;
import org.mindswap.owl.OWLObjectConverterRegistry;
import org.mindswap.owls.grounding.AtomicGrounding;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.grounding.MessageMap;
import org.mindswap.owls.grounding.MessageMap.StringMessageMap;

import br.ufba.dcc.linked_data.LinkedDataAtomicGrounding;
import br.ufba.dcc.linked_data.LinkedDataGrounding;
import br.ufba.dcc.linked_data.LinkedDataGroundingProvider;
import br.ufba.dcc.linked_data.LinkedDataSparqlPrefixes;
import br.ufba.dcc.linked_data.LinkedDataSparqlTriples;
import br.ufba.dcc.linked_data.OWLSLinkedDataVocabulary;

public class LinkedDataGroundingProviderImpl implements LinkedDataGroundingProvider{

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLAtomicGrounding(java.net.URI, org.mindswap.owl.OWLModel) */
	public LinkedDataAtomicGrounding createLinkedDataAtomicGrounding(final URI uri, final OWLModel model)
	{
		return new LinkedDataAtomicGroundingImpl(model.createInstance(OWLSLinkedDataVocabulary.SparqlAtomicProcessGrounding, uri));
	}

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLGrounding(java.net.URI, org.mindswap.owl.OWLModel) */
	public LinkedDataGrounding createLinkedDataGrounding(final URI uri, final OWLModel model)
	{
		return new LinkedDataGroundingImpl(model.createInstance(OWLSLinkedDataVocabulary.SparqlGrounding, uri));
	}

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLInputMessageMap(java.net.URI, org.mindswap.owl.OWLModel) */
	public MessageMap<String> createSparqlInputParamMap(final URI uri, final OWLModel model)
	{
		return new LinkedDataSparqlDataParamMapImpl(model.createInstance(OWLSLinkedDataVocabulary.SparqlIntputParamMap, uri));
	}

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLOutputMessageMap(java.net.URI, org.mindswap.owl.OWLModel) */
	public MessageMap<String> createSparqlOutputParamMap(final URI uri, final OWLModel model)
	{
		return new LinkedDataSparqlDataParamMapImpl(model.createInstance(OWLSLinkedDataVocabulary.SparqlOutputParamMap, uri));
	}

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLOperationRef(java.net.URI, org.mindswap.owl.OWLModel) */
	public LinkedDataSparqlPrefixes createLinkedDataSparqlPrefixes(final URI uri, final OWLModel model)
	{
		return new LinkedDataSparqlPrefixesImpl(model.createInstance((OWLClass) OWLSLinkedDataVocabulary.SparqlPrefixMap, uri));
	}
	
	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLOperationRef(java.net.URI, org.mindswap.owl.OWLModel) */
	public LinkedDataSparqlTriples createLinkedDataSparqlTriples(final URI uri, final OWLModel model)
	{
		return new LinkedDataSparqlTriplesImpl(model.createInstance(OWLSLinkedDataVocabulary.SparqlTripleMap, uri));
	}

	/* @see org.mindswap.owl.OWLObjectConverterProvider#registerConverters(org.mindswap.owl.OWLObjectConverterRegistry) */
	public void registerConverters(final OWLObjectConverterRegistry registry)
	{
		System.out.println("um começo!");
		
		final OWLObjectConverter<LinkedDataGrounding> gc = new GenericOWLConverter<LinkedDataGrounding>(
				LinkedDataGroundingImpl.class, OWLSLinkedDataVocabulary.SparqlGrounding);
		
		final OWLObjectConverter<LinkedDataAtomicGrounding> agc = new GenericOWLConverter<LinkedDataAtomicGrounding>(
				LinkedDataAtomicGroundingImpl.class, OWLSLinkedDataVocabulary.SparqlAtomicProcessGrounding);

		final OWLObjectConverter<LinkedDataSparqlDataParamMapImpl> immc =
			new GenericOWLConverter<LinkedDataSparqlDataParamMapImpl>(LinkedDataSparqlDataParamMapImpl.class,
					OWLSLinkedDataVocabulary.SparqlIntputParamMap);
		
		final OWLObjectConverter<LinkedDataSparqlDataParamMapImpl> ommc =
			new GenericOWLConverter<LinkedDataSparqlDataParamMapImpl>(LinkedDataSparqlDataParamMapImpl.class,
					OWLSLinkedDataVocabulary.SparqlOutputParamMap);

		registry.registerConverter(LinkedDataSparqlPrefixes.class,
			new GenericOWLConverter<LinkedDataSparqlPrefixes>(LinkedDataSparqlPrefixesImpl.class, OWLSLinkedDataVocabulary.SparqlPrefixMap));
		
		registry.registerConverter(LinkedDataSparqlTriples.class,
				new GenericOWLConverter<LinkedDataSparqlTriples>(LinkedDataSparqlTriplesImpl.class, OWLSLinkedDataVocabulary.SparqlTripleMap));

		registry.registerConverter(LinkedDataGrounding.class, gc);
		registry.registerConverter(LinkedDataAtomicGrounding.class, agc);

		registry.extendByConverter(Grounding.class, gc);
		registry.extendByConverter(AtomicGrounding.class, agc);

		registry.extendByConverter(MessageMap.class, immc);
		registry.extendByConverter(MessageMap.class, ommc);
		registry.extendByConverter(StringMessageMap.class, immc);
		registry.extendByConverter(StringMessageMap.class, ommc);
	}


}
