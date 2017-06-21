package com.microsoft.azure.documentDB.container;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.prefs.Preferences;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTableClient;

public class TableContainer {
	CloudStorageAccount cloudStorageAccount;
	CloudTableClient tableClient;
	
	String uri;
	
	public TableContainer(String uri) throws InvalidKeyException, URISyntaxException {
				
		this.uri = uri;
		
		this.cloudStorageAccount =
		        CloudStorageAccount.parse("DefaultEndpointsProtocol=https;AccountName=tsotab1;AccountKey=r3NyifG69rUKvYXCIQzGFfcLLMNsiTQ3WT9IWSVXInerzSrWDOr7HRAsyuR7tYzdEImGhfxmaI97WlFYnGo1hA==;");
		
	}
	
	public void createClient() {
		
		this.tableClient = this.cloudStorageAccount.createCloudTableClient();
				
	
	}
	
	
	public Iterable<String> listTables() {
		Iterable<String> iteration =  tableClient.listTables();
		
		System.out.println("Got Iteration");
		
		return iteration;
		
	}
	
	public void savePreferences() {
		Preferences preferences = Preferences.userRoot();

		preferences.put("azure-table-uri", uri);

	}
	
	static public void main(String [] args) {
		try {
			System.out.println("IM here a1");
			
		CloudStorageAccount cloudStorageAccount;
		CloudTableClient tableClient;
		
		cloudStorageAccount =
		        CloudStorageAccount.parse("DefaultEndpointsProtocol=https;AccountName=tsotab1;AccountKey=r3NyifG69rUKvYXCIQzGFfcLLMNsiTQ3WT9IWSVXInerzSrWDOr7HRAsyuR7tYzdEImGhfxmaI97WlFYnGo1hA==");
		System.out.println("IM here a2");

		tableClient = cloudStorageAccount.createCloudTableClient();
		System.out.println("IM here a3");
		
		for (String table : tableClient.listTables()) {
		
			System.out.println("Table: '" + table + "'");
		}
		
		System.out.println("IM here b");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
