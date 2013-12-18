package ca.uptoeleven.gofish

class PlayerHand(c: List[Card]) {
  val cards = c
  
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