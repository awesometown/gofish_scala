package ca.uptoeleven.gofish

class InsufficientCardsException extends RuntimeException

class Dealer(startingCards: List[Card]) {

  private var cards = startingCards

  def dealStartingHands(numHands: Int, numCards: Int) = {
    val emptyHands = List.fill[PlayerHand](numHands)(new PlayerHand)
    dealCardsToAllPlayers(emptyHands, numCards)
  }
  
  def dealCardsToAllPlayers(hands: List[PlayerHand], numCards: Int) : List[PlayerHand] = {
    checkCardCount(hands.size * numCards)
    if (numCards == 0) {
      hands
    } else {
      val newHands = dealCardToAllPlayers(hands)
      dealCardsToAllPlayers(newHands, numCards-1)
    }
  }
  
  def dealCardToAllPlayers(hands: List[PlayerHand]): (List[PlayerHand]) = {
    checkCardCount(hands.size)
    val (newHands, remainingCards) = dealCardToAllPlayersInternal(hands, cards)
    cards = remainingCards
    newHands
  }
    
  private def dealCardToAllPlayersInternal(hands: List[PlayerHand], availableCards: List[Card]): (List[PlayerHand], List[Card]) = {
    if (hands == Nil) {
      (List[PlayerHand](), availableCards)
    } else {
      val newHand = hands.head.addCard(availableCards.head)
      val (newHands, remainingCards) = dealCardToAllPlayersInternal(hands.tail, availableCards.tail)
      (newHand :: newHands, remainingCards)
    }
  }
  
  private def checkCardCount(needed: Int) {
    if (cards.size < needed) {
      throw new InsufficientCardsException
    }
  }
}