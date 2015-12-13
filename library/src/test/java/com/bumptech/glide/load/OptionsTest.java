package com.bumptech.glide.load;

import com.google.common.testing.EqualsTester;

import com.bumptech.glide.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18, constants = BuildConfig.class)
public class OptionsTest {

  @Test
  public void testEquals() {
    Option<Object> firstOption = Option.memory("firstKey");
    Object firstValue = new Object();
    Option<Object> secondOption = Option.memory("secondKey");
    Object secondValue = new Object();
    new EqualsTester()
        .addEqualityGroup(new Options(), new Options())
        .addEqualityGroup(
            new Options().set(firstOption, firstValue),
            new Options().set(firstOption, firstValue)
        )
        .addEqualityGroup(
            new Options().set(secondOption, secondValue),
            new Options().set(secondOption, secondValue)
        )
        .addEqualityGroup(
            new Options().set(firstOption, firstValue).set(secondOption, secondValue),
            new Options().set(firstOption, firstValue).set(secondOption, secondValue)
        ).testEquals();
  }

}
