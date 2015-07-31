package br.ufba.dcc.linked_data.discovery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLProperty;
import org.mindswap.owl.OWLValue;
import org.mindswap.owl.OWLWriter;
import org.mindswap.owls.grounding.MessageMap;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.owls.vocabulary.OWLS_1_2.Grounding;
import org.mindswap.utils.URIUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.ufba.dcc.linked_data.SparqlAtomicGrounding;
import br.ufba.dcc.linked_data.SparqlGrounding;
import br.ufba.dcc.linked_data.SparqlPrefixes;
import br.ufba.dcc.linked_data.SparqlTriples;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;


public class RequestToSparqlGrounding {

	private org.mindswap.owl.OWLOntology ontSWoDS;
	private Service service, SWoDS;
	private List<Input> inputs;
	private List<Output> outputs;
	private String sparqlQuery;
	private OntModel modelJena;
	private List<NodeList> inputResources;
	private List<NodeList> outputResources;
	
	public String baseURI = "http://localhost/owl_s/services/1.2/SWoDS__CITY__DESCRIPTION__POPULATION.owls#";
	
	public RequestToSparqlGrounding(List<Input> inputs, List<Output> outputs) throws SAXException, ParserConfigurationException, XPathExpressionException{
		long time_start = System.currentTimeMillis();
		long time_read_ontology=0;
		long time_create_query_t1=0;
		long time_create_query_t2=0;
		long time_run_query_t1=0;
		long time_run_query_t2=0;
		
		OWLKnowledgeBase aKB = OWLFactory.createKB();
		
		this.modelJena = ModelFactory.createOntologyModel();
		this.modelJena.setStrictMode(false);
		
		this.inputs = inputs;
		this.outputs = outputs;
		
		try {
		
			String filename = "/home/leandrojsa/Mestrado/Dissertação/dbpedia/properties_by_class.xml";
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(filename));
	        XPathFactory xPathfactory = XPathFactory.newInstance();
	        XPath xpath = xPathfactory.newXPath();
			
	        
	        this.inputResources = new ArrayList<NodeList>();
			for(int i=0; i < this.inputs.size(); i++){
				URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
				
				//FIXME: Paramtype returns null, so i need do it
				String input_ontology = this.inputs.get(i).getParamType().getURI().toString();
				//String input_ontology = this.inputs.get(i).getPropertiesAsDataValue(paramTypeURI).get(0).getValue().toString(); 
	        	XPathExpression expr = xpath.compile("//class[@uri='" + input_ontology + "']/property");
	        	NodeList properties = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
	        	//System.out.println(properties.getLength());
	        	this.inputResources.add(i, properties);
			}
			
	       this.outputResources = new ArrayList<NodeList>();
			for(int i=0; i < this.outputs.size(); i++){
				URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
				String output_ontology="";
				Iterator<OWLDataValue> props = this.outputs.get(i).getPropertiesAsDataValue(paramTypeURI).iterator();
				while(props.hasNext()){
					OWLDataValue p = props.next();
					output_ontology = p.getValue().toString().trim();
				}						
				//String output_ontology = this.outputs.get(i).getParamType().getURI().toString();
	        	XPathExpression expr = xpath.compile("//class[@uri='" + output_ontology + "']/property");
	        	NodeList properties = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
	        	//System.out.println(properties.getLength());
	        	this.outputResources.add(i, properties);
			}
			
			/*
			this.inputResources = new ArrayList<Resource>();
			for(int i=0; i < this.inputs.size(); i++){
				Model m = this.modelJena.read(this.inputs.get(i).getParamType().getURI().toString());
				this.inputResources.add(i, m.getResource(this.inputs.get(i).getParamType().getURI().toString()));
				
			}*/
			//System.out.println(serviceIsPossibleInput(this.service, aKB));
			
			if(serviceIsPossibleOutput(this.service, aKB)){
				buildSparqlQueryOuput();
				time_create_query_t1 = System.currentTimeMillis();
				//runSparqlAskQuery();
				time_run_query_t1 = System.currentTimeMillis();
				//System.out.println("Run on dbpedia endpoint: " + runSparqlAskQuery());
			}else if(serviceIsPossibleInput(this.service, aKB)){
				buildSparqlQueryInput();
				time_create_query_t2 = System.currentTimeMillis();
				//runSparqlAskQuery();
				time_run_query_t2 = System.currentTimeMillis();
				//System.out.println("Run on dbpedia endpoint: " + runSparqlAskQuery());
			}else{
				this.sparqlQuery = null;
				//System.out.println("don't possible");
			}
			//System.out.println("Tempo de leitura das ontologias: " + (time_read_ontology-time_start));
			//System.out.println("Tempo de criação da query: " + (time_create_query_t1 -time_read_ontology));
			//System.out.println("Tempo de Execução da query: " + (time_run_query_t1 - time_create_query_t1));
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	public RequestToSparqlGrounding(String uri) throws SAXException, ParserConfigurationException, XPathExpressionException{
		long time_start = System.currentTimeMillis();
		long time_read_ontology=0;
		long time_create_query_t1=0;
		long time_create_query_t2=0;
		long time_run_query_t1=0;
		long time_run_query_t2=0;
		
		URI aURI = URI.create(uri);
		OWLKnowledgeBase aKB = OWLFactory.createKB();
		
		this.modelJena = ModelFactory.createOntologyModel();
		this.modelJena.setStrictMode(false);		
		
		try {
			
			this.service = aKB.readService(aURI);
			time_read_ontology = System.currentTimeMillis();
			this.inputs = this.service.getProcess().getInputs();
			this.outputs = this.service.getProcess().getOutputs();
			
			String filename = "/home/leandrojsa/Mestrado/Dissertação/dbpedia/properties_by_class.xml";
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(filename));
	        XPathFactory xPathfactory = XPathFactory.newInstance();
	        XPath xpath = xPathfactory.newXPath();
			
	        
	        this.inputResources = new ArrayList<NodeList>();
			for(int i=0; i < this.inputs.size(); i++){
				URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
				
				//FIXME: Paramtype returns null, so i need do it
				String input_ontology = this.inputs.get(i).getParamType().getURI().toString();
				//String input_ontology = this.inputs.get(i).getPropertiesAsDataValue(paramTypeURI).get(0).getValue().toString(); 
	        	XPathExpression expr = xpath.compile("//class[@uri='" + input_ontology + "']/property");
	        	NodeList properties = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
	        	//System.out.println(properties.getLength());
	        	this.inputResources.add(i, properties);
			}
			
	       this.outputResources = new ArrayList<NodeList>();
			for(int i=0; i < this.outputs.size(); i++){
				URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
				String output_ontology="";
				Iterator<OWLDataValue> props = this.outputs.get(i).getPropertiesAsDataValue(paramTypeURI).iterator();
				while(props.hasNext()){
					OWLDataValue p = props.next();
					output_ontology = p.getValue().toString().trim();
				}						
				//String output_ontology = this.outputs.get(i).getParamType().getURI().toString();
	        	XPathExpression expr = xpath.compile("//class[@uri='" + output_ontology + "']/property");
	        	NodeList properties = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
	        	//System.out.println(properties.getLength());
	        	this.outputResources.add(i, properties);
			}
			
			/*
			this.inputResources = new ArrayList<Resource>();
			for(int i=0; i < this.inputs.size(); i++){
				Model m = this.modelJena.read(this.inputs.get(i).getParamType().getURI().toString());
				this.inputResources.add(i, m.getResource(this.inputs.get(i).getParamType().getURI().toString()));
				
			}*/
			//System.out.println(serviceIsPossibleInput(this.service, aKB));
			
			if(serviceIsPossibleOutput(this.service, aKB)){
				buildSparqlQueryOuput();
				time_create_query_t1 = System.currentTimeMillis();
				runSparqlAskQuery();
				time_run_query_t1 = System.currentTimeMillis();
				//System.out.println("Run on dbpedia endpoint: " + runSparqlAskQuery());
			}else if(serviceIsPossibleInput(this.service, aKB)){
				buildSparqlQueryInput();
				time_create_query_t2 = System.currentTimeMillis();
				runSparqlAskQuery();
				time_run_query_t2 = System.currentTimeMillis();
				//System.out.println("Run on dbpedia endpoint: " + runSparqlAskQuery());
			}else{
				this.sparqlQuery = null;
				System.out.println("don't possible");
			}
			System.out.println("Tempo de leitura das ontologias: " + (time_read_ontology-time_start));
			System.out.println("Tempo de criação da query: " + (time_create_query_t1 -time_read_ontology));
			System.out.println("Tempo de Execução da query: " + (time_run_query_t1 - time_create_query_t1));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean serviceIsPossibleOutput(Service service, OWLKnowledgeBase owlsKB){
		boolean flag=false;

	    for(int i=0; i < this.outputs.size(); i++){
	    	flag = false;
	    	NodeList properties = this.outputResources.get(i);
	    	for(int j=0; j < this.inputs.size(); j++){
	    		String input_ontology = this.inputs.get(j).getParamType().getURI().toString();
	    		for(int z=0; z < properties.getLength(); z++){
	    			Element e = (Element)properties.item(z);
	    			
	    			if(e.getAttribute("uri").contains(input_ontology)){
	    				flag = true;
	    			}
	    		}
	    	}
	    	
	    	
		}
		return flag;	
	}
	
	private boolean serviceIsPossibleInput(Service service, OWLKnowledgeBase owlsKB){
		boolean flag=false;

	    for(int i=0; i < this.inputs.size(); i++){
	    	flag = false;
	    	NodeList properties = this.inputResources.get(i);
	    	for(int j=0; j < this.outputs.size(); j++){
	    		URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
				String output_ontology="";
				Iterator<OWLDataValue> props = this.outputs.get(i).getPropertiesAsDataValue(paramTypeURI).iterator();
				while(props.hasNext()){
					OWLDataValue p = props.next();
					output_ontology = p.getValue().toString().trim();
				}
	    		//String output_ontology = this.outputs.get(j).getParamType().getURI().toString();
	    		for(int z=0; z < properties.getLength(); z++){
	    			Element e = (Element)properties.item(z);
	  
	    			if(e.getAttribute("uri").contains(output_ontology)){
	    				flag = true;
	    			}
	    		}
	    	}
	    	
	    	
		}
		return flag;
		
	}
	
	/*	
	private boolean serviceIsPossible(Service service, OWLKnowledgeBase owlsKB){
			boolean flag=false;	
			
			for(int i=0; i < this.inputs.size(); i++){
				flag = false;
				String uriInputDomain = "";
				System.out.println("Input URI: " + this.inputs.get(i).getParamType().getURI().toString());
				
				StmtIterator properties = this.inputResources.get(i).listProperties();
				while(properties.hasNext()) {
					Statement p = properties.next();
					System.out.println("\t\t" + p);
					if (p.getPredicate().toString().contains("http://www.w3.org/2000/01/rdf-schema#domain")){
						System.out.println("\tdominio: " + p.getObject());
						uriInputDomain = p.getObject().toString();
					}
				}
				
				for(int j=0; j < this.outputs.size(); j++){
					System.out.println("Output URI: " + this.outputs.get(j).getParamType().getURI().toString());
					if(this.outputs.get(j).getParamType().getURI().toString() == uriInputDomain){
						flag = true;
					}
				}									
			}
			return flag;
		}


	private void buildSparqlQuery(){
		
		StringBuffer prefix = new StringBuffer();
		StringBuffer ask = new StringBuffer();
		
		ask.append("ASK\n");
		StringBuffer where = new StringBuffer();
		where.append("WHERE {\n");
		
		String whereInputs = "";
		for(int i=0; i < this.inputs.size(); i++){
			
			String prefixName = this.inputs.get(i).getLocalName().toLowerCase();
			prefix.append("PREFIX " + prefixName + ": ");
			prefix.append("<" + this.inputs.get(i).getParamType().getURI() + ">\n");
			
			String variable = "?var_" + this.inputs.get(i).getLocalName().toLowerCase();
			whereInputs += "<" +this.inputs.get(i).getParamType().getURI() + "> " + variable + " .\n";	
			
		}
		
		for(int i=0; i < this.outputs.size(); i++){
			
			String prefixName = this.outputs.get(i).getLocalName().toLowerCase();				
			prefix.append("PREFIX " + prefixName + ": ");
			prefix.append("<" + this.outputs.get(i).getParamType().getURI() + ">\n");

			String variable = "?var_" + this.outputs.get(i).getLocalName().toLowerCase();
			
			where.append(variable + " rdf:type " + "<" + this.outputs.get(i).getParamType().getURI() + "> .\n" );
			
			// code for relate inputs with domain of inputs
			for(int j=0; j < this.inputs.size(); j++){
				StmtIterator properties = this.inputResources.get(j).listProperties();
				while(properties.hasNext()) {
					Statement p = properties.next();
					if (p.getPredicate().toString().contains("http://www.w3.org/2000/01/rdf-schema#domain")){
						if(this.outputs.get(i).getParamType().getURI().toString() == p.getObject().toString()){
							where.append(variable + " " + whereInputs);
						}
					}
				}
			}
		}
		
		where.append("}\n");
		
		this.sparqlQuery = (prefix.toString() + ask.toString() + where.toString());
		System.out.println(this.sparqlQuery);
		
	}
	*/
	
	private void buildSparqlQueryOuput(){
		
		//things to create a SWoDS		
		OWLKnowledgeBase kb;			
		kb = OWLFactory.createKB();
		this.ontSWoDS = kb.createOntology(URIUtils.createURI(baseURI));
		this.SWoDS = this.ontSWoDS.createService(URIUtils.createURI(baseURI + "SWoDS_Service"));
	    AtomicProcess process = this.ontSWoDS.createAtomicProcess(URIUtils.createURI(baseURI + "SWoDS_Process"));
	    Profile profile = this.ontSWoDS.createProfile(URIUtils.createURI(baseURI + "SWoDS_Profile"));     
	    SparqlGrounding aGrounding = this.ontSWoDS.createSparqlGrounding(URIUtils.createURI(baseURI + "TestGrounding"));
	    
	    SparqlAtomicGrounding atomicGrounding = this.ontSWoDS.createSparqlAtomicGrounding(URIUtils.createURI(baseURI +"TestAtomicGrounding"));
	    atomicGrounding.setSparqlEndPoint(URIUtils.createURI("http://dbpedia.org/sparql"));
	    atomicGrounding.setProcess(process);
	    
	    SparqlPrefixes sparqlPrefix = this.ontSWoDS.createSparqlPrefixes(URIUtils.createURI(baseURI + "RDFPrefix"));
	    sparqlPrefix.setPrefixName("rdf");
	    sparqlPrefix.setPrefixUri(URIUtils.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
	    atomicGrounding.addPrefix(sparqlPrefix);
	    
	    List<SparqlTriples> lTriple;
	    lTriple = new ArrayList<SparqlTriples>();
		//end
		
		StringBuffer prefix = new StringBuffer();
		prefix.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
		StringBuffer ask = new StringBuffer();
		
		ask.append("ASK\n");
		StringBuffer where = new StringBuffer();
		where.append("WHERE {\n");
		
		List<String> whereInputs;
		whereInputs = new ArrayList<String>();
		for(int i=0; i < this.inputs.size(); i++){
			
			//String prefixName = this.inputs.get(i).getLocalName().toLowerCase();
			//prefix.append("PREFIX " + prefixName + ": ");
			//prefix.append("<" + this.inputs.get(i).getParamType().getURI() + ">\n");
			
			String variable = "?var_" + this.inputs.get(i).getLocalName().toLowerCase();
			
			//add triple to SWoDS
			SparqlTriples otriple = this.ontSWoDS.createSparqlTriples(URIUtils.createURI(baseURI +"oTriple_"+this.inputs.get(i).getLocalName().toLowerCase()));
			otriple.setTriplePredicate("<" + this.inputs.get(i).getParamType().getURI() + ">");
			otriple.setTripleObject(variable);
			lTriple.add(i, otriple);
			//end
			
			//add input to SWoDS
			Input newInput = this.ontSWoDS.createInput(URIUtils.createURI("Input" + i));
			newInput.setLabel(inputs.get(i).getLabel(null), null);
			newInput.setProperty(OWLS.Process.parameterType, this.inputs.get(i).getParamType().getURI());	
	        //newInput.setParamType(inputs.get(i).getParamType());
			newInput.setProcess(process);	    	
	    	profile.addInput(newInput);
	    	process.addInput(newInput);
	    	MessageMap<String> inputMap = this.ontSWoDS.createSparqlInputParamMap(URIUtils.createURI("InputMap" + i));
	    	inputMap.setGroundingParameter(variable);
	    	inputMap.setOWLSParameter(newInput);
		    atomicGrounding.addInputMap(inputMap);
			//end
			
			whereInputs.add(i, "<" +this.inputs.get(i).getParamType().getURI() + "> " + variable + " .\n");
			
			
			
		}
		
		for(int i=0; i < this.outputs.size(); i++){
			
			//String prefixName = this.outputs.get(i).getLocalName().toLowerCase();				
			//prefix.append("PREFIX " + prefixName + ": ");
			//prefix.append("<" + this.outputs.get(i).getParamType().getURI() + ">\n");

			String variable = "?var_" + this.outputs.get(i).getLocalName().toLowerCase();
			
			//add triple to SWoDS
			SparqlTriples triple = this.ontSWoDS.createSparqlTriples(URIUtils.createURI(baseURI + "Triple_" + this.inputs.get(i).getLocalName().toLowerCase()));
		    triple.setTripleSubject(variable);
		    triple.setTriplePredicate("rdf:type");
		    triple.setTripleObject("<" + this.outputs.get(i).getParamType().getURI() + ">");
		    atomicGrounding.addTriple(triple);
			//end
		    
		  //add output to SWoDS
			Output newOutput = this.ontSWoDS.createOutput(URIUtils.createURI("Output" + i));
			newOutput.setLabel(outputs.get(i).getLabel(null), null);
			newOutput.setProperty(OWLS.Process.parameterType, this.outputs.get(i).getParamType().getURI());	
	        //newInput.setParamType(inputs.get(i).getParamType());
			newOutput.setProcess(process);	    	
	    	profile.addOutput(newOutput);
	    	process.addOutput(newOutput);
	    	MessageMap<String> outputMap = this.ontSWoDS.createSparqlOutputParamMap(URIUtils.createURI("OutputMap" + i));
	    	outputMap.setGroundingParameter(variable);
	    	outputMap.setOWLSParameter(newOutput);
		    atomicGrounding.addOutputMap(outputMap);
			//end
			
			where.append(variable + " rdf:type " + "<" + this.outputs.get(i).getParamType().getURI() + "> .\n" );
			
			NodeList properties = this.outputResources.get(i);		
			// code for relate inputs with domain of inputs
			for(int j=0; j < this.inputs.size(); j++){
				String input_ontology = this.inputs.get(j).getParamType().getURI().toString();
				for(int z=0; z < properties.getLength(); z++){
	    			Element e = (Element)properties.item(z);
	    			if(e.getAttribute("uri").contains(input_ontology)){
	    				
	    				//add triple to SWoDS
	    				SparqlTriples otriple = lTriple.get(j);
						otriple.setTripleSubject(variable);
						atomicGrounding.addTriple(otriple);
	    				//end
	    				
	    				where.append(variable + " " + whereInputs.get(j));
	    			}
	    		}
			}
		}
		
		where.append("}\n");
		
		this.SWoDS.addProfile(profile);
		this.SWoDS.setProcess(process);
	    aGrounding.addGrounding(atomicGrounding);
	    this.SWoDS.addGrounding(aGrounding);
		
		this.sparqlQuery = (prefix.toString() + ask.toString() + where.toString());
		System.out.println(this.sparqlQuery);
		
	}
	
	private void buildSparqlQueryInput(){
		
		//things to create a SWoDS		
		OWLKnowledgeBase kb;			
		kb = OWLFactory.createKB();
		this.ontSWoDS = kb.createOntology(URIUtils.createURI(baseURI));
		this.SWoDS = this.ontSWoDS.createService(URIUtils.createURI(baseURI + "SWoDS_Service"));
	    AtomicProcess process = this.ontSWoDS.createAtomicProcess(URIUtils.createURI(baseURI + "SWoDS_Process"));
	    Profile profile = this.ontSWoDS.createProfile(URIUtils.createURI(baseURI + "SWoDS_Profile"));    
	    SparqlGrounding aGrounding = this.ontSWoDS.createSparqlGrounding(URIUtils.createURI(baseURI + "TestGrounding"));
	    
	    SparqlAtomicGrounding atomicGrounding = this.ontSWoDS.createSparqlAtomicGrounding(URIUtils.createURI(baseURI +"TestAtomicGrounding"));
	    atomicGrounding.setSparqlEndPoint(URIUtils.createURI("http://dbpedia.org/sparql"));
	    atomicGrounding.setProcess(process);
	    
	    SparqlPrefixes sparqlPrefix = this.ontSWoDS.createSparqlPrefixes(URIUtils.createURI(baseURI + "RDFPrefix"));
	    sparqlPrefix.setPrefixName("rdf");
	    sparqlPrefix.setPrefixUri(URIUtils.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
	    atomicGrounding.addPrefix(sparqlPrefix);
	    
	    List<SparqlTriples> lTriple;
	    lTriple = new ArrayList<SparqlTriples>();
		//end
		
		StringBuffer prefix = new StringBuffer();
		prefix.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
		StringBuffer ask = new StringBuffer();
		
		ask.append("ASK\n");
		StringBuffer where = new StringBuffer();
		where.append("WHERE {\n");
		
		List<String> whereInputs;
		whereInputs = new ArrayList<String>();
		for(int i=0; i < this.outputs.size(); i++){
			
			
			//String prefixName = this.outputs.get(i).getLocalName().toLowerCase();
			//prefix.append("PREFIX " + prefixName + ": ");
			//prefix.append("<" + this.outputs.get(i).getParamType().getURI() + ">\n");
			
			String variable = "?var_" + this.outputs.get(i).getLocalName().toLowerCase();
			
			URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
			String output_ontology="";
			Iterator<OWLDataValue> props = this.outputs.get(i).getPropertiesAsDataValue(paramTypeURI).iterator();
			while(props.hasNext()){
				OWLDataValue p = props.next();
				output_ontology = p.getValue().toString().trim();
			}
			//add triple to SWoDS
			SparqlTriples otriple = this.ontSWoDS.createSparqlTriples(URIUtils.createURI(baseURI +"oTriple_"+this.outputs.get(i).getLocalName().toLowerCase()));
			otriple.setTriplePredicate("<" +output_ontology + ">");
			otriple.setTripleObject(variable);
			lTriple.add(i, otriple);
			//end
			
			//add output to SWoDS
			Output newOutput = this.ontSWoDS.createOutput(URIUtils.createURI("Output" + i));
			newOutput.setLabel(outputs.get(i).getLabel(null), null);
			newOutput.setProperty(OWLS.Process.parameterType, URIUtils.createURI(output_ontology));	
	        //newInput.setParamType(inputs.get(i).getParamType());
			newOutput.setProcess(process);	    	
	    	profile.addOutput(newOutput);
	    	process.addOutput(newOutput);
	    	MessageMap<String> outputMap = this.ontSWoDS.createSparqlOutputParamMap(URIUtils.createURI("OutputMap" + i));
	    	outputMap.setGroundingParameter(variable);
	    	outputMap.setOWLSParameter(newOutput);
		    atomicGrounding.addOutputMap(outputMap);
			//end
			whereInputs.add(i, "<" +output_ontology + "> " + variable + " .\n");	
			
		}
		
		for(int i=0; i < this.inputs.size(); i++){
			
			//String prefixName = this.inputs.get(i).getLocalName().toLowerCase();				
			//prefix.append("PREFIX " + prefixName + ": ");
			//prefix.append("<" + this.inputs.get(i).getParamType().getURI() + ">\n");
			
			String variable = "?var_" + this.inputs.get(i).getLocalName().toLowerCase();
			
			//add triple to SWoDS
			SparqlTriples triple = this.ontSWoDS.createSparqlTriples(URIUtils.createURI(baseURI + "Triple_" + this.inputs.get(i).getLocalName().toLowerCase()));
		    triple.setTripleSubject(variable);
		    triple.setTriplePredicate("rdf:type");
		    triple.setTripleObject("<" + this.inputs.get(i).getParamType().getURI() + ">");
		    atomicGrounding.addTriple(triple);
			//end
		  //add input to SWoDS
			Input newInput = this.ontSWoDS.createInput(URIUtils.createURI("Input" + i));
			newInput.setLabel(inputs.get(i).getLabel(null), null);
			newInput.setProperty(OWLS.Process.parameterType, this.inputs.get(i).getParamType().getURI());	
	        //newInput.setParamType(inputs.get(i).getParamType());
			newInput.setProcess(process);	    	
	    	profile.addInput(newInput);
	    	process.addInput(newInput);
	    	MessageMap<String> inputMap = this.ontSWoDS.createSparqlInputParamMap(URIUtils.createURI("InputMap" + i));
	    	inputMap.setGroundingParameter(variable);
	    	inputMap.setOWLSParameter(newInput);
		    atomicGrounding.addInputMap(inputMap);
	    	
			//end
			
			where.append(variable + " rdf:type " + "<" + this.inputs.get(i).getParamType().getURI() + "> .\n" );
			
			NodeList properties = this.inputResources.get(i);		
			// code for relate inputs with domain of inputs
			for(int j=0; j < this.outputs.size(); j++){
				
				URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
				String output_ontology="";
				Iterator<OWLDataValue> props = this.outputs.get(i).getPropertiesAsDataValue(paramTypeURI).iterator();
				while(props.hasNext()){
					OWLDataValue p = props.next();
					output_ontology = p.getValue().toString().trim();
				}
				
				//String output_ontology = this.outputs.get(j).getParamType().getURI().toString();
				for(int z=0; z < properties.getLength(); z++){
	    			Element e = (Element)properties.item(z);
	    			if(e.getAttribute("uri").contains(output_ontology)){
	    				//add triple to SWoDS
	    				SparqlTriples otriple = lTriple.get(j);
						otriple.setTripleSubject(variable);
						atomicGrounding.addTriple(otriple);
	    				//end
	    				where.append(variable + " " + whereInputs.get(j));
	    			}
	    		}
			}
		}
		
		where.append("}\n");
		
		this.SWoDS.addProfile(profile);
		this.SWoDS.setProcess(process);
	    aGrounding.addGrounding(atomicGrounding);
	    this.SWoDS.addGrounding(aGrounding);
		
		this.sparqlQuery = (prefix.toString() + ask.toString() + where.toString());
		System.out.println(this.sparqlQuery);
		
		
	}
	
	public String getSparqlQuery(){
		return this.sparqlQuery;
	}

	public boolean runSparqlAskQuery(){
		boolean result;
		
		if(!this.sparqlQuery.isEmpty()){
			Query query = QueryFactory.create(this.sparqlQuery);
			QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
			//QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
			result = qexec.execAsk() ;
			qexec.close() ;
		}else{
			result = false;
		}
		return result;
	}
	
	public void buildSWoDS(String filePath, Set<org.mindswap.owl.OWLOntology> imports) throws FileNotFoundException{
		
		this.ontSWoDS.addImports(imports);
		FileOutputStream aOutputStream = new FileOutputStream(filePath);
	    OWLWriter aWriter = this.ontSWoDS.getWriter();

	    // write this ontology out to the specified output stream
	    aWriter.write(this.ontSWoDS, aOutputStream, this.ontSWoDS.getURI());	
	}

}
