package mod.mindcraft.seasons.asm;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Lists;

public class Transformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		
		if (transformedName.equals("net.minecraft.world.biome.BiomeGenBase")) {
			System.out.println("Starting BiomeGenBase Patch...");
			ClassReader cr = new ClassReader(basicClass);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			boolean obf = false;
			for (MethodNode mn : cn.methods) {
				if ((mn.name.equals("a") || mn.name.equals("getFloatTemperature")) && (mn.desc.equals("(Lcj;)F") || mn.desc.equals("(Lnet/minecraft/util/BlockPos;)F"))) {
					System.out.println("Patching getFloatTemperature...");
					obf = mn.name.equals("a");
					mn.instructions.clear();
					mn.instructions.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/SeasonsAPI;"));
					mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/IWorldInterface;", false));
					mn.instructions.add(new VarInsnNode(ALOAD, 1));
					mn.instructions.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/IWorldInterface", "getTemperature", "(L" + (obf ? "cj" : "net/minecraft/util/BlockPos") + ";)F", true));
					mn.instructions.add(new InsnNode(FRETURN));
				}
			}
			{
				System.out.println("Adding isRainEnabled method...");
				MethodNode method = new MethodNode();
				method.desc = "()Z";
				method.name = "isRainEnabled";
				method.exceptions = Lists.newArrayList();
				method.instructions.add(new VarInsnNode(ALOAD, 0));
				method.instructions.add(new FieldInsnNode(GETFIELD, obf ? "ady" : "net/minecraft/world/biome/BiomeGenBase", obf ? "ay" : "enableRain", "Z"));
				method.instructions.add(new InsnNode(IRETURN));
				method.visitMaxs(0, 0);
				cn.methods.add(method);
			}
			System.out.println("BiomeGenBase patch complete!");
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			return cw.toByteArray();
		}
		
		else if (transformedName.equals("net.minecraft.block.BlockLeaves") || transformedName.equals("net.minecraft.block.BlockOldLeaf")) {
			System.out.println("Starting BlockLeaves Patch...");
			ClassReader cr = new ClassReader(basicClass);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			for (MethodNode mn : cn.methods) {
				if ((mn.name.equals("a") || mn.name.equals("colorMultiplier")) && (mn.desc.equals("(Ladq;Lcj;I)I") || mn.desc.equals("(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;I)I"))) {
					boolean obf = mn.name.equals("a");
					System.out.println("Patching colorMultiplier...");
					mn.instructions.clear();
					mn.instructions.add(new VarInsnNode(ALOAD, 1));
					mn.instructions.add(new VarInsnNode(ALOAD, 2));
					mn.instructions.add(new MethodInsnNode(INVOKESTATIC, "mod/mindcraft/seasons/colorizer/LeavesUtils", "getLeavesColor", obf ? "(Ladq;Lcj;)I" : "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)I", false));
					mn.instructions.add(new InsnNode(IRETURN));
				}
			}
			System.out.println("BlockLeaves patch complete!");
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			return cw.toByteArray();
		}
		else if (transformedName.equals("net.minecraft.world.World")) {
			System.out.println("Starting World Patch...");
			ClassReader cr = new ClassReader(basicClass);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			for (MethodNode mn : cn.methods) {
				if ((mn.name.equals("canSnowAtBody")) && (mn.desc.equals("(Lcj;Z)Z") || mn.desc.equals("(Lnet/minecraft/util/BlockPos;Z)Z"))) {
					boolean obf = mn.desc.equals("(Lcj;Z)Z");
					System.out.println("Patching canSnowAtBody...");
					InsnList insn = new InsnList();
					insn.add(new VarInsnNode(ALOAD, 0));
					insn.add(new VarInsnNode(ALOAD, 1));
					insn.add(new MethodInsnNode(INVOKEVIRTUAL, obf ? "adm" : "net/minecraft/world/World", obf ? "b" : "getBiomeGenForCoords", obf ?  "(Lcj;)Lady;" : "(Lnet/minecraft/util/BlockPos;)Lnet/minecraft/world/biome/BiomeGenBase;", false));
					insn.add(new MethodInsnNode(INVOKEVIRTUAL, obf ? "ady" : "net/minecraft/world/biome/BiomeGenBase", "isRainEnabled", "()Z", false));
					LabelNode lb = new LabelNode();
					insn.add(new JumpInsnNode(IFEQ, lb));
					insn.add(new InsnNode(ICONST_0));
					insn.add(new InsnNode(IRETURN));
					insn.add(lb);
					Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
					while (iter.hasNext()) {
						AbstractInsnNode ain = iter.next();
						if (ain instanceof LabelNode) {
							mn.instructions.insertBefore(ain, insn);
							break;
						}
					}
				}
			}
			System.out.println("World patch complete!");
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			return cw.toByteArray();			
		}
		return basicClass;
	}
	
	public boolean x(boolean i) {
		return i;
	}
}
