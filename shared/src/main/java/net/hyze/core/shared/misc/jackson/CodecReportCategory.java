package net.hyze.core.shared.misc.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.hyze.core.shared.misc.report.ReportCategory;
import net.hyze.core.shared.misc.report.ReportManager;
import java.io.IOException;

public class CodecReportCategory {

    public static class Serializer extends StdSerializer<ReportCategory> {

        private static final long serialVersionUID = -7866073763862876852L;

        protected Serializer() {
            super(ReportCategory.class);
        }

        @Override
        public void serialize(ReportCategory value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.getName());
        }

    }

    public static class Deserializer extends StdDeserializer<ReportCategory> {

        private static final long serialVersionUID = -8574261351719014834L;

        protected Deserializer() {
            super(ReportCategory.class);
        }

        @Override
        public ReportCategory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return ReportManager.getCategory(p.getValueAsString());
        }

    }

}
