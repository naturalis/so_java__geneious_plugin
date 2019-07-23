package nl.naturalis.geneious;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.CONTIG;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;

import java.util.Comparator;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.note.Note;
import nl.naturalis.geneious.util.DocumentUtils;

/**
 * A wrapper around the Geneious-native {@code AnnotatedPluginDocument} class with all of its Naturalis-specific
 * annotations pre-fetched into a {@link NaturalisNote} instance. A {@code StoredDocument} has presumably been retrieved
 * through some database query and upon instantiation is exactly like the database record. As the operation proceeds the
 * {@code NaturalisNote} instance may get updated, and, if so, will be saved back to the document.
 */
public class StoredDocument {

  /**
   * Compares 2 {@code StoredDocument} instances using their URN.
   */
  public static Comparator<StoredDocument> URN_COMPARATOR = (o1, o2) -> {
    return o1.doc.getURN().toString().compareTo(o2.doc.getURN().toString());
  };

  public static Comparator<StoredDocument> IDENTITY_COMPARATOR = (o1, o2) -> {
    return System.identityHashCode(o1) - System.identityHashCode(o2);
  };

  private final AnnotatedPluginDocument doc;
  private final NaturalisNote note;
  private final DocumentType type;

  private DocumentNotes notes;

  /**
   * Creates a wrapper around the provided document.
   * 
   * @param document
   */
  public StoredDocument(AnnotatedPluginDocument document) {
    this(document, new NaturalisNote(document));
  }

  /**
   * Creates a wrapper around the provided document with the annotations explicitly provided through the {@code note}
   * argument. Only used to created yet-to-be-saved dummy documents.
   * 
   * @param doc
   * @param note
   */
  public StoredDocument(AnnotatedPluginDocument doc, NaturalisNote note) {
    this.doc = doc;
    this.note = note;
    this.type = DocumentUtils.getDocumentType(doc, note);
  }

  /**
   * Returns the Geneious-native document instance.
   * 
   * @return
   */
  @JsonIgnore
  public AnnotatedPluginDocument getGeneiousDocument() {
    return doc;
  }

  /**
   * Returns the name of the document (as displayed in the left-most column in the Geneious GUI).
   * 
   * @return
   */
  public String getName() {
    return doc.getName();
  }

  /**
   * Returns the full path for the document.
   * 
   * @return
   */
  public String getLocation() {
    String folder;
    if(doc.getDatabase() == null) {
      folder = "<folder unknown>";
    } else {
      folder = doc.getDatabase().getFullPath();
    }
    return folder + System.getProperty("file.separator") + doc.getName();
  }

  /**
   * Returns the annotations associated with the Geneious document.
   * 
   * @return
   */
  public NaturalisNote getNaturalisNote() {
    return note;
  }

  /**
   * Adds the annotations present in the provided note to this document, but does not save the document to the database.
   */
  public void attach(Note note) {
    note.copyTo(getDocumentNotes());
  }

  /**
   * Adds the annotations present in the provided note to this document, but does not save the document to the database.
   * Existing annotations will be overwritten. Returns {@code true} if the document actually changed as a consequence,
   * {@code false} otherwise.
   */
  public boolean attach(NaturalisNote note) {
    return note.copyTo(this.note);
  }

  /**
   * Adds the annotations present in the provided note to this document, but does not save the document to the database.
   * Returns {@code true} if the document actually changed as a consequence, {@code false} otherwise. If {@code overwrite}
   * is true, existing annotations will be overwritten, otherwise they are left alone.
   */
  public boolean attach(NaturalisNote note, boolean overwrite) {
    return overwrite ? note.copyTo(this.note) : note.mergeInto(this.note);
  }

  /**
   * Saves the annotations to the database.
   */
  public void saveAnnotations() {
    DocumentNotes notes = getDocumentNotes();
    note.copyTo(notes);
    notes.saveNotes(true);
  }

  /**
   * Returns the document type (AB1, FASTA or DUMMY).
   * 
   * @return
   */
  public DocumentType getType() {
    return type;
  }

  /**
   * Whether or not this instance wraps an AB1 document.
   * 
   * @return
   */
  @JsonIgnore
  public boolean isAB1() {
    return type == AB1;
  }

  /**
   * Whether or not this instance wraps a FASTA document.
   * 
   * @return
   */
  @JsonIgnore
  public boolean isFasta() {
    return type == FASTA;
  }

  /**
   * Whether or not this instance wraps a CONTIG document.
   * 
   * @return
   */
  @JsonIgnore
  public boolean isContig() {
    return type == CONTIG;
  }

  /**
   * Whether or not this instance wraps a DUMMY document.
   * 
   * @return
   */
  @JsonIgnore
  public boolean isDummy() {
    return type == DUMMY;
  }

  private DocumentNotes getDocumentNotes() {
    if(notes == null) {
      notes = doc.getDocumentNotes(true);
    }
    return notes;
  }

}