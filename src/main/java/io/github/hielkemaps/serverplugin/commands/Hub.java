package io.github.hielkemaps.serverplugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.hielkemaps.serverplugin.Main;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Hub {
    public Hub() {
        (new CommandAPICommand("hub")).withAliases("lobby").executesPlayer((p, args) -> {
            ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytearray);

            try {
                out.writeUTF("Connect");
                out.writeUTF("lobby");
            } catch (IOException var5) {
                var5.printStackTrace();
            }

            p.sendPluginMessage(Main.getInstance(), "BungeeCord", bytearray.toByteArray());
        }).register();
    }
}
