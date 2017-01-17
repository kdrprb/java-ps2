package twitter;

import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Filter {

    /**
     * Find tweets written by a particular user.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        //throw new RuntimeException("not implemented");
        //if(tweets.isEmpty()) throw new IllegalArgumentException("tweets is empty");

        List<Tweet> tweetsbyAuthor = new LinkedList<Tweet>();

        Iterator<Tweet> tweetIterator = tweets.iterator();
        while(tweetIterator.hasNext()){
            Tweet currentTweet = tweetIterator.next();
            if(currentTweet.getAuthor().toLowerCase().equals(username.toLowerCase())) tweetsbyAuthor.add(currentTweet);
        }
        
        return tweetsbyAuthor;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        //throw new RuntimeException("not implemented");
        //if(tweets.isEmpty()) throw new RuntimeException("tweets is empty");

        List<Tweet> tweetsInTimespan = new LinkedList<Tweet>();

        Iterator<Tweet> tweetIterator = tweets.iterator();
        while(tweetIterator.hasNext()){
            Tweet currentTweet = tweetIterator.next();
            Instant currentTS = currentTweet.getTimestamp(); 
            Instant spanStart =  timespan.getStart();
            Instant spanEnd =  timespan.getEnd();
            if((currentTS.isAfter(spanStart) || currentTS.equals(spanStart)) && (currentTS.isBefore(spanEnd) || currentTS.equals(spanEnd))) {
                tweetsInTimespan.add(currentTweet);                
            }
        }

        return tweetsInTimespan;
    }

    /**
     * Find tweets that contain certain words.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param words
     *            a list of words to search for in the tweets. 
     *            A word is a nonempty sequence of nonspace characters.
     * @return all and only the tweets in the list such that the tweet text (when 
     *         represented as a sequence of nonempty words bounded by space characters 
     *         and the ends of the string) includes *at least one* of the words 
     *         found in the words list. Word comparison is not case-sensitive,
     *         so "Obama" is the same as "obama".  The returned tweets are in the
     *         same order as in the input list.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        //throw new RuntimeException("not implemented");
        //if(tweets.isEmpty()) throw new RuntimeException("tweets is empty");
        
        List<Tweet> tweetsWithWords = new LinkedList<Tweet>();
        //Pattern twitterWordPattern = Pattern.compile("(?<=([\\s\\^]))(\\S+)(?=([\\s\\$]))");
        Pattern twitterWordPattern = Pattern.compile("(\\S+)");

        List<String> lowerCaseWords = new LinkedList<String>();
        for (String temp : words) lowerCaseWords.add(temp.toLowerCase());

        Map<Tweet,List<String>> mapTweetsToListOfWords = new LinkedHashMap<Tweet,List<String>>();
        Iterator<Tweet> tweetIterator1 = tweets.iterator();
        while(tweetIterator1.hasNext()){
            Tweet currentTweet = tweetIterator1.next();
            String currentTweetText = currentTweet.getText().toLowerCase();
            List<String> currentTweetWordArray = new LinkedList<String>();
            Matcher matcher = twitterWordPattern.matcher(currentTweetText);
            while (matcher.find()) {
                //System.out.println(matcher.group());
                currentTweetWordArray.add(matcher.group().toLowerCase());
            }
            
/*            System.out.println("******************");
            System.out.println("currentTweet:" + currentTweet.toString());
            System.out.println("------------------");
            System.out.println("currentTweetWordArray:" + currentTweetWordArray.toString());
            System.out.println("******************");*/
            
            mapTweetsToListOfWords.put(currentTweet, currentTweetWordArray);
        }
        


        Iterator<Tweet> tweetIterator2 = tweets.iterator();
        while(tweetIterator2.hasNext()){
            Tweet currentTweet2 = tweetIterator2.next();
            List<String> currentTweetWordArray2 = mapTweetsToListOfWords.get(currentTweet2);
            
            SEARCH: for (String currentSearchWord : lowerCaseWords) {
                for (String currentTweetWord2 : currentTweetWordArray2) {
                    if(currentSearchWord.equals(currentTweetWord2)) {
                        tweetsWithWords.add(currentTweet2);
                        break SEARCH;
                    }
                }
            }
        }

        return tweetsWithWords;
    }

    
    
    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
