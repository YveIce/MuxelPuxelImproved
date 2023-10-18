#version 150

in vec3 Position;
in vec2 UV0;


uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

out vec2 texCoord0;

vec4 applyMatrix(mat4 matrix, vec4 vector) {
    return matrix * vector;
}

void main() {
    gl_Position = applyMatrix(ProjMat, applyMatrix(ModelViewMat, vec4(Position, 1.0)));
    texCoord0 = UV0;
}
