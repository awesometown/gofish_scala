package ca.uptoeleven.gofish

class Match(card1: Card, card2: Card) {
	require(card1.rank == card2.rank)
	
	override def toString = {
	  "Match: " + card1 + ", " + card2
	}
}