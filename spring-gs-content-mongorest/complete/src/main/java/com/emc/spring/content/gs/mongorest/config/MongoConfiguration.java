package com.emc.spring.content.gs.mongorest.config;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

	private static final Log log = LogFactory.getLog(MongoConfiguration.class);
	
	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}
	
	@Override
	protected String getDatabaseName() {
		return "spring-content";
	}

	@Override
	public MongoDbFactory mongoDbFactory() throws Exception {
		
		if (System.getenv("spring_gs_content_mongo_host") != null) {
	    	String host = System.getenv("spring_gs_content_mongo_host");
	    	String port = System.getenv("spring_gs_content_mongo_port");
	    	String username = System.getenv("spring_gs_content_mongo_username");
	    	String password = System.getenv("spring_gs_content_mongo_password");

	    	log.info(String.format("Connecting to %s:%s", host, port));

			 // Set credentials      
		    MongoCredential credential = MongoCredential.createCredential(username, getDatabaseName(), password.toCharArray());
		    ServerAddress serverAddress = new ServerAddress(host, Integer.parseInt(port));
	
		    // Mongo Client
		    MongoClient mongoClient = new MongoClient(serverAddress,Arrays.asList(credential)); 
	
		    // Mongo DB Factory
		    return new SimpleMongoDbFactory(mongoClient, getDatabaseName());
		}
		return super.mongoDbFactory();
	}

	@Override
	public Mongo mongo() throws Exception {
    	log.info("Connecting to localhost");
        return new MongoClient();
	}
}
