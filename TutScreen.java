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

public class TutScreen implements Screen{

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

    private ArrayList<Sprite> nitroBases;

    public TutScreen(StartDNAC game, GameScreen screen){

        controller = new Controllers();
        this.screen = screen;

        GameScreen.music.play();

        atlas = new TextureAtlas(Gdx.files.internal("textureAtlas/textures.atlas"));
        this.game = game;
        batch = new SpriteBatch();
        text = new Fonts(20, Color.WHITE, 5.0F, Color.BLACK);
        header = new Fonts(10, Color.WHITE, 10.0F, Color.BLACK);

        nitroBases = new ArrayList<>();
        Sprite adnine = atlas.createSprite("adenine");
        Sprite guanine = atlas.createSprite("guanine");
        Sprite thymine = atlas.createSprite("thymine");
        Sprite cytosine = atlas.createSprite("cytosine");
        adnine.setBounds(0.05F*width, 0.7F*height, 0.1F*height, 0.1F*height);
        thymine.setBounds(0.05F*width + 0.1F*height, 0.7F*height, 0.1F*height, 0.1F*height);
        guanine.setBounds(0.05F*width, 0.55F*height, 0.1F*height, 0.1F*height);
        cytosine.setBounds(0.05F*width + 0.1F*height, 0.55F*height, 0.1F*height, 0.1F*height);
        nitroBases.add(adnine);
        nitroBases.add(thymine);
        nitroBases.add(guanine);
        nitroBases.add(cytosine);

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
        header.draw(batch, game.locale[10], 0.5F*(width - header.getWidth(game.locale[10])), 0.95F*height - 2.0F*header.getHeight("A"));

        String[] letter = new String[]{game.locale[11],game.locale[12],game.locale[13],game.locale[14]};
        for(int i=0;i<4;i++) {
            nitroBases.get(i).draw(batch);
            text.draw(
                    batch,
                    letter[i],
                    nitroBases.get(i).getX() + 0.5F*(nitroBases.get(i).getWidth() - text.getWidth(letter[i])),
                    nitroBases.get(i).getY() + 0.5F*(nitroBases.get(i).getHeight() + text.getHeight(letter[i]))
            );
        }
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
                "+",
                0.05F*width + 0.5F*(0.1F*height + 0.1F*height - text.getWidth("+")),
                0.75F*height + 0.5F*text.getHeight("+")
        );
        text.draw(
                batch,
                "+",
                0.05F*width + 0.5F*(0.1F*height + 0.1F*height - text.getWidth("+")),
                0.6F*height + 0.5F*text.getHeight("+")
        );
        text.draw(
                batch,
                "=",
                0.05F*width + 0.5F*(0.1F*height + 0.3F*height - text.getWidth("=")),
                0.75F*height + 0.5F*text.getHeight("=")
        );
        text.draw(
                batch,
                "=",
                0.05F*width + 0.5F*(0.1F*height + 0.3F*height - text.getWidth("=")),
                0.6F*height + 0.5F*text.getHeight("=")
        );
        text.draw(
                batch,
                game.locale[15],
                0.05F*width + 0.5F*(0.1F*height + 0.35F*height),
                0.75F*height + 0.5F*text.getHeight(game.locale[15])
        );
        text.draw(
                batch,
                game.locale[15],
                0.05F*width + 0.5F*(0.1F*height + 0.35F*height),
                0.6F*height + 0.5F*text.getHeight(game.locale[15])
        );
        text.draw(
                batch,
                game.locale[16],
                0.025F*width,
                0.5F*height
        );
        text.draw(
                batch,
                game.locale[17],
                0.025F*width,
                0.45F*height
        );
        text.draw(
                batch,
                game.locale[18],
                0.025F*width,
                0.4F*height
        );
        text.draw(
                batch,
                game.locale[19],
                0.025F*width,
                0.35F*height
        );
        text.draw(
                batch,
                game.locale[20],
                0.025F*width,
                0.3F*height
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
