package tamaized.autoally.asm;

import com.feed_the_beast.ftblib.lib.EnumTeamStatus;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import net.minecraft.launchwrapper.IClassTransformer;
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

@SuppressWarnings("unused")
public class ClassTransformer implements IClassTransformer {

	public static EnumTeamStatus hook(EnumTeamStatus original, ForgePlayer player) {
		return original;
	}

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
						instructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // ForgePlayer
						instructions.add(new MethodInsnNode(

								Opcodes.INVOKESTATIC,

								"tamaized/autoally/asm/ClassTransformer",

								"hook",

								"(Lcom/feed_the_beast/ftblib/lib/EnumTeamStatus;Lcom/feed_the_beast/ftblib/lib/data/ForgePlayer;)Lcom/feed_the_beast/ftblib/lib/EnumTeamStatus;",

								false

						));
						method.instructions.insertBefore(ins, instructions);
						ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
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
