package ca.uptoeleven.gofish

import akka.actor.{ Actor, ActorRef, FSM }
import akka.actor.LoggingFSM
import akka.actor.Props

case object Join

case class YouAre(playerId: Int, client: ActorRef)
case class NotifyHand(hand: PlayerHand)
case class NotifyPlayerTurn(playerId: Int)
case class NotifyGameOver(winnerId: Int)
case class MakePlay(targetPlayerId: Int, requestedRank: Int)
case class MatchFound(fromPlayerId: Int, card: Card)
case class GoFish(fromPlayerId: Int)

case class NotifyState(gameState: GameState)

sealed trait State
case object WaitingForPlayers extends State
case object Playing extends State
case object GameOver extends State

sealed trait GameData
case object Uninitialized extends GameData
case class OnePlayer(player1: ActorRef) extends GameData

case class GamePlayer(playerRef: ActorRef, hand: PlayerHand)

case class WaitingPlayers(waitingPlayers: Map[Int, ActorRef]) extends GameData

case class GameState(dealer: Dealer, players: Map[Int, ActorRef], currPlayerId: Int) extends GameData {
  def incrPlayer: GameState = {
    new GameState(dealer, players, if (currPlayerId >= players.size - 1) 0 else (currPlayerId + 1))
  }

  def currPlayer: ActorRef = {
    players(currPlayerId)
  }
}

class GameLogic extends Actor with LoggingFSM[State, GameData] {

  val MinPlayers = 2
  val NumStartingCards = 5

  startWith(WaitingForPlayers, WaitingPlayers(Map[Int, ActorRef]()))

  when(WaitingForPlayers) {
    case Event(Join, WaitingPlayers(players)) =>
      val playerId = players.size
      val newPlayer = context.actorOf(Props[Player])
      val newPlayers = players + (playerId -> newPlayer)
      newPlayer ! YouAre(playerId, sender)

      if (newPlayers.size < MinPlayers) {
        goto(WaitingForPlayers) using WaitingPlayers(newPlayers)
      } else {
        val dealer = new Dealer(Deck.shuffledDeck)
        val playerHands = dealer.dealStartingHands(newPlayers.size, NumStartingCards)
        newPlayers.values.zip(playerHands).foreach { case (player, hand) => player ! NotifyHand(hand) }
        val startingState = GameState(dealer, newPlayers, 0)
        goto(Playing) using startingState
      }
  }

  when(Playing) {
    case Event(MakePlay(target, card), gameState: GameState) if (gameState.currPlayer == sender) =>
      val newState = gameState.incrPlayer
      newState.currPlayer ! NotifyPlayerTurn(newState.currPlayerId)
      goto(Playing) using newState
    case Event(MakePlay(target, card), gameState: GameState) =>
      stay
  }

  onTransition {
    case _ -> Playing =>
      nextStateData match {
        case gameState: GameState =>
          gameState.currPlayer ! NotifyPlayerTurn(gameState.currPlayerId)
        case _ =>
        //don't care
      }
    case x -> y => println("Moved from " + x + " to " + y)
  }
}