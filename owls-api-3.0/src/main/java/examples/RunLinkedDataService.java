package examples;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
	 */
	public static void main(String[] args) throws URISyntaxException, IOException, ExecutionException {
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		exec.addMonitor(new DefaultProcessMonitor());
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
		
		
		// Carregando ontologias que descrevem o serviço
		//URI uri = new URI("http://127.0.0.1/owl_s/services/1.2/isbn_book_service.owls");
		URI uri = new URI("http://127.0.0.1/owl_s/services/1.2/isbn_book_linked_data_grounding.owls");
		Service service = kb.readService(uri);
		
		//System.out.println(service.getGrounding());
		
		org.mindswap.owls.process.Process process = service.getProcess();
		//System.out.println(service.getGrounding());
		// Definindo entradas
		ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();
		inputs.setValue(process.getInput("_ISBN"), kb.createDataValue("ISBN 978-0-671-21781-5 (first edition)"));
		
		//inputs.setValue(process.getInput("YNS-AppID"), kb.createDataValue("YahooDemo"));
		//inputs.setValue(process.getInput("YNS-Query"), kb.createDataValue("Brasil"));
		
		// Criando conjunto de saídas e executando processo
		ValueMap<Output, OWLValue> outputs = new ValueMap<Output, OWLValue>();
		outputs = exec.execute(process, inputs, kb);

		// Exibindo a saída em RDF
		System.out.println(process.getOutput().toRDF(true, true));
		
		// Exibindo a saída em String
		final OWLValue outputValue = outputs.getValue(process.getOutput());
		System.out.println(outputValue.toString());

	}

}
