package fr.baptouk.pokerixe.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.*;

@Configuration
public class MongoConfiguration {

    @Value("${MONGO_URI:mongodb://localhost:27017/pokerixe}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory(MongoClient mongoClient) {
        String database = new ConnectionString(mongoUri).getDatabase();
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory,
                                       MappingMongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }

    /*
     * Escape les "." dans les clés de Map
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory mongoDbFactory,
            MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext) {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);

        converter.setMapKeyDotReplacement("_");
        converter.afterPropertiesSet();

        return converter;
    }
}