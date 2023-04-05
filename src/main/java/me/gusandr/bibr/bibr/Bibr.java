package me.gusandr.bibr.bibr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Bibr extends JavaPlugin implements Listener {

    private Map<String, String> playerPrefixes = new HashMap<String, String>(); // Словарь для хранения кастомных префиксов

    @Override
    public void onEnable() {

        saveDefaultConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        loadConfig();
    }

    public void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Can't create config file!", e);
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Сохраняем кастомные префиксы из конфига в словарь
        for (String key : config.getKeys(false)) {
            playerPrefixes.put(key, config.getString(key));
        }
    }

    public void saveConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Сохраняем кастомные префиксы из словаря в конфиг
        for (Map.Entry<String, String> entry : playerPrefixes.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Can't save config file!", e);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String playerName = p.getName();
        String message = e.getMessage();

        // Получаем кастомный префикс игрока
        String playerPrefix = playerPrefixes.getOrDefault(playerName, "");

        // Добавляем префикс к сообщению и отменяем его отправку
        e.setCancelled(true);
        getServer().broadcastMessage(playerPrefix + " " + ChatColor.RESET + message);
    }

    public String getPlayerPrefix(String playerName) {
        return playerPrefixes.getOrDefault(playerName, "");
    }

    public void setPlayerPrefix(String playerName, String prefix) {
        playerPrefixes.put(playerName, prefix);
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}
