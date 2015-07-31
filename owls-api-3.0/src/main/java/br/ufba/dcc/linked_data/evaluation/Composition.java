package br.ufba.dcc.linked_data.evaluation;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mindswap.exceptions.ExecutionException;
import org.mindswap.exceptions.InvalidURIException;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLType;
import org.mindswap.owl.OWLValue;
import org.mindswap.owl.OWLWriter;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.grounding.AtomicGrounding;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.grounding.MessageMap;
import org.mindswap.owls.grounding.WSDLAtomicGrounding;
import org.mindswap.owls.grounding.WSDLGrounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.Split;
import org.mindswap.owls.process.execution.DefaultProcessMonitor;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.query.ValueMap;
import org.mindswap.utils.URIUtils;
import org.xml.sax.SAXException;

import br.ufba.dcc.linked_data.SparqlAtomicGrounding;
import br.ufba.dcc.linked_data.SparqlGrounding;
import br.ufba.dcc.linked_data.SparqlPrefixes;
import br.ufba.dcc.linked_data.SparqlTriples;
import br.ufba.dcc.linked_data.discovery.RequestToSparqlGrounding;
import examples.ExampleURIs;

import org.mindswap.owls.process.variable.ProcessVar;

public class Composition {

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static OWLOntology ont;
	public static URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
	public static String baseURI = "http://localhost/owl_s/services/1.2/";
	public static String CompositeURI = "http://localhost/owl_s/services/1.2/composite.owls#";
	public static String filePath = "/var/www/html/owl_s/services/1.2/";
	public static String compositePath = "/var/www/html/owl_s/services/1.2/composite.owls";
	public static String swods_uri = "http://localhost/owl_s/requests/1.2/SWoDS__CITY__DESCRIPTION__POPULATION.owls";
	
	public static void main(String[] args) throws ExecutionException, URISyntaxException, IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		exec.addMonitor(new DefaultProcessMonitor());
		final OWLKnowledgeBase kb = OWLFactory.createKB();
		
		
		ont = kb.createOntology(URIUtils.standardURI(CompositeURI));
		URI uri = new URI("http://localhost/owl_s/requests/1.2/city_tourism_request.owls");
		Service request = kb.readService(uri);
		
		org.mindswap.owls.process.Process r_process = request.getProcess();
		List<Input> r_inputs = r_process.getInputs();
		List<Output> r_outputs = r_process.getOutputs();
		
		List<Service> services = getServicesRespository("http://localhost/owl_s/services/1.2/");
		List<Service> pluginServices = new ArrayList<Service>();
		
		//find exact request services
		System.out.println("Request of service: " + uri);
		System.out.print("\n\nSearching a exact service...");
		Service exactService = null;	
		Iterator<Service> i_services = services.iterator();
		while(i_services.hasNext()){
			Service service = i_services.next();
			org.mindswap.owls.process.Process s_process = service.getProcess();
			if(isExactMatch(r_inputs, r_outputs, s_process.getInputs(), s_process.getOutputs())){
				exactService = service;
			}
		}
		if(exactService == null){
			
			System.out.println("nothing found \n\nTry to build a composition...");
			
			i_services = services.iterator();
			while(i_services.hasNext()){
				Service service = i_services.next();
				org.mindswap.owls.process.Process s_process = service.getProcess();
				if(isPluginMatch(r_inputs, r_outputs, s_process.getInputs(), s_process.getOutputs())){
					System.out.println("Plugin service: " + service.getURI());
					pluginServices.add(service);
				}
			}
			List<Output> noFoundOutputs = getOutputsWithoutPluginService(request, pluginServices);
			if (noFoundOutputs.size() > 0){
				System.out.println("Starting Web Data Service discovery module...");
				RequestToSparqlGrounding SWSData = new RequestToSparqlGrounding(request.getProcess().getInputs(), noFoundOutputs);
				System.out.println("Runing SPARQL Ask query...");
				if(SWSData.runSparqlAskQuery()){
					System.out.println("There are data in Linked Data cloud to meet the request");
					System.out.println("Building Semantic Web Service Data...	");
					
					org.mindswap.owl.OWLOntology ont;
					ont = kb.createOntology(uri);					
					String serviceName = buildServiceName(request.getProcess().getInputs(), noFoundOutputs);
					String serviceFileName = filePath + serviceName;			
					SWSData.buildSWoDS(serviceFileName, ont.getImports(false));
					System.out.println("Added a new service: " + serviceFileName);
					
					pluginServices.add(kb.readService(URIUtils.createURI(baseURI + serviceName)));
				}
			}
		//	Service s = createSequenceService(pluginServices);
		}
		
		

	}
	
	
	
	static Service createSplitService(final List<Service> services) throws FileNotFoundException {
		final Service service = ont.createService(URIUtils.createURI(CompositeURI + "SplitService"));
		final CompositeProcess process = ont.createCompositeProcess(URIUtils.createURI(CompositeURI + "SplitProcess"));
		final Profile profile = ont.createProfile(URIUtils.createURI(CompositeURI + "SplitProfile"));
		final WSDLGrounding grounding = ont.createWSDLGrounding(URIUtils.createURI(CompositeURI + "SplitGrounding"));

		//System.out.println(ont.getKB().getServices(false));
		
		

		createSplitProcess(process, services);
		createProfile(profile, process);
/*
		final OWLIndividualList<Process> list = process.getComposedOf().getAllProcesses(false);
		for (Process pc : list)
		{
			if (pc instanceof AtomicProcess)
			{
				final AtomicGrounding<?> ag = ((AtomicProcess) pc).getGrounding();
				if (ag == null) continue;
				grounding.addGrounding(ag.castTo(WSDLAtomicGrounding.class));
			}
		}
*/
		profile.setLabel(createLabel(services), null);
		profile.setTextDescription(profile.getLabel(null));

		service.addProfile(profile);
		service.setProcess(process);
		service.addGrounding(grounding);
		
		FileOutputStream aOutputStream = new FileOutputStream(compositePath);
	    OWLWriter aWriter = ont.getWriter();

	    // write this ontology out to the specified output stream
	    aWriter.write(ont, aOutputStream, ont.getURI());
		
		return service;
	}
	
	static CompositeProcess createSplitProcess(final CompositeProcess compositeProcess, final List<Service> services) {
		
		final Split split = ont.createSplit(null);
		compositeProcess.setComposedOf(split);

		final Perform[] performs = new Perform[services.size()];
		for (int i = 0; i < services.size(); i++) {
			System.out.println("i = " + i);
			final Service s = services.get(i);
			final Process p = s.getProcess();

			performs[i] = ont.createPerform(null);
			performs[i].setProcess(p);

			split.addComponent(performs[i]);
			
			
		}

		final Perform firstPerform = performs[0];
		final Perform lastPerform = performs[services.size()-1];
		final boolean createInput = firstPerform.getProcess().getInputs().size() > 0;
		final boolean createOutput = lastPerform.getProcess().getOutputs().size() > 0;

		if (createInput) {
			final Input input = firstPerform.getProcess().getInputs().get(0);
			final Input newInput = ont.createInput(URIUtils.createURI(CompositeURI + "TestInput"));
			newInput.setLabel(input.getLabel(null), null);
			newInput.setParamType(input.getParamType());
			newInput.setProcess(compositeProcess);

			// input of the first perform is directly read from the input of the composite process
			//performs[0].addBinding(input, OWLS.Process.ThisPerform, newInput);
		}

		if (createOutput) {
			final Output output = lastPerform.getProcess().getOutputs().get(0);
			final Output newOutput = ont.createOutput(URIUtils.createURI(CompositeURI + "TestOutput"));
			newOutput.setLabel(output.toPrettyString(), null);
			//newOutput.setParamType(output.getParamType());
			String output_ontology = getParamType(output.getPropertiesAsDataValue(paramTypeURI).iterator());
			newOutput.setProperty(OWLS.Process.parameterType, URIUtils.createURI(output_ontology));	
			newOutput.setProcess(compositeProcess);

			// the output of the composite process is the output pf last process
			final Result result = ont.createResult(null);
			//result.addBinding(newOutput, lastPerform, output);

			compositeProcess.addResult(result);
		}

		return compositeProcess;
	}
	
	/**
	 * Create a label for the composite service based on the labels of services.
	 * Basically return the string [Service1 + Service2 + ... + ServiceN] as the
	 * label.
	 */
	static String createLabel(final List<Service> services) {
		String label = "[";

		for(int i = 0; i < services.size(); i++) {
			final Service s = services.get(i);

			if(i > 0) label += " + ";

			label += s.getLabel(null);
		}
		label += "]";

		return label;
	}

	/**
	 * Create a Profile for the composite service. We only set the input and
	 * output of the profile based on the process.
	 */
	static Profile createProfile(final Profile profile, final Process process)
	{
		for (Input input : process.getInputs())
		{
			profile.addInput(input);
		}

		for (Output output : process.getOutputs())
		{
			profile.addOutput(output);
		}

		return profile;
	}
	
	
	private static String buildServiceName(List<Input> inputs, List<Output> outputs ){
		String serviceName = "SWoDS_";
		for(int i=0;i<inputs.size();i++){
			serviceName = serviceName + inputs.get(i).getLocalName() + "_";
		}
		
		for(int i=0;i<outputs.size();i++){
			if (i+1 == outputs.size()){
				serviceName = serviceName + outputs.get(i).getLocalName() + ".owls";
			}else{
				serviceName = serviceName + outputs.get(i).getLocalName() + "_";
			}
		}
		
		return serviceName;
		
	}
	
	private static String getParamType(Iterator<OWLDataValue> props){
		String paramType="";
		//props = this.outputs.get(i).getPropertiesAsDataValue(paramTypeURI).iterator();
		while(props.hasNext()){
			OWLDataValue p = props.next();
			//System.out.println("OPA: "  + p.));
			paramType = p.getValue().toString().trim();
		}
		return paramType;
	}
	
	private static List<Service> getServicesRespository(String uri) throws URISyntaxException, IOException{
		List<Service> services = new ArrayList<Service>();
		final OWLKnowledgeBase kb = OWLFactory.createKB();
		Document doc = null;
		try {
			doc = Jsoup.connect(uri).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elements links = doc.select("tr a");
		for(int i=0; i< links.size(); i++){
			Element l = links.get(i);
			if (i > 4){
				URI u = new URI(uri+ l.attr("href"));
				services.add(kb.readService(u));
				
			}
		}
		return services;
		
	}
	
	public static boolean isExactMatch(List<Input> r_inputs, List<Output> r_outputs, List<Input> s_inputs, List<Output> s_outputs){
		boolean isExact = false;
		
		for(int i=0;i<r_inputs.size();i++){
			Input r_input = r_inputs.get(i);
			isExact = false;
			for(int j=0;j<s_inputs.size();j++){
				Input s_input = s_inputs.get(j);
				String r_type = getParamType(r_input.getPropertiesAsDataValue(paramTypeURI).iterator());
				String s_type = getParamType(s_input.getPropertiesAsDataValue(paramTypeURI).iterator());
				if (r_type != "" && s_type !=  ""){
					if(r_type.contentEquals(s_type)){
						isExact = true;
					}
				}
			}
			if(!isExact)
				break;
		}
		if(isExact){
			for(int i=0;i<r_outputs.size();i++){
				Output r_output = r_outputs.get(i);
				isExact = false;
				for(int j=0;j<s_outputs.size();j++){
					Output s_output = s_outputs.get(j);
					String r_type = getParamType(r_output.getPropertiesAsDataValue(paramTypeURI).iterator());
					String s_type = getParamType(s_output.getPropertiesAsDataValue(paramTypeURI).iterator());
					if (r_type != "" && s_type !=  ""){
						if(r_type.contentEquals(s_type)){
							isExact = true;
						}
					}
				}
				if(!isExact)
					break;
			}
		}
		return isExact;
	}
	
	
	public static boolean isPluginMatch(List<Input> r_inputs, List<Output> r_outputs, List<Input> s_inputs, List<Output> s_outputs){
		boolean isPlugin = false;
		
		for(int i=0;i<s_inputs.size();i++){
			Input s_input = s_inputs.get(i);
			isPlugin = false;
			for(int j=0;j<r_inputs.size();j++){
				Input r_input = r_inputs.get(j);
				String r_type = getParamType(r_input.getPropertiesAsDataValue(paramTypeURI).iterator());
				String s_type = getParamType(s_input.getPropertiesAsDataValue(paramTypeURI).iterator());
				if (r_type != "" && s_type !=  ""){
					if(s_type.contentEquals(r_type)){
						isPlugin = true;
					}
				}
			}
			if(!isPlugin)
				break;
		}
		if(isPlugin){
			for(int i=0;i<s_outputs.size();i++){
				Output s_output = s_outputs.get(i);
				isPlugin = false;
				for(int j=0;j<r_outputs.size();j++){
					Output r_output = r_outputs.get(j);
						String r_type = getParamType(r_output.getPropertiesAsDataValue(paramTypeURI).iterator());
						String s_type = getParamType(s_output.getPropertiesAsDataValue(paramTypeURI).iterator());
						if (r_type != "" && s_type !=  ""){
						if(s_type.contains(r_type)){
							isPlugin = true;							
						}
					}
				}
				if(!isPlugin)
					break;
			}		
		}
		return isPlugin;
	}
	
	public static List<Output> getOutputsWithoutPluginService(Service request, List<Service> plugins){
		List<Output> outputs = new ArrayList<Output>();
		
		List<Output> r_outputs = request.getProcess().getOutputs();
		for(int i=0;i<r_outputs.size();i++){
			Output r_output = r_outputs.get(i);
			String r_type = getParamType(r_output.getPropertiesAsDataValue(paramTypeURI).iterator());
			System.out.print("\tRequest output: " + r_type + "...");
			boolean found = false; 
			for(int j=0;j<plugins.size();j++){
				List<Output> p_outputs = plugins.get(j).getProcess().getOutputs();
				for(int z=0;z<p_outputs.size();z++){
					Output p_output = p_outputs.get(z);
					String p_type = getParamType(p_output.getPropertiesAsDataValue(paramTypeURI).iterator());
					if(p_type.contains(r_type)){
						found = true;
						System.out.println("found");
					}
				}
			}
			if(found == false){
				outputs.add(r_output);
				System.out.println("no found");
			}
		}
		
		
		return outputs;
	}
	
	public static Service buildResquest(List<Input> inputs, List<Output> outputs, Collection<OWLOntology>  imports) throws InvalidURIException, IOException{
		org.mindswap.owl.OWLOntology ont;
		OWLKnowledgeBase kb;
		
		kb = OWLFactory.createKB();
		ont = kb.createOntology(URIUtils.createURI(swods_uri));
		System.out.println(imports);
		ont.addImports(imports);
	    // create the new service
	    Service service = ont.createService(uri("TestService"));
	    // create the composite process for our service
	    AtomicProcess process = ont.createAtomicProcess(uri("TestProcess"));
	    // create the new profile and grounding
	    Profile profile = ont.createProfile(uri("TestProfile"));
	    
	    
	    SparqlAtomicGrounding atomicGrounding = ont.createSparqlAtomicGrounding(uri("TestAtomicGrounding"));
	    
	    SparqlGrounding aGrounding = ont.createSparqlGrounding(uri("TestGrounding"));
	    
	    SparqlPrefixes bla = ont.createSparqlPrefixes(uri("TestPrefix"));
	    bla.setPrefixName("rdf");
	    bla.setPrefixUri(URIUtils.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
	    
	    SparqlTriples triple = ont.createSparqlTriples(uri("TestTriple"));
	    triple.setTripleSubject("bla");
	    triple.setTriplePredicate("bli");
	    triple.setTripleObject("ble");
	    
	    SparqlTriples triple2 = ont.createSparqlTriples(uri("TestTriple2"));
	    triple2.setTripleSubject("abb");
	    triple2.setTriplePredicate("dghsd");
	    triple2.setTripleObject("dsfsdf");
	    
	    
	    MessageMap<String> bli = ont.createSparqlInputParamMap(uri("TestInput"));
	    bli.setGroundingParameter("opa");
	    System.out.println(bli.toString());
	    bli.setOWLSParameter(inputs.get(0));
	    atomicGrounding.addInputMap(bli);
	    atomicGrounding.setSparqlEndPoint(URIUtils.createURI("http://dbpedia.org/sparql"));
	    atomicGrounding.addPrefix(bla);
	    atomicGrounding.addTriple(triple);
	    atomicGrounding.addTriple(triple2);
	    atomicGrounding.setProcess(process);
	    atomicGrounding.addInputMap(bli);
	    //atomicGrounding.add
	    
	    //atomicGrounding.
	    
	    
	    for(int i=0;i<outputs.size();i++){
	    	
	    	Output newOutput = ont.createOutput(uri("TestInput" + i));
	        newOutput.setLabel(outputs.get(i).getLabel("en"), "en");
	        URI opa = URIUtils.createURI(getParamType(outputs.get(i).getPropertiesAsDataValue(paramTypeURI).iterator()));
	       
	        newOutput.setProperty(OWLS.Process.parameterType, opa);	        
	        newOutput.setProcess(process);
	    	
	    	profile.addOutput(newOutput);
	    	process.addOutput(newOutput);
	    }
	    
	    for(int i=0;i<inputs.size();i++){
	    	Input newInput = ont.createInput(uri("TestInput" + i));
	    	//newInput.setLabel(inputs.get(i).getLabel());
	        //newInput.setParamType(inputs.get(i).getParamType());
	        newInput.setProcess(process);
	    	
	    	profile.addInput(newInput);
	    	process.addInput(newInput);
	    }
	    // set the profile, process and grounding for the new service
	    service.addProfile(profile);
	    service.setProcess(process);
	    
	    aGrounding.addGrounding(atomicGrounding);
	   service.addGrounding(aGrounding);
		
	    FileOutputStream aOutputStream = new FileOutputStream(filePath);
	    OWLWriter aWriter = ont.getWriter();

	    // write this ontology out to the specified output stream
	    aWriter.write(ont, aOutputStream, ont.getURI());
		
		
		return service;
	}
	
	private static URI uri( String localName ) { 
		 return URIUtils.createURI(swods_uri + localName); 
	}

}
