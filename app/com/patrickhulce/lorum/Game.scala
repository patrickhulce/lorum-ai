package com.patrickhulce.lorum

import com.patrickhulce.lorum.Cards.Card

trait Game {
  def isOver: Boolean

  def whoseTurn: Player

  def isValidMove(move: Card, player: Player): Boolean =
    availableMoves(player).contains(move)

  def availableMoves(player: Player): Set[Card]

  def score: Map[Player, Double]

  def makeMove(move: Card): GameState
}
