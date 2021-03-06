package org.sadiframework.service.example;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sadiframework.service.annotations.ContactEmail;
import org.sadiframework.service.annotations.TestCase;
import org.sadiframework.service.annotations.TestCases;
import org.sadiframework.utils.SIOUtils;
import org.sadiframework.utils.UniProtUtils;
import org.sadiframework.vocab.Properties;
import org.sadiframework.vocab.SIO;

import uk.ac.ebi.kraken.interfaces.common.Sequence;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxonomyId;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

@ContactEmail("info@sadiframework.org")
@TestCases(
		@TestCase(
				input = "http://sadiframework.org/examples/t/uniprotInfo.input.1.rdf",
				output = "http://sadiframework.org/examples/t/uniprotInfo.output.1.rdf"
		)
)
public class UniProtInfoServiceServlet extends UniProtServiceServlet
{
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(UniProtInfoServiceServlet.class);

	private static final Resource Taxon_Type = ResourceFactory.createResource("http://purl.oclc.org/SADI/LSRN/taxon_Record");
	private static final Resource Taxon_Identifier = ResourceFactory.createResource("http://purl.oclc.org/SADI/LSRN/taxon_Identifier");

	@Override
	protected Model createOutputModel()
	{
		Model model = super.createOutputModel();
		model.setNsPrefix("uniprotInfo", "http://sadiframework.org/examples/uniprotInfo.owl#");
		return model;
	}

	@Override
	public void processInput(UniProtEntry input, Resource output)
	{
		ProteinDescription description = input.getProteinDescription();
		if (description != null) {
			if (description.hasRecommendedName()) {
				attachNames(output, description.getRecommendedName(), true);
			}
			if (description.hasAlternativeNames()) {
				for (Name name: description.getAlternativeNames()) {
					attachNames(output, name, false);
				}
			}
		}

		attachOrganism(output, input);

		attachGeneSymbol(output, input);

		Sequence sequence = input.getSequence();
		if (sequence != null) {
			attachSequence(output, sequence);
		}
	}

	private void attachNames(Resource uniprotNode, Name name, boolean preferred)
	{
//		Resource nameNode = SIOUtils.createAttribute(uniprotNode, preferred ? SIO.preferred_name : SIO.name, getFullName(name));
		Resource nameNode = SIOUtils.createAttribute(uniprotNode, Properties.hasName, preferred ? SIO.preferred_name : SIO.name, getFullName(name));
		String shortName = getShortName(name);
		if (shortName != null) {
//			Resource shortNameNode = SIOUtils.createAttribute(uniprotNode, SIO.name, shortName);
			Resource shortNameNode = SIOUtils.createAttribute(uniprotNode, Properties.hasName, SIO.name, shortName);
			shortNameNode.addProperty(SIO.is_variant_of, nameNode);
		}
	}

	private String getFullName(Name name)
	{
		return UniProtUtils.getFieldString(name.getFieldsByType(FieldType.FULL));
	}

	private String getShortName(Name name)
	{
		return UniProtUtils.getFieldString(name.getFieldsByType(FieldType.SHORT));
	}

	private void attachSequence(Resource uniprotNode, Sequence sequence)
	{
		String value = sequence.getValue();
		if (!StringUtils.isEmpty(value)) {
//			SIOUtils.createAttribute(uniprotNode, SIO.amino_acid_sequence, value);
			SIOUtils.createAttribute(uniprotNode, Properties.hasSequence, SIO.amino_acid_sequence, value);
		}
	}

	private void attachOrganism(Resource uniprotNode, UniProtEntry input)
	{
		for (NcbiTaxonomyId taxonId: input.getNcbiTaxonomyIds()) {
			Resource taxonNode = uniprotNode.getModel().createResource(Taxon_Type);
			SIOUtils.createAttribute(taxonNode, Taxon_Identifier, taxonId.getValue());
			SIOUtils.createAttribute(taxonNode, Properties.hasName, SIO.scientific_name, StringUtils.defaultString(getScientificName(input), "null"));
//			uniprotNode.addProperty(SIO.is_located_in, taxonNode);
			uniprotNode.addProperty(Properties.fromOrganism, taxonNode);
		}
	}

	private void attachGeneSymbol(Resource uniprotNode, UniProtEntry input)
	{
		for (Gene gene: input.getGenes()) {
			String geneSymbol = gene.getGeneName().getValue();
			SIOUtils.createAttribute(uniprotNode, LocalSIO.symbol, geneSymbol);
		}
	}

	private String getScientificName(UniProtEntry input)
	{
		try {
			return input.getOrganism().getScientificName().getValue();
		} catch (Exception e) {
			// probably NPE...
			return null;
		}
	}

	// FIXME delete this when a new sadi.common is deployed.
	private static class LocalSIO extends SIO
	{
		public static Resource symbol = ResourceFactory.createResource( NS + "SIO_000105" );
	}
}
