package nl.naturalis.lims2.oaipmh;

import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.checkMetadataPrefix;
import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.connect;
import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.disconnect;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.createResponseSkeleton;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import nl.naturalis.lims2.oaipmh.jaxb.Geneious;
import nl.naturalis.oaipmh.api.BadResumptionTokenError;
import nl.naturalis.oaipmh.api.NoRecordsMatchError;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;
import nl.naturalis.oaipmh.api.util.ResumptionToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2.ResumptionTokenType;

public abstract class ListRecordsHandler {

	private static final Logger logger = LogManager.getLogger(ListRecordsHandler.class);

	protected final ConfigObject config;
	protected final OAIPMHRequest request;

	protected List<IAnnotatedDocumentPreFilter> preFilters;
	protected List<IAnnotatedDocumentPostFilter> postFilters;

	public ListRecordsHandler(ConfigObject config, OAIPMHRequest request)
	{
		this.request = request;
		this.config = config;
		preFilters = new ArrayList<>(4);
		preFilters.add(new CommonAnnotatedDocumentPreFilter());
		postFilters = new ArrayList<>(4);
		postFilters.add(new CommonAnnotatedDocumentPostFilter());
	}

	public OAIPMHtype handleRequest() throws RepositoryException, OAIPMHException
	{
		checkMetadataPrefix(request);
		preFilters.addAll(getAnnotatedDocumentPreFilters());
		postFilters.addAll(getAnnotatedDocumentPostFilters());
		List<AnnotatedDocument> records = getAnnotatedDocuments();
		if (records.size() == 0) {
			throw new OAIPMHException(new NoRecordsMatchError());
		}
		OAIPMHtype root = createResponseSkeleton(request);
		ListRecordsType listRecords = oaiFactory.createListRecordsType();
		root.setListRecords(listRecords);
		int pageSize = getPageSize();
		int offset = request.getPage() * pageSize;
		if (offset >= records.size()) {
			String msg = "Bad resumption token";
			logger.error(msg);
			throw new OAIPMHException(new BadResumptionTokenError(msg));
		}
		int last = Math.min(records.size(), offset + pageSize);
		logResultSetInfo(records.size());
		for (int i = offset; i < last; ++i) {
			addRecord(records.get(i), listRecords);
		}
		if (last < records.size()) {
			addResumptionToken(listRecords, records.size(), offset);
		}
		return root;
	}

	protected abstract List<IAnnotatedDocumentPreFilter> getAnnotatedDocumentPreFilters();

	protected abstract List<IAnnotatedDocumentPostFilter> getAnnotatedDocumentPostFilters();

	protected abstract void setMetadata(Geneious geneious, AnnotatedDocument ad);

	protected abstract int getPageSize();

	protected String getSQLQuery()
	{
		StringBuilder sb = new StringBuilder(1000);
		sb.append("SELECT id,folder_id,UNIX_TIMESTAMP(modified) AS modified,\n");
		sb.append("       urn,document_xml,plugin_document_xml,reference_count\n");
		sb.append("  FROM annotated_document\n");
		sb.append(" WHERE reference_count=0");
		if (request.getFrom() != null) {
			/*
			 * Column "modified" contains the number of seconds since 01-01-1970
			 * while Date.getTime() returns the number of milliseconds since
			 * 01-01-1970.
			 */
			sb.append("\n AND modified >= ").append(getSeconds(request.getFrom()));
		}
		if (request.getUntil() != null) {
			sb.append("\n AND modified <= ").append(getSeconds(request.getUntil()));
		}
		return sb.toString();
	}

	private List<AnnotatedDocument> getAnnotatedDocuments() throws RepositoryException
	{
		AnnotatedDocumentFactory factory = new AnnotatedDocumentFactory();
		List<AnnotatedDocument> records = new ArrayList<>();
		String sql = getSQLQuery();
		Connection conn = null;
		try {
			conn = connect(config);
			Statement stmt = conn.createStatement();
			logger.debug("Executing query:\n" + sql);
			ResultSet rs = stmt.executeQuery(sql.toString());
			LOOP: while (rs.next()) {
				if (logger.isDebugEnabled())
					logger.debug("Processing annotated_document record (id={})", rs.getInt("id"));
				for (IAnnotatedDocumentPreFilter preFilter : preFilters) {
					if (!preFilter.accept(rs)) {
						continue LOOP;
					}
				}
				AnnotatedDocument record = factory.create(rs);
				for (IAnnotatedDocumentPostFilter postFilter : postFilters) {
					if (!postFilter.accept(record)) {
						continue LOOP;
					}
				}
				records.add(record);
			}
		}
		catch (SQLException e) {
			throw new RepositoryException("Error while executing query", e);
		}
		finally {
			disconnect(conn);
		}
		Collections.sort(records, new Comparator<AnnotatedDocument>() {
			@Override
			public int compare(AnnotatedDocument o1, AnnotatedDocument o2)
			{
				return (int) (o1.getModified() - o2.getModified());
			}
		});
		return records;
	}

	private void addResumptionToken(ListRecordsType listRecords, int numRecords, int offset)
	{
		ResumptionTokenType resumptionToken = oaiFactory.createResumptionTokenType();
		listRecords.setResumptionToken(resumptionToken);
		resumptionToken.setCompleteListSize(BigInteger.valueOf(numRecords));
		resumptionToken.setCursor(BigInteger.valueOf(offset));
		ResumptionToken tokenGenerator = new ResumptionToken();
		String token = tokenGenerator.compose(request);
		resumptionToken.setValue(token);
	}

	private void logResultSetInfo(int resultSetSize)
	{
		int pageSize = getPageSize();
		int offset = request.getPage() * pageSize;
		int recordsToGo = resultSetSize - offset - pageSize;
		int requestsToGo = (int) Math.ceil(recordsToGo / pageSize);
		logger.info("Records satisfying request: " + resultSetSize);
		logger.debug("Records served per request: " + pageSize);
		logger.debug("Remaining records: " + recordsToGo);
		String fmt = "%s more request%s needed for full harvest";
		String plural = requestsToGo == 1 ? "" : "s";
		logger.info(String.format(fmt, requestsToGo, plural));
	}

	private void addRecord(AnnotatedDocument ad, ListRecordsType listRecords)
	{
		RecordType record = oaiFactory.createRecordType();
		listRecords.getRecord().add(record);
		record.setHeader(createHeader(ad));
		record.setMetadata(createMetadata(ad));
	}

	private static HeaderType createHeader(AnnotatedDocument ad)
	{
		HeaderType header = oaiFactory.createHeaderType();
		header.setIdentifier(String.valueOf(ad.getId()));
		long modified = 1000L * ad.getModified();
		header.setDatestamp(dateTimeFormatter.format(new Date(modified)));
		return header;
	}

	private MetadataType createMetadata(AnnotatedDocument ad)
	{
		MetadataType metadata = oaiFactory.createMetadataType();
		Geneious geneious = new Geneious();
		metadata.setAny(geneious);
		setMetadata(geneious, ad);
		return metadata;
	}

	private static long getSeconds(Date date)
	{
		return (long) Math.floor(date.getTime() / 1000);
	}

}