package com.pickmin.logic.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class CustomJsonPrettyPrinter extends DefaultPrettyPrinter {
    
    public CustomJsonPrettyPrinter() {
        super();
        this.indentObjectsWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withIndent("  "));
        this.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE.withIndent("  "));
    }

    @Override
    public DefaultPrettyPrinter createInstance() {
        return new CustomJsonPrettyPrinter();
    }

    // Verwijder spaties rond de dubbele punt, we zullen het anders formatteren
    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(": ");
    }
}
