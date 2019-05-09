// Help from http://twitter4j.org/en/code-examples.html

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class TwitterPoster {

    static String consumerKey = "XXXXXXX";
    static String consumerKeySecret = "XXXXXXXXXX";
    static String accessToken = "XXXXXXXXXXX";
    static String accessTokenSecret = "XXXXXXXXXXX";


    // Get likes from previous tweet
    public void getLikes()  {
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(consumerKey, consumerKeySecret);
            AccessToken t = new AccessToken(accessToken, accessTokenSecret);
            twitter.setOAuthAccessToken(t);

            List<Status> statuses = twitter.getUserTimeline("@FriendlyBot8");
            System.out.println("Getting past likes...");

            boolean flag = false;
            String text = "";
            int likes = 0;

            for (Status status : statuses) {
                if (flag == false) {
                    text = status.getText();
                    text = text.substring(0, Math.min(text.length(), 11));
                    likes = status.getFavoriteCount();
                    flag = true;
                } else {
                    status.getText();
                    status.getFavoriteCount();
                }
            }
            System.out.println("text: " + text + " - likes: " + likes);
//            updateLikes(text, likes);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
    }

//    public void updateLikes(String text, int likes) {
//        File file = new File("likes.txt");
//        Scanner s = new Scanner(file);
//        Integer.parseInt(s.next());
//
//    }
    // Post image
    public void postImage(String text)  {
        // Post image with filepath "recent.png" along with message 'text'
        try {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerKeySecret);
        AccessToken t = new AccessToken(accessToken, accessTokenSecret);
        twitter.setOAuthAccessToken(t);

        String statusMessage = text;
        File file = new File("recent.png");

        StatusUpdate status = new StatusUpdate(statusMessage);
        status.setMedia(file); // set the image to be uploaded here.
        twitter.updateStatus(status);

        System.out.println("Successfully updated the status in Twitter.");
        } catch (TwitterException te) {
            te.printStackTrace();
        }
    }


}







