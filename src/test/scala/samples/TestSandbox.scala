package samples

import scala.collection.mutable.Stack
import org.scalatest.Assertions
import org.junit.Test
import ca.uptoeleven.gofish._

class TestSandbox {
 @Test def dealHandsShouldDoStuff() {
   val deck = Deck.shuffledDeck
   val playerHands = List(new PlayerHand(), new PlayerHand())
   val (dealtHands, remainingDeck) = Deck.dealCards(playerHands, deck)
   println(dealtHands)
   val (twoCardHands, remainingDeck2) = Deck.dealCards(dealtHands, remainingDeck)
   println(twoCardHands)
//    val stack = new Stack[Int]
//    stack.push(1)
//    stack.push(2)
//    assert(stack.pop() === 2)
//    assert(stack.pop() === 1)
  }
}