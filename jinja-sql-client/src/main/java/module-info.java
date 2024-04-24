import raw.client.jinja.sql.JinjaSqlCompilerServiceBuilder;

module raw.client.jinja {
    requires scala.library;
    requires raw.client;

    provides raw.client.api.CompilerServiceBuilder with
            JinjaSqlCompilerServiceBuilder;

}