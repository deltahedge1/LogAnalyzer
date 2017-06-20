package com.microsoft.azure.documentDB.container;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;

import org.apache.commons.lang3.StringUtils;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTableClient;

public class TableContainer {
	CloudStorageAccount cloudStorageAccount;
	CloudTableClient tableClient;
	
	String protocol;
	String accountName;
	String accountKey;
	
	public TableContainer(String protocol, String accountName, String accountKey) throws InvalidKeyException, URISyntaxException {
		
		String connectionString = 
				"DefaultEndpointsProtocol=" + protocol + ";" + 
				"AccountName=" + accountName + ";" + 
				"AccountKey=" + accountKey;
		
		System.out.println(connectionString);
		

		this.protocol = protocol;
		this.accountName = accountName;
		this.accountKey = accountKey;
		
		this.cloudStorageAccount =
		        CloudStorageAccount.parse(connectionString);
		
	}
	
	public void createClient() {
		
		this.tableClient = cloudStorageAccount.createCloudTableClient();
	
	}
	
	
	public Iterable<String> listTables() {
		
		Iterable<String> iteration =  tableClient.listTables();
		
		System.out.println("Got Iteration");
		
		return iteration;
		
	}
	
	public void savePreferences() {
		Preferences preferences = Preferences.userRoot();

		preferences.put("azure-table-protocols", protocol);
		preferences.put("azure-table-account-names", accountName);
		preferences.put("azure-table-account-keys", accountKey);

	}
	
}
