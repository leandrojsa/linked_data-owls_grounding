package br.ufba.dcc.linked_data.evaluation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mindswap.owl.OWLDataType;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLWriter;
import org.mindswap.owl.vocabulary.XSD;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.grounding.AtomicGrounding;
import org.mindswap.owls.grounding.WSDLAtomicGrounding;
import org.mindswap.owls.grounding.WSDLGrounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.Split;
import org.mindswap.owls.process.SplitJoin;
import org.mindswap.owls.process.execution.DefaultProcessMonitor;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.utils.URIUtils;

import br.ufba.dcc.linked_data.SparqlAtomicGrounding;
import br.ufba.dcc.linked_data.SparqlGrounding;
import br.ufg.inf.sws.rest.WADLAtomicGrounding;
import br.ufg.inf.sws.rest.WADLGrounding;
import examples.ExampleURIs;

public class CompositionDataAndThings {
	
	public static OWLOntology ont;
	public static URI paramTypeURI = URIUtils.createURI("http://www.daml.org/services/owl-s/1.2/Process.owl#parameterType");
	public static String baseURI = "http://localhost/owl_s/services/1.2/";
	public static String CompositeURI = "http://localhost/owl_s/services/1.2/compositeDataAndThings.owls#";
	public static String filePath = "/var/www/html/owl_s/services/1.2/bla.owls";
	public static String compositePath = "/var/www/html/owl_s/services/1.2/compositeDataAndThings.owls";
	 //public static String swods_uri = "http://localhost/owl_s/requests/1.2/SWoDS__CITY__DESCRIPTION__POPULATION.owls";
	
	static OWLDataType xsdString;
	
	private static Process TempCity;
	private static Process HumidCity;
	private static Process TempLocal;
	private static Process HumidLocal;
	
	
	private static Service s;
	

	public static void main(String[] args) throws URISyntaxException, IOException {
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		exec.addMonitor(new DefaultProcessMonitor());
		final OWLKnowledgeBase kb = OWLFactory.createKB();
		
		
		
		ont = kb.createOntology(URIUtils.standardURI(CompositeURI));
		
		List<Service> servicesSplit = getSplitServices("http://localhost/owl_s/services/1.2/split/");
		
		List<Service> servicesSequence = getSplitServices("http://localhost/owl_s/services/1.2/sequence");
		
		xsdString = kb.getDataType( XSD.string );
		
		TempCity = kb.readService(URIUtils.createURI("http://localhost/owl_s/services/1.2/split/temperature_by_city.owls")).getProcess();
		HumidCity = kb.readService(URIUtils.createURI("http://localhost/owl_s/services/1.2/split/humidity_by_city.owls")).getProcess();
		TempLocal = kb.readService(URIUtils.createURI("http://localhost/owl_s/services/1.2/split/temp_wadl.owls")).getProcess();
		HumidLocal = kb.readService(URIUtils.createURI("http://localhost/owl_s/services/1.2/split/humidity_by_city.owls")).getProcess();
		
		
		final Service s = createService();
	}
	
	private static Service createService() throws FileNotFoundException	{
		final Service service = ont.createService(URIUtils.createURI(CompositeURI + "ServiceComposite"));

		final CompositeProcess process = createProcess();
		final Profile profile = createProfile(process);
		final WSDLGrounding wsdlGrounding = createThisWSDLGrounding(process);
		final WADLGrounding wadlGrounding = createThisWADLGrounding(process);
		final SparqlGrounding sparqlGrounding = createThisSparqlGrounding(process);

		service.addProfile(profile);
		service.setProcess(process);
		service.addGrounding(wsdlGrounding);
		service.addGrounding(wadlGrounding);
		service.addGrounding(sparqlGrounding);
		
		FileOutputStream aOutputStream = new FileOutputStream(filePath);
     
		OWLWriter aWriter = ont.getWriter();

		// write this ontology out to the specified output stream
		aWriter.write(ont, aOutputStream, ont.getURI());

		return service;
	}
	
	private static WSDLGrounding createThisWSDLGrounding(final CompositeProcess process)
	{
		final WSDLGrounding grounding = ont.createWSDLGrounding(URIUtils.createURI(CompositeURI + "CompositeGroundingWSDL"));

		final OWLIndividualList<Process> list = process.getComposedOf().getAllProcesses(false);
		for (final Process p : list)
		{
			if (p instanceof AtomicProcess)
			{
				if(((AtomicProcess) p).getGrounding().getGroundingType().contains("WSDL"))
					grounding.addGrounding(((AtomicProcess) p).getGrounding().castTo(WSDLAtomicGrounding.class));
			}
		}

		return grounding;
	}
	
	private static SparqlGrounding createThisSparqlGrounding(final CompositeProcess process)
	{
		final SparqlGrounding grounding = ont.createSparqlGrounding(URIUtils.createURI(CompositeURI + "CompositeGroundingWSDL"));

		final OWLIndividualList<Process> list = process.getComposedOf().getAllProcesses(false);
		for (final Process p : list)
		{
			if (p instanceof AtomicProcess)
			{
				if(((AtomicProcess) p).getGrounding().getGroundingType().contains("Sparql"))
					grounding.addGrounding(((AtomicProcess) p).getGrounding().castTo(SparqlAtomicGrounding.class));
			}
		}

		return grounding;
	}
	
	private static WADLGrounding createThisWADLGrounding(final CompositeProcess process)
	{
		final WADLGrounding grounding = ont.createWADLGrounding(URIUtils.createURI(CompositeURI + "CompositeGroundingWSDL"));

		final OWLIndividualList<Process> list = process.getComposedOf().getAllProcesses(false);
		for (final Process p : list)
		{
			if (p instanceof AtomicProcess)
			{
				if(((AtomicProcess) p).getGrounding().getGroundingType().contains("WADL"))
					grounding.addGrounding(((AtomicProcess) p).getGrounding().castTo(WADLAtomicGrounding.class));
			}
		}

		return grounding;
	}
	
	private static Profile createProfile( final Process process ) {
		final Profile profile = ont.createProfile(URIUtils.createURI(CompositeURI + "CompositeProfile"));

		profile.setLabel("Set auto temperature of air conditioner", null);
		profile.setTextDescription("Set auto temperature of air conditioner given city and local");

		for(int i = 0; i < process.getInputs().size(); i++) {
			final Input input = process.getInputs().get( i );

			profile.addInput( input );
		}

		for(int i = 0; i < process.getOutputs().size(); i++) {
			final Output output = process.getOutputs().get( i );

			profile.addOutput( output );
		}

		return profile;
	}
	
	private static CompositeProcess createProcess() {
		// create the composite process
		final CompositeProcess process = ont.createCompositeProcess( URIUtils.createURI(CompositeURI + "CompositeProcess") );

		// Create an input that has parameterType xsd:string
		final Input inputCityName = process.createInput( URIUtils.createURI(CompositeURI + "CityName"), xsdString );		
		final Input inputCountryName = process.createInput( URIUtils.createURI(CompositeURI + "CountryName"), xsdString );

		// Create an output that has parameterType xsd:string
		final Output outputTempAirConditioner = process.createOutput( URIUtils.createURI(CompositeURI + "TempAirConditioner"), xsdString );

		// process is composed of a sequence
		final Sequence sequence = ont.createSequence(null);
		process.setComposedOf(sequence);		

		// second element of the sequence is a Split-Join that has two branches, one perform in each
		final Split split = ont.createSplit(null);
		
		// first perform AmazonPrice
		final Perform CityTemp = ont.createPerform( URIUtils.createURI(CompositeURI + "CityTemp" ) );
	
		CityTemp.setProcess( TempCity );
		// the input of the perform is coming from FindBookInfo perform
		CityTemp.addBinding( TempCity.getInputs().get(0), OWLS.Process.ThisPerform,  inputCountryName);
		CityTemp.addBinding( TempCity.getInputs().get(1), OWLS.Process.ThisPerform, inputCityName);
		// add it to the split-join
		split.addComponent( CityTemp );
		
		// first perform AmazonPrice
		final Perform CityHumid = ont.createPerform( URIUtils.createURI(CompositeURI + "CityHumid" ) );
		CityHumid.setProcess( HumidCity );
		// the input of the perform is coming from FindBookInfo perform
		CityHumid.addBinding( HumidCity.getInputs().get(0), OWLS.Process.ThisPerform, inputCityName );
		CityHumid.addBinding( HumidCity.getInputs().get(1), OWLS.Process.ThisPerform, inputCountryName);
		// add it to the split-join
		split.addComponent(CityHumid);
		
		// first perform AmazonPrice
		final Perform localTemp = ont.createPerform( URIUtils.createURI(CompositeURI + "LocalTemp" ) );
		localTemp.setProcess( TempLocal );
		// add it to the split-join
		split.addComponent(localTemp);
		
		final Perform localHumid = ont.createPerform( URIUtils.createURI(CompositeURI + "LocalHumid" ) );
		localTemp.setProcess( HumidLocal );
		// add it to the split-join
		split.addComponent(localHumid);

		// finally add the Split-Join to the sequence
		sequence.addComponent(split);
/*		
		// first element of the sequence is a simple perform
		final Perform FindBookInfo = ont.createPerform( uri( "FindBookInfo" ) );
		FindBookInfo.setProcess( BookFinder );
		// the input of the FindBookInfo perform is coming from the enclosing perform
		FindBookInfo.addBinding( BookFinder.getInput(), OWLS.Process.ThisPerform, inputBookName );

		// add this perform as the first element of the sequence
		sequence.addComponent( FindBookInfo );

		// last element of the sequence is a composite process that compares the prices and selects the cheaper
		final CompositeProcess ComparePricesProc = createComparePricesProcess();
		final Perform ComparePrices = ont.createPerform(uri("ComparePrices"));
		ComparePrices.setProcess(ComparePricesProc);
		// feed the input from book previous performs to the comparison process
		ComparePrices.addBinding( CP_AmazonPrice, FindAmazonPrice, AmazonPrice.getOutput() );
		ComparePrices.addBinding( CP_BNPrice, FindBNPrice, BNPrice.getOutput() );

		// add the comparison step as the last construct in the sequence
		sequence.addComponent( ComparePrices );
*/
		final Result result = process.createResult(null);
		//result.addBinding( outputBookstore, ComparePrices, CP_Bookstore );
		//result.addBinding( outputBookPrice, ComparePrices, CP_OutputPrice );

		return process;
	}
	
	
	
/*	
	Service createSplitSequenceService(final List<Service> servicesSplit, List<Service> serviceSequence) {
		final Service service = ont.createService(URIUtils.createURI(CompositeURI + "ServiceComposite"));
		final CompositeProcess process = ont.createCompositeProcess(URIUtils.createURI(CompositeURI + "SplitSequenceProcess"));
		final Profile profile = ont.createProfile(URIUtils.createURI(CompositeURI + "SplitSequenceProfile"));
		final WSDLGrounding grounding = ont.createWSDLGrounding(URIUtils.createURI(CompositeURI + "SplitSequenceGrounding"));

		System.out.println(ont.getKB().getServices(false));

		createProcess(process, services);
		createProfile(profile, process);

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

		profile.setLabel(createLabel(services), null);
		profile.setTextDescription(profile.getLabel(null));

		service.addProfile(profile);
		service.setProcess(process);
		service.addGrounding(grounding);
		return service;
	}
	
	*/
	
	
	
	String createLabel(final List<Service> services) {
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
	Profile createProfile(final Profile profile, final Process process)
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

	/**
	 * Create a Sequence process for the processes of given services. Creates
	 * the data flow assuming each service has one output and one input (except
	 * first and last one).
	 */
	
	private static List<Service> getSplitServices(String uri) throws URISyntaxException, IOException{
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

}
