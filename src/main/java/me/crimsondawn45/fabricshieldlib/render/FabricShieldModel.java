package me.crimsondawn45.fabricshieldlib.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class FabricShieldModel extends Model
{
   private final ModelPart shield_flat;
   private final ModelPart shield_handle;

   public FabricShieldModel()
   {
      super(RenderLayer::getEntitySolid);
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.shield_flat = new ModelPart(this, 0, 0);
      this.shield_flat.addCuboid(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F, 0.0F);
      this.shield_handle = new ModelPart(this, 26, 0);
      this.shield_handle.addCuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F, 0.0F);
   }

   /**
    * Equivalent to method_23774
    */
   public ModelPart getShieldFlat()
   {
      return this.shield_flat;
   }
   
   /**
    * Equivalent to method_23775
    */
   public ModelPart getShieldHandle() 
   {
      return this.shield_handle;
   }

   public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
   {
      this.shield_flat.render(matrices, vertices, light, overlay, red, green, blue, alpha);
      this.shield_handle.render(matrices, vertices, light, overlay, red, green, blue, alpha);
   }
}
