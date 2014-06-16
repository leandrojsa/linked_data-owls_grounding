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
package org.mindswap.owls.validator;

/**
 *
 * @author unascribed
 * @version $Rev: 2124 $; $Author: thorsten $; $Date: 2009-01-09 17:23:36 +0100 (Fr, 09 Jan 2009) $
 */
public class OWLSValidationException extends Exception
{
	public static final int ERROR_PARSE = 0;
	public static final int ERROR_IO = 1;

	private static final long serialVersionUID = 6529472154197952001L;

	private final int mId;

	public OWLSValidationException(final int theId, final String theMsg)
	{
		super(theMsg);
		mId = theId;
	}

	public int getId() {
		return mId;
	}

	public static OWLSValidationException createParseException(final String theMsg) {
		return new OWLSValidationException(ERROR_PARSE,theMsg);
	}

	public static OWLSValidationException createIOException(final String theMsg) {
		return new OWLSValidationException(ERROR_IO,theMsg);
	}
}
