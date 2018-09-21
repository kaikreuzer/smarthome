package org.eclipse.smarthome.core.semantics.internal;

import org.eclipse.smarthome.core.semantics.model.Equipment;
import org.eclipse.smarthome.core.semantics.model.Location;
import org.eclipse.smarthome.core.semantics.model.Point;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

public class SmartHomeTypeResolver implements TypeResolver {

    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment env) {
        Object javaObject = env.getObject();
        if (javaObject instanceof Location) {
            return env.getSchema().getObjectType("LocationType");
        } else if (javaObject instanceof Equipment) {
            return env.getSchema().getObjectType("EquipmentType");
        } else if (javaObject instanceof Point) {
            return env.getSchema().getObjectType("PointType");
        } else {
            return null;
        }
    }

}
