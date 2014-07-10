package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.common.ServiceFinder;
import org.mindswap.owl.OWLModel;
import org.mindswap.owls.grounding.MessageMap;

public class SparqlGroundingFactory {
	
	private static final SparqlGroundingProvider provider = ServiceFinder.instance().loadImplementation(
			SparqlGroundingProvider.class, SparqlGroundingProvider.DEFAULT_SPARQL_GROUNDING_PROVIDER);

		public static final SparqlGrounding createSparqlGrounding(final URI uri, final OWLModel model)
		{
			return provider.createSparqlGrounding(uri, model);
		}

		public static final SparqlAtomicGrounding createSparqlAtomicGrounding(final URI uri, final OWLModel model)
		{
			return provider.createSparqlAtomicGrounding(uri, model);
		}

		public static final SparqlPrefixes createSparqlPrefixes(final URI uri, final OWLModel model)
		{
			return provider.createSparqlPrefixes(uri, model);
		}
		
		public static final SparqlTriples createSparqlTriples(final URI uri, final OWLModel model)
		{
			return provider.createSparqlTriples(uri, model);
		}

		public static final MessageMap<String> createSparqlInputParamMap(final URI uri, final OWLModel model)
		{
			return provider.createSparqlInputParamMap(uri, model);
		}

		public static final MessageMap<String> createSparqlOutputParamMap(final URI uri, final OWLModel model)
		{
			return provider.createSparqlOutputParamMap(uri, model);
		}

}
