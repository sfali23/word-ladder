package com.alphasystem.wordladder

import org.rogach.scallop.{ ScallopConf, ScallopOption, Subcommand }

object Main {

  private class Command extends Subcommand("word-ladder") {
    val startWord: ScallopOption[String] =
      opt[String](descr = "Start word", required = true)

    val endWord: ScallopOption[String] =
      opt[String](descr = "End word", required = true)

    val wordsToIgnore: ScallopOption[List[String]] =
      opt[List[String]](descr = "Words to ignore (optional)", required = false)
  }

  private class Conf(args: Array[String]) extends ScallopConf(args) {
    addSubcommand(new Command)
    verify()
  }

  def main(args: Array[String]): Unit = {
    val wordLadder = WordLadder()

    val conf = new Conf(args)
    conf.subcommand match {
      case Some(c: Command) =>
        val result =
          wordLadder.findShortestPath(
            c.startWord().toLowerCase,
            c.endWord().toLowerCase,
            c.wordsToIgnore()
          )
        println(result.map(_.toUpperCase).mkString(" -> "))
        println(s"Optimal path: ${result.length - 1}")

      case _ =>
        Console.err.println(s"No sub-command given")
        conf.printHelp()
    }

  }
}
