package esw.peeplotech.peerrecords.util;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import esw.peeplotech.peerrecords.R;

public class BindingAdapters {

    @BindingAdapter("android:imageURL")
    public static void setImageURL(ImageView imageView, String URL) {
        try {

            if (URL == null || TextUtils.isEmpty(URL)){

                imageView.setImageResource(R.drawable.ic_avatar);

            } else {

                imageView.setAlpha(0f);
                Picasso.get()
                        .load(Uri.parse(URL))
                        .noFade()
                        .placeholder(R.drawable.ic_avatar)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                imageView.animate()
                                        .setDuration(700)
                                        .alpha(1f)
                                        .start();
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

            }

        } catch (Exception e){

        }
    }

}
