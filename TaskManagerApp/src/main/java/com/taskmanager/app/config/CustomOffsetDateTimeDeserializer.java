package com.taskmanager.app.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

public class CustomOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
	@Override
	public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
	    String date = jsonParser.getText();
	    return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

}
