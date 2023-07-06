package ru.maxthetomas.twitchchat;

import com.google.common.io.Files;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class TwitchChatMod implements ClientModInitializer {
    String currentChannel;
    public static boolean crashed = false;
    public static String nickname;//        event.getBot().

    public static String oauth;
    public static String defaultChannel;
    public static Path configPath;

    @Override
    public void onInitializeClient() {
        parseConfig();

        if (nickname == null || oauth == null) {
            System.out.println("Cannot get your nickname & oauth token from config!");
            System.out.println("Open config/twitch-irc.json and put your nickname and oauth token there.");
            System.out.println("(get token value here: https://twitchapps.com/tmi/)");

            crashed = true;
            return;
        }

        newBot(defaultChannel);
        registerCommand();
    }

    private void parseConfig() {
        configPath = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), "twitch-irc.json");
        try {
            if (!configPath.toFile().exists()) {
                var w = Files.newWriter(configPath.toFile(), StandardCharsets.UTF_8);
                w.append("{ \"nickname\": \"\", \"oauth\": \"\", \"default\": \"\" }");
                w.close();
                return;
            }

            var json = JsonParser.parseReader(Files.newReader(configPath.toFile(), StandardCharsets.UTF_8));
            var obj = json.getAsJsonObject();
            nickname = obj.get("nickname").getAsString();
            oauth = obj.get("oauth").getAsString();
            if (Objects.equals(nickname, "") || Objects.equals(oauth, "")) {
                nickname = null; oauth = null;
            }
            defaultChannel = obj.get("default").getAsString();
        } catch (Exception e) {

            System.out.println("Cannot parse twitchmod configuration!");
            e.printStackTrace();
        }
    }


    private void newBot(String channel) {
        ThreadedChatbot.newThread(channel);
    }

    public void setChannel(String c) {
        newBot(c);
        currentChannel = c;
    }

    public void clearChannel() {
        if (currentChannel != null)
            newBot(null);
        currentChannel = null;
    }

    private void registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("twitch").then(ClientCommandManager.argument("channel", string()).executes(ctx -> {
                var c = getString(ctx, "channel");
                setChannel(c);
                var t = Text.translatable("twitchmod.joined", c);
                t.setStyle(t.getStyle().withColor(0xc65afc));
                ctx.getSource().sendFeedback(t);
                return 1;
            })).executes(ctx -> {
                clearChannel();
                var t = Text.translatable("twitchmod.left");
                t.setStyle(t.getStyle().withColor(0xc65afc));
                ctx.getSource().sendFeedback(t);
                return 1;
            }));

            dispatcher.register(ClientCommandManager.literal("tc").then(ClientCommandManager.argument("message", greedyString()).executes(ctx -> {
                var m = getString(ctx, "message");

                ThreadedChatbot.sendMsg(m);

                return 1;
            })));
        });
    }
}
