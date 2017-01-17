package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

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
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

    /*
     * Testing strategy for writtenBy
     *
     * Partition the inputs as follows:
     * 
     * # tweets:        0, 1, > 1
     * # tweets by:     0, 1, > 1
     * Other tests:     check tweet/parameter capitalization, check list order
     * 
     */

    @Test
    public void testWrittenByEmptyList() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "username");
        
        assertEquals("expected empty list", 0, writtenBy.size());
    }
    
    @Test
    public void testWrittenByUsernameNotFound() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "username");
        
        assertEquals("expected empty list", 0, writtenBy.size());
    }
    
    @Test
    public void testWrittenByCheckParameterCapitalization() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "ALYSSA");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testWrittenByCheckTweetCapitalization() {
        Tweet tweet3 = new Tweet(3, "ALYSSA", "hey nobody responded", Instant.now());
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet3), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet3));
    }
    
    @Test
    public void testWrittenByCheckOrder() {
        Tweet tweet3 = new Tweet(3, "alyssa", "hey nobody wrote back!", Instant.now());
        Tweet tweet4 = new Tweet(4, "alyssa", "why use twitter when nobody responds?", Instant.now());
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1,tweet2,tweet3,tweet4), "alyssa");

        assertEquals("expected empty list", 3, writtenBy.size());
        assertTrue("expected list to contain tweets", writtenBy.containsAll(Arrays.asList(tweet1, tweet3, tweet4)));
        assertEquals("expected same order", 0, writtenBy.indexOf(tweet1));
        assertEquals("expected same order", 1, writtenBy.indexOf(tweet3));
        assertEquals("expected same order", 2, writtenBy.indexOf(tweet4));
    }

    /*
     * Testing strategy for inTimespan
     *
     * Partition the inputs as follows:
     * 
     * # tweets found:  0, 1, > 1
     * Other tests:     timespan = 0 seconds, check list order, 
     *                  timespan border inclusion, empty list, timespan singularity inclusion
     * 
     */

    @Test
    public void testInTimespanEmptyList() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());

    }
    
    @Test
    public void testInTimespanZeroSecondTimespan() {
        Instant testInstant = Instant.now();
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testInstant, testInstant));
        
        assertTrue("expected empty list", inTimespan.isEmpty());

    }
    
    @Test
    public void testInTimespanFilterForOneTweet() {
        Instant testStart = Instant.parse("2016-02-17T09:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertTrue("expected list of size 1", inTimespan.size()==1);
        assertTrue("expected list to contain tweet", inTimespan.containsAll(Arrays.asList(tweet1)));
    }
    
    @Test
    public void testInTimespanBorderInclusion() {
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(d1, d2));
        
        assertTrue("expected list of size 2", inTimespan.size()==2);
        assertTrue("expected list to contain tweet", inTimespan.containsAll(Arrays.asList(tweet1,tweet2)));
    }

    @Test
    public void testInTimespanSingularityInclusion() {
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(d1, d1));
        
        assertTrue("expected list of size 1", inTimespan.size()==1);
        assertTrue("expected list to contain tweet", inTimespan.containsAll(Arrays.asList(tweet1)));
    }

    @Test
    public void testInTimespanOrder() {
        Instant d3 = Instant.parse("2016-02-17T09:30:00Z");
        Instant d4 = Instant.parse("2016-02-17T10:30:00Z");

        Tweet tweet3 = new Tweet(3, "seinfeld", "so what's the deal with cancer", d3);
        Tweet tweet4 = new Tweet(4, "seinfeld", "not that there's anything wrong with that", d4);

        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet4, tweet3, tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertTrue("expected list of size 4", inTimespan.size()==4);
        //assertTrue("expected list to contain tweet", inTimespan.containsAll(Arrays.asList(tweet1,tweet2,tweet3,tweet4)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet4));
        assertEquals("expected same order", 1, inTimespan.indexOf(tweet3));
        assertEquals("expected same order", 2, inTimespan.indexOf(tweet1));
        assertEquals("expected same order", 3, inTimespan.indexOf(tweet2));
    }

    /*
     * Testing strategy for containing
     *
     * Partition the inputs as follows:
     * 
     * # search words:  0, 1, > 1
     * # tweets found:  0, 1, > 1
     * Other tests:     empty tweet list, empty word list, special characters, 
     *                  @mentions, email addresses, substrings, capitalization
     * 
     */

    @Test
    public void testContainingEmptyTweetList() {
        List<Tweet> containing = Filter.containing(Arrays.asList(), Arrays.asList("words"));
        
        assertTrue("expected empty list", containing.size()==0);
    }

    @Test
    public void testContainingEmptyWordList() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList());
        
        assertTrue("expected empty list", containing.size()==0);
    }

    @Test
    public void testContainingValidInputsTweetNotFound() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("gobbledygook","rumplestitleskin"));
        
        assertTrue("expected empty list", containing.size()==0);
    }

    @Test
    public void testContainingFirstWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("is"));
        
        assertTrue("expected 1 item list", containing.size()==1);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1)));
    }

    @Test
    public void testContainingHashtag() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("#hype"));
        
        assertTrue("expected 1 item list", containing.size()==1);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet2)));
    }

    @Test
    public void testContainingPunctuationWord() {
        Tweet tweet3 = new Tweet(3, "alyssa", "hey you #!@*!#@ guy", Instant.now());
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet3), Arrays.asList("#!@*!#@"));
        
        assertTrue("expected 1 item list", containing.size()==1);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet3)));
    }

    @Test
    public void testContainingSubstring() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("easonabl"));
        
        assertTrue("expected empty list", containing.size()==0);
    }

    @Test
    public void testContainingCapitalization() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("RIVEST"));
        
        assertTrue("expected non-empty list", containing.size()==2);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    @Test
    public void testContainingAtMention() {
        Tweet tweet3 = new Tweet(3, "alyssa", "hey @professor what's up?", Instant.now());
        Tweet tweet4 = new Tweet(4, "alyssa", "what does @class like most about java?", Instant.now());
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet3, tweet4), Arrays.asList("@professor","@class"));
        
        assertTrue("expected non-empty list", containing.size()==2);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet3, tweet4)));
        assertEquals("expected same order", 0, containing.indexOf(tweet3));
    }


    @Test
    public void testContainingMultipleWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("reasonable","30"));
        
        assertTrue("expected non-empty list", containing.size()==2);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

    @Test
    public void testContainingReverseOrder() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet2, tweet1), Arrays.asList("talk"));
        
        assertTrue("expected non-empty list", containing.size()==2);
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 1, containing.indexOf(tweet1));
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
