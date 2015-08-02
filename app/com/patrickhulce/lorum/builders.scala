package com.patrickhulce.lorum

import com.patrickhulce.lorum.games.HeartsGame

object builders {
  type GameBuilder[T <: Game] = GameState => Game
  
  def HeartsBuilder: GameBuilder[HeartsGame] = new HeartsGame(_)
}