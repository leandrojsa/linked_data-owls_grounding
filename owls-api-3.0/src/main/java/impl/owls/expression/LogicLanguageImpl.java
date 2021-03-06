// The MIT License
//
// Copyright (c) 2004 Evren Sirin
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

/*
 * Created on Jul 7, 2004
 */
package impl.owls.expression;

import impl.owl.WrappedIndividual;

import java.net.URI;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owls.expression.LogicLanguage;
import org.mindswap.owls.vocabulary.OWLS;

/**
 * @author unascribed
 * @version $Rev: 2269 $; $Author: thorsten $; $Date: 2009-08-19 17:21:09 +0200 (Mi, 19 Aug 2009) $
 */
public class LogicLanguageImpl extends WrappedIndividual implements LogicLanguage
{
	public LogicLanguageImpl(final OWLIndividual ind)
	{
		super(ind);
	}

	public LogicLanguageImpl(final OWLIndividual ind, final URI refURI)
	{
	    super(ind);
	    setProperty(OWLS.Expression.refURI, refURI);
	}

	/* @see org.mindswap.owls.generic.expression.LogicLanguage#getRefURI() */
	public URI getRefURI()
	{
		return getPropertyAsURI(OWLS.Expression.refURI);
	}
}
