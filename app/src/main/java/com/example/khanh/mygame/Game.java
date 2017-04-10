package com.example.khanh.mygame;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Khanh on 4/8/2017.
 */

public class Game extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(new GamePanel(this));
    }
    public  boolean OncreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_game,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id==R.id.action_settings) {
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }
}
