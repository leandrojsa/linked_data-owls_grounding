package br.ufba.dcc.linked_data;

import java.net.URI;

import org.mindswap.common.ServiceFinder;
import org.mindswap.owl.OWLModel;
import org.mindswap.owls.grounding.MessageMap;

public class LinkedDataGroundingFactory {
	
	private static final LinkedDataGroundingProvider provider = ServiceFinder.instance().loadImplementation(
			LinkedDataGroundingProvider.class, LinkedDataGroundingProvider.DEFAULT_LINKED_DATA_GROUNDING_PROVIDER);

		public static final LinkedDataGrounding createLinkedDataGrounding(final URI uri, final OWLModel model)
		{
			return provider.createLinkedDataGrounding(uri, model);
		}

		public static final LinkedDataAtomicGrounding createLinkedDataAtomicGrounding(final URI uri, final OWLModel model)
		{
			return provider.createLinkedDataAtomicGrounding(uri, model);
		}

		public static final LinkedDataSparqlPrefixes createLinkedDataSparqlPrefixes(final URI uri, final OWLModel model)
		{
			return provider.createLinkedDataSparqlPrefixes(uri, model);
		}
		
		public static final LinkedDataSparqlTriples createLinkedDataSparqlTriples(final URI uri, final OWLModel model)
		{
			return provider.createLinkedDataSparqlTriples(uri, model);
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
