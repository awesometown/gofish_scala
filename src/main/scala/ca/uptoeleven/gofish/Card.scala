package ca.uptoeleven.gofish

class Card(suit: Suit, rank: Int) {
  override def  toString = {
    "Card (" + suit + ", " + rank + ")"
  }
}