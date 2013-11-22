package ca.uptoeleven.gofish

import akka.actor.{ Actor, ActorRef, FSM }
import akka.actor.LoggingFSM


  case object Join
  case class Play(card: Card)
  
  case class NotifyState(gameState: GameState)
  
  sealed trait State
  case object WaitingForPlayers extends State
  case object PlayerOneTurn extends State
  case object PlayerTwoTurn extends State
  case object GameOver extends State
  
  sealed trait Data
  case object Uninitialized extends Data
  case class OnePlayer(player1: ActorRef) extends Data
  case class GameData(gameState: GameState) extends Data

  case class GameState(player1: ActorRef, player2: ActorRef)
  
class GameLogic extends Actor with LoggingFSM[State, Data] {

    startWith(WaitingForPlayers, Uninitialized)
    
    when(WaitingForPlayers) {
      case Event(Join, Uninitialized) =>
        println("player1 joined")
        stay using OnePlayer(sender)
      case Event(Join, OnePlayer(player1)) =>
         println("player2 joined")
        goto(PlayerOneTurn) using GameData(GameState(player1, sender))
    }
  
    when(PlayerOneTurn) {
      case Event(Play(card), data: GameData) =>
        handlePlay(data.gameState.player1, sender, data)
    }
    
    when(PlayerTwoTurn) {
      case Event(Play(card), data: GameData) =>
        handlePlay(data.gameState.player2, sender, data)
    }
    
    def handlePlay(expected: ActorRef, sender: ActorRef, data: GameData) = {
      if (expected == sender) {
        	println("Got message from correct player")
        } else {
          println("Got play message from some other dude")
        }
        stay
    }
    
    onTransition {
      case x -> y => println("Moved from " + x + " to " + y)
    }
}