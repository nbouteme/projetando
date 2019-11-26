#version 300 es

precision mediump float;

uniform sampler2D tex;
out vec4 outcol;

in float h;
in float raw;
in float inp;
in vec2 opos;

void main() {
//	float col = 1.0;
    vec2 ts = vec2(32.0, 64.0);
    float b = 28.0;
    float yp = gl_FragCoord.y - h * 100.0 + b;
    vec2 coord = vec2(mod(inp * 512.0, ts.x),
                    ts.y / 2.0 - mod(yp, ts.y / 2.));
    if (yp < ts.y * 1.5)
        coord.y += ts.y / 2.0;
	outcol = vec4(texture(tex, coord / ts).rgb, 1.0);
}