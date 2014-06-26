package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.owl.OWLModel;
import org.mindswap.owl.OWLObjectConverterProvider;
import org.mindswap.owls.grounding.MessageMap;


public interface LinkedDataGroundingProvider extends OWLObjectConverterProvider{
	
	public static final String DEFAULT_LINKED_DATA_GROUNDING_PROVIDER = "br.ufba.dcc.linked_data.impl.LinkedDataGroundingProviderImpl";

	public LinkedDataGrounding createLinkedDataGrounding(URI uri, OWLModel model);

	public LinkedDataAtomicGrounding createLinkedDataAtomicGrounding(URI uri, OWLModel model);

	public MessageMap<String> createSparqlInputParamMap(final URI uri, final OWLModel model);

	public MessageMap<String> createSparqlOutputParamMap(final URI uri, final OWLModel model);

	public LinkedDataSparqlPrefixes createLinkedDataSparqlPrefixes(final URI uri, final OWLModel model);
	
	public LinkedDataSparqlTriples createLinkedDataSparqlTriples(final URI uri, final OWLModel model);

}
