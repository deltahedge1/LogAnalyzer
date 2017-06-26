package com.microsoft.azure.documentDB.container;

import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.DocumentCollection;

public class CollectionContainer {
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
	

}
