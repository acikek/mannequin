package com.acikek.mannequin.client;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class LegModel extends SkullModelBase {

	public LegModel(ModelPart modelPart) {
		super(modelPart);
	}

	@Override
	public void setupAnim(float f, float g, float h) {
	}

	public static LayerDefinition createLayer() {
		var mesh = new MeshDefinition();
		var root = mesh.getRoot();
		root.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 16).addBox(-10.0F, -14.0F, 6.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
