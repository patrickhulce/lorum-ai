package com.patrickhulce.lorum

import com.patrickhulce.lorum.Cards.Card

case class GameState(
  playOrder: List[Player],
  hands: Map[Player, Set[Card]],
  plays: List[List[(Player, Card)]]
) {
  def playCard(card: Card): GameState = {
    hands.find(_._2.contains(card)) match {
      case None => throw new Exception("Card not found")
      case Some((player, hand)) =>
        val handWithoutCard = hand - card
        val mostRecentPlay = plays.headOption.getOrElse(Nil)
        val nextMove = (player, card)
        val updatedPlays = if (mostRecentPlay.length == 4) {
          List(nextMove) :: plays
        } else {
          val updatedPlay = nextMove :: mostRecentPlay
          val allOtherPlays = if (plays.isEmpty) Nil else plays.tail
          updatedPlay :: allOtherPlays
        }
        copy(
          hands = hands - player + (player -> handWithoutCard),
          plays = updatedPlays
        )
    }
  }
}
