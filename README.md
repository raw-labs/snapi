# Snapi Language

Snapi is new a data manipulation language designed to filter and combine data from many data sources in real-time.
It has a rich built-in library with ready-to-use connectors to the most common data formats, data lakes and databases.

## Table of Contents

* [Why Snapi?](#why-snapi)
* [Show me some code](#show-me-some-code)
* [How to use](#how-to-use)
* [Frequently Asked Questions](#frequently-asked-questions)
  * [How to contribute](#how-to-contribute)
  * [How is Snapi built?](#how-is-snapi-built)
  * [Where to learn more?](#where-to-learn-more)
* [Known Issues](#known-issues)
* [Roadmap](#roadmap)

## Why Snapi?

Snapi is a high-level programming language designed to simplify data manipulation.
Its syntax and structures are inspired by the [M language](https://learn.microsoft.com/en-us/powerquery-m/) as well as [SQL](https://en.wikipedia.org/wiki/SQL), combining the strengths of these into an easy-to-understand language.
We built Snapi to address the following challenges.

### Support for Complex Data

Languages such as SQL and M are designed to work with tabular data.
However, many data sources are not tabular, and require complex data types.
Transforming - flattening data - into tabular format is often a tedious and error-prone task.
Snapi supports complex data structures, typical in JSON or XML files, and provides a rich set of operations to manipulate them.

### Built-in Connectors

Snapi includes a wide set of built-in connectors.
These connectors support various data formats, data lakes, and databases,
alleviating the need for external dependencies and streamlining the pipeline set-up process.

### AI-Friendly Design

Snapi is engineered to be AI-friendly, presenting a high-level, intuitive syntax that is readily interpretable by [AI Large Language Models](https://en.wikipedia.org/wiki/Large_language_model) (LLMs),
making it a good candidate for automating code generation.

Higher-level language constructs help alleviate hallucination and enable users to better understand the generated code and assess its correctness.

### Unique Approach to Error Handling

Snapi introduces a novel approach to error handling.
This approach is designed specifically for data manipulation tasks,  allowing program execution to continue even when exceptions occur
or when data sources are inaccessible or malformed.
This resilient behavior ensures uninterrupted operation, particularly useful when dealing with unpredictable data sources.

### Understandability

With Snapi, we put strong focus on "easy-to-understand" code.
This is achieved by using a high-level syntax and by providing a rich set of built-in connectors, and well-defined operations.
Whether for AI code generation, or for graphical query builders, Snapi is designed so that operations are intuitive and have minimal syntax.
Even though we do not currently have a graphical query builder, we are developing one based on "node editor" where each node is a Snapi operation.

## Show me some code

Snapi code is easy to understand and write. Here are a few simple examples using the Snapi REPL (see [How to use](#how-to-use) for instructions on installing the Snapi REPL):

```snapi
$ snapi
> 1 + 1
2
> "Hello, world!"
Hello, world!
```

Here are some brief examples of using Snapi:

### Joining a CSV and a JSON file

Here's how to join a CSV and a JSON file using Snapi:

```snapi
let
  trips = Json.InferAndRead("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json"),
  airports = Csv.InferAndRead("https://raw-tutorial.s3.eu-west-1.amazonaws.com/airports.csv")
in
  Collection.EquiJoin(trips, airports, trip -> trip.destination, airport -> airport.IATA_FAA)
```

We can directly read JSON and CSV files and use `InferAndRead` to detect their schema.
The join uses an equality condition.

### Reading and filtering data from a MySQL table

Here's how to read and filter data from a MySQL table.
Note that the connection parameters are read from secrets, which are defined as environment variables:

```snapi
let
  airports = MySQL.InferAndRead(
    "raw", "airports",
    host = Environment.Secret("MYSQL_HOST"),
    username = Environment.Secret("MYSQL_USERNAME"),
    password = Environment.Secret("MYSQL_PASSWORD")
  )
in
  Collection.Filter(airports, x -> Nullable.IsNull(code) or x.iata_faa == code)
```

### Getting data from Wikipedia with a SparQL query

Here's how to get data from [Wikipedia API](https://en.wikipedia.org/) using a [SparQL](https://en.wikipedia.org/wiki/SPARQL) query.
The query is sent to the [Wikipedia API](https://en.wikipedia.org/api/), which returns CSV data that is then parsed by Snapi:

```snapi
let
  query = "SELECT ?item ?birthdate ?itemLabel \n" +
          "WHERE {\n" +
          "    ?item wdt:P31 wd:Q146. # Must be of a cat \n" +
          "    ?item wdt:P569 ?birthdate . # P569 : birthdate\n" +
          "    SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". } \n" +
          "}",
  data = Csv.Read(
    Http.Get(
      "https://query.wikidata.org/bigdata/namespace/wdq/sparql",
      args = [{"query", query}],
      headers = [{"Accept", "text/csv"}]
    ),
    type collection(record(item: string, birthdate: string, itemLabel: string)),
    skip = 1 // Skip first row of the CSV file (header)
  )
in
  data
```

## How to use

Snapi is currently in beta and is now available for public use.
Note that the repository is still under active development.

To get started, follow the instructions below.

### Building and running Snapi

Snapi is built using [Scala](https://www.scala-lang.org) and requires the [GraalVM](https://www.graalvm.org/).
To build the project, you will need to install:
* [sbt](https://www.scala-sbt.org/), the Scala build system;
* [GraalVM](https://www.graalvm.org/), the JDK compiler required by Snapi.

We recommend using [SDKMAN](https://sdkman.io/) to install both:
```bash
$ sdk install sbt
$ sdk install java 21-graalce
```

Once you have both installed and have the GraalVM enabled in your console, you can build all of Snapi's components and dependencies using:
```bash
$ ./rebuild.sh
```

For development and testing purposes, we also include a (very) simple command-line tool. To use it go to the launcher/ and:
```bash
$ cd launcher
$ ./run
> 1 + 1
2
```

### Using Secrets

Snapi includes the ability to read secrets from environment variables.

For development and testing purposes, you can define an environment variable called `SNAPI_<SECRET_NAME>` with the content.
For instance:
```bash
$ export SNAPI_MY_SECRET=1234
$ cd launcher
$ ./run
> Environment.Secret("MY_SECRET")
1234
```

## Frequently Asked Questions

### How to contribute?

Join our [Discord](https://discord.com/invite/AwFHYThJeh) for discussions and up-to-date news.
It is also the easiest way to get in touch with the team and report issues.

### How is Snapi built?

Snapi is built using the [Scala](https://www.scala-lang.org) programming language.

Snapi uses the [Truffle Language Implementation Framework](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/) that is part of the [GraalVM](https://www.graalvm.org/).

Another notable dependency is [Kiama](https://github.com/inkytonik/kiama), a Scala library for language processing, and [JLine](https://github.com/jline/jline3), a Java library for building command-line tools.

### Where to learn more?

Snapi is a core component of the [RAW Platform](https://www.raw-labs.com/), an end-to-end platform to create and host APIs that provide real-time data.

Visit the [RAW Labs](https://www.raw-labs.com) website to learn more how Snapi is used in production in the platform.

### Why are all packages named "raw"?

Snapi is a component from the [RAW Platform](https://www.raw-labs.com/), an end-to-end platform to create and host APIs that provide real-time data.
That's where the prefix "raw-" originates from and we kept it for consistency.

### What is the license used?

Refer to the [LICENSE](./LICENSE) file for more information.

## Known Issues

* This is an early-access release of Snapi open source. The language and build system are still in active development and are subject to change.
* There are issues with the Intellij integration; we currently recommend using BSP to import the project in Intellij.

## Roadmap

The main areas of work are:

* Open sourcing the AI code generation system. The [RAW Platform](https://www.raw-labs.com/) includes an LLM-based code generation system that generates Snapi code from natural language. This is not yet open source. [We'd love to hear from you](https://discord.com/invite/AwFHYThJeh) if this is interesting to open source.
* [Truffle Interoperability](https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/InteropMigration/). Snapi is not currently built as a Truffle interop language. This means that it cannot be used as a library in other Truffle languages. We are working on supporting Truffle interoperability with focus on Javascript and Python.
* Improvements to the REPL/CLI tool, such as autocompletion and better output and error reporting.
* Support for [GraalVM native images](https://www.graalvm.org/22.0/reference-manual/native-image/).
* Improvements to the dynamic typing system and staged compiler.

If you are interested in collaborating in any of these topics, [please get in touch](https://discord.com/invite/AwFHYThJeh).

# Development notes for contributors

## Overview of Components

Snapi is part of the [RAW Platform](https://www.raw-labs.com/), which supports languages other than Snapi.
In fact, other languages, such as Python, are also supported.
Therefore, this repository contains other components to support languages other than Snapi.
Note that each folder corresponds to a separate component.

Here is an overview of each component:
* `client`: This component defines the common interfaces and APIs for interacting with languages in the [RAW Platform](https://www.raw-labs.com/).
* `snapi-frontend`: This includes the compiler frontend, e.g. parser, type checker, pretty printers, etc for the Snapi language;
* `snapi-truffle`: This includes the implementation of the Snapi language in Truffle.
* `snapi-client`: This implements the `client` interface using the Snapi Truffle backend engine.
* `python-client`: This is an early-preview of the Python support for the [RAW Platform](https://www.raw-labs.com/). Effectively, it implements `client` interface using the [GraalPy][https://github.com/oracle/graalpython) implementation.
* `launcher`: A simple CLI implementation against the `client` interface. By default it runs with the Snapi language.
* `utils: Some common utils shared among components.

## IntelliJ

The following are some notes in setting up a development environment in IntelliJ.

Before starting:
* You will need a GraalVM distribution installed in your machine. Refer to [Building and running Snapi](#building-and-running-snapi) for more information;
* Install/Enable the Scala plugin in Intellij.

To setup the project in Intellij, we recommend cloning the repo and opening it with "Import project from existing sources".
You can choose to import as an "SBT" project, but we found that "BSP" with "SBT" also works well.
"SBT" with "Shell for importing building" is also recommended.
Reach out to us (Discord)[https://discord.com/invite/AwFHYThJeh] if you have issues setting up the project.

If Intellij prompts you to use 'scalafmt', say "Yes" as this is our code formatter.

As a contributor, you may want to add new files to the project.
You will need to follow the copyright header information, as described in [this section](#copyright-headers).

## Scala coding guidelines

For general Scala coding guidelines, refer to the [Databricks Scala Guide](https://github.com/databricks/scala-style-guide).

## Java Scala code formatting

We use [scalafmt](https://scalameta.org/scalafmt/) and [sbt-java-formatter](https://github.com/sbt/sbt-java-formatter/tree/master) to format code.

To use it manually, run:
```bash
sbt javafmtAll
sbt scalafmtAll
```

The CI checks that the code follows the expected standard.

## Copyright headers

We use [sbt-header](https://github.com/sbt/sbt-header) to add the proper license headers to the files.

To use it manually, run:
```bash
sbt headerCreateAll
sbt headerCheckAll
```

The CI checks that Java and Scala files in the compiler folder follow the proper copyright.

You may want to set Intellj to automatically add the header.
To do so:
- In Intellij settings, create a new "Copyright Profile". Call it "RAW Labs BSL". Add the following copyright template test to it:
```
Copyright $today.year RAW Labs S.A.

Use of this software is governed by the Business Source License
included in the file licenses/BSL.txt.

As of the Change Date specified in that file, in accordance with
the Business Source License, use of this software will be governed
by the Apache License, Version 2.0, included in the file
licenses/APL.txt.
```
- Then, under Copyright, make it the Default Profile.
- Then, still under Copyright, create a new Scope, choose "All", and choose the RAW Labs BSL profile.
- That's it. There's no need to configure how the Copyright is rendered in Scala or Java since Intellij's default options match ours.
