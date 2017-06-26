package com.microsoft.azure.documentDB.container;

import java.util.Properties;
import java.util.UUID;

import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentCollection;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;
import com.microsoft.azure.documentdb.SerializationFormattingPolicy;



public class CollectionContainer {
	public interface QueryResult {

		String getContinuation();

		String getObjects();

	}
	
	final Database database;
	final DocumentCollection collection;

	public DocumentCollection getCollection() {
		return collection;
	}

	public Database getDatabase() {
		return database;
	}

	public CollectionContainer(Database database, DocumentCollection collection) {

		this.database = database;
		this.collection = collection;
	}

	@Override
	public String toString() {

		return collection.getId();

	}

	public QueryResult getJSONObjects(DocumentClient documentClient) {
		FeedOptions queryOptions = new FeedOptions();
		queryOptions.setMaxBufferedItemCount(100);
		queryOptions.setEnableCrossPartitionQuery(true);

		final FeedResponse<Document> queryResults = documentClient.queryDocuments(collection.getSelfLink(), "SELECT TOP 100 * from c	" ,
				queryOptions);
		final StringBuilder builder = new StringBuilder();

		for (Document document : queryResults.getQueryIterable()) {
			
			if (builder.length() > 0) {
				builder.append("\n");
			}
			builder.append(document.toJson(SerializationFormattingPolicy.Indented));

		}

		System.out.println(builder.toString());

		return new QueryResult() {

			@Override
			public String getContinuation() {
				return queryResults.getResponseContinuation();
			}

			@Override
			public String getObjects() {
				return builder.toString();
			}

		};

	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		
		properties.put("Created On", collection.getTimestamp());

		return properties;
	
	}
	

}
