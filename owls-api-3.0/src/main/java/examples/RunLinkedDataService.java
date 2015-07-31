package examples;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.execution.DefaultProcessMonitor;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

public class RunLinkedDataService {

	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static long a;
	public static long b;
	public static long c;
	public static long d;
	public static void main(String[] args) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
			System.gc();
			Runtime.getRuntime().freeMemory();
			final OWLKnowledgeBase kb = OWLFactory.createKB();
			
			/*
			Service service = kb.readService(ExampleURIs.JGROUNDING_OWLS12);
			org.mindswap.owls.process.Process process = service.getProcess();
			
			
	 
			// get the parameter using the local name
			ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();
			inputs.setValue(process.getInput("FirstParam"), kb.createDataValue("2"));
			inputs.setValue(process.getInput("SecParam"), kb.createDataValue("3"));
	
			ValueMap<Output, OWLValue> outputs = exec.execute(process, inputs, kb);
			*/
			
			long time_start = System.currentTimeMillis();
			long time_start_process_execution=0;
			long time_end_process_execution=0;
			// Carregando ontologias que descrevem o serviço
			//URI uri = new URI("http://127.0.0.1/owl_s/services/1.2/isbn_book_service.owls");
			//URI uri = new URI("http://127.0.0.1/owl_s/services/1.2/isbn_book_linked_data_grounding.owls");
			//URI uri = new URI("http://localhost/owl_s/services/1.2/country_population_capital_SWoDs.owls");
			//URI uri = new URI("http://localhost/owl_s/services/1.2/temperature_by_city.owls");
			URI uri = new URI("http://localhost/owl_s/services/1.2/humidity_by_city.owls");
			
			Service service = kb.readService(uri);
			RunLinkedDataService.d = time_start_process_execution=System.currentTimeMillis();
			//System.out.println("Inicio do Grounding SPAQRL: " + (RunLinkedDataService.d- RunLinkedDataService.c));
			//System.out.println("Leitura inicial do serviço (antes do Process execute): " + (time_start_process_execution- time_start));
			
			//System.out.println(service.getGrounding());
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
			exec.addMonitor(new DefaultProcessMonitor());
			org.mindswap.owls.process.Process process = service.getProcess();
			//System.out.println(service.getGrounding());
			// Definindo entradas
			ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();
			inputs.setValue(process.getInput("_COUNTRY"), kb.createDataValue("'Brazil'@en"));
			inputs.setValue(process.getInput("_CITY"), kb.createDataValue("'Salvador'@en"));			
			//inputs.setValue(process.getInput("YNS-AppID"), kb.createDataValue("YahooDemo"));
			//inputs.setValue(process.getInput("YNS-Query"), kb.createDataValue("Brasil"));
			
			// Criando conjunto de saídas e executando processo
			ValueMap<Output, OWLValue> outputs = new ValueMap<Output, OWLValue>();
			
			
			outputs = exec.execute(process, inputs, kb);
			
			//System.out.println("Fim execução do serviço (somente do process execute): " + (time_end_process_execution- time_start_process_execution));
			//System.out.println("Fim execução do serviço (Total): " + (time_end_process_execution- time_start));
			
			// Exibindo a saída em RDF
			//System.out.println(process.getOutput().toRDF(true, true));
			
			// Exibindo a saída em String
			final OWLValue outputValue = outputs.getValue(process.getOutput());
			//System.out.println(outputValue.toString());
			System.out.println(Runtime.getRuntime().totalMemory());
			System.runFinalization();
			
		

	}

}
