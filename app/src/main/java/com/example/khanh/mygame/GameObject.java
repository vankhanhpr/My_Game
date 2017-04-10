package com.example.khanh.mygame;

import android.graphics.Rect;

/**
 * Created by Khanh on 4/9/2017.
 */


public class GameObject
{
    protected  int x;
    protected  int y;
    protected  int dx;
    protected  int dy;
    protected  int weight;
    protected  int height;
    public  void setX(int x)
    {
        this.x=x;
    }

    public  void setY(int y)
    {
        this.y=y;
    }
    public  int getX()
    {
        return x;
    }
    public  int getY()
    {
        return  y;
    }
    public  int getHeight()
    {
        return  height;
    }
    public  int getWeight()
    {
        return  weight;
    }
    public Rect getRectangle()
    {
        return  new  Rect(x,y,x+weight,y+height);
    }

}
