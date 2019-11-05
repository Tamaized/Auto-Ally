package tamaized.autoally;

import com.feed_the_beast.ftblib.events.RegisterRankConfigEvent;
import com.feed_the_beast.ftblib.events.ServerReloadEvent;
import com.feed_the_beast.ftblib.lib.EnumTeamStatus;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
@Mod(modid = AutoAlly.MODID, acceptableRemoteVersions = "*", dependencies = "required-after:" + FTBUtilities.MOD_ID)
public class AutoAlly {

	public static final String MODID = "autoally";

	public static final String ID_ALLY = MODID + ".team";

	private static final Map<Short, List<UUID>> DATA = new HashMap<>();
	private static final Map<UUID, String> CACHE = new HashMap<>();

	private static List<UUID> get(short team) {
		if (!DATA.containsKey(team))
			DATA.put(team, new ArrayList<>());
		return DATA.get(team);
	}

	private static void add(short id, UUID player) {
		List<UUID> list = get(id);
		if (!list.contains(player))
			list.add(player);
	}

	private static boolean contains(short team, UUID player) {
		return get(team).contains(player);
	}

	@SuppressWarnings("unused")
	public static EnumTeamStatus hook(EnumTeamStatus original, ForgeTeam team, ForgePlayer player) {
		return original != null && original.ordinal() < EnumTeamStatus.ALLY.ordinal() && contains(team.getUID(), player.getId()) ? EnumTeamStatus.ALLY : original;
	}

	@SubscribeEvent
	public static void tick(TickEvent.PlayerTickEvent e) {
		if (e.phase != TickEvent.Phase.START || e.player.world == null || e.player.world.isRemote || !(e.player instanceof EntityPlayerMP) || e.player.ticksExisted <= 0 || e.player.ticksExisted % (20 * 10) != 0)
			return;
		if (e.player.getServer() == null)
			return;
		String val = Ranks.INSTANCE.getConfigValue(e.player.getServer(), e.player.getGameProfile(), Node.get(ID_ALLY)).getString();
		if (val.isEmpty() || val.equalsIgnoreCase("null"))
			return;
		if (val.equals(CACHE.get(e.player.getUniqueID())))
			return;
		boolean flag = false;
		for (String v : val.split(",")) {
			if (v.startsWith(" "))
				v = v.substring(1);
			ForgeTeam team = Universe.get().getTeam(v);
			if (team != Universe.get().getTeam((short) 0) && !contains(team.getUID(), e.player.getUniqueID())) {
				add(team.getUID(), e.player.getUniqueID());
				flag = true;
			}
		}
		if (flag)
			CACHE.put(e.player.getUniqueID(), val);
	}

	@SubscribeEvent
	public static void reload(ServerReloadEvent e) {
		DATA.clear();
		CACHE.clear();
	}

	@SubscribeEvent
	public static void registerConfigs(RegisterRankConfigEvent event) {
		event.register(Node.get(ID_ALLY), new ConfigString(""));
	}

}
