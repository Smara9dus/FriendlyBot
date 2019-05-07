import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {    // TODO: get posting to FB

        // First get the previous tweet and add its likes into likes.txt
//        TwitterPoster tp = new TwitterPoster();
//        try {
//            System.out.println(tp.getOptions());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        // Generate image as "recent.png"
        HeadlessSimple headlessSimple = new HeadlessSimple();
        String text = headlessSimple.script(); // This is the text that will be posted along with the image
        System.out.println(text);

        // Post image "recent.png"

    }
}
