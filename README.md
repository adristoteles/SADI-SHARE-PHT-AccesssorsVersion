# SADI-SHARE-PHT-AccesssorsVersion
SADI-SHARE adaptition to use nanopubs, Accesors and Projections

and follow the FAIR data principles.

Most of the Accesors code is in SHAREKnowledgeBase
 
 
we use the https://github.com/LinkedDataFragments/Client.Java
adapter that lets you use Triple Pattern Fragments in Jena.

For example, use as a Jena Model:

LinkedDataFragmentGraph ldfg = new LinkedDataFragmentGraph("http://data.linkeddatafragments.org/dbpedia");
Model model = ModelFactory.createModelForGraph(ldfg);

Example queries:

SELECT ?p ?o WHERE { <http://dbpedia.org/resource/Barack_Obama> ?p ?o }
