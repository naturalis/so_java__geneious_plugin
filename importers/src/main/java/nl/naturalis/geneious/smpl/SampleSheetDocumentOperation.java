package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.utilities.IconUtilities.getIconsFromJar;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.log.LogSession;

/**
 * Framework-plumbing class used to import sample sheets.
 */
public class SampleSheetDocumentOperation extends DocumentOperation {

  // Releative position with menu and toolbar
  private static final double position = .99992;

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetDocumentOperation.class);

  public SampleSheetDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Samples", "Enriches documents by parsing their name", getIconsFromJar(getClass(), "/images/nbc_blue.png"))
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, position)
        .setInMainToolbar(true, position)
        .setInPopupMenu(true, position)
        .setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Enrich documents with data sample sheet data";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new SampleSheetImportOptions(DocumentUtilities.getSelectedDocuments());
  }

  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    try (LogSession session = GuiLogManager.startSession("Sample sheet import")) {
      SampleSheetImportOptions opts = (SampleSheetImportOptions) options;
      SampleSheetImporter importer = new SampleSheetImporter(opts.createImportConfig());
      importer.execute();
    }
    return null;
  }

}
