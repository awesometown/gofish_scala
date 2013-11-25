package ca.uptoeleven.gofish

import akka.actor.{ Actor, ActorRef, FSM }
import akka.actor.LoggingFSM

case object Join
case class Play(card: Card)


case class YouAre(playerId: Int)
case class NotifyState(gameState: GameState)

sealed trait State
case object WaitingForPlayers extends State
case object Playing extends State
case object GameOver extends State

sealed trait Data
case object Uninitialized extends Data
case class OnePlayer(player1: ActorRef) extends Data

case class GameState(currPlayer: ActorRef, nextPlayer: ActorRef) extends Data

class GameLogic extends Actor with LoggingFSM[State, Data] {

  startWith(WaitingForPlayers, Uninitialized)

  when(WaitingForPlayers) {
    case Event(Join, Uninitialized) =>
      println("player1 joined")
      sender ! YouAre(1)
      stay using OnePlayer(sender)
    case Event(Join, OnePlayer(player1)) =>
      println("player2 joined")
      sender ! YouAre(2)
      goto(Playing) using GameState(player1, sender)
  }

  when(Playing) {
    case Event(Play(card), data: GameState) =>
      if (data.currPlayer == sender) {
        println("Got message from correct player")
        stay using GameState(data.nextPlayer, data.currPlayer)
      } else {
        println("Got play message from some other dude")
        stay
      }
      
  }

  onTransition {
    case x -> y => println("Moved from " + x + " to " + y)
  }
}