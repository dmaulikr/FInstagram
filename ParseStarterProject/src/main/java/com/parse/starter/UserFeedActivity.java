package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import static com.parse.starter.R.id.linearLayout;

public class UserFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        //intent used to get to the user feed from the UserListActivity class
        Intent intent= getIntent();

        String activeUsername = intent.getStringExtra("username");

        setTitle(activeUsername + "'s Feed");

        //this query gets all the image posted by the user,
        //the class we want to query is the image class
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");

        //find all the images by the username where the user tapped on
        query.whereEqualTo("username", activeUsername);
        //created at is a date used by parse dashboard
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        for(ParseObject object: objects)
                        {
                            //gets the image created and downloads it
                            ParseFile file = (ParseFile) object.get("image");

                            //actually downloads the file
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null && data != null)
                                    {
                                        //converts the bytes of data into a bitmap to create the image
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        //creating an image view within the application context
                                        ImageView imageView = new ImageView(getApplicationContext());

                                        //makes image full width of the screen and a proportional height
                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));

                                        //set the image view to the bitmap
                                        imageView.setImageBitmap(bitmap);

                                        //add it to the linear layout
                                        linearLayout.addView(imageView);

                                    }
                                }
                            });
                        }
                    } else {

                        //incase the user uploaded zero photos
                        Toast.makeText(getApplicationContext(), "User has no photos :(", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });





    }
}
