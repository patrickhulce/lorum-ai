import com.patrickhulce.lorum.ai.RandomAI
import com.patrickhulce.lorum.{Player, GameState}
import com.patrickhulce.lorum.builders._
import com.patrickhulce.lorum.games.HeartsGame
import com.patrickhulce.lorum.simulation.{GameRunner, Dealer}

val players = List(Player(1), Player(2), Player(3), Player(4))
val ais = players.map(_ -> RandomAI).toMap

val trials = 1000

val startTime = System.currentTimeMillis
for (i <- 1 to trials) {
  val hands = Dealer.dealTo(players)
  val state = GameState(players, hands, List.empty)
  val runner = new GameRunner[HeartsGame](HeartsBuilder, ais, state)
  runner.runUntilComplete
}

val totalTime = System.currentTimeMillis - startTime
println(s"$totalTime ms total time")
println(s"${totalTime.toDouble / trials.toDouble} ms per")