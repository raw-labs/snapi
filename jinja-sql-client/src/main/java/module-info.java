module raw.client.jinja.sql {
  requires scala.library;
  requires raw.client;
  requires jinjava;

  provides raw.client.api.CompilerServiceBuilder with
      raw.client.jinja.sql.JinjaSqlCompilerServiceBuilder;
}
