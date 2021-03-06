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
import org.mindswap.owls.grounding.WSDLOperationRef;
import org.mindswap.owls.grounding.MessageMap.StringMessageMap;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.owls.vocabulary.MoreGroundings.Java;
import org.mindswap.query.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import br.ufba.dcc.linked_data.SparqlAtomicGrounding;
import br.ufba.dcc.linked_data.SparqlGrounding;
import br.ufba.dcc.linked_data.OWLSSparqlVocabulary;
import br.ufba.dcc.linked_data.SparqlPrefixes;
import br.ufba.dcc.linked_data.SparqlTriples;



public class SparqlAtomicGroundingImpl extends MessageMapAtomicGroundingImpl<String> implements SparqlAtomicGrounding{
	
	private String query;
	private StringBuffer prefixes;
	private StringBuffer select;
	private List<Vector<String>> l_triples;
	private static final Logger logger = LoggerFactory.getLogger(SparqlAtomicGroundingImpl.class);
	

	public SparqlAtomicGroundingImpl(OWLIndividual ind) {
		super(ind);
	}
	
	public URI getSparqlEndPoint(){
		return getPropertyAsURI(OWLSSparqlVocabulary.sparqlEndPoint);
	}
	
	public void setSparqlEndPoint(URI uri){
		setProperty(OWLSSparqlVocabulary.sparqlEndPoint, uri);
	}
	
	public List<SparqlPrefixes> getPrefixes() {
		return getPropertiesAs(OWLSSparqlVocabulary.SparqlPrefixes, SparqlPrefixes.class);
	}
	
	public void addPrefix(OWLIndividual value){		
		addProperty(OWLSSparqlVocabulary.SparqlPrefixes, value);
	}
	
	
	
	public void setInputMessage(final MessageMap<String> map) {
		addInputMap(map);
	}
	
/*	
	public void addInputMap(final URI uri, final String type, final int index, final Input owlsParameter)
	{
		final OWLIndividual ind = getOntology().createInstance(OWLSSparqlVocabulary.SparqlInputParamMap, uri);

		ind.setProperty(Java.javaType, type);
		ind.setProperty(OWLSSparqlVocabulary.SparqlDataParam, owlsParameter);
		addProperty(Java.hasJavaParameter, ind);
	}
*/
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
	
	public void addTriple(OWLIndividual value){
		addProperty(OWLSSparqlVocabulary.SparqlTriples, value);
	}
	
	public List<Vector<String>> buildTriples(){
		 List<Vector<String>> v_triples = new ArrayList<Vector<String>>();;
		 
		 List<SparqlTriples> triples = getTriples();
		 Iterator<SparqlTriples> i=triples.iterator();
			
			while(i.hasNext()){
				SparqlTriples t = i.next();
				Vector<String> v = new Vector<String>();
				//verificar se a string é URI
				if (isURI(t.getTripleSubject())){
					v.add("<"+t.getTripleSubject()+">");
				}else{
					v.add(t.getTripleSubject());
				}
				
				if (isURI(t.getTriplePredicate())){
					v.add("<"+t.getTriplePredicate()+">");
				}else{
					v.add(t.getTriplePredicate());
				}
				
				if (isURI(t.getTripleObject())){
					v.add("<"+t.getTripleObject()+">");
				}else{
					v.add(t.getTripleObject());
				}
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
				select.append(var + " ");
			}
			
		}
		select.append("\n");
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
		long x = System.currentTimeMillis();
		//System.out.println("Inicio execução: " + x);
		
		prefixes = buildPrexies();
		select = buildSelectStatement();
		l_triples = buildTriples();
	
		
		//code for relate inputs data with varibles in triples
		Iterator<Entry<Input, OWLValue>> i = inputs.iterator();
		while(i.hasNext()){
			Entry<Input, OWLValue> input = i.next();
			System.out.println("OPA: " + input.getValue().toString());
			for (final Input inputParam : getProcess().getInputs()){
				final MessageMap<String> mp = getMessageMap(inputParam);
				final String var = mp.getGroundingParameter();
				Iterator<Vector<String>> t = l_triples.iterator();
				while(t.hasNext()){
					Vector<String> triple = t.next();
					for(int j=0; j<3; j++){
						if (triple.get(j).contentEquals(var)){
							System.out.println("VAR: " + var + "OPA: " + input.getValue().toString());
							triple.set(j, input.getValue().toString());
						}
					}
					
				}
			}			
		}
		
		// build query string to execute sparql query
		query = prefixes.toString();
		query = query + select.toString();
		query = query + "WHERE {\n";
		Iterator<Vector<String>> t = l_triples.iterator();
		while(t.hasNext()){	
			Vector<String> triple = t.next();
			query = query + triple.get(0) + " " + triple.get(1) + " " + triple.get(2) + " .\n";
		}		
		query = query + "}";
		System.out.println(query);
		//String q = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/> \n SELECT ?name ?person \n WHERE {\n ?person foaf:name ?name .\n} LIMIT 3";
		Query sparqlQuery = QueryFactory.create(query + "LIMIT 1");
		QueryExecution qexec = QueryExecutionFactory.sparqlService(getSparqlEndPoint().toString(), sparqlQuery);
		//QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		long y = System.currentTimeMillis();
		//System.out.println("Fim  do grounding: (y) " + y );
		System.out.println("Tempo de preparação para execução do serviço: " + (y-x));
		System.out.println(query + "LIMIT 1");
		ResultSet result = qexec.execSelect() ;
		ResultSetFormatter.out(System.out, result);
		System.out.println(result.next().get("populacao"));
		final ValueMap<Output, OWLValue> results = new ValueMap<Output, OWLValue>();
		//fix after
		
		//end
		
		//build output
		
		for (final Output outputParam : getProcess().getOutputs()){
			ResultSet tempResult = result;
			final MessageMap<String> mp = getMessageMap(outputParam);
			final String outputVar = mp.getGroundingParameter();
			System.out.println("output: " + outputVar );
			while(tempResult.hasNext()){
				QuerySolution r = tempResult.next();
				Object outputValue = r.get(outputVar);
				//System.out.println(outputParam.getParamType());
				
				//if (outputParam.getParamType().isDataType())
				//	results.setValue(outputParam, env.createDataValue(outputValue));
				//else
				//	results.setValue(outputParam, env.parseLiteral(outputValue.toString()));
			}
			//System.out.println(outputVar);
		}
		qexec.close() ;
		return results;
	}
	
	private boolean isURI(String urlString)
    {
        boolean result = false;
        try
        {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            if (protocol != null && protocol.trim().length() > 0)
                result = true;
        }
        catch (MalformedURLException e)
        {
            return false;
        }
        return result;
    }

	@Override
	protected MessageMap<String> createInputMessageMap() {
		return getOntology().createSparqlInputParamMap(null);
		}

	@Override
	protected MessageMap<String> createOutputMessageMap() {
		return getOntology().createSparqlOutputParamMap(null);
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
