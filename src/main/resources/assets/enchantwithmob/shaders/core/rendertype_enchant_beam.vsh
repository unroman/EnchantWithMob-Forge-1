#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform int FogShape;
uniform float GameTime;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    float wave = cos(length(Position.x) * 0.1 + GameTime * 100) * 0.15;
    float wave2 = cos(length(Position.y) * 0.1 + GameTime * 100) * 0.1;
    float wave3 = cos(length(Position.z) * 0.1 + GameTime * 100) * 0.15;
    gl_Position = ProjMat * ModelViewMat * vec4(Position + vec3(wave, wave2, wave3), 1.0);

    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
    vertexColor = Color;
    texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
}
