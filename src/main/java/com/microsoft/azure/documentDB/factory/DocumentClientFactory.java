package com.microsoft.azure.documentDB.factory;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;

public class DocumentClientFactory {
    private static final String HOST = "https://tsocdb-docdb.documents.azure.com:443/";
    private static final String MASTER_KEY = "FWKGHkoThZC6f88XJ0cg5Le052omV0iu7ErOVueEkKEMQSZl2yCKg8g5x4Xe3t95u9dky6raKltwEnIH8LQPvA==";

    private  DocumentClient documentClient;

    final String host;
    final String masterKey;
       
   public DocumentClientFactory() {
	   this(HOST, MASTER_KEY);
	   
   }
    
   public DocumentClientFactory(String host, String masterKey) {
   	
      	this.host = host;
      	this.masterKey = masterKey;
         	
   }
   
   public  DocumentClient getDocumentClient() {
        if (documentClient == null) {
            documentClient = new DocumentClient(HOST, MASTER_KEY,
                    ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        }

        return documentClient;
    }

}
