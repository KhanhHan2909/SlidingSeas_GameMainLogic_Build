
package gameplay.animation;

import board.Tile;

/*
 * Hình ảnh tạm thời của một tile trong lúc animation.
 *
 * x, y: góc dưới bên trái.
 * width, height: kích thước hình ảnh.
 * alpha: độ trong suốt, từ 0 đến 1.
 */
public record TileVisual(int type, float x, float y, float width, float height, float alpha) {}
