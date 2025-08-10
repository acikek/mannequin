package com.acikek.mannequin.client;

import com.acikek.mannequin.util.LimbOrientation;
import com.acikek.mannequin.util.LimbType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class LimbModel extends Model {

	public LimbModel(ModelPart modelPart) {
		super(modelPart, RenderType::entityTranslucent);
	}

	public record Coordinates(int baseU, int baseV, int layerU, int layerV) {

		public static Coordinates create(LimbType type, LimbOrientation orientation) {
			if (type == LimbType.TORSO || orientation == LimbOrientation.NONE) {
				return new Coordinates(0, 0, 0, 0);
			}
			return switch (type) {
				case LEG -> switch (orientation) {
					case LEFT -> new Coordinates(16, 48, 0, 48);
					case RIGHT -> new Coordinates(0, 16, 0, 32);
					default -> throw new IllegalStateException();
				};
				case ARM -> switch (orientation) {
					case LEFT -> new Coordinates(32, 48, 48, 48);
					case RIGHT -> new Coordinates(40, 16, 40, 32);
					default -> throw new IllegalStateException();
				};
				default -> throw new IllegalStateException();
			};
		}
	}

	public static LayerDefinition createLayer(LimbType type, LimbOrientation orientation, boolean slim) {
		var coordinates = Coordinates.create(type, orientation);
		var mesh = new MeshDefinition();
		var root = mesh.getRoot();
		root.addOrReplaceChild("main", CubeListBuilder.create()
			.texOffs(coordinates.baseU(), coordinates.baseV()).addBox(-10.0F, -14.0F, 6.0F, slim ? 3.0F : 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(coordinates.layerU(), coordinates.layerV()).addBox(-10.0F, -14.0F, 6.0F, slim ? 3.0F : 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
			PartPose.offset(8.0F, 24.0F, -8.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
