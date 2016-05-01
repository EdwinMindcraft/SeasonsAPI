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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
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
		else if (transformedName.equals("net.minecraft.item.ItemArmor")) {
			return transformItemArmor(basicClass);
		}
		return basicClass;
	}
	
	private byte[] transformItemArmor(byte[] basicClass) {
		logger.info("Starting ItemArmor Patch...");
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		cn.interfaces.add("mod/mindcraft/seasons/api/interfaces/ITemperatureModifier");
		boolean obf = false;
		for (MethodNode mn : cn.methods) {
			if (mn.name.equals("a"))
				obf = true;
		}
		{
			logger.info("Adding getTemperature method...");
			MethodNode method = new MethodNode();
			method.access = ACC_PUBLIC;
			method.desc = obf ? "(Ladq;F)F" : "(Lnet/minecraft/item/ItemStack;F)F";
			method.name = "getTemperature";
			method.exceptions = Lists.newArrayList();
			method.instructions.add(new VarInsnNode(ALOAD, 1));
			method.instructions.add(new VarInsnNode(FLOAD, 2));
			method.instructions.add(new MethodInsnNode(INVOKESTATIC, "mod/mindcraft/seasons/helper/ArmorTemperatureHelper", "getArmorTemperature", method.desc, false));
			method.instructions.add(new InsnNode(FRETURN));
			method.visitMaxs(0, 0);
			cn.methods.add(method);
		}
		logger.info("ItemArmor patch complete!");
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}
	
	private byte[] transformBiomeGenBase(byte[] basicClass) {
		logger.info("Starting BiomeGenBase Patch...");
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		boolean obf = false;
		InsnList oldInsn = new InsnList();
		for (MethodNode mn : cn.methods) {
			if ((mn.name.equals("a") || mn.name.equals("getFloatTemperature")) && (mn.desc.equals("(Lcj;)F") || mn.desc.equals("(Lnet/minecraft/util/math/BlockPos;)F"))) {
				logger.info("Patching getFloatTemperature...");
				obf = mn.name.equals("a");
				oldInsn.insert(mn.instructions);
				mn.instructions.clear();
				mn.instructions.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/init/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/init/SeasonsAPI;"));
				mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/init/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/interfaces/IWorldInterface;", false));
				mn.instructions.add(new VarInsnNode(ALOAD, 1));
				mn.instructions.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/interfaces/IWorldInterface", "getTemperature", "(L" + (obf ? "cj" : "net/minecraft/util/math/BlockPos") + ";)F", true));
				mn.instructions.add(new InsnNode(FRETURN));
			}
			else if ((mn.name.equals("b") || mn.name.equals("generateBiomeTerrain")) && (mn.desc.equals("(Laht;Ljava/util/Random;Latf;IID)V") || mn.desc.equals("(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/chunk/ChunkPrimer;IID)V"))) {
				logger.info("Patching generateBiomeTerrain...");
				obf = mn.name.equals("b");	
				InsnList newInsn = new InsnList();
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode ain = (AbstractInsnNode) iter.next();
					if (ain instanceof MethodInsnNode) {
						MethodInsnNode min = (MethodInsnNode) ain;
						if ((min.desc.equals("(Lcj;)F") || min.desc.equals("(Lnet/minecraft/util/math/BlockPos;)F")) && (min.name.equals("a") || min.name.equals("getFloatTemperature")) && (min.owner.equals("aig") || min.owner.equals("net/minecraft/world/biome/BiomeGenBase"))) {
							logger.info("Located getFloatTemperature");
							min.name = "getFloatTemperature_old";
							newInsn.add(min);
							continue;
						}
					}
					newInsn.add(ain);
				}
				mn.instructions = newInsn;
			}
			else if ((mn.name.equals("c") || mn.name.equals("getFoliageColorAtPos")) && (mn.desc.equals("(Lcj;)I") || mn.desc.equals("(Lnet/minecraft/util/math/BlockPos;)I"))) {
				logger.info("Patching getFoliageColorAtPos...");
				obf = mn.name.equals("c");	
				InsnList newInsn = new InsnList();
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode ain = (AbstractInsnNode) iter.next();
					if (ain instanceof MethodInsnNode) {
						MethodInsnNode min = (MethodInsnNode) ain;
						if ((min.desc.equals("(Lcj;)F") || min.desc.equals("(Lnet/minecraft/util/math/BlockPos;)F")) && (min.name.equals("a") || min.name.equals("getFloatTemperature")) && (min.owner.equals("aig") || min.owner.equals("net/minecraft/world/biome/BiomeGenBase"))) {
							logger.info("Located getFloatTemperature");
							min.name = "getFloatTemperature_old";
							newInsn.add(min);
							continue;
						}
					}
					newInsn.add(ain);
				}
				mn.instructions = newInsn;
			}
			else if ((mn.name.equals("b") || mn.name.equals("getGrassColorAtPos")) && (mn.desc.equals("(Lcj;)I") || mn.desc.equals("(Lnet/minecraft/util/math/BlockPos;)I"))) {
				logger.info("Patching getGrassColorAtPos...");
				obf = mn.name.equals("b");	
				InsnList newInsn = new InsnList();
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode ain = (AbstractInsnNode) iter.next();
					if (ain instanceof MethodInsnNode) {
						MethodInsnNode min = (MethodInsnNode) ain;
						if ((min.desc.equals("(Lcj;)F") || min.desc.equals("(Lnet/minecraft/util/math/BlockPos;)F")) && (min.name.equals("a") || min.name.equals("getFloatTemperature")) && (min.owner.equals("aig") || min.owner.equals("net/minecraft/world/biome/BiomeGenBase"))) {
							logger.info("Located getFloatTemperature");
							min.name = "getFloatTemperature_old";
							newInsn.add(min);
							continue;
						}
					}
					newInsn.add(ain);
				}
				mn.instructions = newInsn;
			}
		}
		{
			logger.info("Adding isRainEnabled method...");
			MethodNode method = new MethodNode();
			method.desc = "()Z";
			method.name = "isRainEnabled";
			method.exceptions = Lists.newArrayList();
			method.instructions.add(new VarInsnNode(ALOAD, 0));
			method.instructions.add(new FieldInsnNode(GETFIELD, obf ? "aig" : "net/minecraft/world/biome/BiomeGenBase", obf ? "G" : "enableRain", "Z"));
			method.instructions.add(new InsnNode(IRETURN));
			method.visitMaxs(0, 0);
			cn.methods.add(method);
		}
		{
			logger.info("Readding getFloatTemperature method...");
			MethodNode method = new MethodNode();
			method.desc = obf ? "(Lcj;)F" : "(Lnet/minecraft/util/math/BlockPos;)F";
			method.name = "getFloatTemperature_old";
			method.exceptions = Lists.newArrayList();
			method.instructions = oldInsn;
			method.visitMaxs(0, 0);
			cn.methods.add(method);
		}
		logger.info("BiomeGenBase patch complete!");
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
			if ((mn.name.equals("canSnowAtBody")) && (mn.desc.equals("(Lcj;Z)Z") || mn.desc.equals("(Lnet/minecraft/util/math/BlockPos;Z)Z"))) {
				boolean obf = mn.desc.equals("(Lcj;Z)Z");
				logger.info("Patching canSnowAtBody...");
				InsnList insn = new InsnList();
				insn.add(new VarInsnNode(ALOAD, 0));
				insn.add(new VarInsnNode(ALOAD, 1));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, obf ? "aht" : "net/minecraft/world/World", obf ? "b" : "getBiomeGenForCoords", obf ?  "(Lcj;)Laig;" : "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/BiomeGenBase;", false));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, obf ? "aig" : "net/minecraft/world/biome/BiomeGenBase", "isRainEnabled", "()Z", false));
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
			if ((mn.name.equals("b") || mn.name.equals("updateTick")) && (mn.desc.equals("(Laht;Lcj;Larc;Ljava/util/Random;)V") || mn.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"))) {
				boolean obf = mn.desc.equals("(Laht;Lcj;Larc;Ljava/util/Random;)V");
				logger.info("Patching updateTick...");
				InsnList insn = new InsnList();
				LabelNode end = new LabelNode();
				insn.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/init/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/init/SeasonsAPI;"));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/init/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/interfaces/IWorldInterface;", false));
				insn.add(new VarInsnNode(ALOAD, 2));
				insn.add(new InsnNode(ICONST_1));
				insn.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/interfaces/IWorldInterface", "getTemperature", obf ? "(Lcj;Z)F" : "(Lnet/minecraft/util/math/BlockPos;Z)F", true));
				insn.add(new LdcInsnNode(new Float("5")));
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
			if ((mn.name.equals("b") || mn.name.equals("updateTick")) && (mn.desc.equals("(Laht;Lcj;Larc;Ljava/util/Random;)V") || mn.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"))) {
				boolean obf = mn.desc.equals("(Laht;Lcj;Larc;Ljava/util/Random;)V");
				logger.info("Patching updateTick...");
				InsnList insn = new InsnList();
				LabelNode end = new LabelNode();
				insn.add(new FieldInsnNode(GETSTATIC, "mod/mindcraft/seasons/api/init/SeasonsAPI", "instance", "Lmod/mindcraft/seasons/api/init/SeasonsAPI;"));
				insn.add(new MethodInsnNode(INVOKEVIRTUAL, "mod/mindcraft/seasons/api/init/SeasonsAPI", "getWorldInterface", "()Lmod/mindcraft/seasons/api/interfaces/IWorldInterface;", false));
				insn.add(new VarInsnNode(ALOAD, 2));
				insn.add(new InsnNode(ICONST_1));
				insn.add(new MethodInsnNode(INVOKEINTERFACE, "mod/mindcraft/seasons/api/interfaces/IWorldInterface", "getTemperature", obf ? "(Lcj;Z)F" : "(Lnet/minecraft/util/math/BlockPos;Z)F", true));
				insn.add(new LdcInsnNode(new Float("5")));
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
			if ((mn.name.equals("b") || mn.name.equals("updateTick")) && (mn.desc.equals("(Laht;Lcj;Larc;Ljava/util/Random;)V") || mn.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"))) {
				logger.info("Patching updateTick...");
				boolean obf = mn.name.equals("b");
				InsnList insn = new InsnList();
				insn.add(new VarInsnNode(ALOAD, 1));
				insn.add(new VarInsnNode(ALOAD, 2));
				insn.add(new VarInsnNode(ALOAD, 3));
				insn.add(new MethodInsnNode(INVOKESTATIC, "mod/mindcraft/seasons/api/utils/EventUtils", "postCropUdate", obf ? "(Laht;Lcj;Larc;)Larc;" : "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", false));
				insn.add(new VarInsnNode(ASTORE, 3));
				insn.add(new LabelNode());
				Iterator<AbstractInsnNode> iter = mn.instructions.iterator();
				while (iter.hasNext()) {
					AbstractInsnNode ain = iter.next();
					if (ain instanceof LabelNode) {
						mn.instructions.insert(ain, insn);
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
