package ru.erked.dnacorrector;

import com.badlogic.gdx.Game;

public class StartDNAC extends Game {

    public static String[] locale;
    public final AdMob adMob;
    public final GPGS gpgs;

    public StartDNAC(String[] locale, AdMob adMob, GPGS gpgs){
        this.locale = locale;
        this.adMob = adMob;
        this.gpgs = gpgs;
    }

    @Override
    public void create (){
        setScreen(new GameScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose (){
    }
}
