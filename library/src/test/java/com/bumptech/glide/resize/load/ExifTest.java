package com.bumptech.glide.resize.load;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

import com.bumptech.glide.BuildConfig;
import com.bumptech.glide.load.engine.bitmap_recycle.LruByteArrayPool;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.bumptech.glide.testutil.TestResourceUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;

@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, constants = BuildConfig.class)
public class ExifTest {

  private LruByteArrayPool byteArrayPool;

  private InputStream open(String imageName) throws IOException {
    return TestResourceUtil.openResource(getClass(), "exif-orientation-examples/" + imageName);
  }

  private void assertOrientation(String filePrefix, int expectedOrientation) {
    InputStream is = null;
    try {
      is = open(filePrefix + "_" + expectedOrientation + ".jpg");
      assertEquals(new ImageHeaderParser(is, byteArrayPool).getOrientation(),
          expectedOrientation);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          // Do nothing.
        }
      }
    }
  }

  @Before
  public void setUp() {
    byteArrayPool = new LruByteArrayPool();
  }

  @Test
  public void testIssue387() throws IOException {
    InputStream is = TestResourceUtil.openResource(getClass(), "issue387_rotated_jpeg.jpg");
    assertThat(new ImageHeaderParser(is, byteArrayPool).getOrientation()).isEqualTo(6);
  }

  @Test
  public void testLandscape() throws IOException {
    for (int i = 1; i <= 8; i++) {
      assertOrientation("Landscape", i);
    }
  }

  @Test
  public void testPortrait() throws IOException {
    for (int i = 1; i <= 8; i++) {
      assertOrientation("Portrait", i);
    }
  }

  @Test
  public void testHandlesInexactSizesInByteArrayPools() {
    for (int i = 1; i <= 8; i++) {
      byteArrayPool.put(new byte[LruByteArrayPool.STANDARD_BUFFER_SIZE_BYTES]);
      assertOrientation("Portrait", i);
    }
    for (int i = 1; i <= 8; i++) {
      byteArrayPool.put(new byte[LruByteArrayPool.STANDARD_BUFFER_SIZE_BYTES]);
      assertOrientation("Landscape", i);
    }
  }
}
