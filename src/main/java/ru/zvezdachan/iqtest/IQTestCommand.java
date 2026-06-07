package ru.zvezdachan.iqtest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.geyser.api.GeyserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IQTestCommand implements CommandExecutor {

    private final IQTestPlugin plugin;
    private final FormManager formManager;

    public IQTestCommand(IQTestPlugin plugin, FormManager formManager) {
        this.plugin = plugin;
        this.formManager = formManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("iqtest.admin")) {
            sender.sendMessage("§cУ вас нет прав, соси лошпедус.");
            return true;
        }

        // Парсинг аргументов с учетом кавычек
        String fullCmd = String.join(" ", args);
        List<String> parsedArgs = new ArrayList<>();
        Matcher m = Pattern.compile("([^\" ]\\S*|\".+?\")\\s*").matcher(fullCmd);
        while (m.find()) {
            parsedArgs.add(m.group(1).replace("\"", ""));
        }

        if (parsedArgs.size() < 4) {
            sender.sendMessage("§cИспользование: /iqtest <игрок> <сложность> \"<win_cmd>\" \"<fail_cmd>\"");
            return true;
        }

        Player target = Bukkit.getPlayer(parsedArgs.get(0));
        if (target == null) {
            sender.sendMessage("§cИгрок не найден.");
            return true;
        }

		if (!GeyserApi.api().isBedrockPlayer(target.getUniqueId())) {
            sender.sendMessage("§cЭтот игрок играет не с Bedrock Edition! Погоди ка... Ты что решил проверить ArtiomZd или NikitaB3rg? оглядывайся.");
            return true;
        }

        String difficulty = parsedArgs.get(1).toLowerCase();
        String winCmd = parsedArgs.get(2);
        String failCmd = parsedArgs.get(3);

        formManager.startTest(target, difficulty, winCmd, failCmd);
        sender.sendMessage("§aТест на аутиста запущен для " + target.getName());
        return true;
    }
}
