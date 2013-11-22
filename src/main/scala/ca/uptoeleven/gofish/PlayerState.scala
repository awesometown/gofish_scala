package ca.uptoeleven.gofish

import akka.actor.ActorRef

case class PlayerState(ref: ActorRef, hand: List[Card], matches: List[Match]) {

}