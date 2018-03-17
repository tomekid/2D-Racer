package com.mediocrefireworks.realracer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Gl20Test {

    String vertexShader = "attribute vec4 a_position;    \n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;" +
            "varying vec2 v_texCoords;" +
            "void main()                  \n" +
            "{                            \n" +
            "   v_color = vec4(1, 1, 1, 1); \n" +
            "   v_texCoords = a_texCoord0; \n" +
            "   gl_Position =  u_projTrans * a_position;  \n" +
            "}                            \n";
    String fragmentShader = "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main()                                  \n" +
            "{                                            \n" +
            "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
            "}";
    private ShaderProgram shader;
    private Mesh mesh;
    private Texture texture;


    public Gl20Test() {
        shader = new ShaderProgram(vertexShader, fragmentShader);


        mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[]
                {-0.5f, -0.5f, 0, 1, 1, 1, 1, 0, 1,
                        0.5f, -0.5f, 0, 1, 1, 1, 1, 1, 1,
                        0.5f, 0.5f, 0, 1, 1, 1, 1, 1, 0,
                        -0.5f, 0.5f, 0, 1, 1, 1, 1, 0, 0});
        mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});
        texture = new Texture(Gdx.files.internal("data/img/leveltest/surface.png"));
    }

    public void render(OrthographicCamera camera) {


        texture.bind();
        shader.begin();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        shader.setUniformi("u_texture", 0);
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
    }


}
