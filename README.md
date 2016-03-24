# Thrifty

> A library for parsing Thrift IDLs with minimal dependencies.

[![Build Status](https://travis-ci.org/laurencer/thrifty.svg?branch=master)](https://travis-ci.org/laurencer/thrifty)

This project is essentially a fork of [Scrooge Generator](https://github.com/twitter/scrooge).
The reason for copying the code instead of using it as a library/dependency is
to ensure that the project can be built for multiple versions of Scala and
easily included in other tools/projects.

## Getting Started

To get started using the library - include it as a dependency in your `build.sbt`:

```scala

libraryDependencies += "com.rouesnel" %% "thrifty" % "0.1.0"

```

## Quick Start

> This example can be run by copying and pasting the example into `sbt console`.

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

## Release Guide

Releases are automatically handled by the TravisCI build using an approach
similar to [what the Guardian use](https://www.theguardian.com/info/developer-blog/2014/sep/16/shipping-from-github-to-maven-central-and-s3-using-travis-ci).

On a successful build on master, [sbt-sonatype](https://github.com/xerial/sbt-sonatype)
is used to publish a signed build to [Sonatype OSS](https://oss.sonatype.org/),
which is then released (using the same plugin). The releases are signed with a PGP key (included in the repository in an
encrypted form).

### Credentials

Credentials are included in the repository in the following encrypted files:

- `credentials.sbt.enc`: an encrypted `sbt` file that contains credentials for
  uploading to Sonatype OSS and the passphrase for the PGP keyring.
- `pubring.gpg.enc`: an encrypted gpg public keyring.
- `secring.gpg.enc`: an encrypted gpg private keyring.

During the build these files are decrypted using the `ENCRYPTION_PASSWORD` that
has been encrypted in the `.travis.yml` file.
