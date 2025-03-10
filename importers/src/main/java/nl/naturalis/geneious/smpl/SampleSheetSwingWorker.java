package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static java.util.stream.Collectors.toList;
import static nl.naturalis.geneious.Precondition.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.Precondition.AT_LEAST_ONE_DOCUMENT_SELECTED;
import static nl.naturalis.geneious.Precondition.VALID_TARGET_FOLDER;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.Precondition;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.CsvImportStats;
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.DocumentLookupTable;
import nl.naturalis.geneious.util.Messages.Info;

/**
 * Manages and coordinates the import of sample sheets into Geneious.
 * 
 * @author Ayco Holleman
 */
class SampleSheetSwingWorker extends PluginSwingWorker<SampleSheetImportConfig> {

  static final String FILE_DESCRIPTION = "Sample sheet";
  static final String KEY_NAME = "extract ID";

  private static final GuiLogger logger = GuiLogManager.getLogger(SampleSheetSwingWorker.class);

  SampleSheetSwingWorker(SampleSheetImportConfig config) {
    super(config);
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws DatabaseServiceException, NonFatalException {
    if (config.isCreateDummies()) {
      return updateOrCreateDummies();
    }
    return updateOnly();
  }

  private List<AnnotatedPluginDocument> updateOnly() throws NonFatalException {
    Info.loadingFile(logger, config);
    List<String[]> rows = new RowSupplier(config).getDataRows();
    Info.displayRowCount(logger, FILE_DESCRIPTION, rows.size());
    RuntimeInfo runtime = new RuntimeInfo(rows.size());
    SampleSheetImporter1 importer = new SampleSheetImporter1(config, runtime);
    List<AnnotatedPluginDocument> selectedDocuments = config.getSelectedDocuments();
    DocumentLookupTable<String> lookups = new DocumentLookupTable<>(selectedDocuments, this::getKey);
    importer.importRows(rows, lookups);
    List<AnnotatedPluginDocument> updated = null;
    if (runtime.countUpdatedDocuments() != 0) {
      runtime.getUpdatedDocuments().forEach(StoredDocument::saveAnnotations);
      updated = runtime.getUpdatedDocuments().stream().map(StoredDocument::getGeneiousDocument).collect(toList());
      updated = addAndReturnGeneratedDocuments(updated, true, Collections.emptyList());
    }
    CsvImportStats stats = new CsvImportStats(selectedDocuments, runtime);
    stats.print(logger);
    Info.explainUnusedRowForSampleSheets1(logger);
    Info.operationCompletedSuccessfully(logger, getLogTitle());
    return updated == null ? Collections.emptyList() : updated;
  }

  private List<AnnotatedPluginDocument> updateOrCreateDummies() throws NonFatalException {
    Info.loadingFile(logger, config);
    List<String[]> rows = new RowSupplier(config).getDataRows();
    Info.displayRowCount(logger, FILE_DESCRIPTION, rows.size());
    RuntimeInfo runtime = new RuntimeInfo(rows.size());
    SampleSheetImporter2 importer = new SampleSheetImporter2(config, runtime);
    List<AnnotatedPluginDocument> selectedDocuments = config.getSelectedDocuments();
    DocumentLookupTable<String> lookups = new DocumentLookupTable<>(selectedDocuments, this::getKey);
    importer.importRows(rows, lookups);
    List<AnnotatedPluginDocument> all = null;
    if (runtime.countUpdatedDocuments() > 0 || importer.getNewDummies().size() > 0) {
      runtime.getUpdatedDocuments().forEach(StoredDocument::saveAnnotations);
      importer.getNewDummies().forEach(StoredDocument::saveAnnotations);
      all = new ArrayList<>(runtime.countUpdatedDocuments() + importer.getNewDummies().size());
      runtime.getUpdatedDocuments().stream().map(StoredDocument::getGeneiousDocument).forEach(all::add);
      importer.getNewDummies().stream().map(StoredDocument::getGeneiousDocument).forEach(all::add);
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList(), config.getTargetFolder());
    }
    int unchanged = selectedDocuments.size() - runtime.countUpdatedDocuments() - importer.getUpdatedDummies().size();
    logger.info("Number of valid rows ................: %3d", runtime.countGoodRows());
    logger.info("Number of empty/bad rows ............: %3d", runtime.countBadRows());
    logger.info("Number of unused rows ...............: %3d", runtime.countUnusedRows());
    logger.info("Number of selected documents ........: %3d", selectedDocuments.size());
    logger.info("Number of updated documents .........: %3d", runtime.countUpdatedDocuments() - importer.getUpdatedDummies().size());
    logger.info("Number of updated dummies ...........: %3d", importer.getUpdatedDummies().size());
    logger.info("Number of unchanged documents .......: %3d", unchanged);
    logger.info("Number of dummy documents created ...: %3d", importer.getNewDummies().size());
    Info.explainUnusedRowForSampleSheets2(logger);
    Info.operationCompletedSuccessfully(logger, SampleSheetDocumentOperation.NAME);
    return all == null ? Collections.emptyList() : all;
  }

  private String getKey(StoredDocument sd) {
    String s = sd.getNaturalisNote().getExtractId();
    return s == null ? null : s.substring(1); // Remove the 'e' at the beginning of the extract ID
  }

  @Override
  protected String getLogTitle() {
    return SampleSheetDocumentOperation.NAME;
  }

  @Override
  protected Set<Precondition> getPreconditions() {
    if (config.isCreateDummies()) {
      return EnumSet.of(ALL_DOCUMENTS_IN_SAME_DATABASE, VALID_TARGET_FOLDER);
    }
    return EnumSet.of(ALL_DOCUMENTS_IN_SAME_DATABASE, AT_LEAST_ONE_DOCUMENT_SELECTED);
  }

}
