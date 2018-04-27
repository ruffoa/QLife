package engsoc.qlife.Matchers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import engsoc.qlife.R;

public class DrawableMatcher extends TypeSafeMatcher<View> {
    private final int mExpectedId;
    private final Integer mExpectedTint;
    private String mResourceName;

    DrawableMatcher(int id, Integer tint) {
        super(View.class);
        mExpectedId = id;
        mExpectedTint = tint;
    }

    @Override
    protected boolean matchesSafely(View target) {
        if (!(target instanceof ImageView)) {
            return false;
        }
        ImageView imageView = (ImageView) target;
        if (mExpectedId < 0) {
            return imageView.getDrawable() == null;
        }
        Resources resources = target.getContext().getResources();
        Drawable expectedDrawable = resources.getDrawable(mExpectedId);
        mResourceName = resources.getResourceEntryName(mExpectedId);
        if (expectedDrawable == null) {
            return false;
        }

        Drawable actualDrawable = imageView.getDrawable();
        if (actualDrawable instanceof VectorDrawable) {
            return vectorDrawableToBitmap((VectorDrawable) expectedDrawable)
                    .sameAs(vectorDrawableToBitmap((VectorDrawable) actualDrawable));
        } else {
            Bitmap bitmap = getBitmap(imageView.getDrawable());
            Bitmap otherBitmap = getBitmap(expectedDrawable);
            return bitmap.sameAs(otherBitmap);
        }
    }

    private Bitmap vectorDrawableToBitmap(VectorDrawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        if (mExpectedTint != null && mExpectedTint > 0) {
            Paint paint = new Paint();
            paint.setColorFilter(new PorterDuffColorFilter(mExpectedTint, PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, 0, 0, paint);
        } else {
            drawable.draw(canvas);
        }
        return bitmap;
    }


    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with drawable from resource id: ");
        description.appendValue(mExpectedId);
        if (mResourceName != null) {
            description.appendText("[");
            description.appendText(mResourceName);
            description.appendText("]");
        }
    }
}
