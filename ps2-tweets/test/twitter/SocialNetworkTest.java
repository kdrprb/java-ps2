package twitter;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

    /*
     * Testing strategy for guessFollowsGraph
     *
     * Partition the inputs as follows:
     * 
     * # tweets:            0 (already tested above), > 1
     * # @mentions by user: 0, > 1 
     * Other tests: self @mentions
     * 
     */
    
    private static Map<String, Set<String>> convertMapToLowerCase(Map<String,Set<String>> originalMap)
    {
        Map<String,Set<String>> newMap = new HashMap<String,Set<String>>();
        Iterator<String> myMapIterator = originalMap.keySet().iterator();
        while(myMapIterator.hasNext())
        {
            String currentKey = (String) myMapIterator.next();
            Set<String> newSet = new HashSet<String>();
            
            Iterator<String> currentSetIterator = originalMap.get(currentKey).iterator();
            while(currentSetIterator.hasNext())
            {
                newSet.add((String) currentSetIterator.next().toLowerCase());
            }
            newMap.put(currentKey.toLowerCase(),newSet);
        }
        
        return newMap;
    }
    

    private static boolean isGraphSubset(Map<String,Set<String>> testGraph, Set<String> allValuesSet)
    {
        Iterator<String> testSetIterator = testGraph.keySet().iterator();
        while(testSetIterator.hasNext())
        {
            String currentKey = (String) testSetIterator.next();
            if(!allValuesSet.contains(currentKey)) return false;
            Iterator<String> currentValueIterator = testGraph.get(currentKey).iterator();
            while(currentValueIterator.hasNext())
            {
                String currentValue = (String) currentValueIterator.next();
                if(!allValuesSet.contains(currentValue)) return false;
            }
        }
        return true;
    }

    
    @Test
    public void testGuessFollowsGraphNoAtMentions() throws IOException {
        Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", Instant.now());
        Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", Instant.now());
        
        List<Tweet> testTweetList = new ArrayList<Tweet>();
        testTweetList.add(tweet1);
        testTweetList.add(tweet2);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(testTweetList);
        Map<String, Set<String>> followsGraphLC = convertMapToLowerCase(followsGraph);
        
        
        //assertTrue("expected empty graph", followsGraph.isEmpty());
        Set<String> allPossibleUsers = new HashSet<String>(Arrays.asList("alyssa","bbitdiddle"));
        assertTrue("expected graph size <= 2", followsGraph.size() <= 2);
        assertTrue("invalid user names", isGraphSubset(followsGraphLC,allPossibleUsers));
    }

    @Test
    public void testGuessFollowsGraphSelfMentions() {
        Tweet tweet1 = new Tweet(1, "seinfeld", "@seinfeld", Instant.now());
        
        List<Tweet> testTweetList = new ArrayList<Tweet>();
        testTweetList.add(tweet1);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(testTweetList);
        Map<String, Set<String>> followsGraphLC = convertMapToLowerCase(followsGraph);
        
        Set<String> allPossibleUsers = new HashSet<String>(Arrays.asList("seinfeld"));
        assertTrue("expected graph size <= 1", followsGraph.size() <= 1);
        assertTrue("invalid user names", isGraphSubset(followsGraphLC,allPossibleUsers));
    }


    public void testGuessFollowsGraphSingleAtMention() {
        Tweet tweet1 = new Tweet(1, "seinfeld", "@elaine", Instant.now());
        
        List<Tweet> testTweetList = new ArrayList<Tweet>();
        testTweetList.add(tweet1);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(testTweetList);
        
        assertTrue("expected 1 item in graph", followsGraph.keySet().size()==1);
        assertTrue("seinfeld follows elaine",followsGraph.get("seinfeld").iterator().next().equals("elaine"));
    }

    @Test
    public void testGuessFollowsGraphDuplicateAtMentions() {
        Tweet tweet1 = new Tweet(1, "seinfeld", "@elaine what's up?", Instant.now());
        Tweet tweet2 = new Tweet(2, "seinfeld", "HELLO ARE YOU THERE @ELAINE??", Instant.now());
        
        List<Tweet> testTweetList = new ArrayList<Tweet>();
        testTweetList.add(tweet1);
        testTweetList.add(tweet2);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(testTweetList);
        Map<String, Set<String>> followsGraphLC = convertMapToLowerCase(followsGraph);
        
        Set<String> allPossibleUsers = new HashSet<String>(Arrays.asList("seinfeld","elaine"));
        assertTrue("expected >=1 items in graph", followsGraph.keySet().size() >= 1);
        assertTrue("expected <=2 items in graph", followsGraph.keySet().size() <= 2);
        assertTrue("invalid user names", isGraphSubset(followsGraphLC,allPossibleUsers));
        assertTrue("seinfeld follows elaine",followsGraphLC.get("seinfeld").iterator().next().equals("elaine"));
    }
    
    @Test
    public void testGuessFollowsGraphCrossMentions() {
        Tweet tweet1 = new Tweet(1, "seinfeld", "@elaine what's up?", Instant.now());
        Tweet tweet2 = new Tweet(2, "elaine", "shutup @seinfeld", Instant.now());
        
        List<Tweet> testTweetList = new ArrayList<Tweet>();
        testTweetList.add(tweet1);
        testTweetList.add(tweet2);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(testTweetList);
        Map<String, Set<String>> followsGraphLC = convertMapToLowerCase(followsGraph);
        
        assertTrue("expected 2 items in graph", followsGraph.keySet().size()==2);
        assertTrue("seinfeld follows elaine",followsGraphLC.get("seinfeld").iterator().next().equals("elaine"));
        assertTrue("elaine follows seinfeld",followsGraphLC.get("elaine").iterator().next().equals("seinfeld"));
    }

    @Test
    public void testGuessFollowsGraphMultipleTweetsMultipleMentions() {
        Tweet tweet1 = new Tweet(1, "seinfeld", "my pals are @elaine @george @kosmo-kramer", Instant.now());
        Tweet tweet2 = new Tweet(2, "seinfeld", "I don't like: @newman_postman and @kennybania", Instant.now());
        Tweet tweet3 = new Tweet(3, "george", "thanks @seinfeld what about you guys @Elaine and @kosmo-kramer", Instant.now());
        Tweet tweet4 = new Tweet(4, "elaine", "@george you are unpleasant and @kosmo-kramer is a hipster doofus.", Instant.now());
        Tweet tweet5 = new Tweet(5, "KRAMER", "that hurts @elaine!  you hurt my #katra.  where's @crazyjoedevola?", Instant.now());
        Tweet tweet6 = new Tweet(6, "Elaine", "@KOSMO-KRAMER you've been fighting 12 year olds!", Instant.now());
        Tweet tweet7 = new Tweet(7, "kramer", "@kennybania has been working out.  @NEWMAN_postman you need to get on that!", Instant.now());
        Tweet tweet8 = new Tweet(8, "NEWMAN_POSTMAN", "hello @seinfeld.  @kosmo-kramer I think I'll have chicken wings and draw myself a footbath instead.", Instant.now());
        Tweet tweet9 = new Tweet(9, "Seinfeld", "Nice to see you all getting along @elaine @george @kosmo-kramer @newman_postman @kennybania", Instant.now());
        Tweet tweet10 = new Tweet(10, "kennybania", "Everyone just leave me alone!", Instant.now());
        
        List<Tweet> testTweetList = new ArrayList<Tweet>();
        testTweetList.add(tweet1);
        testTweetList.add(tweet2);
        testTweetList.add(tweet3);
        testTweetList.add(tweet4);
        testTweetList.add(tweet5);
        testTweetList.add(tweet6);
        testTweetList.add(tweet7);
        testTweetList.add(tweet8);
        testTweetList.add(tweet9);
        testTweetList.add(tweet10);

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(testTweetList);
        Map<String, Set<String>> followsGraphLC = convertMapToLowerCase(followsGraph);
        
        Set<String> allPossibleUsers = new HashSet<String>(Arrays.asList("seinfeld","george","elaine","kramer","newman_postman","kennybania","kosmo-kramer","crazyjoedevola"));
        assertTrue("expected >=5 items in graph", followsGraph.keySet().size() >= 5);
        assertTrue("expected <=8 items in graph", followsGraph.keySet().size() <= 8);
        assertTrue("invalid user names", isGraphSubset(followsGraphLC,allPossibleUsers));

        
        assertTrue("seinfeld follows >=5 authors",followsGraphLC.get("seinfeld").size()>=5);
        assertTrue("seinfeld follows wrong people", followsGraphLC.get("seinfeld").containsAll(Arrays.asList("elaine","george","kosmo-kramer","newman_postman","kennybania")));        
        assertTrue("george follows >=3 authors",followsGraphLC.get("george").size()>=3);
        assertTrue("george folllows wrong people", followsGraphLC.get("george").containsAll(Arrays.asList("seinfeld","elaine","kosmo-kramer")));        
        assertTrue("elaine  follows >=2 authors",followsGraphLC.get("elaine").size()>=2);
        assertTrue("elaine folllows wrong people", followsGraphLC.get("elaine").containsAll(Arrays.asList("george","kosmo-kramer")));        
        assertTrue("kramer follows >=4 authors",followsGraphLC.get("kramer").size()>=4);
        assertTrue("kramer folllows wrong people", followsGraphLC.get("kramer").containsAll(Arrays.asList("elaine","crazyjoedevola","newman_postman","kennybania")));        
        assertTrue("newman_postman follows >=2 authors",followsGraphLC.get("newman_postman").size()>=2);
        assertTrue("newman_postman folllows wrong people", followsGraphLC.get("newman_postman").containsAll(Arrays.asList("seinfeld","kosmo-kramer")));
    }


    /*
     * Testing strategy for influencers
     *
     * Partition the inputs as follows:
     * 
     * # nodes:             0 (already tested above), 1, > 1
     * # followers by user: 0, > 1
     * Other tests: test order by #followers, tie for # followers?, capitalization
     * 
     */

    @Test
    public void testInfluencersNodeWithEmptyFollowersList() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("superman", new HashSet<String>(Arrays.asList()));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected 1 element in list", influencers.size()==1);
        assertTrue("expected superman", influencers.get(0).equals("superman"));        
    }
    
    @Test
    public void testInfluencersOneNodeGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("superman", new HashSet<String>(Arrays.asList("batman")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected 2 elements in list", influencers.size()==2);
        assertTrue("batman has a follower", influencers.get(0).equals("batman"));
        assertTrue("superman is also there", influencers.get(1).equals("superman"));
    }

    @Test
    public void testInfluencersCapitalization() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("superman", new HashSet<String>(Arrays.asList("wonderwoman")));
        followsGraph.put("batman", new HashSet<String>(Arrays.asList("WONDERWOMAN")));
        followsGraph.put("joker", new HashSet<String>(Arrays.asList("WonderWoman")));
        followsGraph.put("penguin", new HashSet<String>(Arrays.asList("wonderWOMAN")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected 5 elements in list", influencers.size()==5);
        assertTrue("wonderwoman has many followers", influencers.get(0).equals("wonderwoman"));
        assertTrue("superman is also there", influencers.contains("superman"));
        assertTrue("batman is also there", influencers.contains("batman"));
        assertTrue("joker is also there", influencers.contains("joker"));
        assertTrue("penguin is also there", influencers.contains("penguin"));
    }

    @Test
    public void testInfluencersOneNodeWithMultipleFollows() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("superman", new HashSet<String>(Arrays.asList("batman","wonderwoman")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected 3 element in list", influencers.size()==3);
        assertTrue("batman has a follower", influencers.contains("batman"));
        assertTrue("wonderwoman has a follower", influencers.contains("wonderwoman"));
        assertTrue("superman is also there", influencers.contains("superman"));
    }

    @Test
    public void testInfluencersMultiNodeWithMultipleFollows() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("superman", new HashSet<String>(Arrays.asList("batman","wonderwoman")));
        followsGraph.put("batman", new HashSet<String>(Arrays.asList("superman","joker","penguin")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected 5 element in list", influencers.size()==5);
        assertTrue("batman has a follower", influencers.contains("batman"));
        assertTrue("wonderwoman has a follower", influencers.contains("wonderwoman"));
        assertTrue("superman has a follower", influencers.contains("superman"));
        assertTrue("joker has a follower", influencers.contains("joker"));
        assertTrue("penguin has a follower", influencers.contains("penguin"));
    }

    @Test
    public void testInfluencersOrderByFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("superman", new HashSet<String>(Arrays.asList("wonderwoman","lex")));
        followsGraph.put("batman", new HashSet<String>(Arrays.asList("wonderwoman","superman","lex")));
        followsGraph.put("joker", new HashSet<String>(Arrays.asList("wonderwoman","superman","lex","batman")));
        followsGraph.put("penguin", new HashSet<String>(Arrays.asList("wonderwoman")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected 6 element in list", influencers.size()==6);
        assertTrue("wonderwoman has a follower", influencers.contains("wonderwoman"));
        assertTrue("wonderwoman has a follower", influencers.get(0).equals("wonderwoman"));
        assertTrue("lex has a follower", influencers.contains("lex"));
        assertTrue("lex has the same or fewer followers", influencers.get(1).equals("lex"));
        assertTrue("superman has a follower", influencers.contains("superman"));
        assertTrue("superman has the same or fewer followers", influencers.get(2).equals("superman"));
        assertTrue("batman has a follower", influencers.contains("batman"));
        assertTrue("batman has the same or fewer followers", influencers.get(3).equals("batman"));
        assertTrue("joker is also there", influencers.contains("joker"));
        assertTrue("penguin is also there", influencers.contains("penguin"));
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
