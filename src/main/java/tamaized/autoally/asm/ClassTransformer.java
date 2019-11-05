package tamaized.autoally.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

@IFMLLoadingPlugin.MCVersion(Loader.MC_VERSION)
public class ClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if ("com.feed_the_beast.ftblib.lib.data.ForgeTeam".equals(transformedName)) {
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			for (MethodNode method : node.methods) {
				if (method.name.equals("getSetStatus") &&

						method.desc.equals("(Lcom/feed_the_beast/ftblib/lib/data/ForgePlayer;)Lcom/feed_the_beast/ftblib/lib/EnumTeamStatus;")) {
					AbstractInsnNode ins = null;
					for (Iterator<AbstractInsnNode> i = method.instructions.iterator(); i.hasNext(); ) {
						AbstractInsnNode t = i.next();
						if (t.getOpcode() == Opcodes.ARETURN)
							ins = t;
					}
					if (ins != null) {
						InsnList instructions = new InsnList();
						instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
						instructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // ForgePlayer
						instructions.add(new MethodInsnNode(

								Opcodes.INVOKESTATIC,

								"tamaized/autoally/AutoAlly",

								"hook",

								"(Lcom/feed_the_beast/ftblib/lib/EnumTeamStatus;Lcom/feed_the_beast/ftblib/lib/data/ForgeTeam;Lcom/feed_the_beast/ftblib/lib/data/ForgePlayer;)Lcom/feed_the_beast/ftblib/lib/EnumTeamStatus;",

								false

						));
						method.instructions.insertBefore(ins, instructions);
						ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
							@Override
							protected String getCommonSuperClass(String type1, String type2) {
								return super.getCommonSuperClass(FMLDeobfuscatingRemapper.INSTANCE.map(type1), FMLDeobfuscatingRemapper.INSTANCE.map(type2));
							}
						};
						node.accept(writer);
						return writer.toByteArray();
					}
					return bytes;
				}
			}
		}
		return bytes;
	}
}
