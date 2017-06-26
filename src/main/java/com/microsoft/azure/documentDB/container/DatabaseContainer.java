package com.microsoft.azure.documentDB.container;

import com.microsoft.azure.documentdb.Database;

public class DatabaseContainer {

	final Database database;

	public Database getDatabase() {
		return database;
	}

	public DatabaseContainer(Database database) {

		this.database = database;

	}

	@Override
	public String toString() {

		return database.getId();

	}

}
