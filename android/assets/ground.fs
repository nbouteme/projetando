#version 300 es

precision mediump float;

uniform sampler2D tex;
uniform vec2 ts;
out vec4 outcol;

in float h;
in float raw;
in float inp;
in float v;
in vec2 opos;

void main() {
//	float col = 1.0;
    float yp = (100.0 + h * 100.0) - gl_FragCoord.y;
    vec2 its = vec2(16., 32.) * 4.;
    vec2 coord = vec2(inp * 512.0f,
                    mod(yp, its.y * 0.5));
    if (yp > its.y * 0.5)
        coord.y += its.y * 0.5;
	outcol = vec4(texture(tex, coord / its).rgb, 1.0);
	//outcol = vec4(1.0);
}