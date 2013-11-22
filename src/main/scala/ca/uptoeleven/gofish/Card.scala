package ca.uptoeleven.gofish

case class Card(suit: Suit, rank: Int) {  
  override def  toString = {
    "Card (" + suit + ", " + rank + ")"
  }
}