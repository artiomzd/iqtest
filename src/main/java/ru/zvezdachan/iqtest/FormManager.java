package ru.zvezdachan.iqtest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FormManager {

    private final IQTestPlugin plugin;

    public FormManager(IQTestPlugin plugin) {
        this.plugin = plugin;
    }

    public void startTest(Player player, String difficulty, String winCmd, String failCmd) {
        File file = new File(plugin.getDataFolder() + "/difficulties", difficulty + ".yml");
        if (!file.exists()) {
            plugin.getLogger().warning("Файл сложности не найден: " + difficulty);
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> questions = config.getMapList("questions");
        
        if (questions.isEmpty()) return;
        
        sendQuestion(player, questions, 0, winCmd, failCmd);
    }

    private void sendQuestion(Player player, List<Map<?, ?>> questions, int index, String winCmd, String failCmd) {
        if (index >= questions.size()) {
            executeCommand(winCmd, player);
            return;
        }

        Map<?, ?> question = questions.get(index);
        String formType = (String) question.get("form-type");
        String title = (String) question.get("title");
        String content = (String) question.get("content");

        if ("simple".equalsIgnoreCase(formType)) {
            SimpleForm.Builder builder = SimpleForm.builder().title(title).content(content);
            List<Map<?, ?>> components = (List<Map<?, ?>>) question.get("components");
            
            for (Map<?, ?> comp : components) {
                String text = (String) comp.get("text");
                String img = (String) comp.get("image");
                if (img != null && !img.isEmpty()) {
                    FormImage.Type imgType = img.startsWith("http") ? FormImage.Type.URL : FormImage.Type.PATH;
                    builder.button(text, imgType, img);
                } else {
                    builder.button(text);
                }
            }
            
            int correctAnswer = (int) ((Map<?, ?>) question.get("correct-answers")).get("button-index");
            
            builder.validResultHandler(response -> {
                if (response.clickedButtonId() == correctAnswer) {
                    sendQuestion(player, questions, index + 1, winCmd, failCmd);
                } else {
                    executeCommand(failCmd, player);
                }
            }).closedOrInvalidResultHandler(response -> executeCommand(failCmd, player));

			GeyserConnection connection = GeyserApi.api().connectionByUuid(player.getUniqueId());
            if (connection != null) connection.sendForm(builder.build());

        } else if ("modal".equalsIgnoreCase(formType)) {
            String b1 = (String) question.get("button1");
            String b2 = (String) question.get("button2");
            boolean correctAnswer = (boolean) ((Map<?, ?>) question.get("correct-answers")).get("modal-result");

            ModalForm form = ModalForm.builder()
                .title(title).content(content)
                .button1(b1).button2(b2)
                .validResultHandler(response -> {
                    if (response.clickedFirst() == correctAnswer) {
                        sendQuestion(player, questions, index + 1, winCmd, failCmd);
                    } else {
                        executeCommand(failCmd, player);
                    }
                }).closedOrInvalidResultHandler(response -> executeCommand(failCmd, player)).build();
            
			GeyserConnection connection = GeyserApi.api().connectionByUuid(player.getUniqueId());
            if (connection != null) connection.sendForm(form);

        } else if ("custom".equalsIgnoreCase(formType)) {
            CustomForm.Builder builder = CustomForm.builder().title(title);
            List<Map<?, ?>> components = (List<Map<?, ?>>) question.get("components");
            
            for (Map<?, ?> comp : components) {
                if ("slider".equalsIgnoreCase((String) comp.get("type"))) {
                    builder.slider((String) comp.get("text"), 
                        Float.parseFloat(comp.get("min").toString()), 
                        Float.parseFloat(comp.get("max").toString()), 
                        Float.parseFloat(comp.get("step").toString()), 
                        Float.parseFloat(comp.get("default").toString()));
                }
            }

            Map<?, ?> correctAnswers = (Map<?, ?>) question.get("correct-answers");
            
            builder.validResultHandler(response -> {
                boolean passed = true;
                for (Object key : correctAnswers.keySet()) {
                    int compIndex = Integer.parseInt(key.toString());
                    float expected = Float.parseFloat(correctAnswers.get(key).toString());
                    if ((float) response.asSlider(compIndex) != expected) {
                        passed = false; break;
                    }
                }
                if (passed) sendQuestion(player, questions, index + 1, winCmd, failCmd);
                else executeCommand(failCmd, player);
            }).closedOrInvalidResultHandler(response -> executeCommand(failCmd, player));

			GeyserConnection connection = GeyserApi.api().connectionByUuid(player.getUniqueId());
            if (connection != null) connection.sendForm(builder.build());
        }
    }

    private void executeCommand(String cmd, Player player) {
        if (cmd == null || cmd.equals("0") || cmd.equalsIgnoreCase("none")) return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
        });
    }
}
