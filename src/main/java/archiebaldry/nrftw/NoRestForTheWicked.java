package archiebaldry.nrftw;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoRestForTheWicked implements ModInitializer {
	public static final String MOD_ID = "nrftw";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
			if (!world.isClient) {
				BlockPos pos = hitResult.getBlockPos();

				BlockState state = world.getBlockState(pos);

				Block block = state.getBlock();

				if (block instanceof BedBlock) {
					player.sendMessage(Text.literal("There ain't no rest for the wicked"), true);

					return ActionResult.SUCCESS;
				}
			}

			return ActionResult.PASS;
		}));

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			for (ServerWorld world : server.getWorlds()) {
				world.getGameRules().get(GameRules.DO_INSOMNIA).set(false, server);
			}
		});
	}
}
