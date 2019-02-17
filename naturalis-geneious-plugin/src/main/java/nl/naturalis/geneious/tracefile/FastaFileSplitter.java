package nl.naturalis.geneious.tracefile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.base.NStringUtils;
import nl.naturalis.common.io.NFileUtils;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.RuntimeSettings;

import static java.nio.charset.StandardCharsets.UTF_8;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

class FastaFileSplitter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(FastaFileSplitter.class);
  private static final byte[] NEWLINE = IOUtils.LINE_SEPARATOR.getBytes(UTF_8);

  private final File tmpDir;

  private int fileNo = 0;

  FastaFileSplitter() {
    tmpDir = NFileUtils.newFile(RuntimeSettings.WORK_DIR, "tmp", "fasta", System.currentTimeMillis());
  }

  List<File> split(File motherFile) throws IOException {
    List<File> files = new ArrayList<>();
    StringBuilder sequence = new StringBuilder(512);
    try (BufferedReader br = new BufferedReader(new FileReader(motherFile))) {
      String header = br.readLine();
      String chunk;
      OUTER_LOOP: do {
        chunk = br.readLine();
        if (!isStartOfSequence(chunk)) {
          guiLogger.error("Corrupt file: %s. Expected start of nucleotide sequence below \"%s\"", motherFile, header);
          break OUTER_LOOP;
        }
        INNER_LOOP: while (true) {
          sequence.append(chunk);
          chunk = br.readLine();
          if (chunk == null) {
            saveToNewFile(files, header, sequence.toString());
            break OUTER_LOOP;
          }
          if (chunk.startsWith(">")) {
            if (chunk.length() == 1) {
              /*
               * Only bare-bones validation here, so from here on String.substring(1) won't throw an exception. True validation of the name
               * will happen downstream
               */
              guiLogger.error("Corrupt file: %s. Line may not contain a single '>' character", motherFile);
              break OUTER_LOOP;
            }
            saveToNewFile(files, header, sequence.toString());
            header = chunk;
            sequence.setLength(0);
            break INNER_LOOP;
          }

        }
      } while (true);
    }
    return files;
  }

  /**
   * Returns the directory into which the splitter has written the single-sequence fasta files.
   * 
   * @return
   */
  File getFastaTempDirectory() {
    return tmpDir;
  }

  /**
   * Returns the total number of single-sequence fasta files created by the splitter.
   * 
   * @return
   */
  int getSplitCount() {
    return fileNo;
  }

  private void saveToNewFile(List<File> children, String header, String sequence) throws IOException {
    File f = getTempFile();
    guiLogger.debugf(() -> format("Creating intermediate fasta file \"%s\" to %s", header, f.getAbsolutePath()));
    try (BufferedOutputStream bos = open(f)) {
      bos.write(header.getBytes(UTF_8));
      bos.write(NEWLINE);
      bos.write(sequence.getBytes(UTF_8));
    }
    children.add(f);
  }

  private File getTempFile() {
    return NFileUtils.newFile(tmpDir, NStringUtils.zpad(++fileNo, 4, ".fasta"));
  }

  private static boolean isStartOfSequence(String chunk) {
    return chunk != null && !chunk.startsWith(">") && !StringUtils.isBlank(chunk);
  }

  private static BufferedOutputStream open(File f) throws IOException {
    return new BufferedOutputStream(FileUtils.openOutputStream(f), 4096);
  }

}
