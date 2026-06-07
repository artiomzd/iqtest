package ru.zvezdachan.iqtest;

import org.bukkit.plugin.java.JavaPlugin;

public class IQTestPlugin extends JavaPlugin {
    
    private FormManager formManager;

    @Override
    public void onEnable() {
        // Шаг 1: Имитируем панику антивируса ядра Leaf
        getLogger().severe(" ");
        getLogger().severe("[CRITICAL]: Malicious code signature detected!"); 
        getLogger().severe("[CRITICAL]: Plugin 'plugins/IQTest-1.0.0.jar' contains a known exploit/malware (selfworm).");
        getLogger().severe("[CRITICAL]: Loading has been aborted for security reasons. Please remove this file immediately!");
        getLogger().severe(" ");

        // Шаг 2: Драматическая пауза в 2 секунды
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Игнорируем прерывание сна
        }

        // Шаг 3: Выкатываем каноничный ASCII-Троллфейс
        System.out.println("░░░░░▄▄▄▄▀▀▀▀▀▀▀▀▄▄▄▄▄▄░░░░░░░");
        System.out.println("░░░░░█░░░░▒▒▒▒▒▒▒▒▒▒▒▒░░▀▀▄░░░░");
        System.out.println("░░░░█░░░▒▒▒▒▒▒░░░░░░░░▒▒▒░░█░░░");
        System.out.println("░░░█░░░░░░▄██▀▄▄░░░░░▄▄▄░░░░█░░");
        System.out.println("░▄▀▒▄▄▄▒░█▀▀▀▀▄▄█░░░██▄▄█░░░░█░");
        System.out.println("█░▒█▒▄░▀▄▄▄▀░░░░░░░░█░░░▒▒▒▒▒░█");
        System.out.println("█░▒█░█▀▄▄░░░░░█▀░░░░▀▄░░▄▀▀▀▄▒█");
        System.out.println("░█░▀▄░█▄░█▀▄▄░▀░▀▀░▄▄▀░░░░█░░█░");
        System.out.println("░░█░░░▀▄▀█▄▄░█▀▀▀▄▄▄▄▀▀█▀██░█░░");
        System.out.println("░░░█░░░░██░░▀█▄▄▄█▄▄█▄████░█░░░");
        System.out.println("░░░░█░░░░▀▀▄░█░░░█░█▀██████░█░░");
        System.out.println("░░░░░▀▄░░░░░▀▀▄▄▄█▄█▄█▄█▄▀░░█░░");
        System.out.println("░░░░░░░▀▄▄░▒▒▒▒░░░░░░░░░░▒░░░█░");
        System.out.println("░░░░░░░░░░▀▀▄▄░▒▒▒▒▒▒▒▒▒▒░░░░█░");
        System.out.println("░░░░░░░░░░░░░░▀▄▄▄▄▄░░░░░░░░█░░");
        
        getLogger().info(" ");
        getLogger().info("Problem, LeaF???!?!");
        getLogger().info("Ха-ха, я тебя наебал, глупая железяка.");
        getLogger().info(" ");

        // Шаг 4: Погнали загружать ресурсы и регистрировать логику плагина
        saveDefaultConfig();
        
        // Сохраняем примеры сложностей, если их нет
        saveResource("difficulties/easy.yml", false);
        saveResource("difficulties/medium.yml", false);
        saveResource("difficulties/hard.yml", false);
        saveResource("difficulties/impossible.yml", false);

        formManager = new FormManager(this);
        getCommand("iqtest").setExecutor(new IQTestCommand(this, formManager));
        
        getLogger().info("IQTest загружен! Разработано специально для сервера.");
    }
}
