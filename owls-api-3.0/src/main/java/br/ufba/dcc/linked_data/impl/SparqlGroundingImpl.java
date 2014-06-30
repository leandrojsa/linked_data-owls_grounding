package br.ufba.dcc.linked_data.impl;

import impl.owls.grounding.GroundingImpl;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.process.AtomicProcess;

import br.ufba.dcc.linked_data.SparqlAtomicGrounding;
import br.ufba.dcc.linked_data.SparqlGrounding;

public class SparqlGroundingImpl extends GroundingImpl<SparqlAtomicGrounding, String> implements SparqlGrounding {


	public SparqlGroundingImpl(final OWLIndividual ind)
	{
		super(ind);
	}

	/* @see org.mindswap.owls.grounding.Grounding#getAtomicGrounding(org.mindswap.owls.process.AtomicProcess) */
	public SparqlAtomicGrounding getAtomicGrounding(final AtomicProcess process)
	{
		return getAtomicGrounding(process, SparqlAtomicGrounding.class);
	}

	/* @see org.mindswap.owls.grounding.Grounding#getAtomicGroundings() */
	public OWLIndividualList<SparqlAtomicGrounding> getAtomicGroundings()
	{
		return getAtomicGroundings(SparqlAtomicGrounding.class);
	}

	/* @see org.mindswap.owls.grounding.Grounding#getAtomicGroundings(org.mindswap.owls.process.AtomicProcess) */
	public OWLIndividualList<SparqlAtomicGrounding> getAtomicGroundings(final AtomicProcess process)
	{
		return getAtomicGroundings(process, SparqlAtomicGrounding.class);
	}
}