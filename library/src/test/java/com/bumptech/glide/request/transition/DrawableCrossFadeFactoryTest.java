package com.bumptech.glide.request.transition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.BuildConfig;
import com.bumptech.glide.load.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, constants = BuildConfig.class)
public class DrawableCrossFadeFactoryTest {

  private DrawableCrossFadeFactory factory;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    ViewAnimationFactory<Drawable> viewAnimationFactory = mock(ViewAnimationFactory.class);
    factory = new DrawableCrossFadeFactory(viewAnimationFactory, 100 /*duration*/);
  }

  @Test
  public void testReturnsNoAnimationIfFromMemoryCache() {
    assertEquals(NoTransition.<Drawable>get(),
        factory.build(DataSource.MEMORY_CACHE, true /*isFirstResource*/));
  }

  @Test
  public void testReturnsReturnsAnimationIfNotFromMemoryCacheAndIsFirstResource() {
    assertNotEquals(NoTransition.<Drawable>get(),
        factory.build(DataSource.DATA_DISK_CACHE, true /*isFirstResource*/));
  }

  @Test
  public void testReturnsAnimationIfNotFromMemocyCacheAndNotIsFirstResource() {
    assertNotEquals(NoTransition.<Drawable>get(),
        factory.build(DataSource.DATA_DISK_CACHE, false /*isFirstResource*/));
  }
}
