package ca.uptoeleven.gofish

object App {
  
  def main(args : Array[String]) {
    var (card, deck) = (new Deck).shuffle.dealCard
  }
}
