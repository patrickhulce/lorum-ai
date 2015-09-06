package com.patrickhulce.lorum

import com.patrickhulce.lorum.games.HeartsGame

object builders {
  type GameBuilder[GameType <: Game] = GameState => GameType
  
  def HeartsBuilder: GameBuilder[HeartsGame] = new HeartsGame(_)
}