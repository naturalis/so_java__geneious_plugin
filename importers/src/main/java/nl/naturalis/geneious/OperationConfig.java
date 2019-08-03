package nl.naturalis.geneious;

import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

/**
 * Base class for objects capturing the user input and other configuration data for one of the plugin's operations. {@code OperationConfig}
 * objects are generated from {@link OperationOptions} objects.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class OperationConfig {

  private List<AnnotatedPluginDocument> selectedDocuments;
  private WritableDatabaseService targetFolder;

  public OperationConfig() {
    this.selectedDocuments = DocumentUtilities.getSelectedDocuments();
    this.targetFolder = ServiceUtilities.getResultsDestination();
  }

  /**
   * Returns the folder selected by the user just before the start of the operation. We must freeze this value immediately because the
   * operation runs in a separate thread, so the user can click around while the operation is in progress.
   * 
   * @return
   */
  public WritableDatabaseService getTargetFolder() {
    return targetFolder;
  }

  /**
   * Sets the folder selected by the user.
   * 
   * @param targetFolder
   */
  public void setTargetFolder(WritableDatabaseService targetFolder) {
    this.targetFolder = targetFolder;
  }

  /**
   * Returns the database containing the target folder.
   * 
   * @return
   */
  public WritableDatabaseService getTargetDatabase() {
    return getTargetFolder().getPrimaryDatabaseRoot();
  }

  /**
   * Returns name of the database that contains the folder that is currently selected by the user, or "&lt;no database selected&gt;" if no
   * folder has been selected yet.
   * 
   * @return
   */
  public String getTargetDatabaseName() {
    if (getTargetDatabase() == null) {
      return "<no database selected>";
    }
    return getTargetDatabase().getFolderName();
  }

  /**
   * Returns the documents selected by the user.
   * 
   * @return
   */
  public List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

  /**
   * Sets the documents selected by the user.
   * 
   * @param selectedDocuments
   */
  public void setSelectedDocuments(List<AnnotatedPluginDocument> selectedDocuments) {
    this.selectedDocuments = selectedDocuments;
  }

  /**
   * Returns the name of the operation configured by this configuration object.
   * 
   * @return
   */
  public abstract String getOperationName();

}
