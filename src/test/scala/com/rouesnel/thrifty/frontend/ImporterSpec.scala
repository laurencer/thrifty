package com.rouesnel.thrifty.frontend

import java.io.{File, FileOutputStream}

import org.specs2.mutable.Specification

class ImporterSpec extends Specification {
  "fileImporter" should {


    "finds files on the path" in {
      val testFolder = TempDirectory.create(None)
      val folder1 = new File(testFolder, "f1")
      val folder2 = new File(testFolder, "f2")
      folder1.mkdir()
      folder2.mkdir()

      val f = new FileOutputStream(new File(folder2, "a.thrift"))
      f.write("hello".getBytes("UTF-8"))
      f.close()

      val importer = Importer(Seq(folder1.getAbsolutePath, folder2.getAbsolutePath))
      val c = importer.apply("a.thrift")
      c.isDefined must beTrue
      c.get.data must beEqualTo("hello")
      c.get.thriftFilename.get must beEqualTo("a.thrift")
    }

    "follows relative links correctly" in {
      val testFolder = TempDirectory.create(None)
      val folder1 = new File(testFolder, "f1")
      val folder2 = new File(testFolder, "f2")
      folder1.mkdir()
      folder2.mkdir()

      val f = new FileOutputStream(new File(folder2, "a.thrift"))
      f.write("hello".getBytes("UTF-8"))
      f.close()

      val importer = Importer(Seq(folder1.getAbsolutePath))
      val c = importer.apply("../f2/a.thrift")
      c.isDefined must beTrue
      c.get.data must beEqualTo("hello")
      (c.get.importer.canonicalPaths contains folder2.getCanonicalPath) must beTrue
      c.get.thriftFilename.get must beEqualTo("a.thrift")
    }

    "reads utf-8 data correctly" in {
      val testFolder = TempDirectory.create(None)
      val folder1 = new File(testFolder, "f1")
      val folder2 = new File(testFolder, "f2")
      folder1.mkdir()
      folder2.mkdir()

      val f = new FileOutputStream(new File(folder2, "a.thrift"))
      f.write("你好".getBytes("UTF-8"))
      f.close()

      val importer = Importer(Seq(folder1.getAbsolutePath, folder2.getAbsolutePath))
      val c = importer.apply("a.thrift")
      c.isDefined must beTrue
      c.get.data must beEqualTo("你好")
      c.get.thriftFilename.get must beEqualTo("a.thrift")
    }

    "returns resolved path correctly" in {
      val testFolder = TempDirectory.create(None)
      val folder1 = new File(testFolder, "f1")
      val folder2 = new File(testFolder, "f2")
      folder1.mkdir()
      folder2.mkdir()

      val f = new FileOutputStream(new File(folder2, "a.thrift"))
      f.write("hello".getBytes("UTF-8"))
      f.close()

      val importer = Importer(Seq(folder1.getAbsolutePath, folder2.getAbsolutePath))

      importer.getResolvedPath("a.thrift") must beEqualTo(Some(new File(testFolder, "f2/a.thrift").getCanonicalPath))
      importer.getResolvedPath("b.thrift") must beEqualTo(None)
      importer.getResolvedPath("f2/a.thrift") must beEqualTo(None)
    }
  }
}

object TempDirectory {
  /**
    * Create a new temporary directory in the current directory,
    * which will be deleted upon the exit of the VM.
    *
    * @return File representing the directory
    */
  def create(dir: Option[File], deleteAtExit: Boolean = true): File = {
    val file = dir match {
      case Some(d) => File.createTempFile("temp", "dir", d)
      case None => File.createTempFile("temp", "dir")
    }
    file.delete()
    file.mkdir()

    if (deleteAtExit) file.deleteOnExit()

    file
  }
}
