package uk.ac.ebi.interpro.scan.io.match.phobius;

import junit.framework.TestCase;
import org.junit.Test;
import uk.ac.ebi.interpro.scan.io.ParseException;
import uk.ac.ebi.interpro.scan.io.match.phobius.parsemodel.PhobiusFeature;
import uk.ac.ebi.interpro.scan.io.match.phobius.parsemodel.PhobiusProtein;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Tests the PhobiusMatchParser, specifically looking at memory usage.
 *
 * @author Phil Jones
 * @version $Id$
 * @since 1.0
 */
public class PhobiusMatchParserTest extends TestCase {

    private static final String TEST_FILE_PATH = "data/phobius/10k.phobius.out";

    /**
     * Parses a (largish) file and outputs memory usage at the end of the parse.
     */
    @Test
    public void testParserEfficiency() throws IOException, ParseException {
        logMemUsage("Before parse: ");
        InputStream is = PhobiusMatchParserTest.class.getClassLoader().getResourceAsStream(TEST_FILE_PATH);
        PhobiusMatchParser parser = new PhobiusMatchParser();
        Set<PhobiusProtein> results = parser.parse(is, TEST_FILE_PATH);
        is.close();
        logMemUsage("After parse: ");
        System.out.println("Protein count: " + results.size());
        for (PhobiusProtein protein : results){
            assertTrue("The protein should be one or both of TM and SP.", protein.isSP() || protein.isTM());
            // Now test that those two methods work properly!
            boolean isSignal = false;
            boolean isTM = false;
//            System.out.println(protein.toString());
            for (PhobiusFeature feature : protein.getFeatures()){
                if (PhobiusFeature.C_REGION.equals(feature.getQualifier())
                        || PhobiusFeature.H_REGION.equals(feature.getQualifier())
                        || PhobiusFeature.N_REGION.equals(feature.getQualifier())){
                    isSignal = true;
                }
                if (PhobiusFeature.TRANSMEM.equals(feature.getName())){
                    isTM = true;
                }
            }
            assertTrue("The methods PhobiusProtein.isSP and / or PhobiusProtein.isTM are not returning expected results.", isSignal || isTM);
        }
    }

    private void logMemUsage(String prefix){
        System.gc();System.gc();System.gc();System.gc();
        System.gc();System.gc();System.gc();System.gc();
        System.gc();System.gc();System.gc();System.gc();
        System.gc();System.gc();System.gc();System.gc();
        System.gc();System.gc();System.gc();System.gc();
        System.out.println(prefix + (Runtime.getRuntime().totalMemory() -
            Runtime.getRuntime().freeMemory()) / (1024*1024) + " MB.");
    }
}