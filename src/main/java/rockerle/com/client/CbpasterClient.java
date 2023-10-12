package rockerle.com.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.telemetry.WorldLoadedEvent;
import org.lwjgl.glfw.GLFW;
import java.util.List;
import java.util.ListIterator;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CbpasterClient implements ClientModInitializer {
    KeyBinding print;
    Keyboard clipboard;
    List<String> content;
    ListIterator<String> contentIter;
    @Override
    public void onInitializeClient() {

        print = KeyBindingHelper.registerKeyBinding(new KeyBinding("print ASCII art", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J,"printing ASCII art"));
        clipboard = new Keyboard(MinecraftClient.getInstance());
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{
            content = null;
            contentIter = null;
        });
        ClientTickEvents.START_CLIENT_TICK.register(ctx->{
            if(print.wasPressed()){
                content = clipboard.getClipboard().lines().toList();
                contentIter = content.listIterator();
            }
            if(content != null && contentIter.hasNext() && ctx.player.age%15==0)
                ctx.getNetworkHandler().sendChatMessage(contentIter.next());
        });
    }
}