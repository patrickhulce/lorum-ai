package com.patrickhulce.lorum.simulation

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.ai.AI
import com.patrickhulce.lorum.{Game, Player, GameState}

class GameRunner[T <: Game](
  builder: GameBuilder[T],
  players: Map[Player, AI],
  startingState: GameState
) {
  var game = builder.build(startingState)

  def runUntilComplete: Map[Player, Double] = {
    while (!game.isOver) step()
    game.score
  }

  def move(move: Card): Unit = {
    game = builder.build(game.makeMove(move))
  }

  def step(): Unit = {
    val actingPlayer = game.whoseTurn
    val ai = players.get(actingPlayer).get
    val nextMove = ai.nextMove(game, actingPlayer)
    move(nextMove)
  }
}
