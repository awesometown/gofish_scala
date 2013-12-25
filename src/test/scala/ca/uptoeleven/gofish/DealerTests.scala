package ca.uptoeleven.gofish

import org.junit.Test
import ca.uptoeleven.gofish._


class DealerTests {
  @Test def dealSingleCardToSingleStartingHandGivesOneCard = {
    val dealer = new Dealer(Deck.shuffledDeck)
    val hands = dealer.dealStartingHands(1,1)
    assert(hands.size == 1)
    assert(hands(0).cards.size == 1)
  }
  
  @Test def deal5CardsTo5StartingHandsGives25Cards = {
    val dealer = new Dealer(Deck.shuffledDeck)
    val hands = dealer.dealStartingHands(5, 5)
    assert(hands.size == 5)
    assert(hands.forall(h => h.cards.size == 5))
  }
  
  @Test def dealCardToExistingHandAddsSingleCardToHead = {
    val dealer = new Dealer(Deck.shuffledDeck)
    val hands = dealer.dealStartingHands(1, 3)
    val newHands = dealer.dealCardToAllPlayers(hands)
    assert(newHands(0).cards.size == hands(0).cards.size + 1)
    assert(newHands(0).cards.tail == hands(0).cards)
  }
  
//  @Test def remainingDeckLessOneCardAfterDeal = {
//    val deck = Deck.shuffledDeck
//    val playerHands = List(new PlayerHand())
//    val (dealtHands, remainingDeck) = deck.dealCards(playerHands)
//    assert(deck.size - 1 == remainingDeck.size)
//  }
//  
//  @Test def dealCardToFivePlayersRemovesFiveCards = {
//    val deck = Deck.shuffledDeck
//    val playerHands = List.fill(5) {new PlayerHand()}
//    val (dealtHands, remainingDeck) = deck.dealCards(playerHands)
//    assert(deck.size -5 == remainingDeck.size)
//  }
//  
//  @Test def dealHandsShouldDoStuff() {
//   val deck = Deck.shuffledDeck
//   val playerHands = List(new PlayerHand(), new PlayerHand())
//   val (dealtHands, remainingDeck) = deck.dealCards(playerHands)
//   println(dealtHands)
//   val (twoCardHands, remainingDeck2) = remainingDeck.dealCards(dealtHands)
//   println(twoCardHands)
////    val stack = new Stack[Int]
////    stack.push(1)
////    stack.push(2)
////    assert(stack.pop() === 2)
////    assert(stack.pop() === 1)
//  }
}