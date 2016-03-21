# Thrifty

> A library for parsing Thrift IDLs with minimal dependencies.

This project is essentially a fork of [Scrooge Generator](https://github.com/twitter/scrooge).
The reason for copying the code instead of using it as a library/dependency is
to ensure that the project can be built for multiple versions of Scala and
easily included in other tools/projects.

## Getting Started

```scala

import java.io.File
import com.rouesnel.thrifty.frontend._

// Directory containing all of the thrift files.
val thriftDirectory = new File("src/test/resources")

// Thrift file to parse.
val thriftFile = new File(thriftDirectory, "Example.thrift")

// The importer is used to resolve references/links to other Thrift files.
val importer: Importer = Importer(directory)

// Parser that can be re-used.
val parser: ThriftParser = new ThriftParser(Importer(thriftDirectory))

// Parsed document.
val parsed: ResolvedDocument = TypeResolver()(parser.parseFile(thriftFile.getAbsolutePath))

// Finding a particular struct
val Some(myStruct) = parsed.document.structs.find(_.sid.name == "MyStruct")

// Print all the field names and types.
myStruct.fields.foreach(field => {
  println(field.docstring.getOrElse("No comment provided"))
  println(s"${field.index}: ${field.sid.name}")
  println(s"Type: ${field.fieldType}")
  println()
})

```

> NB. this example can be run by using the `console` sbt command and by copying
> and pasting the example.
