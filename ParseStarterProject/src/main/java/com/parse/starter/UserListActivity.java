package com.parse.starter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static com.google.android.gms.analytics.internal.zzy.e;
import static com.google.android.gms.analytics.internal.zzy.r;
import static com.google.android.gms.analytics.internal.zzy.s;
import static com.google.android.gms.analytics.internal.zzy.t;
import static com.parse.starter.R.id.userListView;

public class UserListActivity extends AppCompatActivity {

    public void getPhoto()
    {
        //ACTION_PICK allows us to specify to pick from the media store
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //allows us to choose images from the device, allows us to expect a result
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode == 1) //the 1 is coming from onCreate
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //what we'll do when shared button is tapped

        if(item.getItemId() == R.id.share)
        {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    //if we don;t already have permission we'll ask permission
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    getPhoto();
                }
            } else {
                getPhoto();
            }
        } else if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            //jumps user to main actitvity class after user clicks on logout
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check to make sure data != null to make sure that the user did not cancel the request
        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            //a link to our image
            Uri selectedImage = data.getData();

            try {
                //converts image to a bitmap image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("Photo", "received");

                //takes bitmap and uploads it to parse server

                //allows us to convert image into a parse file
                // which we can then upload as part of a parse object to our parse server
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                //take bitmap and compress it into a PNG format
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //convert PNG format into a byte array
                byte[] byteArray = stream.toByteArray();

                //converts byte array into a parse file called "image.png"
                ParseFile file = new ParseFile("image.png", byteArray);

                //creates new parse object with a clas of image
                //this will be where we store all of our image
                ParseObject object = new ParseObject("Image");

                //this is a variable that stores the image
                object.put("image", file);

                //this is a viarable that store the username of the user
                object.put("username", ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null)
                        {
                            Toast.makeText(UserListActivity.this, "Image shared!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(UserListActivity.this, "Image could not be shared - please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("FInsta-FRIENDS");

        final ArrayList<String> usernames = new ArrayList<>();

        final ListView userListView = (ListView) findViewById(R.id.userListView);

        final ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usernames);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //set up an intent to go to the UserFeedActivity class
                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                intent.putExtra("username", usernames.get(i));
                startActivity(intent);
            }
        });

        //returns parseusers,
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        //makes sure list does not contain current user
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null)
                {
                    for(ParseUser user : objects)
                    {
                        //adds the user on to the list
                        usernames.add(user.getUsername());
                    }


                    userListView.setAdapter(arrayAdapter);
                } else {
                    e.printStackTrace();
                }

            }
        });







    }
}
