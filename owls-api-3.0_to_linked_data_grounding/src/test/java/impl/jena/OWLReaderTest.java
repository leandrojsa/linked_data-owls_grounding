/*
 * Created 26.03.2009
 *
 * (c) 2009 Thorsten Möller - University of Basel Switzerland
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
package impl.jena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.utils.URIUtils;

import examples.ExampleURIs;

/**
 * JUnit tests for class {@link OWLReaderImpl}.
 *
 * @author unascribed
 * @version $Rev: 2269 $; $Author: thorsten $; $Date: 2009-08-19 17:21:09 +0200 (Mi, 19 Aug 2009) $
 */
public class OWLReaderTest
{
	@BeforeClass
	public static void note()
	{
		System.out.println("Note that this test requires the local host to be connected to the Internet and " +
			"the availability of remote resources, which might change, move, or disappear. Consequently, " +
			"in case this test fails this does not necessarily mean that there is a problem with the code!");
	}

	/**
	 * Test method for {@link impl.jena.OWLReaderImpl#read(OWLKnowledgeBase, java.io.Reader, java.net.URI)}.
	 */
	@Test
	public final void testReadReaderURI()
	{
		String invalidXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		OWLKnowledgeBase kb = OWLFactory.createKB();
		try
		{
			kb.read(new StringReader(invalidXML), null);
			fail(IOException.class + " expected.");
		}
		catch (IOException ignore)
		{
			// nice
		}
	}

	/**
	 * Test method for {@link impl.jena.OWLReaderImpl#read(OWLKnowledgeBase, InputStream, java.net.URI)}.
	 */
	@Test
	public final void testReadInputStreamURI() throws IOException
	{
		// try to read ont that imports owls:Service, which is available
		InputStream availableImport = ClassLoader.getSystemResourceAsStream("owl/available_import.owl");
		assertNotNull("Bug in JUnit Test. Required OWL test file not found.", availableImport);
		OWLKnowledgeBase kb = OWLFactory.createKB();
		try
		{
			OWLOntology ont = kb.read(availableImport, null);
			assertNotNull(ont);
			Set<OWLOntology> imports = ont.getImports(true);
			assertEquals(1, imports.size()); // the file exactly has one import
			OWLOntology serviceOnt = imports.iterator().next();
			assertEquals(URIUtils.standardURI(OWLS.SERVICE_NS), serviceOnt.getURI());
		}
		catch (IOException e)
		{
			fail("Unexpected exception " + e);
		}
		availableImport.close();

		// try to read ont that imports another ont that does not exist and we ignore failure
		InputStream unavailableImport = ClassLoader.getSystemResourceAsStream("owl/unavailable_import.owl");
		assertNotNull("Bug in JUnit Test. Required OWL test file not found.", unavailableImport);
		kb = OWLFactory.createKB();
		kb.getReader().setIgnoreFailedImport(true);
		try
		{
			OWLOntology ont = kb.read(unavailableImport, null);
			assertNotNull(ont);
			Set<OWLOntology> imports = ont.getImports(true);
			assertEquals(1, imports.size()); // imported ontology exists but should be empty
			OWLOntology unavailableOnt = imports.iterator().next();
			assertEquals(0, ((OWLOntologyImpl) unavailableOnt).getImplementation().size());
		}
		catch (IOException e)
		{
			fail("Unexpected exception " + e);
		}
		unavailableImport.close();

		// try to read ont that imports another ont that does not exist and we do not ignore failure
		unavailableImport = ClassLoader.getSystemResourceAsStream("owl/unavailable_import.owl");
		assertNotNull("Bug in JUnit Test. Required OWL test file not found.", unavailableImport);
		kb = OWLFactory.createKB();
		kb.getReader().setIgnoreFailedImport(false);
		try
		{
			kb.read(unavailableImport, null);
			fail("Exception expected.");
		}
		catch (IOException e)
		{
			// nice
		}
		unavailableImport.close();

		// do the same again
		unavailableImport = ClassLoader.getSystemResourceAsStream("owl/unavailable_import.owl");
		assertNotNull("Bug in JUnit Test. Required OWL test file not found.", unavailableImport);
		kb = OWLFactory.createKB();
		kb.getReader().setIgnoreFailedImport(false);
		try
		{
			kb.read(unavailableImport, null);
			fail("Exception expected.");
		}
		catch (IOException e)
		{
			// nice
		}
		unavailableImport.close();
	}

	/**
	 * Tests reading two example OWL-S services from a local file concurrently
	 * by multiple threads. We only want to analyze if typical concurrent
	 * modification exceptions occur.
	 */
	@Test
	public final void testReadUsingMultipleThreads() throws Exception
	{
		int numOfConcurrentTasks = 5;
		List<Callable<Exception>> tasks = new ArrayList<Callable<Exception>>(numOfConcurrentTasks);
		ExecutorService executor = Executors.newFixedThreadPool(numOfConcurrentTasks);

		for (int i = 0; i < numOfConcurrentTasks; i++)
		{
			tasks.add(new Callable<Exception>() {
				public Exception call() throws Exception
				{
					try
					{
						OWLKnowledgeBase kb = OWLFactory.createKB();
//						kb.getReader().setIgnoreFailedImport(true);
						File f = new File("src/examples/owl-s/1.2/FrenchDictionary.owl");
						FileInputStream fis = new FileInputStream(f);
						Service service = kb.readService(fis, ExampleURIs.FRENCH_DICTIONARY_OWLS12);
						if (service == null) return new Exception("Service null upon read.");
						fis.close();
						return null;
					}
					catch (IOException ioe)
					{
						return ioe;
					}
				}
			});

			tasks.add(new Callable<Exception>() {
				public Exception call() throws Exception
				{
					try
					{
						OWLKnowledgeBase kb = OWLFactory.createKB();
//						kb.getReader().setIgnoreFailedImport(true);
						File f = new File("src/examples/owl-s/1.2/CurrencyConverter.owl");
						FileInputStream fis = new FileInputStream(f);
						Service service = kb.readService(fis, ExampleURIs.CURRENCY_CONVERTER_OWLS12);
						if (service == null) return new Exception("Service null upon read.");
						fis.close();
						return null;
					}
					catch (IOException ioe)
					{
						return ioe;
					}
				}
			});
		}

		List<Future<Exception>> futures = executor.invokeAll(tasks);

		boolean fail = false;
		StringBuilder errors = new StringBuilder();
		for (Future<Exception> future : futures)
		{
			Exception e = future.get();
			if (e != null)
			{
				fail = true;
				errors.append(" ").append(e.toString());
			}
		}

		if (fail)
		{
			fail("Callable threw exception. Details:" + errors);
		}
	}

}
