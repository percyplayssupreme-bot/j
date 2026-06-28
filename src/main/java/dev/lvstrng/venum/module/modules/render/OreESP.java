package dev.lvstrng.venum.module.modules.render;

import dev.lvstrng.venum.event.events.GameRenderListener;
import dev.lvstrng.venum.module.Category;
import dev.lvstrng.venum.module.Module;
import dev.lvstrng.venum.utils.EncryptedString;
import dev.lvstrng.venum.utils.RenderUtils;
import dev.lvstrng.venum.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import java.awt.*;

public final class OreESP extends Module implements GameRenderListener {
 public OreESP(){super(EncryptedString.of("Ore ESP"),EncryptedString.of("Highlights ores through walls with colored boxes"),-1,Category.RENDER);} 
 @Override public void onEnable(){eventManager.add(GameRenderListener.class,this);super.onEnable();}
 @Override public void onDisable(){eventManager.remove(GameRenderListener.class,this);super.onDisable();}
 @Override public void onGameRender(GameRenderEvent e){for(WorldChunk c: WorldUtils.getLoadedChunks().toList()){BlockPos.stream(c.getPos().getStartPos(), c.getPos().getEndPos()).forEach(pos->{BlockState s=mc.world.getBlockState(pos); Color col=getOreColor(s.getBlock()); if(col!=null) RenderUtils.renderFilledBox(e.matrices,pos.getX()+0.15,pos.getY()+0.15,pos.getZ()+0.15,pos.getX()+0.85,pos.getY()+0.85,pos.getZ()+0.85,col);});}}
 private Color getOreColor(Block b){ if(b==Blocks.DIAMOND_ORE||b==Blocks.DEEPSLATE_DIAMOND_ORE) return new Color(0,255,255,120); if(b==Blocks.GOLD_ORE||b==Blocks.DEEPSLATE_GOLD_ORE) return new Color(255,215,0,120); if(b==Blocks.IRON_ORE||b==Blocks.DEEPSLATE_IRON_ORE) return new Color(216,165,117,120); if(b==Blocks.EMERALD_ORE||b==Blocks.DEEPSLATE_EMERALD_ORE) return new Color(0,255,0,120); if(b==Blocks.REDSTONE_ORE||b==Blocks.DEEPSLATE_REDSTONE_ORE) return new Color(255,0,0,120); if(b==Blocks.LAPIS_ORE||b==Blocks.DEEPSLATE_LAPIS_ORE) return new Color(0,0,255,120); if(b==Blocks.COAL_ORE||b==Blocks.DEEPSLATE_COAL_ORE) return new Color(60,60,60,120); if(b==Blocks.COPPER_ORE||b==Blocks.DEEPSLATE_COPPER_ORE) return new Color(184,115,51,120); return null; }
}
