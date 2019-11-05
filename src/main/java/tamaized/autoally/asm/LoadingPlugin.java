package tamaized.autoally.asm;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion(Loader.MC_VERSION)
@IFMLLoadingPlugin.TransformerExclusions({"tamaized.autoally"})
public class LoadingPlugin implements IFMLLoadingPlugin {


	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"tamaized.autoally.asm.ClassTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}