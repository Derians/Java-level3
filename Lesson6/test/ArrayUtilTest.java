import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Chaykin Ivan
 * @version 27.10.2019
 */
public class ArrayUtilTest {
    private static ArrayUtil arrayUtil;

    @BeforeClass
    public static void setUp() throws Exception {
        arrayUtil = new ArrayUtil();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        arrayUtil = null;
    }

    @Test
    public void testNotNullExtractFromArray() {
        int[] firstArray = {1, 2, 4, 4, 2, 3, 4, 1, 7};
        assertNotNull(arrayUtil.extractFromArray(firstArray, 4));
    }

    @Test
    public void testNullExtractFromArray() {
        int[] firstArray = {};
        assertNull(arrayUtil.extractFromArray(firstArray, 4));
    }

    @Test (expected = RuntimeException.class)
    public void testExceptionExtractFromArray() {
        int[] firstArray = {1, 2, 4, 4, 2, 3, 4, 1, 7};
        assertNotNull(arrayUtil.extractFromArray(firstArray, 8));
    }

    @Test
    public void test1ExtractFromArray() {
        int[] firstArray = {1, 2, 4, 4, 2, 3, 4, 1, 7};
        int[] resultArray = {1, 7};
        Assert.assertArrayEquals(arrayUtil.extractFromArray(firstArray, 4), resultArray);
    }

    @Test
    public void test2ExtractFromArray() {
        int[] firstArray = {1, 2, 4, 4, 2, 3, 1, 7};
        int[] resultArray = {2, 3, 1, 7};
        Assert.assertArrayEquals(arrayUtil.extractFromArray(firstArray, 4), resultArray);
    }

    @Test
    public void test3ExtractFromArray() {
        int[] firstArray = {1, 2, 4, 4, 2, 3, 4, 1, 7, 4};
        int[] resultArray = {};
        Assert.assertArrayEquals(arrayUtil.extractFromArray(firstArray, 4), resultArray);
    }

    @Test
    public void test4ExtractFromArray() {
        int[] firstArray = {1, 2, 4, 4, 2, 3, 4, 1, 4, 7};
        int[] resultArray = {7};
        Assert.assertArrayEquals(arrayUtil.extractFromArray(firstArray, 4), resultArray);
    }


    @Test
    public void test1FalseValidateArray() {
        int[] firstArray = {};
        assertFalse(arrayUtil.validateArray(firstArray, 1, 4));
    }

    @Test
    public void test2FalseValidateArray() {
        int[] firstArray = {1, 28, 40, 64, 202, 51, 216, 188, 124, 100};
        assertFalse(arrayUtil.validateArray(firstArray, 1, 4));
    }

    @Test
    public void test3FalseValidateArray() {
        int[] firstArray = {4, 28, 40, 64, 202, 51, 216, 188, 124, 100};
        assertFalse(arrayUtil.validateArray(firstArray, 1, 4));
    }

    @Test
    public void testTrue1ValidateArray() {
        int[] firstArray = {1, 2, 4, 4, 2, 3, 4, 1, 7};
        assertTrue(arrayUtil.validateArray(firstArray, 1, 4));
    }

    @Test
    public void testTrue2ValidateArray() {
        int[] firstArray = {8, 2, 7, 4, 2, 3, 5, 1, 7};
        assertTrue(arrayUtil.validateArray(firstArray, 1, 4));
    }
}