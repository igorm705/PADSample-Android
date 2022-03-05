/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.padsample;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

public class StickerApplication extends Application {

    @Override
    public void onCreate() {

        Log.i("RecyclerView", "class StickerApplication, " +
                " public void onCreate() ");

        super.onCreate();
        Fresco.initialize(this);
    }
}
