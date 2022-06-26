package com.alphasystem.wordladder

import com.alphasystem.wordladder.utils.{ GraphBuilder, Utils }

import scala.annotation.tailrec

class WordLadder(dictionary: Map[String, List[String]]) {

  private val graphBuilder = GraphBuilder(dictionary)

  def findShortestPath(
    startWord: String,
    endWord: String,
    wordsToIgnore: List[String] = Nil
  ): List[String] = {
    val (numOfEdges, graph) = graphBuilder.buildGraph(startWord, wordsToIgnore)
    val maybeEndVertex = graph.find(_.word == endWord)
    if (maybeEndVertex.isEmpty) {
      throw new IllegalArgumentException(
        s"Word $endWord does not exists in the dictionary"
      )
    }
    reconstructPath(maybeEndVertex.get, solve(numOfEdges, startWord, graph))
  }

  private def solve(
    numOfEdges: Int,
    startWord: String,
    graph: IndexedSeq[Vertex]
  ): Array[Edge] = {
    val a = Array.fill(numOfEdges)(Edge(0, ""))
    a(0) = Edge(0, startWord, -1, 0)

    graph.foldLeft(a) { case (_a, vertex) =>
      traverse(a(vertex.index), vertex.edges, _a)
    }
  }

  private def reconstructPath(
    endVertex: Vertex,
    a: Array[Edge]
  ): List[String] = {
    var currentEdge = a(endVertex.index)
    var parentIndex = currentEdge.parentIndex
    var result = List(currentEdge.word)
    while (parentIndex > -1) {
      currentEdge = a(parentIndex)
      parentIndex = currentEdge.parentIndex
      result = result :+ currentEdge.word
    }
    result.reverse
  }

  @tailrec
  private def traverse(
    parentEdge: Edge,
    vertices: List[Vertex],
    a: Array[Edge]
  ): Array[Edge] = {
    vertices match {
      case vertex :: tail =>
        val currentEdge = a(vertex.index)
        val edge = Edge(
          vertex.index,
          vertex.word,
          parentEdge.index,
          parentEdge.distance + 1
        )
        if (edge.distance < currentEdge.distance) {
          a(vertex.index) = edge
          traverse(parentEdge, tail, a)
        } else traverse(parentEdge, tail, a)

      case Nil => a
    }
  }
}

object WordLadder {

  def apply(): WordLadder = WordLadder(Utils.readDictionary)
  def apply(dictionary: Map[String, List[String]]) = new WordLadder(dictionary)
}
