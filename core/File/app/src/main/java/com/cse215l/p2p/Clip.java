package com.cse215l.p2p;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Clip {
    public static String getClipboardText(Context context) {
       
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

    
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                
                CharSequence clipboardText = clipData.getItemAt(0).getText();
                return clipboardText != null ? clipboardText.toString() : "";
            }
        }
        return ""; 
    }
}
