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

case class GamePlayer(playerRef: ActorRef, hand: PlayerHand)

case class WaitingPlayers(waitingPlayers: List[ActorRef]) extends GameData

case class GameState(dealer: Dealer, players: List[GamePlayer], currPlayerId: Int) extends GameData {
  def incrPlayer: GameState = {
    new GameState(dealer, players, if (currPlayerId >= players.size-1) 0 else (currPlayerId + 1))
  }

  def currPlayer: GamePlayer = {
    players(currPlayerId)
  }
}

class GameLogic extends Actor with LoggingFSM[State, GameData] {

  val MinPlayers = 2
  val StartingCards = 5

  startWith(WaitingForPlayers, WaitingPlayers(List[ActorRef]()))

  when(WaitingForPlayers) {
    case Event(Join, WaitingPlayers(players)) =>
      val playerId = players.size
      println("player " + playerId + " joined")
      sender ! YouAre(playerId)
      
      val newPlayers = (sender :: players).reverse
      if(newPlayers.size < MinPlayers) {
        goto(WaitingForPlayers) using WaitingPlayers(newPlayers)
      } else {
        val startingState = buildStartingGameState(newPlayers)
        startingState.players.foreach(player => player.playerRef ! NotifyHand(player.hand))
        goto(Playing) using startingState
      }
  }
    
  when(Playing) {
    case Event(Play(card), gameState: GameState) =>
      println("Got message")
      println(gameState.players)
      println(gameState.currPlayer)
      println(sender)
      if (gameState.currPlayer.playerRef == sender) {
        println("Got message from correct player")
        val newState = gameState.incrPlayer
        newState.currPlayer.playerRef ! NotifyPlayerTurn(newState.currPlayerId)
        goto(Playing) using newState
      } else {
        println("Got play message from some other dude")
        stay
      }
  }

  onTransition {
    case _ -> Playing =>
      nextStateData match {
        case gameState: GameState =>
          println("notify player")
          gameState.currPlayer.playerRef ! NotifyPlayerTurn(gameState.currPlayerId)
        case _ =>
          //don't care
      }
    case x -> y => println("Moved from " + x + " to " + y)
  }
  
  def buildStartingGameState(playerRefs: List[ActorRef]) = {
    val dealer = new Dealer(Deck.shuffledDeck)
    val playerHands = dealer.dealStartingHands(playerRefs.size, StartingCards)
    val players = playerRefs.zip(playerHands) map { case(ref, hand) => new GamePlayer(ref, hand) }
    new GameState(dealer, players, 0)
  }
}