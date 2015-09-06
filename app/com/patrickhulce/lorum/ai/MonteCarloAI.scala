package com.patrickhulce.lorum.ai

import com.patrickhulce.lorum.Cards.Card
import com.patrickhulce.lorum.simulation.{Dealer, GameRunner}
import com.patrickhulce.lorum.{GameState, Player, Game}
import com.patrickhulce.lorum.builders.GameBuilder

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class MonteCarloAI[GameType <: Game](
  gameBuilder: GameBuilder[GameType],
  isOmniscient: Boolean,
  trials: Int
) extends AI[GameType] {
  def nextMove(game: GameType, player: Player): Card = {
    val averageScoresByCardF = game.availableMoves(player).map { card =>
      Future(card -> simulateOption(game, player, card))
    } toList

    val bestOptionF = Future.sequence(averageScoresByCardF).map { averageScoresByCard =>
      val bestOption = averageScoresByCard.maxBy {
        case (card, scores) =>
          val myScore = scores.get(player).get
          val othersScores = scores - player
          val avgOtherScore = othersScores.values.sum / othersScores.values.size
          avgOtherScore - myScore
      }
      bestOption._1
    }

    // TODO: Change rest of code runner to respect Future
    Await.result(bestOptionF, Duration.Inf)
  }

  private[ai] def simulateOption(game: GameType, player: Player, choice: Card): Map[Player, Double] = {
    val startingState = if (isOmniscient) {
      game.makeMove(choice)
    } else {
      obscureHands(player, game.makeMove(choice))
    }

    val scores = Range(1, trials).map { i =>
      val ais = game.state.playOrder.map(
        _ -> new RandomAI[GameType]
      ).toMap
      val runner = new GameRunner[GameType](gameBuilder, ais, startingState)
      runner.runUntilComplete
    }

    val totalScores = scores.reduce { (acc, scoreMap) =>
      acc.map {
        case (key, value) => key -> (scoreMap.getOrElse(key, 0d) + value)
      }
    }

    totalScores.mapValues(_ / trials)
  }

  private[ai] def obscureHands(steadyPlayer: Player, state: GameState): GameState = {
    val myHand = state.hands.get(steadyPlayer).get
    val othersHands = state.hands - steadyPlayer
    val poolOfCards = othersHands.values.reduce(_ ++ _)

    val playOrder = state.playOrder.filter(_ != steadyPlayer)
    val indexToStartWith = state.playOrder.indexOf(steadyPlayer) % 3
    val newOpponentHands = Dealer.dealTo(playOrder, poolOfCards, indexToStartWith)
    val newHands = newOpponentHands + (steadyPlayer -> myHand)
    state.copy(hands = newHands)
  }
}
