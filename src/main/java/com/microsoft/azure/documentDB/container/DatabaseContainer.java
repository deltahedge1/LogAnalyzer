package com.microsoft.azure.documentDB.container;

import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;

public class DatabaseContainer {
	
	interface QueryResult {
		
		String getContinuation();
		String getObjects();
		
	}
	
	final Database database;
	
	public Database getDatabase() {
		return database;
	}

	public DatabaseContainer(Database database) {
		
		this.database = database;
		
	}
	
	QueryResult getJSONObjects(DocumentClient documentClient) {
		FeedOptions queryOptions = new FeedOptions();
		queryOptions.setPageSize(100);
		queryOptions.setEnableCrossPartitionQuery(true);
		FeedResponse<Document> queryResults = documentClient.queryDocuments(database.getSelfLink(),
				"SELECT * from " + database.getId(), queryOptions);
		final StringBuilder builder = new StringBuilder();
		
		for (Document family : queryResults.getQueryIterable()) {
		
			builder.append(family.toJson());
			
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
	
	@Override
	public String toString() {
		
		return database.getId();
		
	}
	
}
