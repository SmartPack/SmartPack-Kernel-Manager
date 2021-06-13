/*
 * Copyright (c) Gustavo Claramunt (AnderWeb) 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adw.library.widgets.discreteseekbar.internal.compat;

import android.animation.ValueAnimator;

public class AnimatorCompatV11 extends AnimatorCompat {

    private final ValueAnimator animator;

    public AnimatorCompatV11(float start, float end, final AnimationFrameUpdateListener listener) {
        super();
        animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(animation -> listener.onAnimationFrame((Float) animation.getAnimatedValue()));
    }

    @Override
    public void cancel() {
        animator.cancel();
    }

    @Override
    public boolean isRunning() {
        return animator.isRunning();
    }

    @Override
    public void setDuration(int duration) {
        animator.setDuration(duration);
    }

    @Override
    public void start() {
        animator.start();
    }
}
