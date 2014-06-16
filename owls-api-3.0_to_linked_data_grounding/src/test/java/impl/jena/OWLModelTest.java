/*
 * Created 15.07.2009
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;

import examples.ExampleURIs;

/**
 * JUnit test cases for {@link OWLModelImpl}.
 *
 * @author unascribed
 * @version $Rev: 2269 $; $Author: thorsten $; $Date: 2009-08-19 17:21:09 +0200 (Mi, 19 Aug 2009) $
 */
public class OWLModelTest
{
	private static final OWLKnowledgeBaseImpl kb = new OWLKnowledgeBaseImpl();

	@BeforeClass
	public static void loadOntologies()
	{
		try
		{
			kb.read(ExampleURIs.BABELFISH_TRANSLATOR_OWLS12);
			kb.read(ExampleURIs.BN_BOOK_PRICE_OWLS12);
			kb.read(ExampleURIs.BOOK_FINDER_OWLS12);
			kb.read(ExampleURIs.FRENCH_DICTIONARY_OWLS12);
		}
		catch (IOException e)
		{
			assumeNoException(e);
		}
	}

	/**
	 * Test method for {@link impl.jena.OWLModelImpl#getServices(boolean)}.
	 */
	@Test
	public final void testGetServices()
	{
		kb.setReasoner((Object) null);

		OWLIndividualList<Service> services = kb.getServices(true);
		assertEquals(0, services.size()); // all services are in sub ontologies of the KB

		kb.setReasoner("Pellet");
		services = kb.getServices(true);
		assertEquals(0, services.size()); // even if reasoner is used we still don't get them

		kb.setReasoner((Object) null);

		// now we want to get all services that exist
		services = kb.getServices(false);
		assertEquals(5, services.size());
		assertNoDuplicates(services);

		kb.setReasoner("Pellet");
		services = kb.getServices(false);
		assertEquals(5, services.size());
		assertNoDuplicates(services);
	}

	/**
	 * Test method for {@link impl.jena.OWLModelImpl#getProfiles(boolean)}.
	 */
	@Test
	public final void testGetProfiles()
	{
		kb.setReasoner((Object) null);

		OWLIndividualList<Profile> profiles = kb.getProfiles(true);
		assertEquals(0, profiles.size()); // all profiles are in sub ontologies of the KB

		kb.setReasoner("Pellet");
		profiles = kb.getProfiles(true);
		assertEquals(0, profiles.size()); // even if reasoner is used we still don't get them


		// now we want to get all services that exist

		kb.setReasoner((Object) null);
		profiles = kb.getProfiles(false);

		// without a reasoner attached we only get direct instances of Profile,
		// and there is only one w.r.t. the loaded services
		assertEquals(1, profiles.size());
		assertNoDuplicates(profiles);

		kb.setReasoner("Pellet");
		profiles = kb.getProfiles(false);

		// using a reasoner we get instances of subclasses of Profile as well,
		// which amount to 5 in total w.r.t. the loaded services
		assertEquals(5, profiles.size());
		assertNoDuplicates(profiles);
	}

	private <T extends OWLIndividual> void assertNoDuplicates(final OWLIndividualList<T> originals)
	{
		for (Iterator<T> it = originals.iterator(); it.hasNext(); )
		{
			T original = it.next();
			it.remove();

			OWLIndividualList<T> clones = OWLFactory.createIndividualList();
			clones.addAll(originals);
			for (Iterator<T> cit = clones.iterator(); cit.hasNext(); )
			{
				T clone = cit.next();
				assertNotSame(original, clone);
			}
		}
	}

}
