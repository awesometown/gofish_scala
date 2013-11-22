package ca.uptoeleven.gofish

case class GameState(
    player1State: PlayerState,
    player2State: PlayerState,
    remainingCards: Deck) {
}