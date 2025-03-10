package nl.naturalis.geneious;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Contains and provides version-related information showin in the <i>Tools -&gt; Preferences</i> panel.
 *
 * @author Ayco Holleman
 */
public class PluginInfo {

  private static PluginInfo instance;

  /**
   * Returns the one and only instance of this class.
   * 
   * @return
   */
  public static PluginInfo getInstance() {
    if (instance == null) {
      instance = new PluginInfo();
    }
    return instance;
  }

  private final String version;
  private final String buildDate;
  private final String gitBranch;
  private final String gitCommit;
  private final String commitCount;

  private PluginInfo() {
    InputStream is = getClass().getResourceAsStream("/git.properties");
    if (is == null) {
      // Can only happen during development. This file is generated in the "target" folder by the git commit id plugin.
      throw new RuntimeException("Yo, run Maven -> Update Project...");
    }
    Properties props = new Properties();
    try {
      props.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    version = props.getProperty("git.closest.tag.name");
    buildDate = props.getProperty("git.build.time");
    gitBranch = props.getProperty("git.branch");
    gitCommit = props.getProperty("git.commit.id.abbrev");
    commitCount = props.getProperty("git.total.commit.count");
  }

  /**
   * Returns the version of the plugin.
   * 
   * @return
   */
  public String getVersion() {
    return version;
  }

  /**
   * Returns the build date of the plugin.
   * 
   * @return
   */
  public String getBuildDate() {
    return buildDate;
  }

  /**
   * Returns the git branch on which the release was built.
   * 
   * @return
   */
  public String getGitBranch() {
    return gitBranch;
  }

  /**
   * Returns the commit hash.
   * 
   * @return
   */
  public String getGitCommit() {
    return gitCommit;
  }

  /**
   * Returns the total number of git commits.
   * 
   * @return
   */
  public String getCommitCount() {
    return commitCount;
  }

}
