package com.example.khanh.mygame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Khanh on 4/10/2017.
 */

public class TopBorder extends  GameObject
{
    private Bitmap image;

    public TopBorder(Bitmap res, int x, int y, int h)
    {
        height = h;
        weight = 20;

        this.x = x;
        this.y = y;

        dx = GamePanel.MOVESPEED;
        image = Bitmap.createBitmap(res, 0, 0, weight, height);
    }
    public void update()
    {
        x+=dx;
    }
    public void draw(Canvas canvas)
    {
        try{canvas.drawBitmap(image,x,y,null);}catch(Exception e){};
    }
    }
