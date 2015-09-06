package com.patrickhulce.lorum.ai

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.{Player, Game}

import scala.util.Random

class RandomAI[GameType <: Game] extends AI[GameType] {
  def nextMove(game: GameType, player: Player): Card = {
    val possibleMoves = game.availableMoves(player)
    Random.shuffle(possibleMoves.toList).head
  }
}
