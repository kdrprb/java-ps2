package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

    /*
     * Testing strategy for getTimespan
     *
     * Partition the inputs as follows:
     * 
     * # tweets:        1, > 1
     * for > 1 tweets:  duplicate Timestamps, out of order tweets
     * 
     */
    @Test
    public void testGetTimespanEmptyList() {
        Timespan timespan = Extract.getTimespan(Arrays.asList());
        
        assertTrue(timespan != null);
        assertTrue(timespan.getStart().equals(timespan.getEnd()));
    }
    
    @Test
    public void testGetTimespanSingleTweet() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanDuplicateTimestamps() {
        Instant d3 = Instant.parse(d1.toString());        
        Tweet tweet3 = new Tweet(3, "seinfeld", "so what's the deal with cancer?", d3);

        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1,tweet3));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanOutOfOrder() {
        Instant d4 = Instant.parse("2016-02-17T10:30:00Z");        
        Tweet tweet4 = new Tweet(4, "bania", "that's gold @jerry ... GOLD!", d4);

        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1,tweet2,tweet4));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    
    /*
     * Testing strategy for getMentionedUsers
     *
     * Partition the inputs as follows:
     * 
     * # tweets:        0, 1, > 1
     * # @mentions:     0, 1, > 1
     * Other tests:     lone @, email address (e.g. bitdiddle@mit.edu), capitalization, valid chars in @mentions (i.e. -)
     *                  special chars in @mentions (e.g. !/*?), punctuation, duplicate @mentions
     * 
     */
    
     @Test
     public void testGetMentionedUsersEmptyList() {
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList());
         assertTrue("expected empty set", mentionedUsers.isEmpty());
     }
     
     @Test
     public void testGetMentionedUsersLoneAtSign() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "@ newman is stupid", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));         
         assertTrue("expected empty set", mentionedUsers.isEmpty());
     }
     
     @Test
     public void testGetMentionedUsersEmailAddress() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "just saying bitdiddle@mit.edu is a weird email address", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));         
         assertTrue("expected empty set", mentionedUsers.isEmpty());
     }
     
     @Test
     public void testGetMentionedUsersOnlyAtMention() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "@newman", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));         

         assertTrue("expected single value",mentionedUsers.size()==1);
         assertTrue("should contain newman",mentionedUsers.iterator().next().toLowerCase().equals("newman"));
     }
     
     @Test
     public void testGetMentionedUsersSingleMention() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "hello @newman", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));

         assertTrue("expected single value",mentionedUsers.size()==1);
         assertTrue("should contain newman",mentionedUsers.iterator().next().toLowerCase().equals("newman"));
     }

     @Test
     public void testGetMentionedUsersValidChar() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "hello @new-man", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));

         assertTrue("expected single value",mentionedUsers.size()==1);
         assertTrue("should contain new-man",mentionedUsers.iterator().next().toLowerCase().equals("new-man"));
     }

     @Test
     public void testGetMentionedUsersInvalidChar() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "hello @new?man", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));

         assertTrue("expected single value",mentionedUsers.size()==1);
         assertTrue("should contain new",mentionedUsers.iterator().next().toLowerCase().equals("new"));
     }

     @Test
     public void testGetMentionedUsersPunctuation() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "what the heck :@elaine!?!???!", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));

         assertTrue("expected single value",mentionedUsers.size()==1);
         assertTrue("should contain elaine",mentionedUsers.iterator().next().toLowerCase().equals("elaine"));
     }

     @Test
     public void testGetMentionedUsersTwoMentions() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "hello @newman and @bania", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));
         
         assertTrue("expected two values",mentionedUsers.size()==2);
         assertTrue("should contain newman",mentionedUsers.toString().toLowerCase().contains("newman"));
         assertTrue("should contain bania",mentionedUsers.toString().toLowerCase().contains("bania"));
     }

     
     @Test
     public void testGetMentionedUsersDuplicateMentions() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "@newman is slow and @newman is stupid", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));
         
         assertTrue("expected single value",mentionedUsers.size()==1);
         assertTrue("should contain newman",mentionedUsers.toString().toLowerCase().contains("newman"));
     }
     
     @Test
     public void testGetMentionedUsersChangeCase() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "@newman is slow and @NewmaN is stupid", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));
         
         assertTrue("expected single value",mentionedUsers.size()==1);
         assertTrue("should contain newman",mentionedUsers.toString().toLowerCase().contains("newman"));
     }

     @Test
     public void testGetMentionedUsersMultipleTweets() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "hello @newman", Instant.now());
         Tweet tweet6 = new Tweet(6, "seinfeld", "hello @bania", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5,tweet6));
         
         assertTrue("expected two values",mentionedUsers.size()==2);
         assertTrue("should contain bania",mentionedUsers.toString().toLowerCase().contains("bania"));
         assertTrue("should contain newman",mentionedUsers.toString().toLowerCase().contains("newman"));
     }

     @Test
     public void testGetMentionedUsersDuplicationAcrossMultipleTweets() {
         Tweet tweet5 = new Tweet(5, "seinfeld", "hello @newman and @bania", Instant.now());
         Tweet tweet6 = new Tweet(6, "seinfeld", "goodbye @newman and @bania", Instant.now());
         Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5,tweet6));
         
         assertTrue("expected two values",mentionedUsers.size()==2);
         assertTrue("should contain bania",mentionedUsers.toString().toLowerCase().contains("bania"));
         assertTrue("should contain newman",mentionedUsers.toString().toLowerCase().contains("newman"));
     }


     /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
