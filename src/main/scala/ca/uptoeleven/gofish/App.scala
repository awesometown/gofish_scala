package ca.uptoeleven.gofish

import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }

object App {
  
  def main(args : Array[String]) {
    //var (card, deck) = (new Deck).shuffle.dealCard
    //println(card)
    
    val system = ActorSystem("gameSystem")
    val gameLogic = system.actorOf(Props[GameLogic], "gameLogic")
    val player1 = system.actorOf(Props[Player], "player1")
    val player2 = system.actorOf(Props[Player], "player2")
    
    val inbox = Inbox.create(system)
    player1.tell(JoinGame(gameLogic), ActorRef.noSender)
    player2.tell(JoinGame(gameLogic), ActorRef.noSender)
    
    //gameLogic.tell(Play(new Card(Clubs, 1)), dummy1)
    //gameLogic.tell(Play(new Card(Clubs, 5)), dummy1)
    //gameLogic.tell(Play(new Card(Clubs, 2)), dummy2)
    
    Thread.sleep(5000)
    
    system.shutdown
    println("done")
  }
  
//  class Dummy extends Actor {
//    def receive = {
//      case whatever => println(whatever)
//    }
//  }
}
