package com.alphasystem.wordladder.utils

import java.io.{ File, PrintWriter }
import java.nio.file.{ Files, Paths }
import scala.collection.Factory
import scala.io.Source
import scala.jdk.StreamConverters._

object DictionaryBuilder {

  private def readRawDataFile(file: File): Iterator[String] = {
    val source = Source.fromFile(file)
    source.getLines()
  }

  private def readRawData = {
    Files
      .list(Paths.get("dict", "raw"))
      .toScala(Factory.arrayFactory)
      .map(_.toFile)
      .flatMap(readRawDataFile)
  }

  def buildDictionary: Map[String, List[String]] = {
    val rawData = readRawData
    val map = wordsMap
    rawData.map(_.trim).map(word => word -> mergeSimilarWords(word, map)).toMap
  }

  def saveDictionary(): Unit = {
    val pw = new PrintWriter(Utils.DataFile.toFile)
    buildDictionary
      .toList
      .foreach { case (key, values) =>
        pw.write(
          s"$key|${values.sorted.mkString(",")}${System.lineSeparator()}"
        )
      }
    pw.close()
  }

  private def wordsMap: Map[String, List[String]] =
    readRawData
      .foldLeft(Map.empty[String, List[String]]) { case (map, word) =>
        Utils.combineMaps(map, Utils.parseWord(word.trim))
      }

  private def mergeSimilarWords(
    word: String,
    wordsMap: Map[String, List[String]]
  ): List[String] =
    (Utils.toWordKeys(word).flatMap(wordsMap(_)).toSet - word).toList.sorted
}
