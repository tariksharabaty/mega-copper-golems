package com.oidioomstudio.megacoppergolems.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Set;

public class MegaCopperGolemCube extends ModelPart.Cube {
    private final List<ModelPart.Polygon> customPolygons;

    public MegaCopperGolemCube(List<ModelPart.Polygon> polygons) {
        super(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 128, 128, Set.of());
        this.customPolygons = polygons;
    }

    @Override
    public void compile(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        Matrix4f poseMatrix = pose.pose();
        Vector3f normalVec = new Vector3f();
        Vector3f posVec = new Vector3f();
        for (ModelPart.Polygon polygon : this.customPolygons) {
            Vector3f normal = pose.transformNormal(polygon.normal(), normalVec);
            float nx = normal.x();
            float ny = normal.y();
            float nz = normal.z();
            for (ModelPart.Vertex vertex : polygon.vertices()) {
                poseMatrix.transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), posVec);
                buffer.addVertex(posVec.x(), posVec.y(), posVec.z(), color, vertex.u(), vertex.v(), packedOverlay, packedLight, nx, ny, nz);
            }
        }
    }
}
