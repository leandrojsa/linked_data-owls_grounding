package br.ufba.dcc.linked_data.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import impl.owls.grounding.MessageMapAtomicGroundingImpl;

import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

import br.ufba.dcc.linked_data.SparqlAtomicGrounding;
import br.ufba.dcc.linked_data.SparqlGrounding;
import br.ufba.dcc.linked_data.OWLSSparqlVocabulary;
import br.ufba.dcc.linked_data.SparqlPrefixes;
import br.ufba.dcc.linked_data.SparqlTriples;



public class SparqlAtomicGroundingImpl extends MessageMapAtomicGroundingImpl<String> implements SparqlAtomicGrounding{
	
	private String query;
	private static final Logger logger = LoggerFactory.getLogger(SparqlAtomicGroundingImpl.class);
	

	public SparqlAtomicGroundingImpl(OWLIndividual ind) {
		super(ind);
	}
	
	public URI getSparqlEndPoint(){
		return getPropertyAsURI(OWLSSparqlVocabulary.sparqlEndPoint);
	}
	
	public List<SparqlPrefixes> getPrefixes() {
		return getPropertiesAs(OWLSSparqlVocabulary.SparqlPrefixes, SparqlPrefixes.class);
	}

	public StringBuffer buildPrexies(){
		StringBuffer prefixes_str = new StringBuffer();
		List<SparqlPrefixes> prefixes = getPrefixes();
		Iterator<SparqlPrefixes> i=prefixes.iterator();
		
		while(i.hasNext()){
			SparqlPrefixes p = i.next();
			prefixes_str.append("PREFIX " + p.getPrefixName() + ": <" + p.getPrefixUri() + ">\n");			
		}
		
		return prefixes_str;
	}
	
	public List<SparqlTriples> getTriples() {
		return getPropertiesAs(OWLSSparqlVocabulary.SparqlTriples, SparqlTriples.class);
	}
	
	public List<Vector<String>> buildTriples(){
		 List<Vector<String>> v_triples = new ArrayList<Vector<String>>();;
		 
		 List<SparqlTriples> triples = getTriples();
		 Iterator<SparqlTriples> i=triples.iterator();
			
			while(i.hasNext()){
				SparqlTriples t = i.next();
				Vector<String> v = new Vector<String>();
				v.add(t.getTripleSubject());
				v.add(t.getTriplePredicate());
				v.add(t.getTripleObject());
				v_triples.add(v);
			}
		 
		return v_triples;
	}
	
	public StringBuffer buildSelectStatement(){
		StringBuffer select = new StringBuffer();
		select.append("SELECT ");
		int i_size = getProcess().getOutputs().size();
		int i = 0;
		for (final Output outputParam : getProcess().getOutputs())
		{
			i++;
			final MessageMap<String> mp = getMessageMap(outputParam);
			final String var = mp.getGroundingParameter();
			if(i == i_size){
				select.append(var);
			}else{
				select.append(var + ", ");
			}
			
		}
		return select;
	}
	
	@Override
	public URL getDescriptionURL() {

			return null;

	}

	@Override
	public Grounding<? extends AtomicGrounding<String>, String> getGrounding() {
		return getGrounding(SparqlGrounding.class);
	}

	public String getGroundingType() { return AtomicGrounding.GROUNDING_SPARQL; }

	@Override
	public ValueMap<Output, OWLValue> invoke(ValueMap<Input, OWLValue> values) throws ExecutionException {
		return invoke(values, OWLFactory.createKB());
	}

	@Override
	public ValueMap<Output, OWLValue> invoke(ValueMap<Input, OWLValue> inputs,	OWLKnowledgeBase env)
			throws ExecutionException {
		
		System.out.println("\\o/");
		System.out.println(buildPrexies());
		System.out.println(buildTriples());
		System.out.println(buildSelectStatement());
		Iterator<Entry<Input, OWLValue>> i = inputs.iterator();
		while(i.hasNext()){
			Entry<Input, OWLValue> input = i.next();
			for (final Input inputParam : getProcess().getInputs())
			{

				final MessageMap<String> mp = getMessageMap(inputParam);
				final String var = mp.getGroundingParameter();
				System.out.println(var);
			}			
			//final MessageMap<String> mp = getMessageMap(true, input.getKey().getURI());
			//Input x = mp.getOWLSParameter().castTo(Input.class);
			//inputMessageMapProperty().getPropertyAsIndividual(prop)
			//.getPropertyAsIndividual(input.getKey().getURI())
//			System.out.println("Oooooi: " + x);
		}
		String str_query="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
                         "SELECT ?var_book\n"+
                         "WHERE {\n"+
                         "?var_book rdf:type <http://dbpedia.org/ontology/Book> .\n"+
                         "?var_book <http://dbpedia.org/ontology/isbn> 'ISBN 0-375-50137-1 (1st ed hardcover)'@en .\n"+
						 "}";
		Query query = QueryFactory.create(str_query);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(getSparqlEndPoint().toString(), query);
		//QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet result = qexec.execSelect() ;
		qexec.close() ;
		
		System.out.println(result);
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

	@Override protected OWLObjectProperty inputMessageMapProperty() {
		return OWLSSparqlVocabulary.SparqlInputParam;
	}

	@Override protected OWLObjectProperty outputMessageMapProperty() {
		return OWLSSparqlVocabulary.SparqlOutputParam;
	}

	@Override
	protected Class<? extends MessageMap<String>> messageMapType() { return StringMessageMap.class; }


}
