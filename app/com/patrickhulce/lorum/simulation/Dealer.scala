package com.patrickhulce.lorum.simulation

import com.patrickhulce.lorum.Cards.{Card, Rank, Suit}
import com.patrickhulce.lorum.Player

import scala.util.Random

object Dealer {
  val Deck = {
    val setOfSets = for (suit <- Suit.values) yield {
      for (rank <- Rank.values) yield Card(suit, rank)
    }
    setOfSets.reduce(_ ++ _)
  }

  def dealTo(
    players: List[Player],
    cards: Set[Card] = Deck,
    playerIndexToStartWith: Int = 0
  ): Map[Player, Set[Card]] = {
    val shuffled = Random.shuffle(cards.toList)

    val playerCards = for (i <- 0 to shuffled.length) yield {
      val player = players(i + playerIndexToStartWith)
      val card = shuffled(i)
      (player, card)
    }

    playerCards.groupBy(_._1).mapValues(_.map(_._2).toSet)
  }
}
