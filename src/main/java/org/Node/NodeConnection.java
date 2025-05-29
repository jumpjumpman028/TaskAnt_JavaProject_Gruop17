package org.Node;

import javafx.scene.Parent;
import javafx.scene.shape.Line;

public class NodeConnection extends Line {
    public NodeConnection(Parent fromNode, Parent toNode) {
        // 綁定 startX, startY 到 fromNode 中心
        startXProperty().bind(fromNode.layoutXProperty().add(fromNode.layoutBoundsProperty().get().getWidth()));
        startYProperty().bind(fromNode.layoutYProperty().add(fromNode.layoutBoundsProperty().get().getHeight() / 2));
        // 綁定 endX, endY 到 toNode 中心
        endXProperty().bind(toNode.layoutXProperty().add(0));
        endYProperty().bind(toNode.layoutYProperty().add(toNode.layoutBoundsProperty().get().getHeight() / 2));
    }
}
