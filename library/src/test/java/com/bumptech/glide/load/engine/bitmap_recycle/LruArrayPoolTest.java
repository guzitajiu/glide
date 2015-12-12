package com.bumptech.glide.load.engine.bitmap_recycle;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_COMPLETE;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.bumptech.glide.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = Config.DEFAULT, sdk = 18, constants = BuildConfig.class)
public class LruArrayPoolTest {
  private static final int MAX_SIZE = 10;
  private static final Class ARRAY_CLASS = byte[].class;
  private static final ArrayAdapterInterface ADAPTER = new ByteArrayAdapter();
  private LruArrayPool pool;

  @Before
  public void setUp() throws Exception {
    pool = new LruArrayPool(MAX_SIZE);
  }

  @Test
  public void testNewPoolIsEmpty() {
    assertEquals(pool.getCurrentSize(), 0);
  }

  @Test
  public void testICanAddAndGetValidArray() {
    int size = 758;
    int value = 564;
    fillPool(pool, size - 1, value);
    pool.put(createArray(ARRAY_CLASS, size, value), ARRAY_CLASS);
    Object array = pool.get(size, ARRAY_CLASS);
    assertNotNull(array);
    assertTrue(ADAPTER.getArrayLength(array) >= size);
    assertTrue(array.getClass() == ARRAY_CLASS);
    assertTrue(((byte[]) array)[0] == (byte) 0);
  }

  @Test
  public void testItIsSizeLimited() {
    fillPool(pool, MAX_SIZE / ADAPTER.getElementSizeInBytes() + 1, 1);
    assertTrue(pool.getCurrentSize() <= MAX_SIZE);
  }

  @Test
  public void testArrayLargerThanPoolIsNotAdded() {
    pool = new LruArrayPool(MAX_SIZE);
    pool.put(createArray(ARRAY_CLASS, MAX_SIZE / ADAPTER.getElementSizeInBytes() + 1, 0),
        ARRAY_CLASS);
    assertEquals(0, pool.getCurrentSize());
  }

  @Test
  public void testClearMemoryRemovesAllArrays() {
    fillPool(pool, MAX_SIZE / ADAPTER.getElementSizeInBytes() + 1, 0);
    pool.clearMemory();
    assertEquals(0, pool.getCurrentSize());
  }

  @Test
  public void testTrimMemoryUiHiddenOrLessRemovesHalfOfArrays() {
    testTrimMemory(MAX_SIZE, TRIM_MEMORY_UI_HIDDEN, MAX_SIZE / 2);
  }

  @Test
  public void testTrimMemoryUiHiddenOrLessRemovesNoArraysIfPoolLessThanHalfFull() {
    testTrimMemory(MAX_SIZE / 2, TRIM_MEMORY_UI_HIDDEN, MAX_SIZE / 2);
  }

  @Test
  public void testTrimMemoryBackgroundOrGreaterRemovesAllArrays() {
    for (int trimLevel : new int[] {TRIM_MEMORY_BACKGROUND, TRIM_MEMORY_COMPLETE}) {
      testTrimMemory(MAX_SIZE, trimLevel, 0);
    }
  }

  private void testTrimMemory(int fillSize, int trimLevel, int expectedSize) {
    pool = new LruArrayPool(MAX_SIZE);
    fillPool(pool, fillSize / ADAPTER.getElementSizeInBytes(), 1);
    pool.trimMemory(trimLevel);
    assertEquals("Failed level=" + trimLevel, expectedSize, pool.getCurrentSize());
  }

  private void fillPool(LruArrayPool pool, int arrayCount, int arrayLength) {
    for (int i = 0; i < arrayCount; i++) {
      pool.put(createArray(ARRAY_CLASS, arrayLength, 10), ARRAY_CLASS);
    }
  }

  private Object createArray(Class type, int size, int value) {
    Object array = null;
    if (type.equals(int[].class)) {
      array = new int[size];
      Arrays.fill((int[]) array, value);
    } else if (type.equals(byte[].class)) {
      array = new byte[size];
      Arrays.fill((byte[]) array, (byte) value);
    }
    return array;
  }
}
