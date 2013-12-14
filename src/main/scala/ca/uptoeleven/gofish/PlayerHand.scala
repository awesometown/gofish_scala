package ca.uptoeleven.gofish

class PlayerHand(cards: List[Card]) {
	def this() = {
	  this(List())
	}
	
	def addCard(card: Card) {
	  new PlayerHand(card :: cards)
	}
}