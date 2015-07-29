package com.patrickhulce.lorum

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import com.patrickhulce.lorum.Cards.Rank._
import com.patrickhulce.lorum.Cards.Suit._


@RunWith(classOf[JUnitRunner])
class TrickBasedGameSpec extends Specification {

  class TestTrickBasedGame(s: GameState) extends TrickBasedGame(s) {
    override def score: Map[Player, Double] = Map.empty
  }

  val p1 = Player(1)
  val p2 = Player(2)
  val p3 = Player(3)
  val p4 = Player(4)
  val players = List(p1, p2, p3, p4)

  "whoseTurn" should {
    "compute correct player at the start" in {
      val state = GameState(players, Map.empty, List.empty)
      val game = new TestTrickBasedGame(state)
      game.whoseTurn mustEqual p1
    }

    "compute correct player in middle of a trick" in {
      val plays = List(
        List(
          (p1, Eight of Hearts),
          (p2, Seven of Hearts)
        ).reverse,
        List(
          (p1, Eight of Hearts),
          (p2, Seven of Hearts),
          (p3, King of Hearts),
          (p4, Lower of Hearts)
        ).reverse
      )
      val state = GameState(players, Map.empty, plays)
      val game = new TestTrickBasedGame(state)
      game.whoseTurn mustEqual p3
    }

    "compute correct player after a trick" in {
      val plays = List(
        List(
          (p1, Eight of Hearts),
          (p2, King of Hearts),
          (p3, Ace of Leaves),
          (p4, Lower of Hearts)
        ).reverse,
        List(
          (p1, Eight of Hearts),
          (p2, Seven of Hearts),
          (p3, Lower of Hearts),
          (p4, King of Hearts)
        ).reverse
      )
      val state = GameState(players, Map.empty, plays)
      val game = new TestTrickBasedGame(state)
      game.whoseTurn mustEqual p2
    }
  }

  "availableMoves" should {
    "compute the correct moves when leading at start" in {
      val hands = Map(
        p1 -> Set(Ten of Hearts, Ten of Bells),
        p2 -> Set(Eight of Hearts, Eight of Bells),
        p3 -> Set(Nine of Hearts, Nine of Bells),
        p4 -> Set(Ace of Hearts, Ace of Bells)
      )
      val state = GameState(players, hands, List.empty)
      val game = new TestTrickBasedGame(state)

      val moves = players.map(p => p -> game.availableMoves(p)).toMap
      moves mustEqual hands
    }

    "compute the correct moves when leading in game" in {
      val hands = Map(
        p1 -> Set(Ten of Hearts, Ten of Bells),
        p2 -> Set(Eight of Hearts, Eight of Bells),
        p3 -> Set(Nine of Hearts, Nine of Bells),
        p4 -> Set(Ace of Hearts, Ace of Bells)
      )
      val plays = List(
        List(
          (p1, Eight of Hearts),
          (p2, Seven of Hearts),
          (p3, King of Hearts),
          (p4, Lower of Hearts)
        )
      )
      val state = GameState(players, hands, plays)
      val game = new TestTrickBasedGame(state)

      val moves = players.map(p => p -> game.availableMoves(p)).toMap
      moves mustEqual hands
    }

    "compute the correct moves when following suit" in {
      val hands = Map(
        p1 -> Set(Ten of Bells),
        p2 -> Set(Eight of Hearts, Eight of Bells),
        p3 -> Set(Nine of Hearts, Nine of Bells),
        p4 -> Set(Ace of Hearts, Ace of Bells)
      )
      val plays = List(List(
        (p1, Ten of Bells), (p1, Ten of Hearts)
      ))
      val state = GameState(players, hands, plays)
      val game = new TestTrickBasedGame(state)

      game.availableMoves(p2) mustEqual Set(Eight of Hearts)
      game.availableMoves(p3) mustEqual Set(Nine of Hearts)
      game.availableMoves(p4) mustEqual Set(Ace of Hearts)
    }

    "compute the correct moves when can't follow suit" in {
      val hands = Map(
        p1 -> Set(Ten of Bells),
        p2 -> Set(Eight of Hearts, Eight of Bells),
        p3 -> Set(Nine of Acorns, Nine of Bells),
        p4 -> Set(Ace of Hearts, Ace of Bells)
      )
      val plays = List(List(
        (p1, Ten of Hearts), (p1, Ten of Leaves)
      ))
      val state = GameState(players, hands, plays)
      val game = new TestTrickBasedGame(state)

      val moves = players.map(p => p -> game.availableMoves(p)).toMap
      (moves - p1) mustEqual (hands - p1)
    }
  }

  "winnerOf" should {
    "compute the correct winner when everyone follows suit" in {
      val plays = List(
        List(
          (p1, Eight of Hearts),
          (p2, Seven of Hearts),
          (p3, King of Hearts),
          (p4, Lower of Hearts)
        ).reverse
      )
      val state = GameState(players, Map.empty, plays)
      val game = new TestTrickBasedGame(state)
      game.winnerOf(plays.head) mustEqual p3
    }

    "compute the correct winner when not everyone follows suit" in {
      val plays = List(
        List(
          (p1, Eight of Hearts),
          (p2, Ace of Leaves),
          (p3, King of Hearts),
          (p4, Lower of Hearts)
        ).reverse
      )
      val state = GameState(players, Map.empty, plays)
      val game = new TestTrickBasedGame(state)
      game.winnerOf(plays.head) mustEqual p3
    }

    "compute the correct winner when not no one follows suit" in {
      val plays = List(
        List(
          (p1, Eight of Hearts),
          (p2, Ace of Leaves),
          (p3, King of Bells),
          (p4, Lower of Acorns)
        ).reverse
      )
      val state = GameState(players, Map.empty, plays)
      val game = new TestTrickBasedGame(state)
      game.winnerOf(plays.head) mustEqual p1
    }
  }

  "makeMove" should {
    "yield correct state at start of game" in {
      val hands = Map(
        p1 -> Set(Ten of Hearts),
        p2 -> Set(Ten of Leaves),
        p3 -> Set(Ten of Bells),
        p4 -> Set(King of Hearts)
      )
      val state = GameState(players, hands, List.empty)
      val game = new TestTrickBasedGame(state)
      game.makeMove(Ten of Hearts) mustEqual GameState(
        players,
        hands - p1 + (p1 -> Set.empty),
        List(List((p1, Ten of Hearts)))
      )
    }

    "yield correct state at start of trick" in {
      val hands = Map(
        p1 -> Set(Ten of Hearts),
        p2 -> Set(Ten of Leaves),
        p3 -> Set(Ten of Bells),
        p4 -> Set(King of Hearts)
      )
      val trick = players.map(p => p -> (Ace of Hearts))
      val state = GameState(players, hands, List(trick))
      val game = new TestTrickBasedGame(state)
      game.makeMove(Ten of Bells) mustEqual GameState(
        players,
        hands - p3 + (p3 -> Set.empty),
        List(List((p3, Ten of Bells)), trick)
      )
    }

    "yield correct state in middle of trick" in {
      val hands = Map(
        p1 -> Set(Ten of Hearts),
        p2 -> Set(Ten of Leaves),
        p3 -> Set[Cards.Card](),
        p4 -> Set(King of Hearts)
      )
      val recentTrick = List((p3, King of Bells))
      val firstTrick = players.map(p => p -> (Ace of Hearts))
      val state = GameState(players, hands, List(recentTrick, firstTrick))
      val game = new TestTrickBasedGame(state)
      game.makeMove(King of Hearts) mustEqual GameState(
        players,
        hands - p4 + (p4 -> Set.empty),
        List((p4 -> (King of Hearts))::recentTrick, firstTrick)
      )
    }
  }
}
