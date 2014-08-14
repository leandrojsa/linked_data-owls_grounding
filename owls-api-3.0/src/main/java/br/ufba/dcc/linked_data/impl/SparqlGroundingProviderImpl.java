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

import examples.RunLinkedDataService;

import br.ufba.dcc.linked_data.SparqlAtomicGrounding;
import br.ufba.dcc.linked_data.SparqlGrounding;
import br.ufba.dcc.linked_data.SparqlGroundingProvider;
import br.ufba.dcc.linked_data.SparqlPrefixes;
import br.ufba.dcc.linked_data.SparqlTriples;
import br.ufba.dcc.linked_data.OWLSSparqlVocabulary;

public class SparqlGroundingProviderImpl implements SparqlGroundingProvider{

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLAtomicGrounding(java.net.URI, org.mindswap.owl.OWLModel) */
	public SparqlAtomicGrounding createSparqlAtomicGrounding(final URI uri, final OWLModel model)
	{
		return new SparqlAtomicGroundingImpl(model.createInstance(OWLSSparqlVocabulary.SparqlAtomicProcessGrounding, uri));
	}

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLGrounding(java.net.URI, org.mindswap.owl.OWLModel) */
	public SparqlGrounding createSparqlGrounding(final URI uri, final OWLModel model)
	{
		return new SparqlGroundingImpl(model.createInstance(OWLSSparqlVocabulary.SparqlGrounding, uri));
	}

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLInputMessageMap(java.net.URI, org.mindswap.owl.OWLModel) */
	public MessageMap<String> createSparqlInputParamMap(final URI uri, final OWLModel model)
	{
		return new SparqlDataParamMapImpl(model.createInstance(OWLSSparqlVocabulary.SparqlInputParamMap, uri));
	}

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLOutputMessageMap(java.net.URI, org.mindswap.owl.OWLModel) */
	public MessageMap<String> createSparqlOutputParamMap(final URI uri, final OWLModel model)
	{
		return new SparqlDataParamMapImpl(model.createInstance(OWLSSparqlVocabulary.SparqlOutputParamMap, uri));
	}

	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLOperationRef(java.net.URI, org.mindswap.owl.OWLModel) */
	public SparqlPrefixes createSparqlPrefixes(final URI uri, final OWLModel model)
	{
		return new SparqlPrefixesImpl(model.createInstance((OWLClass) OWLSSparqlVocabulary.SparqlPrefixMap, uri));
	}
	
	/* @see org.mindswap.owls.grounding.WSDLGroundingProvider#createWSDLOperationRef(java.net.URI, org.mindswap.owl.OWLModel) */
	public SparqlTriples createSparqlTriples(final URI uri, final OWLModel model)
	{
		return new SparqlTriplesImpl(model.createInstance(OWLSSparqlVocabulary.SparqlTripleMap, uri));
	}

	/* @see org.mindswap.owl.OWLObjectConverterProvider#registerConverters(org.mindswap.owl.OWLObjectConverterRegistry) */
	public void registerConverters(final OWLObjectConverterRegistry registry)
	{
		RunLinkedDataService.c = System.currentTimeMillis();
		final OWLObjectConverter<SparqlGrounding> gc = new GenericOWLConverter<SparqlGrounding>(
				SparqlGroundingImpl.class, OWLSSparqlVocabulary.SparqlGrounding);
		
		final OWLObjectConverter<SparqlAtomicGrounding> agc = new GenericOWLConverter<SparqlAtomicGrounding>(
				SparqlAtomicGroundingImpl.class, OWLSSparqlVocabulary.SparqlAtomicProcessGrounding);

		final OWLObjectConverter<SparqlDataParamMapImpl> immc =
			new GenericOWLConverter<SparqlDataParamMapImpl>(SparqlDataParamMapImpl.class,
					OWLSSparqlVocabulary.SparqlInputParamMap);
	
		final OWLObjectConverter<SparqlDataParamMapImpl> ommc =
			new GenericOWLConverter<SparqlDataParamMapImpl>(SparqlDataParamMapImpl.class,
					OWLSSparqlVocabulary.SparqlOutputParamMap);

		registry.registerConverter(SparqlPrefixes.class,
			new GenericOWLConverter<SparqlPrefixes>(SparqlPrefixesImpl.class, OWLSSparqlVocabulary.SparqlPrefixMap));
		
		registry.registerConverter(SparqlTriples.class,
				new GenericOWLConverter<SparqlTriples>(SparqlTriplesImpl.class, OWLSSparqlVocabulary.SparqlTripleMap));

		registry.registerConverter(SparqlGrounding.class, gc);
		registry.registerConverter(SparqlAtomicGrounding.class, agc);

		registry.extendByConverter(Grounding.class, gc);
		registry.extendByConverter(AtomicGrounding.class, agc);
		
		
		registry.extendByConverter(StringMessageMap.class, ommc);
		registry.extendByConverter(StringMessageMap.class, immc);
		//registry.extendByConverter(SparqlDataParamMapImpl.class, immc);
		//registry.extendByConverter(SparqlDataParamMapImpl.class, ommc);
		
		registry.extendByConverter(MessageMap.class, immc);
		registry.extendByConverter(MessageMap.class, ommc);
		
	}


}
