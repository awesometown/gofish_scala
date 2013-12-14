package ca.uptoeleven.gofish

import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }

object App {
  
  def main(args : Array[String]) {
    //var (card, deck) = (new Deck).shuffle.dealCard
    //println(card)
    
    val system = ActorSystem("gameSystem")
    val gameLogic = system.actorOf(Props[GameLogic], "gameLogic")
    val dummy1 = system.actorOf(Props[Player], "dummy1")
    val dummy2 = system.actorOf(Props[Player], "dummy2")
    
    val inbox = Inbox.create(system)
    dummy1.tell(JoinGame(gameLogic), ActorRef.noSender)
    dummy2.tell(JoinGame(gameLogic), ActorRef.noSender)
    
    //gameLogic.tell(Play(new Card(Clubs, 1)), dummy1)
    //gameLogic.tell(Play(new Card(Clubs, 5)), dummy1)
    //gameLogic.tell(Play(new Card(Clubs, 2)), dummy2)
    
    Thread.sleep(2000)
    
    system.shutdown
    println("done")
  }
  
  class Dummy extends Actor {
    def receive = {
      case whatever => println(whatever)
    }
  }
}
