package droid.pkg;

import java.util.Timer;
import java.util.TimerTask;
import droid.pkg.R;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorEventListener;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;


public class TiltBallActivity extends Activity {

	BallView mBallView = null;
	Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
	Timer mTmr = null;

    float ajuda, variavelSoma;
	TimerTask mTsk = null;
	int mScrLargura, mScrAltura;
    android.graphics.PointF mBallPos, mBallSpd;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar / Oculta barra de titulo
        getWindow().setFlags(0xFFFFFFFF,
        		LayoutParams.FLAG_FULLSCREEN|LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //create pointer to main screen

        final FrameLayout mainView = (android.widget.FrameLayout) findViewById(R.id.main_view);



        //get screen dimensions
        //pegando dimencoes da tela do celular
        Display display = getWindowManager().getDefaultDisplay();  
        mScrLargura = display.getWidth();
        mScrAltura = display.getHeight();
    	mBallPos = new android.graphics.PointF();
    	mBallSpd = new android.graphics.PointF();
        
        //create variables for ball position and speed
        //variaveis para posicao e velocidade da bola
        mBallPos.x = 10;//coloquei 10 que indica o lugar de começo da bola ..mScrLargura/2;
        mBallPos.y = 10;//mScrAltura/2;
        mBallSpd.x = 0;
        mBallSpd.y = 0; 
        
        //create initial ball
        mBallView = new BallView(this,mBallPos.x,mBallPos.y,10);
                
        mainView.addView(mBallView); //add ball to main screen
        mBallView.invalidate(); //call onDraw in BallView

        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager)getSystemService(Context.SENSOR_SERVICE)).registerListener(
    		new SensorEventListener() {    
    			@Override  
    			public void onSensorChanged(SensorEvent event) {  
    			    //set ball speed based on phone tilt (ignore Z axis)
                    //deixa velocidade da bola baseada no movimento do celular
    				mBallSpd.x = -event.values[0];
    				mBallSpd.y = event.values[1];
    				//timer event will redraw ball
                    //timer ira recolocar a bola
    			}
        		@Override  
        		public void onAccuracyChanged(Sensor sensor, int accuracy) {} //ignore this event
        	},
        	((SensorManager)getSystemService(Context.SENSOR_SERVICE))
        	.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);
        		
        //listener for touch event
        //quando a um toque na tela
        mainView.setOnTouchListener(new android.view.View.OnTouchListener() {
	        public boolean onTouch(android.view.View v, android.view.MotionEvent e) {
	        	//set ball position based on screen touch
                //coloca a bola na lugar do toque
	        	mBallPos.x = e.getX();
	        	mBallPos.y = e.getY();
    			//timer event will redraw ball
	        	return true;
	        }}); 
    } //OnCreate

    //listener for menu button on phone
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Exit"); //only one menu item
        return super.onCreateOptionsMenu(menu);
    }

    //listener for menu item clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle item selection    
    	if (item.getTitle() == "Exit") //user clicked Exit
    		finish(); //will call onPause
   		return super.onOptionsItemSelected(item);    
    }
    
    //For state flow see http://developer.android.com/reference/android/app/Activity.html
    @Override
    public void onPause() //app moved to background, stop background threads
    {
    	mTmr.cancel(); //kill\release timer (our only background thread)
    	mTmr = null;
    	mTsk = null;
    	super.onPause();
    }

    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {

        //create timer to move ball to new position
        //Timer para mover a bola na nova posicao

        mTmr = new Timer();
        mTsk = new TimerTask() {

            public void run() {

                ajuda = ajuda + 1;
                //if debugging with external device,
                //  a cat log viewer will be needed on the device
                android.util.Log.d(
                        "TiltBall", "Timer Hit - " + mBallPos.x + ":" + mBallPos.y+ "  variavel la  " + mTmr);
                //move ball based on current speed
                //movimento da bola baseado no movimento atual
                mBallPos.x += mBallSpd.x;
                mBallPos.y += mBallSpd.y;

                //if (mBallPos.x < mScrLargura && mBallPos.x > 0) mBallPos.x += mBallSpd.x;
                //if (mBallPos.y < mScrAltura && mBallPos.y > 0) mBallPos.y += mBallSpd.y;
                //if ball goes off screen, reposition to opposite side of screen
                if (mBallPos.x > mScrLargura) mBallPos.x = 0;
                if (mBallPos.y > mScrAltura) mBallPos.y = 0;
                //PAREDE INVISIVEL COMECA
                if (mBallPos.x < 10) mBallPos.x = 10;
                if (mBallPos.y < 10) mBallPos.y = 10;
                if (mBallPos.x > 310) mBallPos.x = 310;
                if (mBallPos.y > 555) mBallPos.y = 555;
                //PAREDE INVISIVEL TERMINA



                /*tentativas de paredes
            if (variavelSoma == 1) {
                //nada
            }  if (mBallPos.x > 10 && mBallPos.x > 50 || mBallPos.y > 10 && mBallPos.y > 330){


                    mBallPos.x = -30;
                    mBallPos.y = -30;}
            if (variavelSoma == 2) {
                    //nada
                }
                if   (mBallPos.x > 150 || mBallPos.y > 38 && mBallPos.y > 440) {
                    mBallPos.x = 0;
                    mBallPos.y = 0;
                }
                if (variavelSoma == 1) */
                    //nada
                //} if (mBallPos.x > 100 && mBallPos.x < 250 || mBallPos.y >= 55 && mBallPos.y <= 385)


//                if (mBallPos.x > 200 && mBallPos.x < 250 || mBallPos.y >= 55 && mBallPos.y <= 555)


//                if (mBallPos.x > 200 && mBallPos.x < 250 || mBallPos.y >= 110 && mBallPos.y <= 555)


//                if (mBallPos.x > 100 && mBallPos.x < 150 || mBallPos.y >= 440 && mBallPos.y <= 550)



  //              if (mBallPos.x > 10 && mBallPos.x < 150 || mBallPos.y >= 440 && mBallPos.y <= 550)



    //            if (mBallPos.x > 10 && mBallPos.x < 50 || mBallPos.y >= 440 && mBallPos.y <= 550)

             //       mBallPos.x = 0;
                //    mBallPos.y = 0;



                //if (mBallPos.x > 40) mBallPos.y = 0;
                //if (mBallPos.y > 275) mBallPos.y = 0;
               // if (mBallPos.x > 275) mBallPos.x = 0;
                //if (mBallPos.y > 275) mBallPos.y = 0;

                //if (mBallPos.x < 0) mBallPos.x=mScrLargura; codigo antigo /\
				//if (mBallPos.y < 0) mBallPos.y=mScrAltura;

				//update ball class instance
                mBallView.x = mBallPos.x;
                mBallView.y = mBallPos.y;
				//redraw ball. Must run in background thread to prevent thread lock.
				RedrawHandler.post(new Runnable() {
				    public void run() {	
					   mBallView.invalidate();
				  }});
			}}; // TimerTask

        mTmr.schedule(mTsk,10,10); //start timer
        super.onResume();
    } // onResume


    @Override
    public void onDestroy() //main thread stopped
    {
    	super.onDestroy();
    	System.runFinalizersOnExit(true); //wait for threads to exit before clearing app
    	android.os.Process.killProcess(android.os.Process.myPid());  //remove app from memory 
    }



    //listener for config change. 
    //This is called when user tilts phone enough to trigger landscape view
    //we want our app to stay in portrait view, so bypass event 
    @Override 
    public void onConfigurationChanged(Configuration newConfig)
	{
       super.onConfigurationChanged(newConfig);
	}

}