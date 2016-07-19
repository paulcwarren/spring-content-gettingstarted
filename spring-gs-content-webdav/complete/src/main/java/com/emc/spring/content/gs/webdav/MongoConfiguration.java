package com.emc.spring.content.gs.webdav;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories
public class MongoConfiguration extends AbstractMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "spring-webdav";
	}

	@Override
	public Mongo mongo() throws Exception {
        return new MongoClient();
	}

}
