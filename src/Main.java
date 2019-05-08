import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {    // TODO: get posting to FB

        TwitterPoster tp = new TwitterPoster();
        HeadlessSimple headlessSimple = new HeadlessSimple();

        // First get the previous tweet and add its likes into likes.txt
        // tp.getLikes();
//        tp.configBuilder();

        // Generate image as "recent.png"

        String text = headlessSimple.script(); // This is the text that will be posted along with the image
        tp.postImage(text);

        // Post image "recent.png"

    }
}
