package archiebaldry.nrftw;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.RegistryKey;
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

	@SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final RegistryKey<LootTable> DROWNED_LOOT_TABLE_ID = EntityType.DROWNED.getLootTableKey().get();

	private static final float PHANTOM_MEMBRANE_CHANCE = 0.25f;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello, World!");

		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (source.isBuiltin() && key.equals(DROWNED_LOOT_TABLE_ID)) {
				LootPool.Builder poolBuilder = LootPool.builder()
						.with(ItemEntry.builder(Items.PHANTOM_MEMBRANE)
								.conditionally(KilledByPlayerLootCondition.builder())
								.conditionally(RandomChanceLootCondition.builder(PHANTOM_MEMBRANE_CHANCE)));

				tableBuilder.pool(poolBuilder);
			}
		});

		UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
			if (!world.isClient) {
				BlockPos pos = hitResult.getBlockPos();

				BlockState state = world.getBlockState(pos);

				Block block = state.getBlock();

				if (block instanceof BedBlock) {
					player.sendMessage(Text.literal("There ain't no rest for the wicked"), true);

					LOGGER.info("{} tried to use a bed but there ain't no rest for the wicked.", player.getName().getLiteralString());

					return ActionResult.SUCCESS;
				}
			}

			return ActionResult.PASS;
		}));

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			for (ServerWorld world : server.getWorlds()) {
				world.getGameRules().get(GameRules.DO_INSOMNIA).set(false, server);

				LOGGER.info("Disabled insomnia in {}.", world);
			}
		});
	}
}
