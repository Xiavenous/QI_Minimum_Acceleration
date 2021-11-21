package com.example.qiminimumacceleration;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static Double minAcc = (2*299792458*299792458)/(8.8*Math.pow(10,26));
    private static Double maxRadius = new Double(1000000000);
    private SeekBar velocityBar, radiusBar;
    private ImageView imageView;
    private Double proportnlSpeed, radius, minV;
    private Bitmap bitmap;
    private Canvas canvas;
    private TextView textView;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Double widthVal;
    private int widthInt,iterations;
    private Double yVal, xVal, yPos, xPos, grad, offset;
    private Double aA,bB,cC, x1, x2;
    private Float angl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        velocityBar = findViewById(R.id.velocityBar);
        radiusBar = findViewById(R.id.radiusBar);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        textView.setText("Velocity: 100.0%\nRadius: 0.0\n(Min Velocity: 0.0 m/s)");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        paint.setStrokeWidth(4);


        //imageView.setAdjustViewBounds(true);
        //imageView.setMaxHeight(imageView.getWidth());
        widthInt = size.x;
        widthVal = new Double(size.x);//imageView.getWidth();
        proportnlSpeed = new Double(1);
        radius = new Double(0);

        minV = Math.sqrt(minAcc*(radiusBar.getProgress()));
        bitmap = Bitmap.createBitmap(widthInt,widthInt, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(widthInt/2,widthInt/2,(widthInt/2)-20,paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(widthInt/2,widthInt/2,(widthInt/2)-22,paint);
        paint.setColor(Color.BLACK);
        //canvas.drawLine(widthInt/2,20,20,widthInt/2,paint);
        canvas.drawLine(widthInt/2,20,20, 20,paint);

        canvas.drawCircle(widthInt/2,widthInt/2,5,paint);
        imageView.setImageBitmap(bitmap);

        velocityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                proportnlSpeed = new Double(100-progress);
                proportnlSpeed = proportnlSpeed/100;

                //canvas.restore();

                xVal = new Double(proportnlSpeed);
                xPos = new Double(20+(1.0-xVal)*((widthVal/2)-20));
                yVal = new Double(Math.sqrt(1-proportnlSpeed));
                yPos = new Double(20+(yVal*((widthVal/2)-20))); //divided by two?
                grad = new Double(yVal/xVal);
                offset = new Double(20-((grad*widthVal)/2));
                angl = new Float(2*Math.toDegrees(Math.atan(grad)));
                iterations = Math.round(360/angl);
                System.out.println("Iterations:"+iterations);

                // (0-(40*grad*grad*widthVal-widthVal)
                //        +Math.sqrt(Math.pow(40*grad-grad*grad*widthVal-widthVal,2)-4*(1+grad*grad)*(20*widthVal*(grad*grad*widthVal/80 - grad+1))))/
                //        2*(1+grad*grad))

                // (0-(40*grad*grad*widthVal-widthVal)
                //        -Math.sqrt(Math.pow(40*grad-grad*grad*widthVal-widthVal,2)-4*(1+grad*grad)*(20*widthVal*(grad*grad*widthVal/80 - grad+1))))/
                //        2*(1+grad*grad))

                bB = new Double(40*grad-grad*grad*widthVal-grad*widthVal-widthVal);
                aA = new Double(grad*grad+1.0);
                cC = new Double((grad*grad*widthVal*widthVal - 80*grad*widthVal + 2*grad*widthVal*widthVal + widthVal*widthVal)/4.0); // grad*grad*widthVal/80.0-grad+1);
                x1 = new Double((0-bB+Math.sqrt(bB*bB-4*aA*cC))/(2*aA)); // good
                x2 = new Double((0-bB-Math.sqrt(bB*bB-4*aA*cC))/(2*aA)); // bad
                System.out.println("x1="+x1+"\nx2="+x2+"\n"+widthVal);

                //figure out which one equals widthVal/2 and use the other val
                //then plot for y=x*grad+c

                canvas.rotate(360-(iterations)*angl,Math.round(widthVal/2),Math.round(widthVal/2));
                canvas.drawColor(Color.WHITE);
                paint.setColor(Color.BLACK);
                canvas.drawCircle(widthInt/2,widthInt/2,(widthInt/2)-19,paint);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(widthInt/2,widthInt/2,(widthInt/2)-21,paint);
                paint.setColor(Color.GRAY);
                canvas.drawLine(widthInt/2,20,Math.round(xPos), Math.round(yPos),paint);
                //paint.setColor(Color.WHITE);
                //for(int i = 0; i*30 > widthVal-x1;i++)
                paint.setColor(Color.BLACK);
                canvas.drawCircle(widthInt/2,widthInt/2,5,paint);
                for (int i = 0; i<(iterations)&&i<50; i++){
                    canvas.rotate(angl,Math.round(widthVal/2),Math.round(widthVal/2));
                    // canvas.drawLine(widthInt/2,20,Math.round(xPos), Math.round(yPos),paint);  //remove? full scaffolding
                    canvas.drawLine(widthInt/2,20,Math.round(widthVal-x1),Math.round(x1*grad - (grad*widthVal/2) + 20),paint);
                }
                paint.setColor(Color.RED);
                canvas.drawLine(widthInt/2,20,widthInt/2,Math.round(yPos),paint);//remove?

                paint.setColor(Color.BLUE);
                canvas.drawLine(widthInt/2,20,Math.round(widthVal-x1),Math.round(x1*grad - (grad*widthVal/2) + 20),paint);


                imageView.setImageBitmap(bitmap);
                textView.setText("Velocity: "+(proportnlSpeed*100)+"% of min V\nRadius: "+radius+"\n(Min Velocity: "+minV+" m/s)");
                System.out.println("Input speed is "+proportnlSpeed+"% of the minimum velocity.\nSpeed = "+(minV * proportnlSpeed));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minV = Math.sqrt(minAcc*progress);
                radius = maxRadius*progress;
                radius = radius/100;
                System.out.println("Max radius:"+maxRadius+"\nRadius:"+radius+"\nProgress"+progress);
                textView.setText("Velocity: "+(proportnlSpeed*100)+"% of min V\nRadius: "+radius+"\n(Min Velocity: "+minV+" m/s)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



}
