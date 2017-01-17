package twitter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        //throw new RuntimeException("not implemented");
        Map<String, Set<String>> followGraph = new HashMap<String,Set<String>>();

        // get set of unique authors/tweeters
        Set<String> authors = new HashSet<String>();
        Iterator<Tweet> tweetIterator = tweets.iterator();
        while(tweetIterator.hasNext()) authors.add(tweetIterator.next().getAuthor().toLowerCase());
                
        // iterate through the set of tweets by author/tweeter
        Iterator<String> authorIterator = authors.iterator();
        while(authorIterator.hasNext()) {
            String currentAuthor = authorIterator.next();
            List<Tweet> tweetsByCurrentAuthor = Filter.writtenBy(tweets, currentAuthor);
            Set<String> usersMentionedbyCurrentAuthor = Extract.getMentionedUsers(tweetsByCurrentAuthor);

/*            System.out.println("currentAuthor = " + currentAuthor);
            System.out.println("Tweets by current author: " + tweetsByCurrentAuthor);
            System.out.println("usersMentionedbyCurrentAuthor = " + usersMentionedbyCurrentAuthor);*/

            
            // remove self-references from @mention list before adding to graph
            for (String s : usersMentionedbyCurrentAuthor) if (s.equals(currentAuthor)) usersMentionedbyCurrentAuthor.remove(s);
            if(!usersMentionedbyCurrentAuthor.isEmpty()) followGraph.put(currentAuthor, usersMentionedbyCurrentAuthor);
        }

        return followGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        //throw new RuntimeException("not implemented");
        
        // creating map of users to their total number of followers
        Map<String,Long> followerCount = new HashMap<String,Long>();

        // start with list of twitter users first
        Iterator<String> twitIterator = followsGraph.keySet().iterator();
        while(twitIterator.hasNext()){
            String currentTwit = twitIterator.next().toLowerCase();
            followerCount.put(currentTwit, new Long(0));
        }

        
        // add in data from user follows graph
        Iterator<String> userIterator = followsGraph.keySet().iterator();
        while(userIterator.hasNext()){
            Iterator<String> followedUserIterator = followsGraph.get(userIterator.next()).iterator();
            while(followedUserIterator.hasNext()) {
                String followedUser = followedUserIterator.next().toLowerCase();
                if(followerCount.containsKey(followedUser)) {
                    long oneMoreFollower = followerCount.get(followedUser).longValue() + 1;
                    followerCount.put(followedUser, new Long(oneMoreFollower));
                }
                else {
                    followerCount.put(followedUser, new Long(1));                 
                }
            }// end inner while
        }// end outer while
        
        
        // now we have map follower count with keys of followedUser mapping to a count of followers
        // sorting using sort-by-value example found here: https://www.mkyong.com/java8/java-8-how-to-sort-a-map/
        Map<String,Long> sortedByFollowers = new LinkedHashMap<String,Long>();
        Stream<Map.Entry<String, Long>> sortedByFollowersStream = followerCount.entrySet().stream();
        sortedByFollowersStream.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedByFollowers.put(x.getKey(), x.getValue()));
        
        // converting sorted map to an ordered List to be returned
        List<String> tweetersByDescendingFollowerCount = new LinkedList<String>();
        Iterator<String> sortedTweeterIterator = sortedByFollowers.keySet().iterator();
        while(sortedTweeterIterator.hasNext()) tweetersByDescendingFollowerCount.add(sortedTweeterIterator.next());
        return tweetersByDescendingFollowerCount;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
