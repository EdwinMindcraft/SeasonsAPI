package mod.mindcraft.seasons.asm;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Lists;

public class Transformer implements IClassTransformer {

	private static final Logger logger = LogManager.getLogger("Season API Transformer");
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		
		if (transformedName.equals("net.minecraft.world.biome.BiomeGenBase")) {
			return transformBiomeGenBase(basicClass);
		}
		else if (transformedName.equals("net.minecraft.block.BlockLeaves") || transformedName.equals("net.minecraft.block.BlockOldLeaf")) {
			return transformBlockLeaves(basicClass);
		}
		else if (transformedName.equals("net.minecraft.world.World")) {
			return transformWorld(basicClass);
		}
		else if (transformedName.equals("net.minecraft.block.BlockCrops")) {
			return transformBlockCrops(basicClass);
		}
		else if (transformedName.equals("net.minecraft.block.BlockIce")) {
			return transformBlockIce(basicClass);
		}
		else if (transformedName.equals("net.minecraft.block.BlockSnow")) {
			return transformBlockSnow(basicClass);
		}
		return basicClass;
	}
	
	private byte[] transformBiomeGenBase(byte[] basicClass) {
		logger.info("Starting BiomeGenBase Patch...");
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		boolean obf = false;
		for (MethodNode mn : cn.methods) {
			if ((mn.name.equals("a") || mn.name.equals("getFloatTemperature")) && (mn.desc.equals("(Lcj;)F") || mn.desc.equals("(Lnet/minecraft/util/BlockPos;)F"))) {
				logger.info("Patching getFloatTemperature...");
				obf = mn.name.equals("a");
				mn.instructions.clear();
				mn.instructions.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/SeasonsAPI;"));
				mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/interfaces/IWorldInterface;", false));
				mn.instructions.add(new VarInsnNode(ALOAD, 1));
				mn.instructions.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/interfaces/IWorldInterface", "getTemperature", "(L" + (obf ? "cj" : "net/minecraft/util/BlockPos") + ";)F", true));
				mn.instructions.add(new InsnNode(FRETURN));
			}
		}
		{
			logger.info("Adding isRainEnabled method...");
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
		logger.info("BiomeGenBase patch complete!");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}
	
	private byte[] transformBlockLeaves(byte[] basicClass) {
		logger.info("Starting BlockLeaves Patch...");
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		for (MethodNode mn : cn.methods) {
			if ((mn.name.equals("a") || mn.name.equals("colorMultiplier")) && (mn.desc.equals("(Ladq;Lcj;I)I") || mn.desc.equals("(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;I)I"))) {
				boolean obf = mn.name.equals("a");
				logger.info("Patching colorMultiplier...");
				mn.instructions.clear();
				mn.instructions.add(new VarInsnNode(ALOAD, 1));
				mn.instructions.add(new VarInsnNode(ALOAD, 2));
				mn.instructions.add(new MethodInsnNode(INVOKESTATIC, "mod/mindcraft/seasons/colorizer/LeavesUtils", "getLeavesColor", obf ? "(Ladq;Lcj;)I" : "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)I", false));
				mn.instructions.add(new InsnNode(IRETURN));
			}
		}
		logger.info("BlockLeaves patch complete!");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}
	
	private byte[] transformWorld(byte[] basicClass) {
		logger.info("Starting World Patch...");
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		for (MethodNode mn : cn.methods) {
			if ((mn.name.equals("canSnowAtBody")) && (mn.desc.equals("(Lcj;Z)Z") || mn.desc.equals("(Lnet/minecraft/util/BlockPos;Z)Z"))) {
				boolean obf = mn.desc.equals("(Lcj;Z)Z");
				logger.info("Patching canSnowAtBody...");
				InsnList insn = new InsnList();
				insn.add(new VarInsnNode(ALOAD, 0));
				insn.add(new VarInsnNode(ALOAD, 1));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, obf ? "adm" : "net/minecraft/world/World", obf ? "b" : "getBiomeGenForCoords", obf ?  "(Lcj;)Lady;" : "(Lnet/minecraft/util/BlockPos;)Lnet/minecraft/world/biome/BiomeGenBase;", false));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, obf ? "ady" : "net/minecraft/world/biome/BiomeGenBase", "isRainEnabled", "()Z", false));
				LabelNode lb = new LabelNode();
				insn.add(new JumpInsnNode(IFNE, lb));
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
		logger.info("World patch complete!");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();			
	}
	
	private byte[] transformBlockIce(byte[] basicClass) {
		logger.info("Starting BlockIce Patch...");
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		for (MethodNode mn : cn.methods) {
			if ((mn.name.equals("b") || mn.name.equals("updateTick")) && (mn.desc.equals("(Ladm;Lcj;Lalz;Ljava/util/Random;)V") || mn.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"))) {
				boolean obf = mn.desc.equals("(Ladm;Lcj;Lalz;Ljava/util/Random;)V");
				logger.info("Patching updateTick...");
				InsnList insn = new InsnList();
				LabelNode end = new LabelNode();
				insn.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/SeasonsAPI;"));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/interfaces/IWorldInterface;", false));
				insn.add(new VarInsnNode(ALOAD, 2));
				insn.add(new InsnNode(ICONST_1));
				insn.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/interfaces/IWorldInterface", "getTemperature", obf ? "(Lcj;Z)F" : "(Lnet/minecraft/util/BlockPos;Z)F", true));
				insn.add(new InsnNode(FCONST_0));
				insn.add(new InsnNode(FCMPL));
				insn.add(new JumpInsnNode(IFLE, end));
				
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode ain = iter.next();
					if (ain instanceof JumpInsnNode && ain.getOpcode() == IF_ICMPLE) {
						((JumpInsnNode)ain).setOpcode(IF_ICMPGT);
						mn.instructions.insert(ain, insn);
						while(iter.hasNext()) {
							AbstractInsnNode ain2 = iter.next();
							if (ain2 instanceof InsnNode && ain2.getOpcode() == RETURN) {
								mn.instructions.insertBefore(ain2, end);
								break;
							}
						}
						break;
					}
				}
			}
		}
		logger.info("BlockIce patch complete!");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}
	
	private byte[] transformBlockSnow(byte[] basicClass) {
		logger.info("Starting BlockSnow Patch...");
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		for (MethodNode mn : cn.methods) {
			if ((mn.name.equals("b") || mn.name.equals("updateTick")) && (mn.desc.equals("(Ladm;Lcj;Lalz;Ljava/util/Random;)V") || mn.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"))) {
				boolean obf = mn.desc.equals("(Ladm;Lcj;Lalz;Ljava/util/Random;)V");
				logger.info("Patching updateTick...");
				InsnList insn = new InsnList();
				LabelNode end = new LabelNode();
				insn.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/SeasonsAPI;"));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/interfaces/IWorldInterface;", false));
				insn.add(new VarInsnNode(ALOAD, 2));
				insn.add(new InsnNode(ICONST_1));
				insn.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/interfaces/IWorldInterface", "getTemperature", obf ? "(Lcj;Z)F" : "(Lnet/minecraft/util/BlockPos;Z)F", true));
				insn.add(new InsnNode(FCONST_0));
				insn.add(new InsnNode(FCMPL));
				insn.add(new JumpInsnNode(IFLE, end));
				
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode ain = iter.next();
					if (ain instanceof JumpInsnNode && ain.getOpcode() == IF_ICMPLE) {
						((JumpInsnNode)ain).setOpcode(IF_ICMPGT);
						mn.instructions.insert(ain, insn);
						while(iter.hasNext()) {
							AbstractInsnNode ain2 = iter.next();
							if (ain2 instanceof InsnNode && ain2.getOpcode() == RETURN) {
								mn.instructions.insertBefore(ain2, end);
								break;
							}
						}
						break;
					}
				}
			}
		}
		logger.info("BlockSnow patch complete!");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}
	
	private byte[] transformBlockCrops(byte[] basicClass) {
		logger.info("Starting BlockCrops Patch...");
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		for (MethodNode mn : cn.methods) {
			if ((mn.name.equals("b") || mn.name.equals("updateTick")) && (mn.desc.equals("(Ladm;Lcj;Lalz;Ljava/util/Random;)V") || mn.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"))) {
				logger.info("Patching updateTick...");
				InsnList insn = new InsnList();
				insn.add(new LabelNode());
				insn.add(new IincInsnNode(5, 1));
				insn.add(new LabelNode());
				//SPRING
				insn.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/SeasonsAPI;"));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/interfaces/IWorldInterface;", false));
				insn.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/interfaces/IWorldInterface", "getSeason", "()Lmod/mindcraft/seasons/api/enums/EnumSeason;", true));
				insn.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/enums/EnumSeason", "SPRING", "Lmod/mindcraft/seasons/api/enums/EnumSeason;"));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/enums/EnumSeason", "equals", "(Ljava/lang/Object;)Z", false));
				LabelNode node = new LabelNode();
				insn.add(new JumpInsnNode(IFEQ, node));
				insn.add(new VarInsnNode(ALOAD, 4));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextFloat", "()F", false));
				insn.add(new LdcInsnNode(new Float("0.5")));
				insn.add(new InsnNode(FCMPG));
				insn.add(new JumpInsnNode(IFGE, node));
				insn.add(new LabelNode());
				insn.add(new IincInsnNode(5, 1));
				insn.add(node);
				//WINTER
				insn.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/SeasonsAPI;"));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/interfaces/IWorldInterface;", false));
				insn.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/interfaces/IWorldInterface", "getSeason", "()Lmod/mindcraft/seasons/api/enums/EnumSeason;", true));
				insn.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/enums/EnumSeason", "WINTER", "Lmod/mindcraft/seasons/api/enums/EnumSeason;"));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/enums/EnumSeason", "equals", "(Ljava/lang/Object;)Z", false));
				LabelNode node2 = new LabelNode();
				insn.add(new JumpInsnNode(IFEQ, node2));
				insn.add(new VarInsnNode(ALOAD, 4));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextFloat", "()F", false));
				insn.add(new LdcInsnNode(new Float("0.25")));
				insn.add(new InsnNode(FCMPG));
				insn.add(new JumpInsnNode(IFGE, node2));
				insn.add(new LabelNode());
				insn.add(new IincInsnNode(5, -1));
				insn.add(node2);
				//CHECK
				insn.add(new VarInsnNode(ILOAD, 5));
				insn.add(new IntInsnNode(BIPUSH, 7));
				LabelNode label = new LabelNode();
				insn.add(new JumpInsnNode(IF_ICMPLE, label));
				insn.add(new LabelNode());
				insn.add(new IntInsnNode(BIPUSH, 7));
				insn.add(new VarInsnNode(ISTORE, 5));
				insn.add(new LabelNode());
				insn.add(label);
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode ain = iter.next();
					if (ain instanceof VarInsnNode && ain.getOpcode() == FSTORE) {
						iter.next();
						mn.instructions.insertBefore(iter.next(), insn);
					}
					else if (ain instanceof VarInsnNode && ain.getOpcode() == ILOAD) {
						AbstractInsnNode next = iter.next();
						if (next instanceof InsnNode && next.getOpcode() == ICONST_1) {
							iter.remove();
							iter.next();
							iter.remove();
						}
					}
				}
			}
		}
		logger.info("BlockCrops patch complete!");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}
}
