package ca.uptoeleven.gofish

import akka.actor.Actor
import akka.actor.LoggingFSM
import akka.actor.ActorRef

sealed trait PlayerState
case object NotJoined extends PlayerState
case object WaitingForHand extends PlayerState
case object WaitingForId extends PlayerState
case object WaitingForTurn extends PlayerState
case object MyTurn extends PlayerState

sealed trait PlayerData
case object PlayerUninitialized extends PlayerData
case class PlayerGameData(playerId: Int, hand: PlayerHand) extends PlayerData

case class JoinGame(game: ActorRef)

class Player extends Actor with LoggingFSM[PlayerState, PlayerData] {

  startWith(NotJoined, PlayerUninitialized)

   when(NotJoined) {
    case Event(JoinGame(game), _) =>
     game ! Join
     goto(WaitingForId)
  }
  
  when(WaitingForId) {
    case Event(YouAre(id), _) =>
      println("I am " + id)
      goto(WaitingForHand) using PlayerGameData(id, null)
  }
  
  when(WaitingForHand) {
    case Event(NotifyHand(hand), PlayerGameData(id, _)) =>
      println("My hand: " + hand)
      goto(WaitingForTurn) using PlayerGameData(id, hand)
  }
  
  when(WaitingForTurn) {
    case Event(NotifyPlayerTurn(idForTurn), PlayerGameData(myId, _)) =>
      println("My turn!")
      if(idForTurn == myId) {
        sender ! Play(new Card(Diamonds, 5))
        stay
      } else {
        stay
      }
    case Event(NotifyGameOver(winnderId), PlayerGameData(id, _)) =>
      println("Not my turn :(")
      goto(WaitingForHand) using PlayerGameData(id, null)
  }
}