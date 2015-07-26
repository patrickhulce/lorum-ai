package com.patrickhulce.lorum

object Cards {

  object Suit extends Enumeration {
    type Suit = Value
    val Acorns, Bells, Hearts, Leaves = Value
  }

  object Rank extends Enumeration {
    type Rank = Value
    val Seven = Value(7)
    val Eight = Value(8)
    val Nine = Value(9)
    val Ten = Value(10)
    val Lower = Value(11)
    val Upper = Value(12)
    val King = Value(13)
    val Ace = Value(14)
  }

  import Suit._
  import Rank._

  case class Card(suit: Suit, rank: Rank)

  implicit class RankWithSugar(r: Rank) {
    def of(s: Suit) = Card(s, r)
  }
}
