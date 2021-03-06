package Color_yr.Minecraft_QQ.Side.SideVelocity;

import Color_yr.Minecraft_QQ.API.IMinecraft_QQ;
import Color_yr.Minecraft_QQ.API.Placeholder;
import Color_yr.Minecraft_QQ.Json.ReadOBJ;
import Color_yr.Minecraft_QQ.Minecraft_QQ;
import Color_yr.Minecraft_QQ.Minecraft_QQVelocity;
import Color_yr.Minecraft_QQ.Side.ASide;
import Color_yr.Minecraft_QQ.Utils.Function;
import Color_yr.Minecraft_QQ.Utils.logs;
import com.google.gson.Gson;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.util.Collection;

public class SideVelocity implements IMinecraft_QQ {
    @Override
    public void send(Object sender, String message) {
        CommandSource temp = (CommandSource) sender;
        temp.sendMessage(Component.text(message));
    }

    @Override
    public void message(String message) {
        try {
            String msg = message;
            if (Minecraft_QQ.Config.getSystem().isDebug())
                Minecraft_QQ.log.info("处理数据：" + msg);
            ProxyServer proxyserver = Minecraft_QQVelocity.plugin.server;
            while (msg.indexOf(Minecraft_QQ.Config.getSystem().getHead()) == 0 && msg.contains(Minecraft_QQ.Config.getSystem().getEnd())) {
                String buff = Function.get_string(msg, Minecraft_QQ.Config.getSystem().getHead(), Minecraft_QQ.Config.getSystem().getEnd());
                ReadOBJ readobj;
                try {
                    Gson read_gson = new Gson();
                    readobj = read_gson.fromJson(buff, ReadOBJ.class);
                } catch (Exception e) {
                    Minecraft_QQ.log.info("数据传输发生错误:" + e.getMessage());
                    return;
                }
                if (readobj.getIsCommand().equals("false")) {
                    if (readobj.getCommand().equalsIgnoreCase(Placeholder.speak)) {
                        String say = Minecraft_QQ.Config.getServerSet().getSay()
                                .replaceFirst(Minecraft_QQ.Config.getPlaceholder().getServerName(), Minecraft_QQ.Config.getServerSet().getServerName())
                                .replaceFirst(Minecraft_QQ.Config.getPlaceholder().getMessage(), readobj.getMessage())
                                .replaceFirst(Minecraft_QQ.Config.getPlaceholder().getPlayer(), readobj.getPlayer());
                        say = say.replaceAll("&", "§");
                        if (Minecraft_QQ.Config.getLogs().isGroup()) {
                            logs.logWrite("[Group]" + say);
                        }
                        for (Player player1 : proxyserver.getAllPlayers()) {
                            if (!Minecraft_QQ.Config.getMute().contains(player1.getUsername()))
                                player1.sendMessage(Component.text(say));
                        }
                    } else if (readobj.getCommand().equalsIgnoreCase(Placeholder.online)) {
                        int allPlayerNumber = 0;
                        StringBuilder allServerPlayer = new StringBuilder();
                        String send = Minecraft_QQ.Config.getServerSet().getPlayerListMessage();
                        if (Minecraft_QQ.Config.getServerSet().isSendOneByOne()) {
                            for (final RegisteredServer serverinfo : proxyserver.getAllServers()) {
                                String oneServerPlayer;
                                int oneServerNumber;
                                final Collection<Player> oneServerPlayers = serverinfo.getPlayersConnected();
                                if (oneServerPlayers.size() == 0) {
                                    if (!Minecraft_QQ.Config.getServerSet().isHideEmptyServer()) {
                                        String serverName = Minecraft_QQ.Config.getServers().get(serverinfo.getServerInfo().getName());
                                        if (serverName == null || serverName.isEmpty()) {
                                            serverName = serverinfo.getServerInfo().getName();
                                        }
                                        oneServerPlayer = Minecraft_QQ.Config.getServerSet().getSendOneByOneMessage()
                                                .replaceAll(Minecraft_QQ.Config.getPlaceholder().getServer(), serverName)
                                                .replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerNumber(), "0")
                                                .replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerList(), "无");
                                        allServerPlayer.append(oneServerPlayer);
                                    }
                                } else {
                                    oneServerNumber = oneServerPlayers.size();
                                    String serverName = Minecraft_QQ.Config.getServers().get(serverinfo.getServerInfo().getName());
                                    if (serverName == null || serverName.isEmpty()) {
                                        serverName = serverinfo.getServerInfo().getName();
                                    }
                                    StringBuilder players = new StringBuilder();
                                    for (Player player : oneServerPlayers) {
                                        players.append(player.getUsername()).append(",");
                                    }
                                    String players1 = players.toString();
                                    oneServerPlayer = Minecraft_QQ.Config.getServerSet().getSendOneByOneMessage()
                                            .replaceAll(Minecraft_QQ.Config.getPlaceholder().getServer(), serverName)
                                            .replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerNumber(), "" + oneServerNumber)
                                            .replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerList(), players1.substring(0, players1.length() - 1));
                                    allPlayerNumber += oneServerNumber;
                                    allServerPlayer.append(oneServerPlayer);
                                }
                            }
                            if (allPlayerNumber == 0) {
                                send = send.replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerNumber(), "0")
                                        .replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerList(), "无");
                            } else {
                                send = send.replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerNumber(), "" + allPlayerNumber)
                                        .replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerList(), allServerPlayer.toString());
                            }
                        } else {
                            final Collection<Player> players = proxyserver.getAllPlayers();
                            if (players.size() == 0) {
                                send = send.replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerNumber(), "0")
                                        .replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerList(), "无");
                            } else {
                                StringBuilder temp = new StringBuilder();
                                for (Player player : players) {
                                    temp.append(player.getUsername()).append(",");
                                }
                                String players1 = temp.toString();
                                send = send.replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerNumber(), "" + players.size())
                                        .replaceAll(Minecraft_QQ.Config.getPlaceholder().getPlayerList(), players1.substring(0, players1.length() - 1));
                            }
                        }
                        send = send.replace(Minecraft_QQ.Config.getPlaceholder().getServerName(), Minecraft_QQ.Config.getServerSet().getServerName());
                        boolean sendok = Minecraft_QQ.control.sendData(Placeholder.data, readobj.getGroup(), "无", send);
                        if (!sendok)
                            Minecraft_QQ.log.warning("§d[Minecraft_QQ]§c数据发送失败");
                        if (Minecraft_QQ.Config.getLogs().isGroup()) {
                            logs.logWrite("[group]查询在线人数");
                        }
                    } else {
                        ASide.globeCheck(readobj);
                    }
                } else if (readobj.getIsCommand().equals("true")) {
                    StringBuilder send_message;
                    VelocityCommander send = new VelocityCommander(readobj.getPlayer());
                    if (Minecraft_QQ.Config.getLogs().isGroup()) {
                        logs.logWrite("[Group]" + readobj.getPlayer() + "执行命令" + readobj.getCommand());
                    }
                    try {
                        proxyserver.getCommandManager().executeImmediatelyAsync(send, readobj.getCommand());
                        Thread.sleep(Minecraft_QQ.Config.getServerSet().getCommandDelay());
                    } catch (Exception e) {
                        Minecraft_QQ.log.info(e.toString());
                    }
                    if (send.getMessage().size() == 1) {
                        send_message = new StringBuilder(send.getMessage().get(0));
                    } else if (send.getMessage().size() > 1) {
                        send_message = new StringBuilder(send.getMessage().get(0));
                        for (int i = 1; i < send.getMessage().size(); i++) {
                            send_message.append("\n");
                            send_message.append(send.getMessage().get(i));
                        }
                    } else
                        send_message = new StringBuilder("已执行，指令无返回");
                    Minecraft_QQ.control.sendData(Placeholder.data, readobj.getGroup(),
                            "控制台", send_message.toString());
                }
                int i = msg.indexOf(Minecraft_QQ.Config.getSystem().getEnd());
                msg = msg.substring(i + Minecraft_QQ.Config.getSystem().getEnd().length());
            }
        } catch (Exception e) {
            Minecraft_QQ.log.warning("§d[Minecraft_QQ]§c发送错误：");
            e.printStackTrace();
        }
    }
}
