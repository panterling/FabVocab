package uk.co.cdevelop.fabvocab.Views;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;

import uk.co.cdevelop.fabvocab.R;

/**
 * Created by Chris on 06/03/2017.
 */

public class PronounciationPlayerView  extends LinearLayout {

    private Button btnPlay;
    private String audioUrl;
    private ImageView ivState;

    public PronounciationPlayerView(Context context) {
        super(context);
        init();
    }

    public PronounciationPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PronounciationPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PronounciationPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.pronounciationplayer, this);

        audioUrl = "";

        btnPlay = (Button) findViewById(R.id.btn_play);
        ivState = (ImageView) findViewById(R.id.iv_state);

        reset();





        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ivState.setImageResource(android.R.drawable.ic_menu_help);
                ivState.animate().alpha(1.0f);
                ivState.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));

                btnPlay.setEnabled(false);
                btnPlay.setAlpha(0.5f);


                //TODO: Redundant thread?
                Thread threadPlayAudioPronounciation = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MediaPlayer mp = new MediaPlayer();
                            mp.setDataSource(getContext(), Uri.parse(audioUrl));
                            mp.prepareAsync();
                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    ivState.clearAnimation();
                                    ivState.setImageResource(android.R.drawable.ic_media_play);
                                    mp.start();
                                }
                            });

                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    ivState.animate().alpha(0.0f);
                                    mp.release();
                                    btnPlay.setEnabled(true);
                                    btnPlay.setAlpha(1.0f);
                                }
                            });

                        } catch (IOException e) {
                            ivState.clearAnimation();
                            ivState.setImageResource(android.R.drawable.ic_delete);
                            //ivState.animate().alpha(0.0f);

                        }
                    }
                });

                threadPlayAudioPronounciation.run();
            }
        });


    }

    public void giveSource(String sourceUrl) {

        // Tidy up anything currently going on with the MediaPlayer


        btnPlay.setEnabled(true);
        btnPlay.setAlpha(1.0f);
        audioUrl = sourceUrl;


        // Do we have a source already that works?

        // IS this new source valid?
            // Exists?
            // reasonable filesize?

                    // Activate the playback button

    }

    public void reset() {
        btnPlay.setEnabled(false);
        btnPlay.setAlpha(0.5f);
        ivState.setAlpha(0.0f);
    }
}
