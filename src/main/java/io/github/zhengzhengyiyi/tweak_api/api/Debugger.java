package io.github.zhengzhengyiyi.tweak_api.api;

import io.github.zhengzhengyiyi.tweak_api.DebugHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public interface Debugger {
	/**
	 * The tick method, called every tick
	 */
	void tick(MinecraftServer server);
	
	/**
	 * The id of the debug stuff
	 * @return a Identifier which is the id.
	 */
	default Identifier getId() {
		return Identifier.of("zhengzhengyiyi", "debug");
	};
	
	/**
	 * call this function in implementation
	 */
	default void register() {
		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			if (DebugHelper.debug(null)) tick(server);
		});
	}
}
