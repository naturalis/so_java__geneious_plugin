package nl.naturalis.geneious;

/**
 * An enumeration of all sources of information used by the plugin.
 */
public enum PluginDataSource {

  /**
   * Used to signify the information was retrieved by the various segments of the sequence name (either the file name of an AB1 file or the
   * header of a fasta sequence).
   */
  SEQUENCE_NAME,
  /**
   * Used to signify the information was retrieved from a sample sheet.
   */
  SAMPLE_SHEET,
  /**
   * Used to signify the information was retrieved from CRS.
   */
  CRS,
  /**
   * Used to signify the information was retrieved from BOLD.
   */
  BOLD,
  /**
   * Used to signify the information was generated by the plugin itself (for example the document version)
   */
  AUTO

}
