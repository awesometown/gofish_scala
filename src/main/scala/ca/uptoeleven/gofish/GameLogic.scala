package ca.uptoeleven.gofish

import akka.actor.{ Actor, ActorRef, FSM }

case class Join(ref: ActorRef)
case class MakePlay(playerId: Int, targetId: Int, card: Card)

sealed trait State
case object WaitingForPlayers extends State
case object PlayerOneTurn extends State
case object PlayerTwoTurn extends State

sealed trait Data
case object Uninitialized extends Data
case class OnePlayer(player1: ActorRef) extends Data
case class TwoPlayers(gameState: GameState) extends Data

class GameLogic extends Actor with FSM[State, Data] {
  startWith(WaitingForPlayers, Uninitialized)
  
  when(WaitingForPlayers) {
    case Event(Join(player1), Uninitialized) =>
      stay using OnePlayer(player1)
    case Event(Join(player2), OnePlayer(player1)) =>
      goto(PlayerOneTurn) using initGameState(player1, player2)
  }
  
  private def initGameState(player1: ActorRef, player2: ActorRef) = {
	  var deck = new Deck().shuffle
	  for(i <- 1 to 5) {
	    val(card1, deck1) = deck.dealCard
	    val(card2, deck2) = deck1.dealCard
	    
	  }
    
  }
}