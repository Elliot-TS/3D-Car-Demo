#version 300 es

precision highp float;

in vec4 modelPosition;
in vec4 worldPosition;
in vec4 texCoord;
in vec3 worldNormal;
out vec4 fragmentColor; //#vec4# A four-element vector [r,g,b,a].; Alpha is opacity, we set it to 1 for opaque.; It will be useful later for transparency.

uniform struct {
  vec3 color;
  } material;

void main(void) {
    fragmentColor = vec4 (material.color, 1); //#1, 1, 0, 1# solid yellow
}
