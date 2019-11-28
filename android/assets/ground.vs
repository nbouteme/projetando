#version 300 es

in vec2 pos;
in float influence;

uniform mat4 view;
uniform vec2 scroll;
uniform float steep;
uniform float minh;
uniform float maxh;

out float h;
out float v;
out vec2 opos;
out float raw;
out float inp;

const float fa = 289.0;

vec3 permute(vec3 x) {
  return mod(((x * 36.0) + 1.0) * x, fa);
}

float snoise(float v) {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
  vec2 v2 = vec2(v, v);
  vec2 i  = floor(v2 + dot(v2, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

  vec2 i1;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

  i = mod(i, fa);
  vec3 k = i.y + vec3(0.0, i1.y, 1.0);
  vec3 l = i.x + vec3(0.0, i1.x, 1.0);
  vec3 p = permute(permute(k) + l);
  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m * m * m * m;
  vec3 x = fract(p * C.w);
  x *= 2.0;
  x -= 1.0;
  vec3 h = abs(x);
  h -= 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

  m *= inversesqrt(a0 * a0 + h * h);

  vec3 g;
  g.x  = a0.x  * x0.x   + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

void main() {
	vec2 calc = pos + scroll;
    vec4 worldpos = view * vec4(calc.x, calc.y, 1.0f, 1.0f);

    //float offset = scroll.x;

    opos = calc;
    raw = snoise(calc.x / steep);
    inp = calc.x / steep;
    h = raw * (maxh - minh) + minh;
    v = influence;
	worldpos = view * vec4(pos.x, pos.y + influence * h * 100.0, 1.0, 1.0);
	gl_Position = worldpos;
}
