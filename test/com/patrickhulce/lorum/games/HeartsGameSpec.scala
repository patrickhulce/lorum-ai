package com.patrickhulce.lorum.games

import com.patrickhulce.lorum.{Player, GameState}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import com.patrickhulce.lorum.Cards.Rank._
import com.patrickhulce.lorum.Cards.Suit._

@RunWith(classOf[JUnitRunner])
class HeartsGameSpec extends Specification {
  val p1 = Player(1)
  val p2 = Player(2)
  val p3 = Player(3)
  val p4 = Player(4)
  val players = List(p1, p2, p3, p4)

  val BellLow = Seven of Bells
  val HeartLow = Seven of Hearts
  val BellHigh = Ace of Bells
  val HeartHigh = Ace of Hearts

  "score" should {
    "calculate correct score for regular game" in {
      val plays = List(
        List(
          (p1, HeartLow),
          (p2, HeartLow),
          (p3, HeartHigh),
          (p4, BellLow)
        ).reverse,
        List(
          (p3, BellLow),
          (p4, HeartLow),
          (p1, HeartHigh),
          (p2, BellHigh)
        ).reverse,
        List(
          (p2, BellLow),
          (p3, BellLow),
          (p4, HeartHigh),
          (p1, BellHigh)
        ).reverse
      )

      val state = GameState(players, Map.empty, plays)
      val game = new HeartsGame(state)
      game.score mustEqual Map(
        p1 -> 1d,
        p2 -> 2d,
        p3 -> 3d,
        p4 -> 0d
      )
    }

    "calculate correct score in shoot the moon game" in {
      val plays = List(
        List(
          (p1, HeartLow),
          (p2, HeartLow),
          (p3, HeartHigh),
          (p4, BellLow)
        ).reverse,
        List(
          (p3, HeartHigh),
          (p4, HeartLow),
          (p1, HeartLow),
          (p2, BellHigh)
        ).reverse,
        List(
          (p3, HeartHigh),
          (p4, HeartLow),
          (p1, BellLow),
          (p2, BellHigh)
        ).reverse
      )

      val state = GameState(players, Map.empty, plays)
      val game = new HeartsGame(state)
      game.score mustEqual Map(
        p1 -> 10d,
        p2 -> 10d,
        p3 -> 0d,
        p4 -> 10d
      )
    }
  }
}
