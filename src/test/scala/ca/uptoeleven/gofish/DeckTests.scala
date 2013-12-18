package ca.uptoeleven.gofish
import org.junit.Test
import ca.uptoeleven.gofish._

class DeckTests {
 
  @Test def dealCardsToSingleHandDealsSingleCard = {
    val deck = Deck.shuffledDeck
    val playerHands = List(new PlayerHand())
    val (dealtHands, remainingDeck) = deck.dealCards(playerHands)
    assert(dealtHands(0).cards.size == 1)
  }
  
  @Test def remainingDeckLessOneCardAfterDeal = {
    val deck = Deck.shuffledDeck
    val playerHands = List(new PlayerHand())
    val (dealtHands, remainingDeck) = deck.dealCards(playerHands)
    assert(deck.size - 1 == remainingDeck.size)
  }
  
  @Test def dealCardToFivePlayersRemovesFiveCards = {
    val deck = Deck.shuffledDeck
    val playerHands = List.fill(5) {new PlayerHand()}
    val (dealtHands, remainingDeck) = deck.dealCards(playerHands)
    assert(deck.size -5 == remainingDeck.size)
  }
  
  @Test def dealHandsShouldDoStuff() {
   val deck = Deck.shuffledDeck
   val playerHands = List(new PlayerHand(), new PlayerHand())
   val (dealtHands, remainingDeck) = deck.dealCards(playerHands)
   println(dealtHands)
   val (twoCardHands, remainingDeck2) = remainingDeck.dealCards(dealtHands)
   println(twoCardHands)
//    val stack = new Stack[Int]
//    stack.push(1)
//    stack.push(2)
//    assert(stack.pop() === 2)
//    assert(stack.pop() === 1)
  }
}