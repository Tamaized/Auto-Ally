package tamaized.autoally;

import com.feed_the_beast.ftbutilities.FTBUtilities;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
@Mod(modid = AutoAlly.MODID, acceptableRemoteVersions = "*", dependencies = "required-after:" + FTBUtilities.MOD_ID)
public class AutoAlly {

	public static final String MODID = "autoally";

	public static final String ID_ALLY = MODID + ".team";

}
