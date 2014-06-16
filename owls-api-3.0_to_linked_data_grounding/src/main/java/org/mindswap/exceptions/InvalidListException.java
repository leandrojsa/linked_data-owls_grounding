/*
 * Created on Dec 21, 2004
 */
package org.mindswap.exceptions;

/**
 *
 * @author unascribed
 * @version $Rev: 2269 $; $Author: thorsten $; $Date: 2009-08-19 17:21:09 +0200 (Mi, 19 Aug 2009) $
 */
public class InvalidListException extends RuntimeException
{
	private static final long serialVersionUID = -1533732259442386944L;

	public InvalidListException()
	{
		super();
	}

	public InvalidListException(final String message)
	{
		super(message);
	}
}
