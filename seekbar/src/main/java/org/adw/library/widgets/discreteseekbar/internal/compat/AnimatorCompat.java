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

public abstract class AnimatorCompat {
    public interface AnimationFrameUpdateListener {
        void onAnimationFrame(float currentValue);
    }

    AnimatorCompat() {
    }

    public abstract void cancel();

    public abstract boolean isRunning();

    public abstract void setDuration(int progressAnimationDuration);

    public abstract void start();

    public static AnimatorCompat create(float start, float end, AnimationFrameUpdateListener listener) {
        return new AnimatorCompatV11(start, end, listener);
    }

}
