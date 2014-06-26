package br.ufba.dcc.linked_data;

import org.mindswap.owl.OWLClass;
import org.mindswap.owl.OWLDataProperty;
import org.mindswap.owl.OWLObjectProperty;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.vocabulary.Vocabulary;
import org.mindswap.utils.URIUtils;

public class OWLSLinkedDataVocabulary extends Vocabulary{
	
	public static final String URI = "http://localhost/owl_s/ontology/";
	public static final String GROUNDING_NS = URI + "sparqlGrounding.owl#";
	public static final OWLOntology ont = loadOntology(GROUNDING_NS);

	//OWL Class
	
	public static OWLClass SparqlGrounding = 	ont.getClass(URIUtils.createURI(GROUNDING_NS + "SparqlGrounding"));
	public static OWLClass SparqlAtomicProcessGrounding = 	ont.getClass(URIUtils.createURI(GROUNDING_NS + "SparqlAtomicProcessGrounding"));
	public static OWLClass SparqlIntputParamMap = 	ont.getClass(URIUtils.createURI(GROUNDING_NS + "SparqlIntputParamMap"));
	public static OWLClass SparqlOutputParamMap = 	ont.getClass(URIUtils.createURI(GROUNDING_NS + "SparqlOutputParamMap"));

	public static OWLClass SparqlTripleMap = ont.getClass(URIUtils.createURI(GROUNDING_NS + "SparqlTripleMap"));
	public static OWLClass SparqlPrefixMap = ont.getClass(URIUtils.createURI(GROUNDING_NS + "SparqlPrefixMap"));
	
	//ObjectProperties
	
	public static OWLObjectProperty SparqlPrefixes = ont.getObjectProperty(URIUtils.createURI(GROUNDING_NS + "SparqlPrefixes"));
	
	
	public static OWLObjectProperty SparqlTriples = ont.getObjectProperty(URIUtils.createURI(GROUNDING_NS + "SparqlTriples"));
	
	
	public static OWLObjectProperty SparqlInputParam = ont.getObjectProperty(URIUtils.createURI(GROUNDING_NS + "SparqlInputParam"));
	public static OWLObjectProperty SparqlOutputParam = ont.getObjectProperty(URIUtils.createURI(GROUNDING_NS + "SparqlOutputParam"));
	
	
	
	
	//DataProperties
	
	public static OWLDataProperty SparqlDataParam = ont.getDataProperty(URIUtils.createURI(GROUNDING_NS + "SparqlDataParam"));
	public static OWLDataProperty PrefixName = ont.getDataProperty(URIUtils.createURI(GROUNDING_NS + "PrefixName"));
	public static OWLDataProperty PrefixUri = ont.getDataProperty(URIUtils.createURI(GROUNDING_NS + "PrefixUri"));
	
	public static OWLDataProperty TripleSubject = ont.getDataProperty(URIUtils.createURI(GROUNDING_NS + "TripleSubject"));
	public static OWLDataProperty TriplePredicate = ont.getDataProperty(URIUtils.createURI(GROUNDING_NS + "TriplePredicate"));
	public static OWLDataProperty TripleObject = ont.getDataProperty(URIUtils.createURI(GROUNDING_NS + "TripleObject"));
	
	public static OWLDataProperty sparqlEndPoint = ont.getDataProperty(URIUtils.createURI(GROUNDING_NS + "sparqlEndPoint"));
	
	
	

}
