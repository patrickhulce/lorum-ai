package com.patrickhulce.lorum.ai

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.{Player, Game}

trait AI {
  def nextMove(game: Game, player: Player): Card
}
