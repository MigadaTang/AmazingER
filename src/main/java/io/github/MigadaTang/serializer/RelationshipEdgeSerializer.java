package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.MigadaTang.ERConnectableObj;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.Relationship;
import io.github.MigadaTang.RelationshipEdge;
import io.github.MigadaTang.common.BelongObjType;

import java.io.IOException;

public class RelationshipEdgeSerializer extends JsonSerializer<RelationshipEdge> {
    private boolean isRenderFormat;

    public RelationshipEdgeSerializer(boolean isRenderFormat) {
        this.isRenderFormat = isRenderFormat;
    }

    @Override
    public void serialize(
            RelationshipEdge edge, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();
        if (isRenderFormat) {
            jgen.writeNumberField("id", edge.getID());
            jgen.writeNumberField("relationshipID", edge.getRelationshipID());
            jgen.writeNumberField("belongObjID", edge.getConnObj().getID());
            jgen.writeNumberField("belongObjType", getBelongObjType(edge.getConnObj()).getValue());
            jgen.writeStringField("belongObjName", edge.getConnObj().getName());
            jgen.writeBooleanField("isKey", edge.getIsKey());
            jgen.writeNumberField("cardinality", edge.getCardinality().getCode());
            jgen.writeNumberField("portAtRelationship", edge.getPortAtRelationship());
            jgen.writeNumberField("portAtBelongObj", edge.getPortAtBelongObj());
        } else {
            if (edge.getConnObj() instanceof Entity) {
                jgen.writeStringField("entity", edge.getConnObj().getName());
            } else if (edge.getConnObj() instanceof Relationship) {
                jgen.writeStringField("relationship", edge.getConnObj().getName());
            }
            jgen.writeStringField("cardinality", edge.getCardinality().getValue());
            if (edge.getIsKey()) {
                jgen.writeBooleanField("isKey", edge.getIsKey());
            }
            if (edge.getPortAtRelationship() != -1) {
                jgen.writeNumberField("portAtRelationship", edge.getPortAtRelationship());
            }
            if (edge.getPortAtBelongObj() != -1)
                jgen.writeNumberField("portAtBelongObj", edge.getPortAtBelongObj());
        }
        jgen.writeEndObject();
    }

    private BelongObjType getBelongObjType(ERConnectableObj connObj) {
        if (connObj instanceof Entity) {
            return BelongObjType.ENTITY;
        } else {
            return BelongObjType.RELATIONSHIP;
        }
    }
}