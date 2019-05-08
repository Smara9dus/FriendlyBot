import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.util.List;

public class TwitterPoster {

    static String consumerKey = "XXXXXX";
    static String consumerKeySecret = "XXXXX";
    static String accessToken = "XXXXXXX";
    static String accessTokenSecret = "XXXXXXXX";


    // Get likes from previous tweet
    public void getLikes()  {
        File file = new File("likes.txt");
    }

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







