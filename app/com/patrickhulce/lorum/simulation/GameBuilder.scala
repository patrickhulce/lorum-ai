package com.patrickhulce.lorum.simulation

import com.patrickhulce.lorum.{Game, GameState}
import com.patrickhulce.lorum.games.HeartsGame

trait GameBuilder[T <: Game] {
  def build(state: GameState): T
}

object HeartsBuilder extends GameBuilder[HeartsGame] {
  def build(state: GameState) = new HeartsGame(state)
}
