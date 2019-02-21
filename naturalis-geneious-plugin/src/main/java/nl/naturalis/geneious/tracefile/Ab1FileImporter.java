package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.split.SequenceNameNotParsableException;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

class Ab1FileImporter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(Ab1FileImporter.class);

  private final List<Ab1FileInfo> ab1Files;

  Ab1FileImporter(List<Ab1FileInfo> ab1Files) {
    guiLogger.debug("Initializing AB1 file importer");
    this.ab1Files = ab1Files;
  }

  List<AnnotatedPluginDocument> importFiles() throws IOException {
    List<AnnotatedPluginDocument> result = new ArrayList<>(ab1Files.size());
    int imported = 0;
    int rejected = 0;
    int enriched = 0;
    for (Ab1FileInfo ab1FileInfo : ab1Files) {
      File f = ab1FileInfo.getSourceFile();
      guiLogger.debugf(() -> format("Processing file: %s", f.getName()));
      List<AnnotatedPluginDocument> apds;
      try {
        apds = PluginUtilities.importDocuments(f, null);
        ++imported;
      } catch (DocumentImportException e) {
        guiLogger.error("Error processing file %s", e, f.getAbsolutePath());
        ++rejected;
        continue;
      }
      if (apds.size() != 1) {
        guiLogger.fatal("Unexpected number of documents created from a single file: %s. Aborting.", apds.size());
        break;
      }
      try {
        ab1FileInfo.getNote().attach(apds.get(0));
        ++enriched;
      } catch (SequenceNameNotParsableException e) {
        guiLogger.error(e.getMessage());
        continue;
      }
      result.addAll(apds);
    }
    guiLogger.info("Number of AB1 files selected: %s", ab1Files.size());
    guiLogger.info("Number of AB1 files imported: %s", imported);
    guiLogger.info("Number of AB1 files rejected: %s", rejected);
    guiLogger.info("Number of AB1 documents enriched: %s", enriched);
    return result;
  }

}
