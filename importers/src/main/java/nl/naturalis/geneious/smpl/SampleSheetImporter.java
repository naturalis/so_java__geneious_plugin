package nl.naturalis.geneious.smpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.APDList;
import nl.naturalis.geneious.util.DummySequenceDocument;
import nl.naturalis.geneious.util.QueryUtils;
import nl.naturalis.geneious.util.RowIterator;
import nl.naturalis.geneious.util.StoredDocument;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentLookupTable;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;

/**
 * Does the actual work of importing a sample sheet into Geneious.
 */
class SampleSheetImporter extends SwingWorker<APDList, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetImporter.class);

  private final SampleSheetImportConfig cfg;

  SampleSheetImporter(SampleSheetImportConfig cfg) {
    this.cfg = cfg;
  }

  /**
   * Enriches the documents selected within the GUI with data from the sample sheet. Documents and sample sheet records are linked using
   * their extract ID. In addition, if requested, this routine will create dummy documents from sample sheet records if their extract ID
   * does not exist yet.
   */
  @Override
  protected APDList doInBackground() throws DatabaseServiceException {
    return importSampleSheet();
  }

  private APDList importSampleSheet() throws DatabaseServiceException {
    if (cfg.isCreateDummies()) {
      return enrichOrCreateDummies();
    }
    return enrichOnly();
  }

  private APDList enrichOrCreateDummies() throws DatabaseServiceException {
    List<String[]> rows = new RowIterator(cfg).getAllRows();
    // Create a lookup table for the selected documents (using extract ID as key)
    StoredDocumentLookupTable selectedDocuments = new StoredDocumentLookupTable(cfg.getSelectedDocuments());
    // Find new extract IDs in sample sheet (rows containing them will become dummies)
    Set<String> newExtractIds = getNewExtractIds(rows, selectedDocuments.keySet());
    APDList updatesOrDummies = new APDList(rows.size());
    int good = 0, bad = 0, updated = 0, newDummies = 0, updatedDummies = 0, unused = 0;
    for (int i = 0; i < rows.size(); ++i) {
      SampleSheetRow row = new SampleSheetRow(i, rows.get(i));
      final int rowNum = i + cfg.getSkipLines();
      if (row.isEmpty()) {
        guiLogger.debugf(() -> format("Ignoring empty record at line %s", rowNum));
        ++bad;
        continue;
      }
      NaturalisNote note;
      try {
        note = row.extractNote();
      } catch (InvalidRowException e) {
        guiLogger.error(e.getMessage());
        ++bad;
        continue;
      }
      ++good;
      StoredDocumentList docs = selectedDocuments.get(note.getExtractId());
      if (docs == null) {
        // The sample sheet row does not correspond to any user-selected document
        if (newExtractIds.contains(note.get(SMPL_EXTRACT_ID))) {
          // It is inf fact a completely new extract ID. Create a dummy.
          guiLogger.debugf(() -> format("Creating dummy document for extract ID %s", note.getExtractId()));
          updatesOrDummies.add(new DummySequenceDocument(note).wrap());
          ++newDummies;
        } else {
          ++unused;
          guiLogger.debugf(() -> format("Sample sheet row %s (extract ID %s) corresponds to one or more existing "
              + " documents, but they were not selected and will not be updated", rowNum, note.getExtractId()));
        }
      } else {
        guiLogger.debugf(() -> format("Enriching selected documents with extract ID %s", note.getExtractId()));
        for (StoredDocument doc : docs) {
          if (note.saveTo(doc)) {
            updatesOrDummies.add(doc.getGeneiousDocument());
            ++updated;
            if (doc.isDummy()) {
              ++updatedDummies;
            }
          }
        }
      }
    }
    int numSelected = cfg.getSelectedDocuments().length;
    int numUnaffected = numSelected - updated - updatedDummies;
    guiLogger.info("Number of valid records in sample sheet: %s", good);
    guiLogger.info("Number of empty/bad records in sample sheet: %s", bad);
    guiLogger.info("Number of selected documents: %s", cfg.getSelectedDocuments().length);
    if (updatedDummies == 0) {
      guiLogger.info("Number selected of documents updated by sample sheet: %s", updated);
    } else {
      guiLogger.info("Number selected of documents updated by sample sheet: %s (of which dummies: %s)", updated, updatedDummies);
    }
    guiLogger.info("Number of selected documents not updated by sample sheet: %s", numUnaffected);
    guiLogger.info("Number of dummy documents created: %s", newDummies);
    guiLogger.info("Number of unused rows (corresponding to existing but not-selected documents): %s", unused);
    guiLogger.info("Import completed successfully");
    return updatesOrDummies;
  }

  private APDList enrichOnly() {
    List<String[]> rows = new RowIterator(cfg).getAllRows();
    Map<String, StoredDocument> selectedDocuments = createLookupTable();
    int numSelected = cfg.getSelectedDocuments().length;
    APDList updates = new APDList(numSelected);
    int good = 0, bad = 0, enriched = 0;
    SampleSheetRow row;
    for (int i = 1; i < rows.size(); ++i) {
      if ((row = new SampleSheetRow(i, rows.get(i))).isEmpty()) {
        ++bad;
        continue;
      }
      NaturalisNote note;
      try {
        note = row.extractNote();
      } catch (InvalidRowException e) {
        guiLogger.error(e.getMessage());
        ++bad;
        continue;
      }
      ++good;
      String extractId = note.getExtractId();
      StoredDocument document = selectedDocuments.get(extractId);
      if (document != null) {
        guiLogger.debugf(() -> format("Enriching document with extract ID %s", extractId));
        note.saveTo(document);
        updates.add(document.getGeneiousDocument());
        ++enriched;
      }
    }
    guiLogger.info("Number of valid records in sample sheet: %s", good);
    guiLogger.info("Number of empty/bad records in sample sheet: %s", bad);
    guiLogger.info("Number of documents selected: %s", numSelected);
    guiLogger.info("Number of documents enriched: %s", enriched);
    guiLogger.info("Import completed successfully");
    return updates;
  }

  /*
   * Scans the sample sheet for new extract IDs. If the user had chosen to create dummies, we must do so if: [1] the extract ID of a sample
   * sheet row does not exist anywhere in the target database; [2] the extract ID does not correspond to any of the documents selected by
   * the user in the GUI. The second condition is implied by the first condition, because the selected documents obviously were somewhre in
   * the target database. But since Geneious hands us the selected records for free, we can discard them when constructing the database
   * query, thus making the query a bit more light-weight.
   */
  private static Set<String> getNewExtractIds(List<String[]> rows, Set<String> selectedIds) throws DatabaseServiceException {
    guiLogger.debug(() -> "Marking rows with new extract IDs (will become dummy sequences)");
    Set<String> allIdsInSheet = new HashSet<>(rows.size(), 1F);
    Set<String> nonSelectedIds = new HashSet<>(rows.size(), 1F);
    int colno = SampleSheetRow.COLNO_EXTRACT_ID;
    for (String[] row : rows) {
      if (colno < row.length && StringUtils.isNotBlank(row[colno])) {
        String id = "e" + row[colno];
        allIdsInSheet.add(id);
        if (!selectedIds.contains(id)) {
          nonSelectedIds.add(id);
        }
      }
    }
    guiLogger.debug(() -> "Searching database ...");
    List<AnnotatedPluginDocument> documents = QueryUtils.findByExtractID(nonSelectedIds);
    Set<String> exists = new HashSet<>(documents.size(), 1F);
    documents.forEach(document -> exists.add(SMPL_EXTRACT_ID.readFrom(document)));
    allIdsInSheet.removeAll(exists);
    allIdsInSheet.removeAll(selectedIds);
    guiLogger.debugf(() -> format("Sample sheet contains %s new extract ID(s)", allIdsInSheet.size()));
    return allIdsInSheet;
  }

  /*
   * Create a lookup table that maps the extract IDs of the selected documents to the selected documents themselves.
   */
  private Map<String, StoredDocument> createLookupTable() {
    int numSelected = cfg.getSelectedDocuments().length;
    Map<String, StoredDocument> map = new HashMap<>(numSelected, 1F);
    for (AnnotatedPluginDocument doc : cfg.getSelectedDocuments()) {
      NaturalisNote note = new NaturalisNote(doc);
      StoredDocument sd = new StoredDocument(doc, note);
      String extractId = note.getExtractId();
      if (extractId == null) {
        guiLogger.debugf(() -> format("Ignoring selected document without extract ID (urn=\"%s\")", doc.getURN()));
      } else {
        map.put(extractId, sd);
      }
    }
    return map;
  }

}
