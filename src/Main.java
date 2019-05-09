import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args)  throws IOException {

        TwitterPoster tp = new TwitterPoster();
        HeadlessSimple headlessSimple = new HeadlessSimple();

        // First get the previous tweet and add its likes into likes.txt
         tp.getLikes();

        // Generate image as "recent.png"

        String text = headlessSimple.script(); // This is the text that will be posted along with the image

        // Post image "recent.png"
        tp.postImage(text);

    }
}
