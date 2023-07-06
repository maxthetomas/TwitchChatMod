package ru.maxthetomas.twitchchat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

public class ThreadedChatbot extends Thread {
    public static ThreadedChatbot instance;

    PircBotX bot;
    String channel;

    public static void newThread(String chat) {
        // Todo leave a channel, and then join different one instead of relogging.

        if (instance != null)
            instance.stopBot();

        if (chat == null) {
            instance = null;
            return;
        }

        instance = new ThreadedChatbot(chat);
    }

    public static void sendMsg(String msg) {
        if (instance == null) return;
        instance.bot.sendIRC().message("#" + instance.channel, msg);

        var txt = Text.literal(String.format("<%s> %s", instance.bot.getUserBot().getNick(), msg));
        txt.setStyle(txt.getStyle().withColor(0x9d8afc).withItalic(false).withFont(Identifier.of("minecraft", "default")));
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(txt);
    }

    private void stopBot() {
        bot.stopBotReconnect();
        bot.close();
    }

    @Override
    public void run() {
        if (bot != null) {
            bot.close();
        }

        var cfg = new Configuration.Builder()
                .setAutoNickChange(false)
                .setOnJoinWhoEnabled(false)
                .setCapEnabled(true)
                .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
                .addServer("irc.twitch.tv")
                .addListener(new ChatInteraction())
                .setName(TwitchChatMod.nickname)
                .setServerPassword(TwitchChatMod.oauth);

        if (channel != null)
            cfg.addAutoJoinChannel("#" + channel);

        try (var mybot = new PircBotX(cfg.buildConfiguration())) {
            bot = mybot;
            mybot.startBot();

        } catch (IOException | IrcException e) {
            stopBot();
        }
    }


    private ThreadedChatbot(String channel) {
       this.channel = channel;
       start();
    }
}
