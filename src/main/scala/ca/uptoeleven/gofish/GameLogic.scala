package ca.uptoeleven.gofish

import akka.actor.{ Actor, ActorRef, FSM }
import akka.actor.LoggingFSM

case object Join
case class Play(card: Card)

case class YouAre(playerId: Int)
case class NotifyHand(hand: PlayerHand)
case class NotifyPlayerTurn(playerId: Int)
case class NotifyGameOver(winnerId: Int)
case class MakePlay(targetPlayerId: Int, requestedRank: Int)

case class NotifyState(gameState: GameState)

sealed trait State
case object WaitingForPlayers extends State
case object Playing extends State
case object GameOver extends State

sealed trait GameData
case object Uninitialized extends GameData
case class OnePlayer(player1: ActorRef) extends GameData

case class GamePlayerState(hand: PlayerHand)

case class GameState(players: List[ActorRef], currPlayerId: Int) extends GameData {
  def incrPlayer: GameState = {
    new GameState(players, if (currPlayerId >= players.size) 0 else (currPlayerId + 1))
  }

  def currPlayer: ActorRef = {
    players(currPlayerId)
  }
}

class GameLogic extends Actor with LoggingFSM[State, GameData] {

  val MinPlayers = 2

  startWith(WaitingForPlayers, Uninitialized)

  when(WaitingForPlayers) {
    case Event(Join, Uninitialized) =>
      val playerId = 0
      println("player" + playerId + " joined")
      sender ! YouAre(playerId)
      stay using GameState(List(sender), 0)
    case Event(Join, GameState(players, currPlayerId)) =>
      val playerId = players.size
      println("player" + playerId + " joined")
      sender ! YouAre(playerId)

      val newState = GameState((sender :: players).reverse, currPlayerId)
      //newState.currPlayer ! NotifyPlayerTurn(currPlayerId)
      goto(Playing) using newState
  }

  def dealHands(gameState: GameState) = {
    val deck = Deck.shuffledDeck
    for (player <- gameState.players) {
      var playerHand = new PlayerHand
      for (i <- 1 to 5) {

      }
    }
  }

  def dealCardToAllPlayers(hands: List[PlayerHand], deck: Deck) {
    hands.fo
  }
  
  def dealCard(hand: PlayerHand, deck: Deck) = {
    val (card, newDeck) = deck.dealCard
    (hand.addCard(card), newDeck)
  }

  when(Playing) {
    case Event(Play(card), gameState: GameState) =>
      if (gameState.currPlayer == sender) {
        println("Got message from correct player")
        val newState = gameState.incrPlayer
        //newState.currPlayer ! NotifyPlayerTurn(newState.currPlayerId)
        goto(Playing) using newState
      } else {
        println("Got play message from some other dude")
        stay
      }
    //    case Event(Play, _) =>
    //      println("hmm...")
    //      stay
  }

  onTransition {
    case _ -> Playing =>
      stateData match {
        case gameState: GameState =>
          gameState.currPlayer ! NotifyPlayerTurn(gameState.currPlayerId)
        case _ =>
        //don't care
      }
    case x -> y => println("Moved from " + x + " to " + y)
  }
}