package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        //throw new RuntimeException("not implemented");
        //if(tweets.isEmpty()) throw new RuntimeException("tweets is empty");

        if(tweets.isEmpty()) {
            Instant now = Instant.now();
            return new Timespan(now,now);
        }
    
        
        Iterator<Tweet> tweetIterator = tweets.iterator();
        Tweet currentTweet =  tweetIterator.next();
        Instant earliestStartInstant = currentTweet.getTimestamp();
        Instant latestEndInstant = currentTweet.getTimestamp();
        
        while(tweetIterator.hasNext()) {
            currentTweet = tweetIterator.next();
            Instant currentTimestamp = currentTweet.getTimestamp();
            if(currentTimestamp.isBefore(earliestStartInstant)) earliestStartInstant = currentTimestamp;
            if(currentTimestamp.isAfter(latestEndInstant)) latestEndInstant = currentTimestamp;
        }
        
        return new Timespan(earliestStartInstant, latestEndInstant);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        //throw new RuntimeException("not implemented");
        //if(tweets.isEmpty()) throw new RuntimeException("tweets is empty");
        
        Set<String> mentionedUsers = new HashSet<String>();
        
        Iterator<Tweet> tweetIterator = tweets.iterator();
        //Pattern wordsInStringPattern = Pattern.compile("\\w+");
        //Pattern wordsInStringPattern = Pattern.compile("[^a-zA-Z_-]@[a-zA-Z_-]+[^a-zA-Z_-]");
        Pattern twitterMentionsPattern = Pattern.compile("(?<![\\w\\-])@([\\w\\-]+)");

        while(tweetIterator.hasNext()) {
            String currentTweetText = tweetIterator.next().getText().toLowerCase();
            //System.out.print(currentTweetText + "\n**************\n");
            Matcher matcher = twitterMentionsPattern.matcher(currentTweetText);
            while (matcher.find()) {
                //System.out.println(matcher.group());
                mentionedUsers.add(matcher.group().substring(1).toLowerCase());
            }
        }
     
        //System.out.print("\n--------------\n");
        //System.out.print(mentionedUsers);
        return mentionedUsers;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
