#version 150

uniform float GameTime;
uniform vec2 ScreenSize;
out vec4 fragColor;

#define Sat(a) clamp(a, 0., 1.)
#define getN(p) normalize(cross(dFdx(p), dFdy(p)))

mat2 rot(float a){
    float c = cos(a), s = sin(a);
    return mat2(c, -s, s, c);
}

void main() {
    // Set the number of samples for antialiasing
    const int numSamples = 2;

    // Accumulator for multisampling
    vec3 colorSum = vec3(0.0);

    // Iterate over samples
    for (int i = 0; i < numSamples; i++) {
        // Offset the sample position within the pixel
        vec2 sampleOffset = vec2(float(i) / float(numSamples), 0.0);

        // Calculate the UV with the sample offset
        vec2 uv = (gl_FragCoord.xy - 0.5 * ScreenSize + sampleOffset) / ScreenSize.y;

        // Rest of the shader code remains the same

        vec2 n, q, u = vec2(uv * 1.6);
        float d = dot(u, u), s = 9.0, t = GameTime * 100.0 + sin(GameTime / 200.0) * 100.0, o = 0.0, j;

        for (mat2 m = rot(7.); j < 18.; j++) {
            u *= m;
            n *= m;
            q = u * s + t * 4.0 + sin(t * 4.0 - d * 6.0) * 0.8 + j + n;
            o += dot(cos(q) / s, vec2(2.));
            n -= sin(q);
            s *= 1.2;
        }

        vec3 N = getN(vec3(u, o));

        vec4 col = vec4(pow(Sat(dot(N, normalize(vec3(0.7, 0.5, 1)))), 5.));
        col *= vec4(0.75, tanh(o * 0.6), tanh(o * 0.5), 0.);
        col += mix(vec4(0.8, .2, .1, 0), vec4(3, 0.5, 0.2, 0), tanh(o * 0.5));

        // Accumulate the color
        colorSum += col.xyz;
    }

    // Calculate the average color
    vec3 finalColor = colorSum / float(numSamples);

    fragColor = vec4(finalColor, 1.0);
}
