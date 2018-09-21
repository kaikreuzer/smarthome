package org.eclipse.smarthome.core.semantics.internal;

import java.io.IOException;
import java.io.InputStreamReader;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Component
public class SemanticModelManager {

    private final Logger logger = LoggerFactory.getLogger(SemanticModelManager.class);

    @Activate
    protected void activate(BundleContext context) {
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        InputStreamReader reader;
        try {
            reader = new InputStreamReader(context.getBundle().getResource("schema/smarthome.graphqls").openStream());
            TypeDefinitionRegistry typeRegistry = schemaParser.parse(reader);
            RuntimeWiring wiring = buildRuntimeWiring();
            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
            logger.info("{}", graphQLSchema);
        } catch (IOException e) {
            logger.error("Failed to create schema", e);
        }
    }

    RuntimeWiring buildRuntimeWiring() {
        SmartHomeTypeResolver typeResolver = new SmartHomeTypeResolver();
        return RuntimeWiring.newRuntimeWiring().type("Location", typeWiring -> typeWiring).build();
    }
}
