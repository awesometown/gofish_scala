package ca.uptoeleven.gofish

import akka.actor.{ Actor, ActorRef, FSM }
import akka.actor.LoggingFSM
import akka.actor.Props

case object Join

//Targeted Outgoing Messages
case class YouAre(playerId: Int, client: ActorRef)
case class YourHand(hand: PlayerHand)
case class YourTurn(playerId: Int)
case class MatchRequested(requesterId: Int, card: Card)
case class NotifyGameState(gameState: GameState)

//Messages from Player Actors
case class QueryState()
case class MakePlay(requesterId: Int, targetPlayerId: Int, card: Card)
case class MatchFound(requesterId: Int, targetPlayerId: Int, card: Card)
case class GoFish(fromPlayerId: Int)

//Broadcast Messages
sealed trait BroadcastMessage
case class NotifyPlayerJoined(playerId: Int) extends BroadcastMessage
case class NewNotifyPlayerTurn(playerId: Int) extends BroadcastMessage
case class NotifyMoveMade(requestingId: Int, targetId: Int, requesterCard: Card) extends BroadcastMessage
case class NotifyMatch(requestingId: Int, targetId: Int, requesterCard: Card, matchingCard: Card) extends BroadcastMessage
case class NotifyGoFish(requestingId: Int, targetId: Int, requesterCard: Card) extends BroadcastMessage
case class NotifyGameOver(winnerId: Int)

sealed trait State
case object WaitingForPlayers extends State
case object PlayerTurn extends State
case object WaitingForResponse extends State
//case object Playing extends State
case object GameOver extends State

sealed trait GameData
case object Uninitialized extends GameData
case class OnePlayer(player1: ActorRef) extends GameData
case class WaitingPlayers(waitingPlayers: Map[Int, ActorRef]) extends GameData
case class GameState(dealer: Dealer, players: Map[Int, ActorRef], currPlayerId: Int) extends GameData {
  def incrPlayer: GameState = {
    new GameState(dealer, players, if (currPlayerId >= players.size - 1) 0 else (currPlayerId + 1))
  }

  def currPlayer: ActorRef = {
    players(currPlayerId)
  }
}

case class GamePlayer(playerRef: ActorRef, hand: PlayerHand)

class GameLogic extends Actor with LoggingFSM[State, GameData] {

  val MinPlayers = 2
  val NumStartingCards = 5

  startWith(WaitingForPlayers, WaitingPlayers(Map[Int, ActorRef]()))

  when(WaitingForPlayers) {
    case Event(Join, WaitingPlayers(players)) =>
      val playerId = players.size
      val newPlayer = context.actorOf(Props[Player], name = "" + playerId)
      val newPlayers = players + (playerId -> newPlayer)
      newPlayer ! YouAre(playerId, sender)

      if (newPlayers.size < MinPlayers) {
        goto(WaitingForPlayers) using WaitingPlayers(newPlayers)
      } else {
        val dealer = new Dealer(Deck.shuffledDeck)
        val playerHands = dealer.dealStartingHands(newPlayers.size, NumStartingCards)
        newPlayers.values.zip(playerHands).foreach { case (player, hand) => player ! YourHand(hand) }
        val startingState = GameState(dealer, newPlayers, 0)
        goto(PlayerTurn) using startingState
      }
  }

  when(PlayerTurn) {
    case Event(MakePlay(requesterId, targetId, card), gameState: GameState) if (gameState.currPlayer == sender) =>
      val target = gameState.players(targetId)
      target ! MatchRequested(requesterId, card)
      goto(WaitingForResponse)
    case Event(MakePlay, gameState: GameState) =>
      stay
  }

  when(WaitingForResponse) {
    case Event(MatchFound, gameState: GameState) =>
      val newState = gameState.incrPlayer
      newState.currPlayer ! YourTurn(newState.currPlayerId)
      goto(PlayerTurn) using newState
    case Event(GoFish, gameState: GameState) =>
      val newState = gameState.incrPlayer
      newState.currPlayer ! YourTurn(newState.currPlayerId)
      goto(PlayerTurn) using newState
  }
  
  whenUnhandled {
    case Event(QueryState, gameState: GameState) =>
      sender ! NotifyGameState(gameState)
      stay
  }
  
  onTransition {
    case _ -> PlayerTurn =>
      nextStateData match {
        case gameState: GameState =>
          gameState.currPlayer ! YourTurn(gameState.currPlayerId)
        case _ =>
        //don't care
      }
    case x -> y => println("Moved from " + x + " to " + y)
  }
}