package com.alphasystem.wordladder.utils

import java.nio.file.{ Path, Paths }
import scala.collection.Searching
import scala.io.Source

object Utils {

  val DataFile: Path = Paths.get("dict", "data.csv")

  def parseWord(word: String): Map[String, List[String]] =
    toWordKeys(word).map(key => key -> List(word)).toMap

  def toWordKeys(word: String): List[String] = {
    require(word.length == 4, s"word {$word} has to be of length 4")
    (0 until 4).map(index => replaceChar(word, index)).toList
  }

  def combineMaps[K, V](
    a: Map[K, List[V]],
    b: Map[K, List[V]]
  ): Map[K, List[V]] =
    a ++ b.map { case (k, v) => k -> (v ++ a.getOrElse(k, Iterable.empty)) }

  def readDictionary: Map[String, List[String]] = {
    val source = Source.fromFile(DataFile.toFile)
    source
      .getLines()
      .map { line =>
        val values = line.split("\\|")
        val words =
          if (values.length == 2) values(1).split(",").toList
          else List()
        values(0) -> words
      }
      .toMap
  }

  def contains(word: String, words: Array[String]): Boolean = {
    words.search(word) match {
      case Searching.Found(_)          => true
      case Searching.InsertionPoint(_) => false
    }
  }

  private def replaceChar(word: String, index: Int) = {
    require(word.length == 4 && (index >= 0 || index <= 3))
    val prefix = word.substring(0, index)
    val suffix = word.substring(index + 1)
    prefix + "-" + suffix
  }
}
