package com.example.khanh.mygame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Khanh on 4/10/2017.
 */

public class BotBorder extends GameObject
{
    private Bitmap image;
    public BotBorder(Bitmap res, int x, int y)
    {
        height = 200;
        weight = 20;

        this.x = x;
        this.y = y;
        dx = GamePanel.MOVESPEED;

        image = Bitmap.createBitmap(res, 0, 0, weight, height);

    }
    public void update()
    {
        x +=dx;

    }
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);

    }
}
