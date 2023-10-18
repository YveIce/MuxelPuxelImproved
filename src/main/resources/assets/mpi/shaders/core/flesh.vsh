#version 150

in vec3 Position;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform vec2 ScreenSize;

vec4 applyMatrix(mat4 matrix, vec4 vector) {
    return matrix * vector;
}

void main() {
    gl_Position = applyMatrix(ProjMat, applyMatrix(ModelViewMat, vec4(Position, 1.0)));
}
