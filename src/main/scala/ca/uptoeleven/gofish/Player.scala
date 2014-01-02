package ca.uptoeleven.gofish

import akka.actor.Actor
import akka.actor.LoggingFSM
import akka.actor.ActorRef

sealed trait PlayerState
case object WaitingForId extends PlayerState
case object WaitingForHand extends PlayerState
case object WaitingForTurn extends PlayerState
case object WaitingForClientChoice extends PlayerState
case object WaitingForAnswer extends PlayerState
case object MyTurn extends PlayerState

sealed trait PlayerData
case object PlayerUninitialized extends PlayerData
case class PlayerGameData(playerId: Int, client: ActorRef, hand: PlayerHand) extends PlayerData

case class GameMessage(message: Any)

case class ClientChoice(targetPlayerId: Int, card: Card)
//case class JoinGame(game: ActorRef)

class Player extends Actor with LoggingFSM[PlayerState, PlayerData] {

  startWith(WaitingForId, PlayerUninitialized)
  
  when(WaitingForId) {
    case Event(YouAre(id, client), _) =>
      println("I am " + id)
      client ! GameMessage(YouAre(id, client))
      goto(WaitingForHand) using PlayerGameData(id, client, null)
  }
  
  when(WaitingForHand) {
    case Event(NotifyHand(hand), PlayerGameData(id, client, _)) =>
      client ! GameMessage(NotifyHand(hand))
      goto(WaitingForTurn) using PlayerGameData(id, client, hand)
  }
  
  when(WaitingForTurn) {
    case Event(NotifyPlayerTurn(idForTurn), PlayerGameData(myId, client, _)) if (idForTurn == myId) =>
      println("My turn!")
      client ! GameMessage(NotifyPlayerTurn(myId))
      goto(WaitingForClientChoice)
    case Event(NotifyGameOver(winnderId), PlayerGameData(id, client, _)) =>
      println("Not my turn :(")
      goto(WaitingForHand) using PlayerGameData(id, client, null)    
  }
  
  when(WaitingForClientChoice) {
    case Event(ClientChoice(target, card), pgd @PlayerGameData(_,_,_)) =>
      context.parent ! MakePlay(target, card)
      goto(WaitingForAnswer)
  }
  
  when(WaitingForAnswer) {
    case Event(MatchFound(fromPlayer, card), _) =>
      //record match
      goto(WaitingForTurn)
    case Event(GoFish, _) =>
      //go fish
      goto(WaitingForTurn)
  }
}