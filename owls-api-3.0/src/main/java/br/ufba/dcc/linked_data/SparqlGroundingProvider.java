package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.owl.OWLModel;
import org.mindswap.owl.OWLObjectConverterProvider;
import org.mindswap.owls.grounding.MessageMap;


public interface SparqlGroundingProvider extends OWLObjectConverterProvider{
	
	public static final String DEFAULT_SPARQL_GROUNDING_PROVIDER = "br.ufba.dcc.linked_data.impl.SparqlGroundingProviderImpl";

	public SparqlGrounding createSparqlGrounding(URI uri, OWLModel model);

	public SparqlAtomicGrounding createSparqlAtomicGrounding(URI uri, OWLModel model);

	public MessageMap<String> createSparqlInputParamMap(final URI uri, final OWLModel model);

	public MessageMap<String> createSparqlOutputParamMap(final URI uri, final OWLModel model);

	public SparqlPrefixes createSparqlPrefixes(final URI uri, final OWLModel model);
	
	public SparqlTriples createSparqlTriples(final URI uri, final OWLModel model);

}
