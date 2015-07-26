package com.patrickhulce.lorum

import com.patrickhulce.lorum.Cards._

abstract class TrickBasedGame(state: GameState) extends Game {
  def isOver = state.plays.length == 8 && state.plays.forall(_.length == 4)

  def whoseTurn: Player = {
    if (state.plays.length == 0) {
      state.playOrder.head
    } else if(state.plays.head.length == 4) {
      winnerOf(state.plays.head)
    } else {
      val lastPlayer = state.plays.head.head._1
      val lastPlayerIndex = state.playOrder.indexOf(lastPlayer)
      state.playOrder((lastPlayerIndex + 1) % 4)
    }
  }

  def availableMoves(player: Player): Set[Card] = {
    val options = state.hands.get(player).get
    state.plays.headOption match {
      case Some(currentTrick)
        if currentTrick.length != 4 &&
          options.count(_.suit == currentTrick.last._2.suit) > 0 =>
        options.filter(_.suit == currentTrick.last._2.suit)
      case _ => options
    }
  }

  def makeMove(move: Card): GameState = state.playCard(move)

  protected[lorum] def winnerOf(trick: List[(Player, Card)]): Player = {
    val leadingSuit = trick.last._2.suit
    val matchingPlays = trick.filter(_._2.suit == leadingSuit)
    val sortedPlays = matchingPlays.sortBy(_._2.rank)
    sortedPlays.last._1
  }
}
