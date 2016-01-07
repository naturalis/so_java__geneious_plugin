/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsNotes;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromExcel extends DocumentAction {

	private List<AnnotatedPluginDocument> docs;
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	LimsExcelFields limsExcelFields = new LimsExcelFields();
	LimsNotes limsNotes = new LimsNotes();
	LimsFileSelector fcd = new LimsFileSelector();

	private String extractIDfileName = "";
	private SequenceDocument seq;
	// private Options options;
	private List<String> msgList = new ArrayList<String>();

	// String logFileName = limsImporterUtil.getLogPath() + File.separator
	// + limsImporterUtil.getLogFilename();

	// LimsLogger limsLogger = new LimsLogger(logFileName);

	private static final Logger logger = LoggerFactory
			.getLogger(LimsReadDataFromExcel.class);

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		logger.info("Start updating selected document(s).");

		if (annotatedPluginDocuments[0] != null) {
			try {
				/** Add selected documents to a list. */
				docs = DocumentUtilities.getSelectedDocuments();
				String fileSelected = fcd.loadSelectedFile();
				if (fileSelected == null) {
					return;
				}
				for (int cnt = 0; cnt < docs.size(); cnt++) {

					logger.info("-------------------------- S T A R T --------------------------");
					logger.info("Start Reading data from a excel file.");

					seq = (SequenceDocument) docs.get(cnt).getDocument();
					extractIDfileName = getExtractIDFromAB1FileName(seq
							.getName());

					msgList.add(seq.getName());

					readDataFromExcel(annotatedPluginDocuments, fileSelected);

					/* set note for Extract-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractIdCode", "Extract ID", "Extract-ID",
							limsExcelFields.getExtractID(), cnt);

					/* set note for Project Plaatnummer */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ProjectPlaatnummerCode", "Project Plaatnummer",
							"Project Plaatnummer",
							limsExcelFields.getProjectPlaatNummer(), cnt);

					/* Set note for Extract Plaatnummer */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractPlaatNummerCode", "Extract Plaatnummer",
							"Extract Plaatnummer",
							limsExcelFields.getExtractPlaatNummer(), cnt);

					/* set note for Taxonnaam */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"TaxonNaamCode", "Taxon naam", "Taxon naam",
							limsExcelFields.getTaxonNaam(), cnt);

					/* set note for Registrationnumber */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"BasisOfRecordCode", "Registrationnumber",
							"Registrationnumber",
							limsExcelFields.getRegistrationNumber(), cnt);

					/* set note for Plaat positie */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"PlaatpositieCode", "Plaat positie",
							"Plaat positie", limsExcelFields.getPlaatPositie(),
							cnt);

					/* set note for Sample method */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"SampleMethodCode", "Sample method",
							"Sample method", limsExcelFields.getSubSample(),
							cnt);

					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"VersieCode", "Version number", "Version number",
							limsExcelFields.getVersieNummer(), cnt);

					logger.info("Done with adding notes to the document");

				}
			} catch (DocumentOperationException e) {
				e.printStackTrace();
			}
			logger.info("--------------------------------------------------------");
			logger.info("Total of document(s) updated: " + docs.size());
		}

		logger.info("-------------------------- E N D --------------------------");
		logger.info("Done with updating the selected document(s). ");
		// limsLogger.removeConsoleHandler();
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				Dialogs.showMessageDialog("Excel: Done with updating the selected document(s): "
						+ msgList.toString());
				msgList.clear();
			}
		});
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Import Geneious samplesheet")
				.setInMainToolbar(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				PluginDocument.class, 0, Integer.MAX_VALUE) };
	}

	private void readDataFromExcel(
			AnnotatedPluginDocument[] annotatedPluginDocuments, String fileName) {

		String csvPath = "";
		String[] record = null;

		/*
		 * try { // csvFile =
		 * limsImporterUtil.getFileFromPropertieFile("excel"); //csvPath =
		 * limsImporterUtil.getPropValues() + fileName;
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 */
		logger.info("CSV file: " + fileName);

		logger.info("Start with adding notes to the document");
		try {
			CSVReader csvReader = new CSVReader(new FileReader(fileName), '\t',
					'\'', 0);

			csvReader.readNext();

			logger.info("Start with adding notes to the document");
			try {
				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					String ID = "e" + record[3];

					if (ID.equals(extractIDfileName)) {
						limsExcelFields.setProjectPlaatNummer(record[0]);
						limsExcelFields.setPlaatPositie(record[1]);
						limsExcelFields.setExtractPlaatNummer(record[2]);
						if (record[3] != null) {
							limsExcelFields.setExtractID(ID);
						}
						limsExcelFields.setRegistrationNumber(record[4]);
						limsExcelFields.setTaxonNaam(record[5]);
						// limsExcelFields.setSubSample(record[0]);

						logger.info("Extract-ID: "
								+ limsExcelFields.getExtractID());
						logger.info("Project plaatnummer: "
								+ limsExcelFields.getProjectPlaatNummer());
						logger.info("Extract plaatnummer: "
								+ limsExcelFields.getExtractPlaatNummer());
						logger.info("Taxon naam: "
								+ limsExcelFields.getTaxonNaam());
						logger.info("Registrationnumber: "
								+ limsExcelFields.getRegistrationNumber());
						logger.info("Plaat positie: "
								+ limsExcelFields.getPlaatPositie());

					} // end IF
				} // end While
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				csvReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extract the ID from the filename
	 * 
	 * @param annotatedPluginDocuments
	 *            set the param
	 * @return
	 */
	private String getExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		logger.info("Document Filename: " + fileName);
		String[] underscore = StringUtils.split(fileName, "_");
		return underscore[0];
	}
}