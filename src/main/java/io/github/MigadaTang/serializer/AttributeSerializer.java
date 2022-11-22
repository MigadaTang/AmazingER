package io.github.MigadaTang.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.MigadaTang.Attribute;

import java.io.IOException;

public class AttributeSerializer extends JsonSerializer<Attribute> {
    private boolean isRenderFormat;

    public AttributeSerializer(boolean isRenderFormat) {
        this.isRenderFormat = isRenderFormat;
    }

    @Override
    public void serialize(
            Attribute attribute, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();


        if (isRenderFormat) {
            jgen.writeNumberField("id", attribute.getID());
        }
        jgen.writeStringField("name", attribute.getName());

        if (isRenderFormat) {
            jgen.writeNumberField("dataType", attribute.getDataType().ordinal());
        } else {
            jgen.writeStringField("dataType", attribute.getDataType().getValue());
        }
        jgen.writeBooleanField("isPrimary", attribute.getIsPrimary());
        jgen.writeBooleanField("nullable", attribute.getNullable());

        jgen.writeNumberField("aimPort", attribute.getAimPort());
        if (attribute.getLayoutInfo() != null) {
            jgen.writeObjectField("layoutInfo", attribute.getLayoutInfo());
        }

        jgen.writeEndObject();
    }
}