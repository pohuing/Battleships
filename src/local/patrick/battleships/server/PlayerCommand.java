package local.patrick.battleships.server;

import local.patrick.battleships.common.commands.Command;

/**
 * Contains additional information on which of the two players has sent the command
 * Workaround to Java's lack of a Pair class or Tuples
 */
public class PlayerCommand {
    public final PlayerTag player;
    public final Command command;


    public PlayerCommand(PlayerTag player, Command command) {
        this.player = player;
        this.command = command;
    }
}
