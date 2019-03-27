package nl.naturalis.geneious.seq;

import java.io.File;

import com.google.common.base.Preconditions;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

import static nl.naturalis.common.io.NFiles.basename;

/**
 * Provides information about an AB1-encoded sequence.
 */
final class Ab1SequenceInfo extends SequenceInfo {

  private final String name;

  private NaturalisNote note;

  Ab1SequenceInfo(File sourceFile) {
    super(sourceFile);
    name = basename(getSourceFile());
  }

  @Override
  DocumentType getDocumentType() {
    return DocumentType.AB1;
  }

  @Override
  String getName() {
    return name;
  }

  @Override
  void createNote() throws NotParsableException {
    note = new SequenceNameParser(name).parseName();
  }

  @Override
  NaturalisNote getNaturalisNote() {
    Preconditions.checkNotNull(note, "Note not yet created");
    return note;
  }

}
