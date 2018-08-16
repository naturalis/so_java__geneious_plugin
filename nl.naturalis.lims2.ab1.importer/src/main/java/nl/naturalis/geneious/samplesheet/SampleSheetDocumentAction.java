package nl.naturalis.geneious.samplesheet;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

public class SampleSheetDocumentAction extends DocumentAction {

  public SampleSheetDocumentAction() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    new SampleSheetSelector().show();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Samples [V2]").setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools)
        .setInMainToolbar(true).setInPopupMenu(true).setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Enriches documents using data from sample sheets";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

}