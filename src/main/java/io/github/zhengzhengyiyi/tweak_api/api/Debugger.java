package io.github.zhengzhengyiyi.tweak_api.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zhengzhengyiyi.tweak_api.Debughelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

/**
 * 
 * <h2>Debugger</h2>
 * 
 * every debugger need to implement this interface.
 * 
 * @author zhengzhengyiyi
 * @since 1.0.1
 */
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
		return new Identifier("zhengzhengyiyi", "debug");
	};
	
	/**
	 * call this function in implementation
	 */
	default void register() {
		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			if (Debughelper.debug(null)) tick(server);
		});
	}
	
	/**
	 * The default logger for debugger
	 */
	final Logger LOGGER = LoggerFactory.getLogger(Debugger.class);
}
