package com.microsoft.azure.documentDB.container;

import java.util.Properties;

import com.microsoft.azure.documentdb.Database;

public class DatabaseContainer {

	final Database database;

	public Database getDatabase() {
		return database;
	}

	public DatabaseContainer(Database database) {

		this.database = database;

	}

	public Properties getProperties() {
		Properties properties = new Properties();
		
		properties.put("Created On", database.getTimestamp());
		
		return properties;
	
	}
	
	@Override
	public String toString() {

		return database.getId();

	}

}
