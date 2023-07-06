package ru.maxthetomas.twitchchat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class ChatInteraction extends ListenerAdapter {
    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        var txt = Text.literal(String.format("<%s> %s", event.getUser().getNick(), event.getMessage()));
        txt.setStyle(txt.getStyle().withColor(0xe7bdfc).withItalic(false).withFont(Identifier.of("minecraft", "default")));
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(txt);
    }
}
