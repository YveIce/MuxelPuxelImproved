#version 150

uniform float GameTime;
uniform vec2 ScreenSize;
out vec4 fragColor;

#define PI      3.14159265359
#define PI2     2.0 * PI
#define RADIAN  180.0 / PI
#define CAMERA_FOV 90.0 * RADIAN

float hash(in vec3 p)
{
    return fract(sin(dot(p, vec3(127.1, 311.7, 321.4))) * 43758.5453123);
}

float noise(in vec3 p)
{
    p.z += GameTime * 0.75;
    vec3 i = floor(p);
    vec3 f = fract(p);
    f *= f * (3.0 - 2.0 * f);
    vec2 c = vec2(0, 1);
    return mix(
        mix(mix(hash(i + c.xxx), hash(i + c.yxx), f.x),
            mix(hash(i + c.xyx), hash(i + c.yyx), f.x),
            f.y),
        mix(mix(hash(i + c.xxy), hash(i + c.yxy), f.x),
            mix(hash(i + c.xyy), hash(i + c.yyy), f.x),
            f.y),
        f.z);
}

float fbm(in vec3 p)
{
    float f = 0.0;
    f += 0.50000 * noise(1.0 * p);
    f += 0.25000 * noise(2.0 * p);
    f += 0.12500 * noise(4.0 * p);
    f += 0.06250 * noise(8.0 * p);
    return f;
}

struct Camera { vec3 p, t, u; };
struct Ray { vec3 o, d; };

void generate_ray(Camera c, out Ray r, in vec2 fragCoord)
{
    float ratio = ScreenSize.x / ScreenSize.y;
    vec2 uv = (2.0 * fragCoord.xy / ScreenSize.xy - 1.0) * vec2(ratio, 1.0);
    r.o = c.p;
    r.d = normalize(vec3(uv.x, uv.y, 1.0 / tan(CAMERA_FOV * 0.5)));
    vec3 cd = c.t - c.p;
    vec3 rx, ry, rz;
    rz = normalize(cd);
    rx = normalize(cross(rz, c.u));
    ry = normalize(cross(rx, rz));
    mat3 tmat = mat3(rx.x, rx.y, rx.z, ry.x, ry.y, ry.z, rz.x, rz.y, rz.z);
    r.d = normalize(tmat * r.d);
}

vec3 cubemap(vec3 d, vec3 c1, vec3 c2)
{
    return fbm(d) * mix(c1, c2, d * 0.9 + 0.1);
}

vec4 HueShift(in vec3 Color, in float Shift)
{
    vec3 P = vec3(0.55735) * dot(vec3(0.55735), Color);
    vec3 U = Color - P;
    vec3 V = cross(vec3(0.55735), U);
    Color = U * cos(Shift * 6.2832) + V * sin(Shift * 6.2832) + P;
    return vec4(Color, 1.0);
}

void main() {
    vec2 uv = (gl_FragCoord.xy - 0.5 * ScreenSize) / ScreenSize.y;

    Camera c;
    c.p = vec3(0.2, 0.66, 2.0);
    c.u = vec3(0.66, 1.0, 0.0);
    c.t = vec3(26.0  * sin(mod(GameTime * 640.0, PI2)),
               28.0  * cos(mod(GameTime * 320.0, PI2)),
               -26.0 * cos(mod(GameTime * 200.0, PI2)));

    Ray r;
    generate_ray(c, r, gl_FragCoord.xy);
    vec3 col = cubemap(r.d, vec3(0.5, 0.9, 0.7), vec3(0.1, 0.1, 0.9));
    fragColor = HueShift(col, sin(GameTime * 222.222));
}

