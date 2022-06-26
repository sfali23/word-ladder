package com.alphasystem.wordladder.utils

import com.alphasystem.wordladder.Vertex

import scala.annotation.tailrec

class GraphBuilder(dictionary: Map[String, List[String]]) {

  /** Build a graph for the given word.
    *
    * @param word
    *   given starting word
    * @return
    *   a tuple of number of edges and sequence of vertices
    */
  def buildGraph(
    word: String,
    wordsToIgnore: List[String] = Nil
  ): (Int, IndexedSeq[Vertex]) = {
    val graph =
      buildGraph(
        List(word),
        0,
        Map.empty[String, Vertex],
        Map.empty[String, Int],
        wordsToIgnore
      )
        .values
        .toIndexedSeq
        .sortWith { (v1, v2) => v1.index <= v2.index }

    (graph.size, graph)
  }

  /** Recursively explore list of given words.
    *
    * @param words
    *   list of words to explore
    * @param initialIndex
    *   initial index of current iteration
    * @param edges
    *   map of word to vertex
    * @param wordsAlreadySeen
    *   map of word to corresponding index of words already seen
    * @return
    *   map of word to vertex
    */
  @tailrec
  private def buildGraph(
    words: List[String],
    initialIndex: Int,
    edges: Map[String, Vertex],
    wordsAlreadySeen: Map[String, Int],
    wordsToIgnore: List[String]
  ): Map[String, Vertex] = {
    words match {
      case word :: tail =>
        val (nextIndex, vertex, childWords, updatedWordsAlreadySeen) =
          buildGraph(word, initialIndex, wordsAlreadySeen, wordsToIgnore)

        val updatedEdges = edges + (word -> vertex)
        buildGraph(
          tail ::: childWords,
          nextIndex,
          updatedEdges,
          updatedWordsAlreadySeen,
          wordsToIgnore
        )

      case Nil => edges
    }
  }

  /** Creates [[Vertex]] for given `word`.
    *
    * @param word
    *   word to explore
    * @param initialIndex
    *   initial index
    * @param wordsAlreadySeen
    *   map of word to corresponding index of words already seen
    * @return
    *   a tuple of next index, vertex of given word, child words to explore,
    *   updated map of word to corresponding index of words already seen
    */
  private[utils] def buildGraph(
    word: String,
    initialIndex: Int,
    wordsAlreadySeen: Map[String, Int],
    wordsToIgnore: List[String]
  ): (Int, Vertex, List[String], Map[String, Int]) = {
    val (nextIndex, vertex, updatedWordsAlreadySeen) =
      wordsAlreadySeen.get(word) match {
        case Some(value) =>
          (initialIndex, Vertex(value, word), wordsAlreadySeen)
        case None =>
          (
            initialIndex + 1,
            Vertex(initialIndex, word),
            wordsAlreadySeen + (word -> initialIndex)
          )
      }

    val (finalIndex, childWords, edges, finalWordsAlreadySeen) =
      dictionary.get(word) match {
        case Some(words) =>
          words
            .diff(wordsToIgnore)
            .foldLeft(
              (
                nextIndex,
                List.empty[String],
                List.empty[Vertex],
                updatedWordsAlreadySeen
              )
            ) { case ((index, wordsToExplore, al, map), word) =>
              map.get(word) match {
                case Some(value) =>
                  (
                    index,
                    wordsToExplore,
                    al :+ Vertex(value, word),
                    map
                  )
                case None =>
                  (
                    index + 1,
                    wordsToExplore :+ word,
                    al :+ Vertex(index, word),
                    map + (word -> index)
                  )
              }
            }

        case None =>
          throw new IllegalArgumentException(
            s"Word $word does not exists in the dictionary"
          )
      }

    (
      finalIndex,
      vertex.copy(edges = edges),
      childWords,
      finalWordsAlreadySeen
    )
  }
}

object GraphBuilder {

  def apply(dictionary: Map[String, List[String]]) =
    new GraphBuilder(dictionary)
}
