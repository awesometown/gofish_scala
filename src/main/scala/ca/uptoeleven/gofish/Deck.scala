package ca.uptoeleven.gofish



class Deck(c: List[Card]) {
  val cards = c
 
  implicit def listToDeck(cards: List[Card]) = new Deck(cards)
  
  def this() = {
    this(Deck.AllCardsInOrder)
  }

  def shuffle = {
    new Deck(util.Random.shuffle(cards))
  }
  
  def size = {
    cards.size
  }
  
  def dealCard = {
    (cards.head, new Deck(cards.tail))
  }
  
  def dealCards(hands: List[PlayerHand]): (List[PlayerHand], Deck) = {
    val (newHands, remainingCards) = dealCardsInternal(hands, c)
    (newHands, new Deck(remainingCards))
  }
  
  def dealCardsInternal(hands: List[PlayerHand], availableCards: List[Card]) : (List[PlayerHand], List[Card]) = {
    if (hands == Nil) {
      (List[PlayerHand](), availableCards)
    } else {
      val newHand = hands.head.addCard(c.head)
      val (newHands, remainingCards) = dealCardsInternal(hands.tail, availableCards.tail)
      (newHand :: newHands, remainingCards)
    }
  }
}

object Deck {
  val Suits = List(Spades, Diamonds, Hearts, Clubs)
  val Ranks = 1 to 13
  val AllCardsInOrder = for (z <- Deck.Suits; rank <- Deck.Ranks) yield (new Card(z, rank))
  
  val shuffledDeck = {
    util.Random.shuffle(AllCardsInOrder)
  }
  
  //def shuffledDeck = {
  //  new Deck(util.Random.shuffle(AllCardsInOrder))
  //}
  
//  def dealCard(deck: List[Card]) = {
//    (deck.head, deck.tail)
//  }
  
//  def dealCards(hands: List[PlayerHand], deck: List[Card]): (List[PlayerHand], List[Card]) = {
//    if (hands == Nil) {
//      (List[PlayerHand](), deck)
//    } else {
//      val newHand = hands.head.addCard(deck.head)
//      val (newHands, remainingDeck) = dealCards(hands.tail, deck.tail)
//      (newHand :: newHands, remainingDeck)
//    }
//  }
}