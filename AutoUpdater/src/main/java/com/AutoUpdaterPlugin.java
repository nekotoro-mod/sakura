package com.example.autospigotupdater;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class AutoSpigotUpdater extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("AutoSpigotUpdater が有効になりました。");
    }

    @Override
    public void onDisable() {
        getLogger().info("AutoSpigotUpdater が無効になりました。");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("autospigotupdater.use")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("update")) {
            sender.sendMessage("§eSpigotのアップデートを開始します...");

            // 非同期でビルド実行
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    // BuildTools.jar があるかチェック
                    File buildTools = new File("BuildTools.jar");
                    if (!buildTools.exists()) {
                        sender.sendMessage("§cBuildTools.jar が見つかりません。");
                        return;
                    }

                    // BuildTools 実行
                    ProcessBuilder pb = new ProcessBuilder("java", "-jar", "BuildTools.jar", "--rev", "1.20.1");
                    pb.directory(new File(".")); // サーバーディレクトリで実行
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        getLogger().info(line);
                    }

                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        sender.sendMessage("§aアップデート完了。サーバーを再起動してください。");
                    } else {
                        sender.sendMessage("§cアップデートに失敗しました。");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("§cエラーが発生しました：" + e.getMessage());
                }
            });

            return true;
        }

        return false;
    }
}
")