package com.patrickhulce.lorum.simulation

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.Cards.Rank._
import com.patrickhulce.lorum.Cards.Suit._
import com.patrickhulce.lorum.ai.{AI, RandomAI}
import com.patrickhulce.lorum.builders.HeartsBuilder
import com.patrickhulce.lorum.games.HeartsGame
import com.patrickhulce.lorum._
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GameRunnerSpec extends Specification {

  case class BogusAI(move: Card) extends AI {
    def nextMove(game: Game, player: Player) = move
  }

  val players = List(Player(1), Player(2), Player(3), Player(4))
  val List(p1, p2, p3, p4) = players

  class MockEndedGame extends Game {
    val state = GameState(List.empty, Map.empty, List.empty)

    def isOver = true

    def whoseTurn = throw new Exception("game over")

    def availableMoves(player: Player) = Set.empty

    def makeMove(m: Card) = throw new Exception("game over")

    def score = Map(p1 -> 10, p2 -> 11)
  }

  "step" should {
    "advance game state properly" in {
      val ais = Map(
        p1 -> BogusAI(Ten of Hearts),
        p2 -> BogusAI(Ten of Bells),
        p3 -> BogusAI(Ten of Bells),
        p4 -> BogusAI(Ten of Bells)
      )
      val hands = Map(
        p1 -> Set(Ten of Hearts),
        p2 -> Set(Ten of Bells),
        p3 -> Set(Ten of Bells),
        p4 -> Set(Ten of Bells)
      )
      val state = GameState(players, hands, List.empty)
      val runner = new GameRunner[HeartsGame](HeartsBuilder, ais, state)

      runner.step()

      val handsAfterOne = hands - p1 + (p1 -> Set.empty[Card])
      val playsAfterOne = List((p1, Ten of Hearts))
      runner.game.whoseTurn mustEqual p2
      runner.game.state.hands mustEqual handsAfterOne
      runner.game.state.plays mustEqual List(playsAfterOne)

      runner.step()

      val handsAfterTwo = handsAfterOne - p2 + (p2 -> Set.empty[Card])
      val playsAfterTwo = (p2, Ten of Bells) :: playsAfterOne
      runner.game.whoseTurn mustEqual p3
      runner.game.state.hands mustEqual handsAfterTwo
      runner.game.state.plays mustEqual List(playsAfterTwo)
    }
  }

  "runUntilComplete" should {
    "not call step if game is over" in {
      val state = GameState(List(p1, p2), Map.empty, List.empty)
      val runner = new GameRunner[MockEndedGame](_ => new MockEndedGame, Map.empty, state)

      // Ended game will throw exceptions if step is called
      runner.runUntilComplete mustEqual Map(
        p1 -> 10,
        p2 -> 11
      )
    }

    "go until completion" in {
      val ais = players.map(_ -> RandomAI).toMap
      val hands = Dealer.dealTo(players)
      val state = GameState(players, hands, List.empty)
      val runner = new GameRunner[HeartsGame](HeartsBuilder, ais, state)

      runner.runUntilComplete.size mustEqual 4
      runner.game.isOver must beTrue

      val endingState = runner.game.state
      endingState.hands.values.map(_.size).toList mustEqual List(0, 0, 0, 0)
      endingState.plays.size mustEqual 8
      endingState.plays.map(_.size) mustEqual List(4, 4, 4, 4, 4, 4, 4, 4)
    }

    "simulate 1000 games in <1s" in {
      val ais = players.map(_ -> RandomAI).toMap

      val startTime = System.currentTimeMillis
      for (i <- 1 to 1000) {
        val hands = Dealer.dealTo(players)
        val state = GameState(players, hands, List.empty)
        val runner = new GameRunner[HeartsGame](HeartsBuilder, ais, state)
        runner.runUntilComplete
      }
      (System.currentTimeMillis - startTime) must be_<=(1000l)
    }
  }
}
