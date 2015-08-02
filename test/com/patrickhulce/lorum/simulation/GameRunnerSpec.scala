package com.patrickhulce.lorum.simulation

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.Cards.Rank._
import com.patrickhulce.lorum.Cards.Suit._
import com.patrickhulce.lorum.ai.AI
import com.patrickhulce.lorum.games.HeartsGame
import com.patrickhulce.lorum.{GameState, Game, Player}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GameRunnerSpec extends Specification  {

  case class BogusAI(move: Card) extends AI {
    def nextMove(game: Game, player: Player) = move
  }

  val players = List(Player(1), Player(2), Player(3), Player(4))
  val List(p1, p2, p3, p4) = players

  "step" should {
    "advance game state properly" in {
      val builder = HeartsBuilder
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
      val runner = new GameRunner[HeartsGame](builder, ais, state)

      runner.step()

      val handsAfterOne = hands - p1 + (p1 -> Set.empty[Card])
      val playsAfterOne = List((p1, Ten of Hearts))
      runner.game.whoseTurn mustEqual p2
      runner.game.state.hands mustEqual handsAfterOne
      runner.game.state.plays mustEqual List(playsAfterOne)

      runner.step()

      val handsAfterTwo = handsAfterOne - p2 + (p2 -> Set.empty[Card])
      val playsAfterTwo = (p2, Ten of Bells)::playsAfterOne
      runner.game.whoseTurn mustEqual p3
      runner.game.state.hands mustEqual handsAfterTwo
      runner.game.state.plays mustEqual List(playsAfterTwo)
    }
  }
}
