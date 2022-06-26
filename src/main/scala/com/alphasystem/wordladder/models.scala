package com.alphasystem.wordladder

case class Vertex(index: Int, word: String, edges: List[Vertex] = Nil)

case class Edge(
  index: Int,
  word: String,
  parentIndex: Int = 1,
  distance: Int = Int.MaxValue)
