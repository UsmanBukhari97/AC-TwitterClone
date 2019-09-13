package com.example.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

//implementing this coz if the user is check and we want to uncheck so creating onitemclicklistener
public class TwitterUsers extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private ListView listView;
    private ArrayList<String> tUsers;
    private ArrayAdapter adapter;

    //initially will have the value null so it wont display null in names.
    private String followedUser = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        FancyToast.makeText(this,"Welcome " +
                ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG, FancyToast.INFO, true).show();

        listView = findViewById(R.id.listView);
        tUsers = new ArrayList<>();
        //we need to pass 3 arguments for array adapter.
        //context, layout of the row. using predefined row. third arg is data. the data we are going to get from array list.
        //to see if we are following or not following the user we will use simple_list_item_checked not simple_list_item_
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, tUsers);
        //after checked:
        //multiple coz checked or unchecked
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        //list view item click listener
        listView.setOnItemClickListener(this);


        //parse query codes inside try and catch
        try {

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            //where username should not be equal to current user
            query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    //if the list we get from server is greater than zero
                    // and no error then we can go ahead and objects we will get are parse users (twitterUSer)
                    if (objects.size() > 0 && e == null) {

                        for (ParseUser twitterUser : objects) {

                            tUsers.add(twitterUser.getUsername());
                        }
                        //setting adapter after for
                        listView.setAdapter(adapter);

                        //iterating over users.
                        for (String twitterUser : tUsers) {


                            if (ParseUser.getCurrentUser().getList("fanOf") != null) {
                                //current followed users + twitter  users or current value + new value and assigning to followed user.
                                //like if not followed following it and assiging it to followed user
                                followedUser = followedUser + twitterUser + "\n";
                                //if we dont have anyone following and we call contain method our app will crash
                                // then we write the above statement^^
                                //if current user fanOf values contain twitter users then its following other users
                                if (ParseUser.getCurrentUser().getList("fanOf").contains(twitterUser)) {

                                    //user is in fanOf column of current user so by running the app again the followed user will be there
                                    listView.setItemChecked(tUsers.indexOf(twitterUser), true);

                                    //Toats that current user is following the user
                                    FancyToast.makeText(TwitterUsers.this,
                                            ParseUser.getCurrentUser().getUsername() + " is following " + "\n" + followedUser,
                                            Toast.LENGTH_LONG, FancyToast.INFO, false).show();


                                }
                            }
                        }

                    }
                }
            });
        } catch (Exception e) {

            e.getMessage();
        }


    }

    //menu on top right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.logout_item:
                //logging out user in background then going back to main activity
                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent intent = new Intent(TwitterUsers.this, MainActivity.class);
                        startActivity(intent);
                        //to finish current activity
                        finish();
                    }
                });

                break;

            case R.id.sendTweetItem:

                Intent intent = new Intent(TwitterUsers.this, SendTweetActivity.class);
               startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent
            , View view, int position, long id) {


        CheckedTextView checkedTextView =  (CheckedTextView) view;

        if (checkedTextView.isChecked()) {

            //following a user.
            FancyToast.makeText(TwitterUsers.this, tUsers.get(position) + " is now followed!",
                    Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
            //current user doing the following operation adding the column of fanOf and the value of the user its following.
            //position is the user that is tapped to be followed.
            ParseUser.getCurrentUser().add("fanOf", tUsers.get(position));

        }
        //if checked view is not checked
        //unfollowing
        else {

            FancyToast.makeText(TwitterUsers.this, tUsers.get(position) + " is now unfollowed!",
                    Toast.LENGTH_SHORT, FancyToast.INFO, true).show();

            //unfollowing someone
            ParseUser.getCurrentUser().getList("fanOf").remove(tUsers.get(position));
            //in order to make this work:
            //in order to unfollow we need to get the user the currewnt user is following.
            //List we get from the server of parse users. key is fanOf. fanof is array.
            List currentUserFanOfList = ParseUser.getCurrentUser().getList("fanOf");
            //completely remove fanOf column from parse server.
            ParseUser.getCurrentUser().remove("fanOf");
            //now adding gain or updating the current following list.
            ParseUser.getCurrentUser().put("fanOf", currentUserFanOfList);

        }

        //definfing value after follwing someone as a 'fanOf' annd saving in background.
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    //if no error the changes are saved.
                    FancyToast.makeText(TwitterUsers.this, "Saved", Toast.LENGTH_SHORT,
                            FancyToast.SUCCESS, true).show();
                }
            }
        });
    }

}

