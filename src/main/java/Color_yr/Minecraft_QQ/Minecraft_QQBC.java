package Color_yr.Minecraft_QQ;

import Color_yr.Minecraft_QQ.Command.CommandBC;
import Color_yr.Minecraft_QQ.Config.Load;
import Color_yr.Minecraft_QQ.Listener.BCEvent;
import Color_yr.Minecraft_QQ.Side.IBungeecord;
import Color_yr.Minecraft_QQ.Socket.SocketControl;
import Color_yr.Minecraft_QQ.Utils.logs;
import Color_yr.Minecraft_QQ.bStats.MetricsBC;
import com.google.gson.Gson;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Logger;

public class Minecraft_QQBC extends Plugin {

    public static Logger log_b;
    public static Plugin plugin;

    public static void Load() {
        try {
            new Load(plugin.getDataFolder());
        } catch (Throwable e) {
            log_b.warning("§d[Minecraft_QQ]§c配置文件读取发生错误");
            e.printStackTrace();
        }
    }

    public static void Save() {
        try {
            String data = new Gson().toJson(Minecraft_QQ.Config);
            if (Minecraft_QQ.FileName.exists()) {
                Writer out = new FileWriter(Minecraft_QQ.FileName);
                out.write(data);
                out.close();
            }
        } catch (Exception e) {
            log_b.warning("§d[Minecraft_QQ]§c配置文件保存错误");
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {

        plugin = this;
        log_b = ProxyServer.getInstance().getLogger();

        Minecraft_QQ.MinecraftQQ = new IBungeecord();

        log_b.info("§d[Minecraft_QQ]§e正在启动，感谢使用，本插件交流群：571239090");

        Load();

        try {
            new logs(plugin.getDataFolder());
            File wiki = new File(plugin.getDataFolder(), "Wiki.txt");
            if (!wiki.exists()) {
                Files.copy(new ByteArrayInputStream(Minecraft_QQ.Wiki.getBytes()), wiki.toPath());
            }
        } catch (IOException e) {
            log_b.warning("§d[Minecraft_QQ]§c日志文件错误");
            e.printStackTrace();
        }

        ProxyServer.getInstance().getPluginManager().registerListener(this, new BCEvent());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandBC());

        new MetricsBC(this, 6608);

        log_b.info("§d[Minecraft_QQ]§e已启动-" + Minecraft_QQ.Version);
        log_b.info("§d[Minecraft_QQ]§eDebug模式" + Minecraft_QQ.Config.getSystem().isDebug());

        SocketControl socket = new SocketControl();
        socket.Start();
    }

    @Override
    public void onDisable() {
        Minecraft_QQ.hand.server_isclose = true;
        new SocketControl().Close();
        log_b.info("§d[Minecraft_QQ]§e已停止，感谢使用");
    }
}
