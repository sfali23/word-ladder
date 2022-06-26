package com.alphasystem.wordladder

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class WordLadderSpec extends AnyWordSpec with Matchers {

  private val wordLadder = WordLadder()

  "WordLadder" should {
    "find shortest path" in {
      wordLadder.findShortestPath("word", "dork") shouldBe List(
        "word",
        "work",
        "dork"
      )
    }

  }

}
