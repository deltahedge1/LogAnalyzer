package com.microsoft.azure.documentDB.container;

import java.util.Properties;

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
		queryOptions.setEnableCrossPartitionQuery(true);
		queryOptions.setPageSize(10);
		
		final FeedResponse<Document> queryResults = documentClient.queryDocuments(collection.getSelfLink(), "SELECT * from c	" ,
				queryOptions);
		final StringBuilder builder = new StringBuilder();

		int iCount = 0;
		
		for (Document document : queryResults.getQueryIterable()) {
			
			if (iCount >= 10) {
				
				break;
				
			}
			
			if (builder.length() > 0) {
				builder.append("\n");
			}
		
			builder.append(document.toJson(SerializationFormattingPolicy.Indented));

			iCount++;
			
		}

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
	
	public QueryResult getJSONObjects(DocumentClient documentClient, String continuationToken) {
		FeedOptions queryOptions = new FeedOptions();
		queryOptions.setEnableCrossPartitionQuery(true);
		queryOptions.setRequestContinuation(continuationToken);
		queryOptions.setPageSize(10);
	
		final FeedResponse<Document> queryResults = documentClient.queryDocuments(collection.getSelfLink(), "SELECT TOP 100 * from c	" ,
				queryOptions);
		final StringBuilder builder = new StringBuilder();

		int iCount = 0;
		
		for (Document document : queryResults.getQueryIterable()) {
		
			if (iCount >= 10) {
				
				break;
				
			}
		
			if (builder.length() > 0) {
				builder.append("\n");
			}
			
			iCount++;
			
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
