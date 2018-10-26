package ru.erked.dnacorrector;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Buttons {

    private float xI;
    private float xA;
    private float yI;
    private float yA;
    private float widthI;
    private float widthA;
    private float heightI;
    private float heightA;
    private float aspectRatio;
    private Sprite spriteA;
    private Sprite spriteI;
    private float s;
    private boolean isActive;

    public Buttons(String textureInactive, String textureActive, float width, float x, float y, float aspectRatio, float s, int index){
        if(index==-1){
            spriteI = GameScreen.atlas.createSprite(textureInactive);
            spriteA = GameScreen.atlas.createSprite(textureActive);
        }else{
            spriteI = GameScreen.atlas.createSprite(textureInactive, index);
            spriteA = GameScreen.atlas.createSprite(textureActive, index);
        }
        this.s = s;
        this.aspectRatio = aspectRatio;
        widthI = width;
        widthA = width*s;
        heightI = (float)width/(float)aspectRatio;
        heightA = (float)widthA/(float)aspectRatio;
        xI = x;
        yI = y;
        xA = (float)x-((float)(widthA-widthI)/2.0F);
        yA = (float)y-((float)(heightA-heightI)/2.0F);
        spriteI.setBounds(xI, yI, widthI, heightI);
        spriteA.setBounds(xA, yA, widthA, heightA);
        isActive = false;
    }

    public float getAspectRatio(){
        return aspectRatio;
    }

    public float getX(){
        return xI;
    }

    public void setX(float x){
        xI = x;
        spriteI.setX(xI);
        xA = (float)x-((float)(widthA-widthI)/2.0F);
        spriteA.setX(xA);
    }

    public float getY(){
        return yI;
    }

    public void setY(float y){
        yI = y;
        spriteI.setY(yI);
        yA = (float)y-((float)(heightA-heightI)/2.0F);
        spriteA.setY(yA);
    }

    public float getWidth(){
        return widthI;
    }

    public void setWidth(float width){
        widthI = width;
        spriteI.setBounds(xI, yI, widthI, heightI);
        widthA = width*s;
        spriteA.setBounds(xA, yA, widthA, heightA);
    }

    public float getHeight(){
        return heightI;
    }

    public void setHeight(float height){
        heightI = height;
        spriteI.setBounds(xI, yI, widthI, heightI);
        heightA = height*s;
        spriteA.setBounds(xA, yA, widthA, heightA);
    }

    public void setActiveMode(boolean bool){
        isActive = bool;
    }

    public boolean isActiveMode(){
        return isActive;
    }

    public Sprite getSprite(){
        if(!isActive) return spriteI;
        else return spriteA;
    }

    public void setSprite(Sprite sprite){
        if(!isActive){
            spriteI = sprite;
            spriteI.setBounds(xI, yI, widthI, heightI);
        }else{
            spriteA = sprite;
            spriteA.setBounds(xA, yA, widthA, heightA);
        }
    }

}
