// Help from http://twitter4j.org/en/code-examples.html

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class TwitterPoster {

//    static String consumerKey = "XXXXXXX";
//    static String consumerKeySecret = "XXXXXXXXXX";
//    static String accessToken = "XXXXXXXXXXX";
//    static String accessTokenSecret = "XXXXXXXXXXX";
    static String consumerKey = "baE5z2254B03W2Q3DCE8tMr8o";
    static String consumerKeySecret = "g4dGIDxhar11zTUL7lhPuiRXJQt4Tuj6L8wy21sNNUyC9m9yov";
    static String accessToken = "1121089235191250944-d04mL1VxeDRaVVDh9iYowlWuczbWlE";
    static String accessTokenSecret = "mxcKtHR0fKwQECln8WHejy7AB35QtzIvT78VGsvY82nFV";


    // Get likes from previous tweet
    public void getLikes() throws IOException {
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
            updateLikes(text, likes);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
    }

    public void updateLikes(String text, int newLikes) throws IOException {

        int parameterslists[] = {4, 9, 14, 16, 20, 23}; //from getOptions
        int likes[] = new int[24];
        File file = new File("likes.txt");
        Scanner s = new Scanner(file);
        for (int i = 0; i < 24; i++) {
            likes[i] = Integer.parseInt(s.next()); //same as getOptions
        }

        //taking the text from a string into an int array
        String lastParametersStr[] = text.split(" ");
        int lastParametersInt[] = new int [lastParametersStr.length];
        for (int i = 0; i < 6; i++) {
            lastParametersInt[i] = Integer.parseInt(lastParametersStr[i]);
        }


        //updating likes
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                likes[lastParametersInt[i]] += newLikes;
            } else {
                likes[lastParametersInt[i] + parameterslists[i-1] + 1] += newLikes;
            }
        }

        //overwriting likes.txt
        BufferedWriter fw = new BufferedWriter(
                new FileWriter("likes.txt", false)  //Set true for append mode
        );
        System.out.println("Updating likes...");

        fw.write(likes[0] + " " + likes[1] + " " + likes[2] + " " + likes[3] + " " + likes[4]);
        fw.newLine();
        fw.write(likes[5] + " " + likes[6] + " " + likes[7] + " " + likes[8] + " " + likes[9]);
        fw.newLine();
        fw.write(likes[10] + " " + likes[11] + " " + likes[12] + " " + likes[13] + " " + likes[14]);
        fw.newLine();
        fw.write(likes[15] + " " + likes[16]);
        fw.newLine();
        fw.write(likes[17] + " " + likes[18] + " " + likes[19] + " " + likes[20]);
        fw.newLine();
        fw.write(likes[21] + " " + likes[22] + " " + likes[23]);

        fw.close();
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







