package br.ufba.dcc.linked_data.evaluation;



import java.io.BufferedReader;
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
import org.mindswap.owl.OWLValue;
import org.mindswap.owl.OWLWriter;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.execution.DefaultProcessMonitor;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;
import org.mindswap.utils.URIUtils;
import org.xml.sax.SAXException;

import br.ufba.dcc.linked_data.discovery.RequestToSparqlGrounding;

import examples.ExampleURIs;

public class Composition {

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
	public static String baseURI = "http://localhost/owl_s/requests/1.2/test.owls#";
	public static String filePath = "/var/www/html/owl_s/requests/1.2/test.owls";
	
	public static void main(String[] args) throws ExecutionException, URISyntaxException, IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		exec.addMonitor(new DefaultProcessMonitor());
		final OWLKnowledgeBase kb = OWLFactory.createKB();
		
		

		URI uri = new URI("http://localhost/owl_s/requests/1.2/city_tourism_request.owls");
		Service request = kb.readService(uri);		
		org.mindswap.owls.process.Process r_process = request.getProcess();
		List<Input> r_inputs = r_process.getInputs();
		List<Output> r_outputs = r_process.getOutputs();
		
		List<Service> services = getServicesRespository("http://localhost/owl_s/services/1.2/");
		List<Service> pluginServices = new ArrayList<Service>();
		
		//find exact request services
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
					System.out.println("Building Semantic Web Service Data...");
				}
			}
		}
		
		

	}
	
	private static String getParamType(Iterator<OWLDataValue> props){
		String paramType="";
		//props = this.outputs.get(i).getPropertiesAsDataValue(paramTypeURI).iterator();
		while(props.hasNext()){
			OWLDataValue p = props.next();
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
		ont = kb.createOntology(URIUtils.createURI(baseURI));
		System.out.println(imports);
		ont.addImports(imports);
	    // create the new service
	    Service service = ont.createService(uri("TestService"));
	    // create the composite process for our service
	    AtomicProcess process = ont.createAtomicProcess(uri("TestProcess"));
	    // create the new profile and grounding
	    Profile profile = ont.createProfile(uri("TestProfile"));
	    //Grounding aGrounding = theOntology.createGrounding(URIUtils.createURI(theBaseURI, "TestGrounding"));

	    
	    for(int i=0;i<outputs.size();i++){
	    	
	    	profile.addOutput(outputs.get(i));
	    	process.addOutput(outputs.get(i));
	    }
	    
	    for(int i=0;i<inputs.size();i++){
	    	profile.addInput(inputs.get(i));
	    	process.addInput(inputs.get(i));
	    }
	    // set the profile, process and grounding for the new service
	    service.addProfile(profile);
	    service.setProcess(process);
	   //aService.addGrounding(aGrounding);
		
	    FileOutputStream aOutputStream = new FileOutputStream(filePath);
	    OWLWriter aWriter = ont.getWriter();

	    // write this ontology out to the specified output stream
	    aWriter.write(ont, aOutputStream, ont.getURI());
		
		
		return service;
	}
	
	private static URI uri( String localName ) { 
		 return URIUtils.createURI(baseURI + localName); 
	}

}
