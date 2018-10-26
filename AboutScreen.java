package ru.erked.dnacorrector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

public class AboutScreen implements Screen{

    private final float width = Gdx.graphics.getWidth();
    private final float height = Gdx.graphics.getHeight();

    private StartDNAC game;
    private SpriteBatch batch;
    private Controllers controller;
    private Fonts text;
    private Fonts header;
    private Screen screen;

    static TextureAtlas atlas;

    private Buttons playButton;
    private Sprite background;
    private Sprite black;

    public AboutScreen(StartDNAC game, GameScreen screen){

        controller = new Controllers();
        this.screen = screen;

        GameScreen.music.play();

        atlas = new TextureAtlas(Gdx.files.internal("textureAtlas/textures.atlas"));
        this.game = game;
        batch = new SpriteBatch();
        text = new Fonts(20, Color.WHITE, 5.0F, Color.BLACK);
        header = new Fonts(10, Color.WHITE, 10.0F, Color.BLACK);

        background = atlas.createSprite("background");
        background.setBounds(0.5F*width - 1.6015625F*height*0.5F, 0.0F, 1.6015625F*height, height);
        playButton = new Buttons("playI","playA", 0.15F*width, 0.8F*width, 0.3F*width, 1.0F, 1.0F, -1);
        black = atlas.createSprite("black");
        black.setBounds(0.0F, 0.0F, width, height);

    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {

        boolean escapePressed = Gdx.input.isKeyPressed( Input.Keys.ESCAPE );
        boolean backPressed   = Gdx.input.isKeyPressed( Input.Keys.BACK );
        if ( escapePressed || backPressed ) {
            GameScreen.prefs.putLong("SCORE", GameScreen.score);
            GameScreen.prefs.flush();
            GameScreen.music.stop();
            Gdx.app.exit();
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        background.draw(batch);
        black.draw(batch);

        header.draw(batch, game.locale[0], 0.5F*(width - header.getWidth(game.locale[0])), 0.95F*height);
        header.draw(batch, game.locale[21], 0.5F*(width - header.getWidth(game.locale[21])), 0.95F*height - 2.0F*header.getHeight("A"));

        textDraw();

        playButton.getSprite().draw(batch);
        if(controller.isOn(playButton.getX(), playButton.getY(), playButton.getWidth(), playButton.getHeight(), false)){
            playButton.setActiveMode(true);
        }else{
            playButton.setActiveMode(false);
        }
        if(controller.isClicked(playButton.getX(), playButton.getY(), playButton.getWidth(), playButton.getHeight(), true, false)){
            game.setScreen(screen);
            this.dispose();
        }

        batch.end();
    }

    private void textDraw(){
        text.draw(
                batch,
                game.locale[22],
                0.025F*width,
                0.75F*height
        );
        text.draw(
                batch,
                game.locale[23],
                0.025F*width,
                0.7F*height
        );
        text.draw(
                batch,
                game.locale[24],
                0.025F*width,
                0.6F*height
        );
        text.draw(
                batch,
                game.locale[25],
                0.025F*width,
                0.55F*height
        );
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
