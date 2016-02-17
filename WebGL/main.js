var gl;

function start() {
    var canvas = document.getElementById('canvas');

    gl = initWebGL(canvas);

    // check for WebGL support
    if(gl) {
        gl.clearColor(0.0, 0.0, 0.0, 1.0);
        gl.enable(gl.DEPTH_TEST);
        gl.depthFunc(gl.LEQUAL);
        gl.clear(gl.CLEAR_COLOR_BIT | gl.DEPTH_BUFFER_BIT);
    }
}

function initWebGL(canvas) {
    var gl = null;

    try {
        gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
    }
    catch(e) {
        gl = null;
    }

    return gl;
}

start();
