package com.patrickhulce.lorum.simulation

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.ai.AI
import com.patrickhulce.lorum.builders.GameBuilder
import com.patrickhulce.lorum.{Game, Player, GameState}


class GameRunner[T <: Game](
  builder: GameBuilder[T],
  players: Map[Player, AI[T]],
  startingState: GameState
) {
  var game = builder(startingState)

  def runUntilComplete: Map[Player, Double] = {
    while (!game.isOver) step()
    score
  }

  def move(move: Card): Unit = {
    game = builder(game.makeMove(move))
  }

  def step(): Unit = {
    val actingPlayer = game.whoseTurn
    val ai = players.get(actingPlayer).get
    val nextMove = ai.nextMove(game, actingPlayer)
    move(nextMove)
  }

  def score: Map[Player, Double] = game.score
}
