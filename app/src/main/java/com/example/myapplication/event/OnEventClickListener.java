package com.example.myapplication.event;

import android.view.View;

import com.example.myapplication.model.Content;

public interface OnEventClickListener {
    void onEventClick(View view, Content mContent);
}
