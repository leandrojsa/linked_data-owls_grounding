package br.ufba.dcc.linked_data.impl;

import impl.owls.grounding.GroundingImpl;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.process.AtomicProcess;

import br.ufba.dcc.linked_data.LinkedDataAtomicGrounding;
import br.ufba.dcc.linked_data.LinkedDataGrounding;

public class LinkedDataGroundingImpl extends GroundingImpl<LinkedDataAtomicGrounding, String> implements LinkedDataGrounding {


	public LinkedDataGroundingImpl(final OWLIndividual ind)
	{
		super(ind);
	}

	/* @see org.mindswap.owls.grounding.Grounding#getAtomicGrounding(org.mindswap.owls.process.AtomicProcess) */
	public LinkedDataAtomicGrounding getAtomicGrounding(final AtomicProcess process)
	{
		return getAtomicGrounding(process, LinkedDataAtomicGrounding.class);
	}

	/* @see org.mindswap.owls.grounding.Grounding#getAtomicGroundings() */
	public OWLIndividualList<LinkedDataAtomicGrounding> getAtomicGroundings()
	{
		return getAtomicGroundings(LinkedDataAtomicGrounding.class);
	}

	/* @see org.mindswap.owls.grounding.Grounding#getAtomicGroundings(org.mindswap.owls.process.AtomicProcess) */
	public OWLIndividualList<LinkedDataAtomicGrounding> getAtomicGroundings(final AtomicProcess process)
	{
		return getAtomicGroundings(process, LinkedDataAtomicGrounding.class);
	}
}