uniform mat4 transform;

attribute vec4 position;
attribute vec4 color;

out vec4 vertColor;

void main() {
    vertColor = color;
    gl_Position = transform * position;
}
