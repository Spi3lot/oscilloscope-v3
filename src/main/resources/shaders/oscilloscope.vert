uniform mat4 transform;
uniform vec4 viewport;

attribute vec4 position;
attribute vec4 color;
attribute vec4 direction;

void main() {
    gl_Position = transform * position;
}
