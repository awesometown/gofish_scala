package ca.uptoeleven.gofish

import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }

object App {
  
  def main(args : Array[String]) {
    //var (card, deck) = (new Deck).shuffle.dealCard
    //println(card)
    
    val system = ActorSystem("gameSystem")
    val gameLogic = system.actorOf(Props[GameLogic], "gameLogic")
    val puppet1 = system.actorOf(Props[Puppet], "player1")
    val puppet2 = system.actorOf(Props[Puppet], "player2")
    
    val inbox = Inbox.create(system)
    gameLogic.tell(Join, puppet1)
    gameLogic.tell(Join, puppet2)
    
    Thread.sleep(5000)
    
    system.shutdown
    println("done")
  }
  
  class Puppet extends Actor {
    def receive = {
      case whatever => println(whatever)
    }
  }
}
