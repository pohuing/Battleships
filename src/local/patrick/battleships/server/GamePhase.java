package local.patrick.battleships.server;

public enum GamePhase {
    /**
     * Phase in which players are allowed to place ships
     */
    PREPARATION,
    /**
     * Phase in which players are allowed to shoot at each other
     */
    BATTLE,
    /**
     * Phase after either player has destroyed all opposing ships, players may only get ask for the game state
     */
    FINISHED
}
