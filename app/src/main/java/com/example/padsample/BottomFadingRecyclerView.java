/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.padsample;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class BottomFadingRecyclerView extends RecyclerView {

    public BottomFadingRecyclerView(Context context) {
        super(context);
        Log.i("RecyclerView", "class BottomFadingRecyclerView, " +
                "public BottomFadingRecyclerView(Context context)");
    }

    public BottomFadingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i("RecyclerView", "class BottomFadingRecyclerView, " +
                "public BottomFadingRecyclerView(Context context, @Nullable AttributeSet attrs) ");
    }

    public BottomFadingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i("RecyclerView", "class BottomFadingRecyclerView, " +
                " public BottomFadingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)");
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        Log.i("RecyclerView", "class BottomFadingRecyclerView, " +
                "protected float getTopFadingEdgeStrength()");
        return 0.0f;
    }
}
