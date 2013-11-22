package ca.uptoeleven.gofish

class Deck(c: List[Card]) {
  val cards = c
  
  def this() = {
    this(Deck.AllCardsInOrder)
  }

  def shuffle = {
    new Deck(util.Random.shuffle(cards))
  }
  
  def dealCard = {
    (cards.head, cards.tail)
  }
}

object Deck {
  val Suits = List(Spades, Diamonds, Hearts, Clubs)
  val Ranks = 1 to 13
  val AllCardsInOrder = for (z <- Deck.Suits; rank <- Deck.Ranks) yield (new Card(z, rank))
}