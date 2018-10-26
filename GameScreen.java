package ru.erked.dnacorrector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.RandomXS128;

import java.util.ArrayList;

public class GameScreen implements Screen{

    private final float width = Gdx.graphics.getWidth();
    private final float height = Gdx.graphics.getHeight();

    public static Music music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/The Lift.mp3"));
    private Sound error = Gdx.audio.newSound(Gdx.files.internal("sounds/misc/error.wav"));
    private boolean isError;

    public static Preferences prefs = Gdx.app.getPreferences("DNA");
    private RandomXS128 rand = new RandomXS128();
    private StartDNAC game;
    private SpriteBatch batch;
    private Controllers controller;
    private Fonts text;
    private Fonts header;

    static TextureAtlas atlas;

    private static boolean isPaused;
    private static boolean isGameOver;
    private Buttons pauseButton;
    private Buttons playButton;
    private Sprite black;
    private Sprite red;
    private Sprite green;
    private Sprite cup;
    private Sprite question;
    private Sprite about;
    private Sprite background;
    private Sprite line;

    public static long score;
    private static long curScore;

    private float speed;
    private ArrayList<Sprite> left;
    private ArrayList<String> letterL;
    private ArrayList<Float> coordsL;
    private ArrayList<Sprite> right;
    private ArrayList<String> letterR;
    private ArrayList<Float> coordsR;

    private float prevDrag;
    private float timer;
    private int iter;

    public GameScreen(StartDNAC game){

        controller = new Controllers();
        Gdx.input.setInputProcessor(controller);

        iter = 0;
        speed = 0.001F*height;
        timer  = 1.0F;
        isError = false;

        atlas = new TextureAtlas(Gdx.files.internal("textureAtlas/textures.atlas"));
        this.game = game;
        batch = new SpriteBatch();
        text = new Fonts(20, Color.WHITE, 5.0F, Color.BLACK);
        header = new Fonts(10, Color.WHITE, 10.0F, Color.BLACK);

        isPaused = true;
        isGameOver = false;

        left = new ArrayList<>();
        letterL = new ArrayList<>();
        coordsL = new ArrayList<>();
        right = new ArrayList<>();
        letterR = new ArrayList<>();
        coordsR = new ArrayList<>();

        for(int i=0;i<7;i++){
            additionLeft(i);
            additionRight(i);
        }

        line = atlas.createSprite("line");
        line.setBounds(0.4975F*width, 0.0F, 0.005F*width, height);
        background = atlas.createSprite("background");
        background.setBounds(0.5F*width - 1.6015625F*height*0.5F, 0.0F, 1.6015625F*height, height);
        red = atlas.createSprite("red");
        red.setBounds(0.0F, 0.0F, 0.1F*width, height);
        green = atlas.createSprite("green");
        green.setBounds(0.9F*width, 0.0F, 0.1F*width, height);
        black = atlas.createSprite("black");
        black.setBounds(0.0F, 0.0F, width, height);
        pauseButton = new Buttons("pauseI","pauseA", 0.15F*width, 0.8F*width, height - 0.2F*width, 1.0F, 1.0F, -1);
        playButton = new Buttons("playI","playA",0.5F*width, 0.25F*width, 0.5F*height - 0.25F*width, 1.0F, 1.0F, -1);
        question = atlas.createSprite("question");
        question.setBounds(0.6F*width, playButton.getY() - 0.4F*width, 0.15F*width, 0.15F*width);
        cup = atlas.createSprite("cup");
        cup.setBounds(0.25F*width, playButton.getY() - 0.4F*width, 0.15F*width, 0.15F*width);
        about = atlas.createSprite("about");
        about.setBounds(0.425F*width, playButton.getY() - 0.4F*width, 0.15F*width, 0.15F*width);

        prevDrag = 0.0F;

    }

    @Override
    public void show() {

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        music.setLooping(true);
        music.play();

        curScore = 0;
        score = prefs.getLong("SCORE", 0);

    }

    @Override
    public void render(float delta) {

        boolean escapePressed = Gdx.input.isKeyPressed( Input.Keys.ESCAPE );
        boolean backPressed   = Gdx.input.isKeyPressed( Input.Keys.BACK );
        if ( escapePressed || backPressed ) {
            prefs.putLong("SCORE", score);
            prefs.flush();
            music.stop();
            Gdx.app.exit();
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        background.draw(batch);

        if(!isPaused) line.draw(batch);

        for(int i=0;i<7;i++){

            left.get(i).setY(left.get(i).getY() - speed);
            left.get(i).draw(batch);
            text.draw(
                    batch,
                    letterL.get(i),
                    coordsL.get(i),
                    left.get(i).getY() + 0.5F*(left.get(i).getHeight() + text.getHeight(letterL.get(i)))
            );

            right.get(i).setY(right.get(i).getY() - speed);
            right.get(i).draw(batch);
            text.draw(
                    batch,
                    letterR.get(i),
                    coordsR.get(i),
                    right.get(i).getY() + 0.5F*(right.get(i).getHeight() + text.getHeight(letterR.get(i)))
            );
        }

        if(isPaused){

            if(controller.isOn(playButton.getX(), playButton.getY(), playButton.getWidth(), playButton.getHeight(), false)){
                playButton.setActiveMode(true);
            }else{
                playButton.setActiveMode(false);
            }
            if(controller.isClicked(playButton.getX(), playButton.getY(), playButton.getWidth(), playButton.getHeight(), true, false)){
                if(isGameOver){
                    if(curScore > score){
                        score = curScore;
                        /**TODO:
                       * Submit the record
                       */
                    }
                    isGameOver = false;
                    curScore = 0;
                    while(left.size() > 0)
                        left.remove(0);
                    while(right.size() > 0)
                        right.remove(0);
                    while(letterL.size() > 0)
                        letterL.remove(0);
                    while(letterR.size() > 0)
                        letterR.remove(0);
                    while(coordsL.size() > 0)
                        coordsL.remove(0);
                    while(coordsR.size() > 0)
                        coordsR.remove(0);
                    for(int i=0;i<7;i++){
                        additionLeft(i);
                        additionRight(i);
                    }
                    isError = false;
                }
                isPaused = false;
            }

            black.draw(batch);
            playButton.getSprite().draw(batch);
            cup.draw(batch);
            question.draw(batch);
            about.draw(batch);
            header.draw(batch, game.locale[0], 0.5F*(width - header.getWidth(game.locale[0])), 0.95F*height);
            header.draw(batch, game.locale[1] + curScore, 0.5F*(width - header.getWidth(game.locale[1] + curScore)), playButton.getY() - 0.01F*width);
            header.draw(batch, game.locale[2] + score, 0.5F*(width - header.getWidth(game.locale[2] + score)), playButton.getY() - 0.125F*width);

            if(isGameOver){
                text.draw(batch, game.locale[3], 0.5F*(width - text.getWidth(game.locale[3])), 0.875F*height);
                if((letterL.get(0).equals(game.locale[11]) && letterR.get(0).equals(game.locale[12]))
                        || (letterL.get(0).equals(game.locale[12]) && letterR.get(0).equals(game.locale[11]))
                        || (letterL.get(0).equals(game.locale[13]) && letterR.get(0).equals(game.locale[14]))
                        || (letterL.get(0).equals(game.locale[14]) && letterR.get(0).equals(game.locale[13]))
                        ){
                    String a = game.locale[11], b = game.locale[12], c = game.locale[13], d = game.locale[14];
                    String s1 = "", s2 = "";
                    String s = letterL.get(0);
                    if (s.equals(a)) {
                        s1 = game.locale[4];
                        s2 = game.locale[5];
                    } else if (s.equals(b)) {
                        s1 = game.locale[5];
                        s2 = game.locale[4];
                    } else if (s.equals(c)) {
                        s1 = game.locale[6];
                        s2 = game.locale[7];
                    } else if (s.equals(d)) {
                        s1 = game.locale[7];
                        s2 = game.locale[6];
                    }
                    text.draw(batch, s1 + " + " + s2, 0.5F*(width - text.getWidth(s1 + " + " + s2)), 0.825F*height);
                    text.draw(batch, game.locale[8], 0.5F*(width - text.getWidth(game.locale[8])), 0.775F*height);
                }else{
                    String a = game.locale[11], b = game.locale[12], c = game.locale[13], d = game.locale[14];
                    String s1 = "", s2 = "";
                    String s = letterL.get(0);
                    if (s.equals(a)) {
                        s1 = game.locale[4];
                    } else if (s.equals(b)) {
                        s1 = game.locale[5];
                    } else if (s.equals(c)) {
                        s1 = game.locale[6];
                    } else if (s.equals(d)) {
                        s1 = game.locale[7];
                    }
                    String s3 = letterR.get(0);
                    if (s3.equals(a)) {
                        s2 = game.locale[4];
                    } else if (s3.equals(b)) {
                        s2 = game.locale[5];
                    } else if (s3.equals(c)) {
                        s2 = game.locale[6];
                    } else if (s3.equals(d)) {
                        s2 = game.locale[7];
                    }
                    text.draw(batch, s1 + " + " + s2, 0.5F*(width - text.getWidth(s1 + " + " + s2)), 0.825F*height);
                    text.draw(batch, game.locale[9], 0.5F*(width - text.getWidth(game.locale[9])), 0.775F*height);
                }
            }

            speed = 0.0F;

            if(controller.isClicked(question.getX(), question.getY(), question.getWidth(), question.getHeight(), true, false)){
                game.setScreen(new TutScreen(game, this));
            }
            if(controller.isClicked(about.getX(), about.getY(), about.getWidth(), about.getHeight(), true, false)){
                game.setScreen(new AboutScreen(game, this));
            }

        }else{

            red.draw(batch);
            green.draw(batch);

            for(int i=0;i<7;i++){
                if(Controllers.tdrRY >= left.get(i).getY() && Controllers.tdrRY <= left.get(i).getY() + left.get(i).getHeight()){
                    if(Controllers.tdrRY >= right.get(i).getY() && Controllers.tdrRY <= right.get(i).getY() + right.get(i).getHeight()){
                        if(prevDrag != 0.0F && Controllers.tdrRX != 0.0F) {
                            left.get(iter).setX(left.get(iter).getX() + 1.0F * (Controllers.tdrRX - prevDrag));
                            right.get(iter).setX(right.get(iter).getX() + 1.0F * (Controllers.tdrRX - prevDrag));
                            coordsL.set(iter, coordsL.get(iter) + 1.0F * (Controllers.tdrRX - prevDrag));
                            coordsR.set(iter, coordsR.get(iter) + 1.0F * (Controllers.tdrRX - prevDrag));
                        }else{
                            iter = i;
                        }
                        prevDrag = Controllers.tdrRX;
                    }
                }
            }
            prevDrag = Controllers.tdrRX;

            if(controller.isOn(pauseButton.getX(), pauseButton.getY(), pauseButton.getWidth(), pauseButton.getHeight(), false)){
                pauseButton.setActiveMode(true);
            }else{
                pauseButton.setActiveMode(false);
            }
            if(controller.isClicked(pauseButton.getX(), pauseButton.getY(), pauseButton.getWidth(), pauseButton.getHeight(), true, false)){
                isPaused = true;
            }

            if(curScore > 0) speed = height*(0.0025F + curScore / 50000.0F);
            else speed = height*0.0025F;

            pauseButton.getSprite().draw(batch);

            text.draw(batch, game.locale[1] + curScore, 0.01F*width, 0.975F*height);

        }

        if(left.get(0).getY() < 0 - 0.25F*height && right.get(0).getY() < 0 - 0.25F*height){
            if((letterL.get(0).equals(game.locale[11]) && letterR.get(0).equals(game.locale[12]))
                    || (letterL.get(0).equals(game.locale[12]) && letterR.get(0).equals(game.locale[11]))
                    || (letterL.get(0).equals(game.locale[13]) && letterR.get(0).equals(game.locale[14]))
                    || (letterL.get(0).equals(game.locale[14]) && letterR.get(0).equals(game.locale[13]))
                    ){
                if(left.get(0).getX() + 0.5F*left.get(0).getWidth() >= width/2){
                    if(isGameOver){
                        if(!isError) curScore++;
                    }else{
                         curScore++;
                    }
                }else{
                    isGameOver = true;
                    if(!isError){
                        error.play();
                        isError = true;
                    }
                }
            }else if(right.get(0).getX() <= width/2){
                if(isGameOver){
                    if(!isError) curScore++;
                }else{
                     curScore++;
                }
            }else{
                isGameOver = true;
                if(!isError){
                    error.play();
                    isError = true;
                }
            }
        }

        if(isGameOver){
            speed = 0.0F;
            timer -= delta;
            if(timer < 0.0F){
                isPaused = true;
                timer = 1.0F;
                isError = true;
            }
        }

        if(left.get(0).getY() < 0 - 0.25F*height){
            if(!isGameOver) {
                coordsL.remove(0);
                letterL.remove(0);
                left.remove(0);
            }
        }
        if(right.get(0).getY() < 0 - 0.25F*height){
            if(!isGameOver) {
                coordsR.remove(0);
                letterR.remove(0);
                right.remove(0);
            }
        }
        if(!isGameOver) {
            if (left.size() < 7) {
                iter--;
                additionLeft(0);
            }
            if (right.size() < 7) {
                additionRight(0);
            }
        }

        batch.end();
    }

    private void additionLeft(int i){
        Sprite adenine = atlas.createSprite("adenine");
        Sprite guanine = atlas.createSprite("guanine");
        Sprite thymine = atlas.createSprite("thymine");
        Sprite cytosine = atlas.createSprite("cytosine");
        int n = rand.nextInt(4) + 1;
        switch (n){
            case 1:{
                letterL.add(game.locale[11]);
                adenine.setBounds(0.15F*height, 1.0F*height + 0.175F*i*height, 0.15F*height, 0.15F*height);
                left.add(adenine);
                coordsL.add(left.get(left.size()-1).getX() + 0.5F*(left.get(left.size()-1).getWidth() - text.getWidth(letterL.get(left.size()-1))));
                break;
            }case 2:{
                letterL.add(game.locale[13]);
                guanine.setBounds(0.15F*height, 1.0F*height + 0.175F*i*height, 0.15F*height, 0.15F*height);
                left.add(guanine);
                coordsL.add(left.get(left.size()-1).getX() + 0.5F*(left.get(left.size()-1).getWidth() - text.getWidth(letterL.get(left.size()-1))));
                break;
            }case 3:{
                letterL.add(game.locale[12]);
                thymine.setBounds(0.15F*height, 1.0F*height + 0.175F*i*height, 0.15F*height, 0.15F*height);
                left.add(thymine);
                coordsL.add(left.get(left.size()-1).getX() + 0.5F*(left.get(left.size()-1).getWidth() - text.getWidth(letterL.get(left.size()-1))));
                break;
            }case 4:{
                letterL.add(game.locale[14]);
                cytosine.setBounds(0.15F*height, 1.0F*height + 0.175F*i*height, 0.15F*height, 0.15F*height);
                left.add(cytosine);
                coordsL.add(left.get(left.size()-1).getX() + 0.5F*(left.get(left.size()-1).getWidth() - text.getWidth(letterL.get(left.size()-1))));
                break;
            }default:{
                break;
            }
        }
    }

    private void additionRight(int i){
        Sprite adenine = atlas.createSprite("adenine");
        Sprite guanine = atlas.createSprite("guanine");
        Sprite thymine = atlas.createSprite("thymine");
        Sprite cytosine = atlas.createSprite("cytosine");
        int n = rand.nextInt(4) + 1;
        switch (n){
            case 1:{
                letterR.add(game.locale[11]);
                adenine.setBounds(width - 0.3F*height, 1.0F*height + 0.175F*i*height, 0.15F*height, 0.15F*height);
                right.add(adenine);
                coordsR.add(right.get(right.size()-1).getX() + 0.5F*(right.get(right.size()-1).getWidth() - text.getWidth(letterR.get(right.size()-1))));
                break;
            }case 2:{
                letterR.add(game.locale[13]);
                guanine.setBounds(width - 0.3F*height, 1.0F*height + 0.175F*i*height, 0.15F*height, 0.15F*height);
                right.add(guanine);
                coordsR.add(right.get(right.size()-1).getX() + 0.5F*(right.get(right.size()-1).getWidth() - text.getWidth(letterR.get(right.size()-1))));
                break;
            }case 3:{
                letterR.add(game.locale[12]);
                thymine.setBounds(width - 0.3F*height, 1.0F*height + 0.175F*i*height, 0.15F*height, 0.15F*height);
                right.add(thymine);
                coordsR.add(right.get(right.size()-1).getX() + 0.5F*(right.get(right.size()-1).getWidth() - text.getWidth(letterR.get(right.size()-1))));
                break;
            }case 4:{
                letterR.add(game.locale[14]);
                cytosine.setBounds(width - 0.3F*height, 1.0F*height + 0.175F*i*height, 0.15F*height, 0.15F*height);
                right.add(cytosine);
                coordsR.add(right.get(right.size()-1).getX() + 0.5F*(right.get(right.size()-1).getWidth() - text.getWidth(letterR.get(right.size()-1))));
                break;
            }default:{
                break;
            }
        }
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
