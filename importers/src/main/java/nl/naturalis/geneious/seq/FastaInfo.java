package nl.naturalis.geneious.seq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.name.NotParsableException;
import nl.naturalis.geneious.name.SequenceInfo;
import nl.naturalis.geneious.name.SequenceNameParser;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * A {@code SequenceInfo} object created from a fasta file.
 */
final class FastaInfo extends SequenceInfo {

  private final File child;

  private String name;
  private String sequence;
  private NaturalisNote note;

  FastaInfo(File mother, String name, File child) {
    super(mother);
    this.name = name;
    this.child = child;
  }

  FastaInfo(File mother, String name, String sequence) {
    super(mother);
    this.child = null;
    this.name = name;
    this.sequence = sequence;
  }

  @Override
  public DocumentType getDocumentType() {
    return DocumentType.FASTA;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void createNote() throws NotParsableException {
    note = new SequenceNameParser(name).parseName();
  }

  @Override
  public NaturalisNote getNaturalisNote() {
    return note;
  }

  /**
   * Returns the fasta-encoded nucleotide sequence.
   * 
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public String getSequence() throws IOException {
    if(sequence == null) {
      try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(child)))) {
        br.readLine(); // skip header
        StringBuilder sb = new StringBuilder(1024);
        for(String line = br.readLine(); line != null; line = br.readLine()) {
          sb.append(line);
        }
        sequence = sb.toString();
      }
    }
    return sequence;
  }

  /**
   * Returns the temporary single-sequence fasta file extracted from the original, user-selected fasta file. If fasta
   * sequence processing is done in-memory, this method returns null;
   * 
   * @return
   */
  public File getChildFile() {
    return child;
  }

}
