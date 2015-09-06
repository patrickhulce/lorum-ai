package com.patrickhulce.lorum.ai

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.{Player, Game}

trait AI[GameType <: Game] {
  def nextMove(game: GameType, player: Player): Card
}
