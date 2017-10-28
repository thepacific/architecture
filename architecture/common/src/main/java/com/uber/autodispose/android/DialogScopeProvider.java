/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.autodispose.android;

import static com.uber.autodispose.android.ViewLifecycleEvent.DETACH;

import android.app.Dialog;
import com.uber.autodispose.LifecycleScopeProvider;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * A {@link LifecycleScopeProvider} that can provide scoping for Android {@link Dialog} classes.
 * <p>
 * <pre><code>
 *   AutoDispose.with(DialogScopeProvider.from(dialog));
 * </code></pre>
 */
public class DialogScopeProvider implements LifecycleScopeProvider<ViewLifecycleEvent> {

  private final LifecycleScopeProvider lifecycleScopeProvider;
  private final Dialog dialog;

  /**
   * Creates a {@link LifecycleScopeProvider} for Android Dialog.
   *
   * @param dialog the dialog to scope for
   * @return a {@link LifecycleScopeProvider} against this dialog.
   */
  public static DialogScopeProvider from(Dialog dialog) {
    if (dialog == null) {
      throw new NullPointerException("dialog == null");
    }
    return new DialogScopeProvider(dialog);
  }

  private DialogScopeProvider(final Dialog dialog) {
    this.dialog = dialog;
    this.lifecycleScopeProvider = ViewScopeProvider.from(dialog.getWindow().getDecorView());
  }

  @Override
  public Observable<ViewLifecycleEvent> lifecycle() {
    return lifecycleScopeProvider.lifecycle();
  }

  @Override
  public Function<ViewLifecycleEvent, ViewLifecycleEvent> correspondingEvents() {
    return lifecycleScopeProvider.correspondingEvents();
  }

  @Override
  public ViewLifecycleEvent peekLifecycle() {
    if (dialog.isShowing() || lifecycleScopeProvider.peekLifecycle() == ViewLifecycleEvent.ATTACH) {
      return ViewLifecycleEvent.ATTACH;
    }
    return DETACH;
  }
}
