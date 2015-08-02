package com.patrickhulce.lorum.ai

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.{Player, Game}

import scala.util.Random

object RandomAI extends AI {
  def nextMove(game: Game, player: Player): Card = {
    val possibleMoves = game.availableMoves(player)
    Random.shuffle(possibleMoves.toList).head
  }
}
