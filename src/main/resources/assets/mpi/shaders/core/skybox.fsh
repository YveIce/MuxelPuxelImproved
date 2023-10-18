#version 150

in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;
uniform vec2 ScreenSize;
uniform float GameTime;

out vec4 FragColor;

void main() {
    // Erhalte die normalisierten Texturkoordinaten
    vec2 uv = texCoord0;

    // Rotiere die Texturkoordinaten basierend auf der GameTime
    mat2 rotationMatrix = mat2(cos(GameTime), -sin(GameTime), sin(GameTime), cos(GameTime));
    uv = rotationMatrix * uv;

    // Bestimme, welche Seite der Skybox angezeigt werden soll
    vec3 faceCoords;
    if (uv.x > 0.5) {
        faceCoords = vec3(1.0, 2.0 * uv.y - 1.0, 0.0);
    } else if (uv.x < 0.5) {
        faceCoords = vec3(-1.0, 2.0 * uv.y - 1.0, 0.0);
    } else if (uv.y > 0.5) {
        faceCoords = vec3(2.0 * uv.x - 1.0, 1.0, 0.0);
    } else {
        faceCoords = vec3(2.0 * uv.x - 1.0, -1.0, 0.0);
    }

    // Abhängig von der Seite, wähle die entsprechende Textur aus
    vec4 color;
    if (faceCoords.x == 1.0) {
        color = texture(Sampler0, faceCoords.yz);
    } else if (faceCoords.x == -1.0) {
        color = texture(Sampler1, faceCoords.yz);
    } else if (faceCoords.y == 1.0) {
        color = texture(Sampler2, faceCoords.xz);
    } else if (faceCoords.y == -1.0) {
        color = texture(Sampler3, faceCoords.xz);
    } else if (faceCoords.z == 1.0) {
        color = texture(Sampler4, faceCoords.xy);
    } else if (faceCoords.z == -1.0) {
        color = texture(Sampler5, faceCoords.xy);
    }

    // Gib die endgültige Farbe aus
    FragColor = color;
}
