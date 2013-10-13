package com.espian.showcaseview.drawing;

import com.espian.showcaseview.ShowcaseView;

import android.graphics.Canvas;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * Draws the text as required by the ShowcaseView
 */
public class TextDrawerImpl implements TextDrawer {

    private final TextPaint mPaintTitle;
    private final TextPaint mPaintDetail;

    private CharSequence mTitle, mDetails;
    private float mDensityScale;
    private float[] mBestTextPosition = new float[3];
    private DynamicLayout mDynamicTitleLayout;
    private DynamicLayout mDynamicDetailLayout;

    public TextDrawerImpl(float densityScale) {
        mDensityScale = densityScale;

        mPaintTitle = new TextPaint();
        mPaintTitle.setAntiAlias(true);

        mPaintDetail = new TextPaint();
        mPaintDetail.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas, boolean hasPositionChanged) {
        if (shouldDrawText()) {
            float[] textPosition = getBestTextPosition();

            if (!TextUtils.isEmpty(mTitle)) {
                canvas.save();
                if (hasPositionChanged) {
                    mDynamicTitleLayout = new DynamicLayout(mTitle, mPaintTitle,
                            (int) textPosition[2], Layout.Alignment.ALIGN_NORMAL,
                            1.0f, 1.0f, true);
                }
                canvas.translate(textPosition[0], textPosition[1] - textPosition[0]);
                mDynamicTitleLayout.draw(canvas);
                canvas.restore();
            }

            if (!TextUtils.isEmpty(mDetails)) {
                canvas.save();
                if (hasPositionChanged) {
                    mDynamicDetailLayout = new DynamicLayout(mDetails, mPaintDetail,
                            ((Number) textPosition[2]).intValue(),
                            Layout.Alignment.ALIGN_NORMAL,
                            1.2f, 1.0f, true);
                }
                canvas.translate(textPosition[0], textPosition[1] + 12 * mDensityScale + (
                        mDynamicTitleLayout.getLineBottom(mDynamicTitleLayout.getLineCount() - 1)
                                - mDynamicTitleLayout.getLineBottom(0)));
                mDynamicDetailLayout.draw(canvas);
                canvas.restore();

            }
        }
    }

    @Override
    public void setDetails(CharSequence details) {
        mDetails = details;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    /**
     * Calculates the best place to position text
     *
     * @param canvasW width of the screen
     * @param canvasH height of the screen
     */
    @Override
    public void calculateTextPosition(int canvasW, int canvasH, ShowcaseView showcaseView) {

        //if the width isn't much bigger than the voided area, just consider top & bottom
        float spaceTop = showcaseView.getVoidedArea().top;
        float spaceBottom = canvasH - showcaseView.getVoidedArea().bottom
                - 64 * mDensityScale; //64dip considers the OK button
        //float spaceLeft = voidedArea.left;
        //float spaceRight = canvasW - voidedArea.right;

        mBestTextPosition[0] = 24 * mDensityScale;
        mBestTextPosition[1] = spaceTop > spaceBottom ? 128 * mDensityScale
                : 24 * mDensityScale + showcaseView.getVoidedArea().bottom;
        mBestTextPosition[2] = canvasW - 48 * mDensityScale;
        //TODO: currently only considers above or below showcase, deal with left or right

    }

    public float[] getBestTextPosition() {
        return mBestTextPosition;
    }

    public boolean shouldDrawText() {
        return !TextUtils.isEmpty(mTitle) || !TextUtils.isEmpty(mDetails);
    }
}