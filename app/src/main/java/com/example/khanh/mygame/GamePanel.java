package com.example.khanh.mygame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.style.SuperscriptSpan;
import android.text.style.TtsSpan;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Khanh on 4/8/2017.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback

{
    public  static   int WIDTH = 856;
    public  static   int HEIGHT = 480;
    public  static  final int MOVESPEED=-5;
    private  long missilesStartTime;
    private  long smokeStartTime;

    private  MainThread thread;
    private  Background bg;
    private Random rand =new Random();

    private Player player;
    private ArrayList<Smokepuff> smoke;
    private  ArrayList<Missile> missiles;
    private  ArrayList<TopBorder> topBorders;
    private  ArrayList<BotBorder> botBorders;
    private  int maxBoderHeight;
    private  int minBoderHeight;
    private  int progressDenom=20;
    private boolean topDown=true;
    private boolean botDown=true;
    private boolean newGameCreated;
    private Explosion explosion;
    private  long  startResetl;
    private boolean reset;
    private  boolean dissapear;
    private  boolean started;
    private  int best;





    public  GamePanel(Context context)
    {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        bg=new Background(BitmapFactory.decodeResource(getResources(),R.drawable.grassbg1));

        player= new Player(BitmapFactory.decodeResource(getResources(),R.drawable.helicopter),65,25,3);

        smoke =new ArrayList<Smokepuff>();
        missiles =new ArrayList<Missile>();
        topBorders = new ArrayList<TopBorder>();
        botBorders=new ArrayList<BotBorder>();


        missilesStartTime = System.nanoTime();


        smokeStartTime=(System.nanoTime());

        thread=new MainThread(getHolder(),this);
        thread.setRunning(true);
        thread.start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry=true;
        int counter=0;
        while (retry&&counter <1000)
        {
            try {
                thread.setRunning(false);
                thread.join();
                retry=false;
                thread=null;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();

            }
        }
    }
    public  boolean onTouchEvent(MotionEvent event)

    {

        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            if(!player.getPlaying() && newGameCreated && reset)
            {
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.getPlaying())
            {
                if(!started)
                {
                    started=true;
                }
                reset=false;
                player.setUp(true);

            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }
        return  super.onTouchEvent(event);
    }

    public  void update()
    {
        if(player.getPlaying())
        {
            if(botBorders.isEmpty())
            {
                player.setPlaying(false);
                return;
            }
            if(topBorders.isEmpty())
            {
                player.setPlaying(false);
                return;
            }

            bg.update();
            player.update();
            maxBoderHeight=30 + player.getScore()/progressDenom;

            if(maxBoderHeight>HEIGHT/4)
            {
                maxBoderHeight=HEIGHT/4;
            }
            minBoderHeight=5+player.getScore()/progressDenom;

            //check bottom border collision
            for(int i = 0; i<botBorders.size(); i++)
            {
                if(collision(botBorders.get(i), player))
                    player.setPlaying(false);
            }

            //check top border collision
            for(int i = 0; i <topBorders.size(); i++)
            {
                if(collision(topBorders.get(i),player))
                    player.setPlaying(false);
            }

            this.updateTopBorder();
            this.updateBottomBorder();



            long missileElapsed=(System.nanoTime()- missilesStartTime)/1000000;

            if(missileElapsed>(2000-player.getScore()/4))
            {
                if(missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile), WIDTH+10, HEIGHT/2,45,15,player.getScore(),13));
                }
                else
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10,(int)(rand.nextDouble()*(HEIGHT-(maxBoderHeight*2))+maxBoderHeight),45,15,player.getScore(),13));
                }
                missilesStartTime=System.nanoTime();
            }
            for(int i=0;i<missiles.size();i++)
            {
                missiles.get(i).update();
                if(collision(missiles.get(i),player))
                {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;

                }
                if(missiles.get(i).getX()<-100)
                {
                    missiles.remove(i);
                    break;
                }
            }



            long elapsed =(System.nanoTime()-smokeStartTime)/1000000;
            if(elapsed>120)
            {
                smoke.add(new Smokepuff(player.getX(),player.getY()+10));
                smokeStartTime=System.nanoTime();
            }
            for(int i=0;i<smoke.size();i++)
            {
                smoke.get(i).update();
                if(smoke.get(i).getX()<-10)
                {
                    smoke.remove(i);
                }
            }
        }
        else
        {
            player.resetDY();
            if(!reset)
            {
                newGameCreated=false;
                startResetl=System.nanoTime();
                reset=true;
                dissapear=true;
                explosion=new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getX(),
                        player.getY()-30,100,100,25);
            }
            explosion.update();
            long resetElapsed=(System.nanoTime()-startResetl)/1000000;

            if( resetElapsed>2500&&!newGameCreated)
            {
                newGame();
            }

            if(!newGameCreated)
            {
                newGame();
            }
        }
    }
    public boolean collision(GameObject a,GameObject b)
    {
        if(Rect.intersects(a.getRectangle(),b.getRectangle()))
        {
            return  true;
        }
        return false;
    }
    public  void  draw(Canvas canvas)
    {
        final float scaleFactorX = getWidth() / (float)WIDTH;
        final float scaleFactorY = getHeight() / (float)HEIGHT;
        if(canvas!=null)
        {
            final int savedState=canvas.save();
            canvas.scale(scaleFactorX,scaleFactorY);
            bg.draw(canvas);

            if(!dissapear)
            {
                player.draw(canvas);
            }

            player.draw(canvas);


            for(Smokepuff sp : smoke)
            {
                sp.draw(canvas);
            }

            for(Missile m : missiles)
            {
                m.draw(canvas);
            }

            for(TopBorder tb:topBorders)
            {
                tb.draw(canvas);
            }
            for(BotBorder bb: botBorders)
            {
                bb.draw(canvas);
            }
            if(started)
            {
                explosion.draw(canvas);
            }
            drawText(canvas);

            canvas.restoreToCount(savedState);
        }

    }
    public  void updateTopBorder()
    {
        if(player.getScore()%50==0)
        {
            topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                    topBorders.get(topBorders.size()-1).getX()+20,0,(int)((rand.nextDouble()*(maxBoderHeight))+1)));

        }
        for(int i=0;i<topBorders.size();i++)
        {
            topBorders.get(i).update();
            if(topBorders.get(i).getX()<-20)
            {
                topBorders.remove(i);
                if(topBorders.get(topBorders.size()-1).getHeight()>=maxBoderHeight)
                {
                    topDown=false;
                }
                if(topBorders.get(topBorders.size()-1).getHeight()<=minBoderHeight)
                {
                    topDown =true;
                }


                if(topDown)
                 {
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topBorders.get(topBorders.size()-1).getX()+20,
                            0,topBorders.get(topBorders.size()-1).getHeight()+1));
                }
                else
                {
                    topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topBorders.get(topBorders.size()-1).getX()+20,
                            0,topBorders.get(topBorders.size()-1).getHeight()-1));
                }
            }
        }
    }
    public  void updateBottomBorder()
    {
        if (player.getScore() % 40 == 0)
        {
            botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    botBorders.get(botBorders.size() - 1).getX() + 20, (int) ((rand.nextDouble()
                    * maxBoderHeight) + (HEIGHT - maxBoderHeight))));
        }
        for (int i = 0; i < botBorders.size(); i++)
        {
            botBorders.get(i).update();
            if (botBorders.get(i).getX() < -20)
            {
                botBorders.remove(i);
                //remove element of arraylist, replace it by adding a new one

                //calculate topdown which determines the direction the border is moving (up or down)
                if (botBorders.get(botBorders.size() - 1).getHeight() >= maxBoderHeight) {
                    botDown = false;
                }

                if (botBorders.get(botBorders.size() - 1).getHeight() <= minBoderHeight) {
                    botDown = true;
                }
                //new border added will have larger height
                if (botDown) {
                    botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick), botBorders.get(botBorders.size() - 1).getX() + 20,
                            botBorders.get(botBorders.size() - 1).getY() + 1));
                }
                //new border added wil have smaller height
                else {
                    botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick), botBorders.get(botBorders.size() - 1).getX() + 20
                            , botBorders.get(botBorders.size() - 1).getY() - 1));
                }
            }

        }
    }
    public void newGame()
    {
        dissapear=false;


        botBorders.clear();
        topBorders.clear();
        missiles.clear();
        smoke.clear();

        minBoderHeight = 5;
        maxBoderHeight = 30;

        player.resetDY();

        player.setY(HEIGHT/2);


        if(player.getScore()>best)
        {
            best = player.getScore();
        }
        player.resetScore();

        //create initial borders

        //initial top border
        for(int i = 0; i*20<WIDTH+40;i++)
        {
            //first top border create
            if(i==0)
            {
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, 10));
            }
            else
            {
                topBorders.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, topBorders.get(i-1).getHeight()+1));
            }
        }
        //initial bottom border
        for(int i = 0; i*20<WIDTH+40; i++)
        {
            //first border ever created
            if(i==0)
            {
                botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20,HEIGHT - minBoderHeight));
            }
            //adding borders until the initial screen is filed
            else
            {
                botBorders.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i * 20, botBorders.get(i - 1).getY() - 1));
            }
        }

        newGameCreated = true;


    }
    public  void  drawText(Canvas canvas)
    {
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        canvas.drawText("DISTANCE:  "+ (player.getScore()*3),10,HEIGHT-10,paint);
        canvas.drawText("BEST:"+best,WIDTH-215,HEIGHT-10,paint);
        if(!player.getPlaying() && newGameCreated)
        {
            Paint paint1=new Paint();
            paint1.setTextSize(40);
            paint1.setTypeface( Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            canvas.drawText("PRESS TO START",WIDTH/2-50,HEIGHT/2,paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP",WIDTH/2-50,HEIGHT/2+20,paint1);
            canvas.drawText("RELEASE TO GO DOWN",WIDTH/2-50,HEIGHT/2+40,paint1);
        }
    }
}
