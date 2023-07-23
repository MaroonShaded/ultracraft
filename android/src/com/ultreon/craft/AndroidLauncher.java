package com.ultreon.craft;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;

public class AndroidLauncher extends AndroidApplication {
    @Override
    public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
        return new AsynchronousAndroidAudio(context, config);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use the full display, even if we have a device with a notch
        Window applicationWindow = this.getApplicationWindow();
        WindowManager.LayoutParams attrib = applicationWindow.getAttributes();
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        GamePlatform.instance = new AndroidPlatform(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.useGL30 = true;
        this.initialize(new GameLibGDXWrapper(new String[]{}), config);

    }
}
