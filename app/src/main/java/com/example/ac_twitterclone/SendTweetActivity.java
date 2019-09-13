package com.example.ac_twitterclone;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendTweetActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTweet;

    private ListView viewTweetsListView;
    private Button btnViewTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_tweet);

        edtTweet = findViewById(R.id.edtSendTweet);

        viewTweetsListView = findViewById(R.id.viewTweetsListView);
        btnViewTweets = findViewById(R.id.btnViewTweets);
        btnViewTweets.setOnClickListener(this);

//        //<datatype, value of data type> name = initializing<>()
//        HashMap<String, Integer> numbers = new HashMap<>();
//        //putting values
//        //put need two args. String which is key, data type of value
//        numbers.put("Number1", 1);
//        numbers.put("Number2", 2);
//
//        //how to access value of hashmap:
//       // numbers.get("Number1");
//
//        FancyToast.makeText(this, numbers.get("Number1") + "", Toast.LENGTH_LONG,
//                FancyToast.WARNING, true).show();

    }
    public void sendTweet(View view) {

        ParseObject parseObject = new ParseObject("MyTweet");
        //sending a tweet
        parseObject.put("tweet", edtTweet.getText().toString());
        //also sending the username who is sending the tweet
        parseObject.put("user", ParseUser.getCurrentUser().getUsername());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    FancyToast.makeText(SendTweetActivity.this, ParseUser.getCurrentUser().getUsername() + " 's tweet" + "(" + edtTweet.getText().toString() + ")" + " is saved!!!",
                            Toast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

                } else {
                    FancyToast.makeText(SendTweetActivity.this, e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                }

                progressDialog.dismiss();
            }
        });


    }

    @Override
    public void onClick(View view) {

        final ArrayList<HashMap<String, String>> tweetList = new ArrayList<>();

        //using Simple Adapter because we are using two items. title and the tweet. title is users name
        //simple list item 2 for 2 values*
        //new String[]{first value is key of first value i.e the username, second value is the users tweet so tweets value
        //int array as the first value is text1 which is for username and text2 for tweets value
        final SimpleAdapter adapter = new SimpleAdapter(SendTweetActivity.this, tweetList,
                android.R.layout.simple_list_item_2, new String[]{"tweetUserName", "tweetValue"},
                new int[]{android.R.id.text1, android.R.id.text2});

        //sometimes when we want to get tweets maybe they dont have tweets and dont exist so we create a try block for app to not to crash
        try {
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("MyTweet");
            //will get tweets current user is following
            parseQuery.whereContainedIn("user", ParseUser.getCurrentUser().getList("fanOf"));

            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseObject tweetObject : objects) {
                            //everytime time this for loop executes i want to have a new object of type hashmap
                            //that accepts the value String as the key and the value string as the value itself
                            HashMap<String, String> userTweet = new HashMap<>();

                            //key, value
                            //username
                            userTweet.put("tweetUserName", tweetObject.getString("user"));
                            //tweet itself
                            userTweet.put("tweetValue", tweetObject.getString("tweet"));
                            //adding hasmap objects with these keys and values to outr tweet list which is of type Arraylist
                            // which accepts hashmap objects so thats why:
                            tweetList.add(userTweet);

                        }

                        
                        viewTweetsListView.setAdapter(adapter);

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
