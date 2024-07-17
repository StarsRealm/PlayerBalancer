package com.jaimemartz.playerbalanceraddon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaimemartz.playerbalanceraddon.util.ConfigurationFile;
import lombok.Getter;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

@Getter
public class PlayerBalancerAddon extends JavaPlugin implements Listener {
    private PluginMessageManager manager;
    private RedisEventListener redisEventListener;
    private PlayerBalancerPlaceholderExpansion expansion;
    private ConfigurationFile config;
    private String version = "2.3.6";

    @Override
    public void onEnable() {
        config = new ConfigurationFile(this, "config.yml");
        manager = new PluginMessageManager(this);
        redisEventListener = new RedisEventListener();
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("spb").setExecutor(new MainCommand(this));
        updateCheck();
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            expansion = new PlayerBalancerPlaceholderExpansion(this);
            expansion.register();
        }
    }

    @Override
    public void onDisable() {
        try {
            redisEventListener.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        ((CraftPlayer) event.getPlayer()).addChannel(PluginMessageManager.PB_CHANNEL);
    }

    public void updateCheck() {
        try {
            String urlString = "https://updatecheck.bghddevelopment.com";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuffer response = new StringBuffer();
            while ((input = reader.readLine()) != null) {
                response.append(input);
            }
            reader.close();
            JsonObject object = new JsonParser().parse(response.toString()).getAsJsonObject();

            if (object.has("plugins")) {
                JsonObject plugins = object.get("plugins").getAsJsonObject();
                JsonObject info = plugins.get("PlayerBalancer").getAsJsonObject();
                String version = info.get("version").getAsString();
                if (version.equals(this.getVersion())) {
                    getLogger().log(Level.INFO, ("PlayerBalancerAddon is on the latest version."));
                } else {
                    getLogger().log(Level.WARNING, (""));
                    getLogger().log(Level.WARNING, (""));
                    getLogger().log(Level.WARNING, ("Your PlayerBalancerAddon version is out of date!"));
                    getLogger().log(Level.WARNING, ("We recommend updating ASAP!"));
                    getLogger().log(Level.WARNING, (""));
                    getLogger().log(Level.WARNING, ("Your Version:" + getDescription().getVersion()));
                    getLogger().log(Level.WARNING, ("Newest Version:" + version));
                    getLogger().log(Level.WARNING, (""));
                    getLogger().log(Level.WARNING, (""));
                    return;
                }
                return;
            } else {
                getLogger().log(Level.SEVERE, ("&cWrong response from update API, contact plugin developer!"));
                return;
            }
        } catch (
                Exception ex) {
            getLogger().log(Level.SEVERE, ("&cFailed to get updater check. (" + ex.getMessage() + ")"));
            return;
        }
    }

    public PluginMessageManager getManager() {
        return manager;
    }

    public RedisEventListener getRedisEventListener() {
        return redisEventListener;
    }
}
