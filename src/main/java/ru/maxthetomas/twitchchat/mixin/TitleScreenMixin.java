package ru.maxthetomas.twitchchat.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.maxthetomas.twitchchat.TwitchChatMod;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    // todo use cloth config or something, bruh

    protected TitleScreenMixin(Text title) { super(title); }
    @Inject(method = "initWidgetsNormal", at = @At("HEAD"))
    public void initWidgets(int y, int spacingY, CallbackInfo ci) {
        if (!TwitchChatMod.crashed) return;

        var b = new ButtonWidget.Builder(Text.translatable("twitchmod.title.config"), p -> {
            try {
                openFileWithDefaultEditor(TwitchChatMod.configPath.toFile());
            } catch (Exception i ) {
                p.setTooltip(Tooltip.of(Text.translatable("twitchmod.tooltip.errored")));
                p.active = false;
            }
        }).dimensions(10, 10, 100, 20).build();
        addDrawableChild(b);

        var b2 = new ButtonWidget.Builder(Text.translatable("twitchmod.title.oauth"), p -> {
            try {
                openURL("https://twitchapps.com/tmi/");
            } catch (Exception e) {
                p.active = false;
            }
        }).dimensions(110, 10, 100, 20).build();
        addDrawableChild(b2);
    }



    // Untested code:
    private static void openFileWithDefaultEditor(File file) throws IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            Desktop.getDesktop().open(file);
        } else {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                Runtime.getRuntime().exec("start \"\" \"" + file.getAbsolutePath() + "\"");
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + file.getAbsolutePath());
            } else if (os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
                Runtime.getRuntime().exec("xdg-open " + file.getAbsolutePath());
            } else {
                throw new UnsupportedOperationException("Platform not supported");
            }
        }
    }


    private static void openURL(String url) throws IOException, URISyntaxException {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            }
        } catch (HeadlessException e) {
            Runtime runtime = Runtime.getRuntime();
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                runtime.exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
                runtime.exec("xdg-open " + url);
            } else {
                throw new UnsupportedOperationException("Platform not supported");
            }
        }
    }
}
