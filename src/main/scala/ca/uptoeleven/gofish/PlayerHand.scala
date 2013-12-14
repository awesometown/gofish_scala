package ca.uptoeleven.gofish

class PlayerHand(cards: List[Card]) {
  def this() = {
    this(List())
  }

  def addCard(card: Card): PlayerHand = {
    new PlayerHand(card :: cards)
  }
  
  override def toString() = {
    cards.toString
  }
}