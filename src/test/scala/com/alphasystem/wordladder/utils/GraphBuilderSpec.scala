package com.alphasystem.wordladder.utils

import com.alphasystem.wordladder.Vertex
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GraphBuilderSpec extends AnyWordSpec with Matchers {

  "GraphBuilder" should {
    "create Vertex of given word" in {
      val dictionary = Map(
        "word" -> List(
          "bord",
          "cord",
          "ford",
          "lord",
          "sord",
          "ward",
          "woad",
          "wold",
          "wood",
          "wore",
          "work",
          "worm",
          "worn",
          "wors",
          "wort"
        )
      )
      val graphBuilder = GraphBuilder(dictionary)

      val (index, vertex, childWords, wordsAlreadySeen) =
        graphBuilder.buildGraph("word", 0, Map.empty, List.empty)

      index shouldBe 16

      val expectedChildWords = dictionary("word")
      val edges = expectedChildWords.zipWithIndex.map { case (word, index) =>
        Vertex(index + 1, word)
      }
      vertex shouldBe Vertex(0, "word", edges)

      childWords shouldBe expectedChildWords

      val expectedWordsAlreadySeen =
        Map("word" -> 0) ++ edges.groupBy(_.word).map { case (word, values) =>
          word -> values.head.index
        }
      wordsAlreadySeen shouldBe expectedWordsAlreadySeen
    }

    "Omit words that already seen and use index of word already seen" in {
      val dictionary = Map(
        "word" -> List(
          "bord",
          "cord",
          "lord",
          "ward",
          "wood",
          "work",
          "worm"
        ),
        "bord" -> List(
          "bard",
          "bird",
          "bold",
          "born",
          "cord",
          "ford",
          "lord",
          "word"
        )
      )

      val graphBuilder = GraphBuilder(dictionary)

      val (index, _, _, wordsAlreadySeen) =
        graphBuilder.buildGraph("word", 0, Map.empty, List.empty)

      val (finalIndex, vertex, childWords, _) =
        graphBuilder.buildGraph("bord", index, wordsAlreadySeen, List.empty)

      finalIndex shouldBe 13
      vertex shouldBe Vertex(
        1,
        "bord",
        List(
          Vertex(8, "bard"),
          Vertex(9, "bird"),
          Vertex(10, "bold"),
          Vertex(11, "born"),
          Vertex(wordsAlreadySeen("cord"), "cord"),
          Vertex(12, "ford"),
          Vertex(wordsAlreadySeen("lord"), "lord"),
          Vertex(wordsAlreadySeen("word"), "word")
        )
      )
      childWords shouldBe List("bard", "bird", "bold", "born", "ford")
    }

    "returns empty child list if all words already seen" in {
      val dictionary = Map(
        "word" -> List(
          "bord",
          "cord",
          "lord",
          "ward",
          "wood",
          "work",
          "worm"
        ),
        "bord" -> List(
          "cord",
          "lord",
          "word"
        )
      )

      val graphBuilder = GraphBuilder(dictionary)

      val (index, _, _, wordsAlreadySeen) =
        graphBuilder.buildGraph("word", 0, Map.empty, List.empty)

      val (_, _, childWords, _) =
        graphBuilder.buildGraph("bord", index, wordsAlreadySeen, List.empty)

      childWords shouldBe empty
    }

    "creates a graph given a starting word" in {
      val builder = GraphBuilder(Utils.readDictionary)
      val (index, graph) = builder.buildGraph("word")

      val graphSize = graph.size
      graphSize shouldBe index
      graphSize shouldBe graph.last.index + 1
      graph.last shouldBe graph(graphSize - 1)

      val headVertex = graph.head
      headVertex.index shouldBe 0
      headVertex.word shouldBe "word"

      // no duplicate in index
      graph.map(_.index).toSet.size shouldBe index
    }
  }
}
