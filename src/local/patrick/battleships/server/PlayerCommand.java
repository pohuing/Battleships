package local.patrick.battleships.server;

import local.patrick.battleships.common.Command;

public class PlayerCommand {
    public final PlayerTag player;
    public final Command command;


    public PlayerCommand(PlayerTag player, Command command) {
        this.player = player;
        this.command = command;
    }
}
