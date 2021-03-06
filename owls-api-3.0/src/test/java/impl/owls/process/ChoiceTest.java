/*
 * Created 26.05.2009
 *
 * (c) 2009 Thorsten M�ller - University of Basel Switzerland
 *
 * The MIT License
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package impl.owls.process;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import impl.owls.process.constructs.ChoiceImpl;
import impl.owls.process.constructs.SequenceImpl;

import java.io.InputStream;

import org.junit.Test;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLValue;
import org.mindswap.owl.vocabulary.XSD;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

import examples.ExampleURIs;


/**
 * JUnit integration test for {@link ChoiceImpl} and {@link SequenceImpl}.
 * Example process is loaded and executed. Indirectly tests (i)
 * {@link impl.swrl.DifferentIndividualsAtomImpl} and (ii)
 * {@link impl.swrl.SameIndividualAtomImpl} because they are used to express
 * process preconditions.
 *
 * @author unascribed
 * @version $Rev: 2269 $; $Author: thorsten $; $Date: 2009-08-19 17:21:09 +0200 (Mi, 19 Aug 2009) $
 */
public class ChoiceTest
{
	@Test
	public void testChoice() throws Exception
	{
		long read = System.nanoTime();
		final OWLKnowledgeBase kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");
//		kb.setReasoner("OWLMicro");
		final InputStream inpStr = ClassLoader.getSystemResourceAsStream("owls/1.2/Choice.owl");
		final Service service = kb.readService(inpStr, ExampleURIs.CHOICE_OWLS12);
		read = (System.nanoTime() - read) / 1000000;
		final Process process = service.getProcess();

		ValueMap<Input, OWLValue> inputs = new ValueMap<Input, OWLValue>();

//		System.out.println(kb.getTypes(kb.getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "Einstein"))));
//		System.out.println(kb.getTypes(kb.getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "Curie"))));
//		System.out.println(kb.getTypes(kb.getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "MarieCurie"))));
//		System.out.println(kb.isDifferentFrom(
//			service.getOntology().getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "Curie")),
//			service.getOntology().getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "Einstein"))));
//		System.out.println(kb.isDifferentFrom(
//			service.getOntology().getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "Einstein")),
//			service.getOntology().getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "Curie"))));
//		System.out.println(kb.isDifferentFrom(
//			service.getOntology().getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "MarieCurie")),
//			service.getOntology().getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "Einstein"))));
//		System.out.println(kb.isDifferentFrom(
//			service.getOntology().getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "Einstein")),
//			service.getOntology().getIndividual(URIUtils.createURI(ExampleURIs.CHOICE_OWLS12, "MarieCurie"))));

		final ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		long exet = System.nanoTime();
		ValueMap<Output, OWLValue> outputs = exec.execute(process, inputs, kb);
		exet = (System.nanoTime() - exet) / 1000000;
		System.out.printf("Read took %5dms, Execution took %4dms%n", read, exet);

		assertNotNull(outputs);
		assertEquals(1, outputs.size());
		OWLValue outputValue = outputs.getValue(process.getOutput("selectedChoice"));
		assertTrue(outputValue.isDataValue());
		OWLDataValue outputDataValue = outputValue.castTo(OWLDataValue.class);

		assertEquals(XSD.string, outputDataValue.getDatatypeURI());
		assertTrue(outputDataValue.getValue() instanceof String);

		System.out.println(outputs);
	}

}
