package nl.naturalis.geneious.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

import jebl.util.ProgressListener;

import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createFieldQuery;
import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createOrQuery;
import static com.biomatters.geneious.publicapi.documents.Condition.EQUAL;

import static nl.naturalis.geneious.note.NaturalisField.EXTRACT_ID;

public class QueryUtils {

  private static final DocumentField QF_EXTRACT_ID = EXTRACT_ID.createQueryField();

  private QueryUtils() {}

  public static WritableDatabaseService getTargetDatabase() {
    return ServiceUtilities.getResultsDestination().getPrimaryDatabaseRoot();
  }

  public static String getTargetDatabaseName() {
    return getTargetDatabase().getName();
  }

  public static List<AnnotatedPluginDocument> findByExtractID(Set<String> extractIds) throws DatabaseServiceException {
    if (extractIds.size() == 0) {
      return Collections.emptyList();
    }
    Query[] subqueries = extractIds.stream().map(id -> createFieldQuery(QF_EXTRACT_ID, EQUAL, id)).toArray(Query[]::new);
    Query query = createOrQuery(subqueries, Collections.emptyMap());
    return getTargetDatabase().retrieve(query, ProgressListener.EMPTY);
  }

}
