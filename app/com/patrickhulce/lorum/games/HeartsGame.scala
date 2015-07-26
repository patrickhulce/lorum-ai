package com.patrickhulce.lorum.games

import com.patrickhulce.lorum.Cards.{Suit, Card}
import com.patrickhulce.lorum.{Player, GameState, TrickBasedGame}

class HeartsGame(gs: GameState) extends TrickBasedGame(gs) {
  def score: Map[Player, Double] = {
    val tricksTakenBy = gs.plays.groupBy(winnerOf)
    val numHeartsTakenBy = tricksTakenBy.mapValues { tricks =>
      tricks.map(countHearts).sum
    }
    numHeartsTakenBy.find(_._2 == 8) match {
      case Some((leader, _)) => gs.playOrder.map(_ -> 10d).toMap - leader + (leader -> 0d)
      case None => gs.playOrder.map(p => p -> numHeartsTakenBy.getOrElse(p, 0).toDouble).toMap
    }
  }

  private def countHearts(trick: List[(Player, Card)]) = trick.count(_._2.suit == Suit.Hearts)
}
