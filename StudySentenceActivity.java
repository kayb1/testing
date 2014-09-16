import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.diotek.dhwr.madeleine.Language;
import com.weaversmind.data.Define;
import com.weaversmind.data.ResManager;
import com.weaversmind.data.TutorialData;
import com.weaversmind.data.WMAnimation;
import com.weaversmind.lib.ui.CustomizeTextView;
import com.weaversmind.lib.ui.ExtendString;
import com.weaversmind.widget.AniMarkingView;
import com.weaversmind.widget.AniPointView;
import com.weaversmind.widget.CustomButtonView;
import com.weaversmind.widget.QuizBaseView;
import com.weaversmind.widget.SentenceStudyView;
import com.weaversmind.widget.TextScriptView;
import com.weaversmind.widget.TextStylableDragView;

import java.util.Vector;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class StudySentenceActivity extends StudyBaseActivity {
    private static final String TAG = StudySentenceActivity.class.getSimpleName();

    private static final int[] ID_IMAGE_DIFFICULTY = {R.id.imgDifficulty1, R.id.imgDifficulty2,
            R.id.imgDifficulty3, R.id.imgDifficulty4, R.id.imgDifficulty5};

    // /////////////////////// Define ///////////////////////////////////////////////
    public static final int STEP_STUDY1 = 0;
    public static final int STATE_STUDY1_PREPARE = 0;
    public static final int STATE_STUDY1_SHOW1 = 1;
    public static final int STATE_STUDY1_SHOW2 = 2;
    public static final int STATE_STUDY1_WAIT = 3;
    public static final int STATE_STUDY1_DRAG = 4;
    public static final int STATE_STUDY1_RESULT = 5;
    public static final int STATE_STUDY1_FINISH = 6;

    public static final int STEP_STUDY2 = 1;
    public static final int STATE_STUDY2_GUIDE_START = 0;
    public static final int STATE_STUDY2_SHOW = 1;
    public static final int STATE_STUDY2_WAIT = 2;
    public static final int STATE_STUDY2_RESULT = 3;
    public static final int STATE_STUDY2_FINISH = 4;

    public static final int STEP_STUDY3 = 2;
    public static final int STATE_STUDY3_GUIDE_START = 0;
    public static final int STATE_STUDY3_SHOW = 1;
    public static final int STATE_STUDY3_WAIT = 2;
    public static final int STATE_STUDY3_RESULT = 3;
    public static final int STATE_STUDY3_FINISH = 4;

    private CustomButtonView mBtnNext = null;
    private CustomButtonView mBtnPrev = null;

    //// 학습관련
    private RelativeLayout mLyStudy = null;
    private LinearLayout mLyDifficulty = null;
    private LinearLayout mLyDifficultySub1 = null;
    private LinearLayout mLyDifficultySub2 = null;
    private ImageView[] mImgDifficulty = new ImageView[ID_IMAGE_DIFFICULTY.length];
    private TextStylableDragView mTxtStudyQuestion = null;
    private ImageView mImgStudyQuestion = null;
    private RelativeLayout mLyStudyAnswer = null;
    private ImageView mImgStudyAnswerBg = null;
    private TextScriptView mTxtAnswerInstruction = null;
    private RelativeLayout mLyDrag = null;
    private TextScriptView mTxtDrag = null;
    private ImageView mImgDragFinger = null;
    private TextView mTxtDragAnswer = null;
    private ScrollView mLySentenceStudyScroll = null;
    private SentenceStudyView mSentenceStudy2View = null;
    private SentenceStudyView mSentenceStudy3View = null;
    private ImageView mImgStudyStepCheck1 = null;
    private ImageView mImgStudyStepCheck2 = null;
    private ImageView mImgStudyStepCheck3 = null;
    private CustomButtonView mBtnStudyStep1 = null;
    private CustomButtonView mBtnStudyStep2 = null;
    private CustomButtonView mBtnStudyStep3 = null;
    private DragTouchListener mDragTouchListener = new DragTouchListener();
    private DragListener mDragListner = new DragListener();
    private boolean mIsDragEnd = false;
    private int mDragState = 0;
    ////

    private AniMarkingView mAniMarkingView = null;
    private AniPointView mAniPointView = null;
    private SparseIntArray mScoreList = new SparseIntArray();

    private int mFlowStudy1;
    private int mFlowStudy3;

    private int mLevel;
    private int mEpisode;
    private int mChapter;

    private boolean mIsAnswer = false;
    private boolean mIsMarking = false;
    private int mRetryNum;
    private int mX, mY;

    private static final boolean IS_JUMP_STUTY1 = mIsDebugMode && false;
    private static final boolean IS_JUMP_STUTY2 = mIsDebugMode && false;
    private static final boolean IS_JUMP_STUTY3 = mIsDebugMode && false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_sentence);

        mLevel = gData.getStudyLevel();
        mEpisode = gData.getStudyEpisode();
        mChapter = gData.getStudyChapter();

        mAniMarkingView = (AniMarkingView) findViewById(R.id.aniMarkingView);
        mAniMarkingView.setMarkingCompleteListener(new AniMarkingView.MarkingComplete() {
            @Override
            public void completeAni() {
                mAniMarkingView.setVisibility(View.INVISIBLE);
                mIsMarking = false;
            }
        });

        //// Study
        mLyStudy = (RelativeLayout) findViewById(R.id.layoutSentenceStudy);
        mLyStudy.setVisibility(View.GONE);
        mLyDifficulty = (LinearLayout) mLyStudy.findViewById(R.id.layoutDifficulty);
        mLyDifficultySub1 = (LinearLayout) mLyDifficulty.findViewById(R.id.layoutDifficultySub1);
        mLyDifficultySub2 = (LinearLayout) mLyDifficulty.findViewById(R.id.layoutDifficultySub2);
        for (int i = 0; i < ID_IMAGE_DIFFICULTY.length; i++) {
            mImgDifficulty[i] = (ImageView) mLyDifficulty.findViewById(ID_IMAGE_DIFFICULTY[i]);
        }
        mTxtStudyQuestion = (TextStylableDragView) mLyStudy.findViewById(R.id.txtStudyQuestion);
        mTxtStudyQuestion.setTypeface(getFont(FontType.CREGOTHIC_M));
        mTxtStudyQuestion.setTextSize(26);
        mTxtStudyQuestion.setLineSpacing(15, 1);

        mImgStudyQuestion = (ImageView) mLyStudy.findViewById(R.id.imgStudyQuestion);
        mLyStudyAnswer = (RelativeLayout) mLyStudy.findViewById(R.id.layoutSentenceStudyAnswer);
        mLyStudyAnswer.setVisibility(View.INVISIBLE);
        mImgStudyAnswerBg = (ImageView) mLyStudyAnswer.findViewById(R.id.imgSentenceStudyAnswerBg);
        mTxtAnswerInstruction = (TextScriptView) mLyStudy.findViewById(R.id.txtInstruction);
        mTxtAnswerInstruction.setTypeface(getFont(FontType.CREGOTHIC_M));
        mLySentenceStudyScroll = (ScrollView) mLyStudy.findViewById(R.id.layoutStudyScrollView);
        mLySentenceStudyScroll.setVerticalFadingEdgeEnabled(true);
        mLySentenceStudyScroll.setFadingEdgeLength(20);
        mLySentenceStudyScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mLySentenceStudyScroll.getScrollY();
                if (scrollY < mTxtDragAnswer.getY() + mTxtDragAnswer.getHeight() / 2) {
                    mImgStudyStepCheck1.setVisibility(View.VISIBLE);
                    mImgStudyStepCheck2.setVisibility(View.INVISIBLE);
                    mImgStudyStepCheck3.setVisibility(View.INVISIBLE);

                    int stepID = getCurStepID();
                    if (stepID == STEP_STUDY1) {
                        setImageButton(mBtnStudyStep1, R.drawable.bt_step_1_1_touch, R.drawable.bt_step_1_1_touch);
                        setImageButton(mBtnStudyStep2, R.drawable.bt_step_2_touch, R.drawable.bt_step_2_touch);
                        setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_touch, R.drawable.bt_step_3_touch);
                    } else if (stepID == STEP_STUDY2) {
                        setImageButton(mBtnStudyStep1, R.drawable.bt_step_1_1_touch, R.drawable.bt_step_1_1_touch);
                        setImageButton(mBtnStudyStep2, R.drawable.bt_step_2, R.drawable.bt_step_2_1_touch);
                        setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_touch, R.drawable.bt_step_3_touch);
                    } else if (stepID == STEP_STUDY3) {
                        setImageButton(mBtnStudyStep1, R.drawable.bt_step_1_1_touch, R.drawable.bt_step_1_1_touch);
                        setImageButton(mBtnStudyStep2, R.drawable.bt_step_2, R.drawable.bt_step_2_1_touch);
                        setImageButton(mBtnStudyStep3, R.drawable.bt_step_3, R.drawable.bt_step_3_1_touch);
                    }
                } else if (scrollY < mSentenceStudy2View.getY() + mSentenceStudy2View.getHeight() / 2) {
                    int stepID = getCurStepID();
                    mImgStudyStepCheck1.setVisibility(View.INVISIBLE);
                    mImgStudyStepCheck2.setVisibility(View.VISIBLE);
                    mImgStudyStepCheck3.setVisibility(View.INVISIBLE);

                    if (stepID == STEP_STUDY2) {
                        setImageButton(mBtnStudyStep1, R.drawable.bt_step_1, R.drawable.bt_step_1_1_touch);
                        setImageButton(mBtnStudyStep2, R.drawable.bt_step_2_1_touch, R.drawable.bt_step_2_1_touch);
                        setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_touch, R.drawable.bt_step_3_touch);
                    } else if (stepID == STEP_STUDY3) {
                        setImageButton(mBtnStudyStep1, R.drawable.bt_step_1, R.drawable.bt_step_1_1_touch);
                        setImageButton(mBtnStudyStep2, R.drawable.bt_step_2_1_touch, R.drawable.bt_step_2_1_touch);
                        setImageButton(mBtnStudyStep3, R.drawable.bt_step_3, R.drawable.bt_step_3_1_touch);
                    }
                } else {
                    int stepID = getCurStepID();
                    mImgStudyStepCheck1.setVisibility(View.INVISIBLE);
                    mImgStudyStepCheck2.setVisibility(View.INVISIBLE);
                    mImgStudyStepCheck3.setVisibility(View.VISIBLE);

                    if (stepID == STEP_STUDY3) {
                        setImageButton(mBtnStudyStep1, R.drawable.bt_step_1, R.drawable.bt_step_1_1_touch);
                        setImageButton(mBtnStudyStep2, R.drawable.bt_step_2, R.drawable.bt_step_2_1_touch);
                        setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_1_touch, R.drawable.bt_step_3_1_touch);
                    }
                }
            }
        });
        mSentenceStudy2View = (SentenceStudyView) mLyStudy.findViewById(R.id.sentenceStudy2View);
        mSentenceStudy2View.setVisibility(View.INVISIBLE);
        mSentenceStudy2View.setOnStudyListener(mOnStudyListener);
        mSentenceStudy3View = (SentenceStudyView) mLyStudy.findViewById(R.id.sentenceStudy3View);
        mSentenceStudy3View.setVisibility(View.INVISIBLE);
        mSentenceStudy3View.setOnStudyListener(mOnStudyListener);
        mLyDrag = (RelativeLayout) mLyStudy.findViewById(R.id.layoutDrag);
        mLyDrag.setOnDragListener(mDragListner);
        mLyDrag.setVisibility(View.INVISIBLE);
        mTxtDrag = (TextScriptView) mLyDrag.findViewById(R.id.txtDragBg);
        mTxtDrag.setTypeface(getFont(FontType.CREGOTHIC_M));
        mImgDragFinger = (ImageView) mLyStudy.findViewById(R.id.imgDragFinger);
        mImgDragFinger.setVisibility(View.INVISIBLE);
        mTxtDragAnswer = (TextView) mLyStudy.findViewById(R.id.txtDragAnswer);
        mTxtDragAnswer.setTypeface(getFont(FontType.CREGOTHIC_M));
        mTxtDragAnswer.setVisibility(View.INVISIBLE);

        mImgStudyStepCheck1 = (ImageView) mLyStudy.findViewById(R.id.imgStepCheck1);
        mImgStudyStepCheck2 = (ImageView) mLyStudy.findViewById(R.id.imgStepCheck2);
        mImgStudyStepCheck3 = (ImageView) mLyStudy.findViewById(R.id.imgStepCheck3);
        mBtnStudyStep1 = (CustomButtonView) mLyStudy.findViewById(R.id.imgStep1);
        mBtnStudyStep1.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnStudyStep1);
        mBtnStudyStep2 = (CustomButtonView) mLyStudy.findViewById(R.id.imgStep2);
        mBtnStudyStep2.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnStudyStep2);
        mBtnStudyStep3 = (CustomButtonView) mLyStudy.findViewById(R.id.imgStep3);
        mBtnStudyStep3.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnStudyStep3);
        ////

        mAniPointView = (AniPointView) findViewById(R.id.aniPointView);
        mAniPointView.setVisibility(View.INVISIBLE);

        createControlPanel(R.layout.panel_control_sentence);
        setWordList(Define.WLT_SENTENCES);

        mWordNum = getIntentParam(0);
    }

    @Override
    protected void onLayoutCompleted() {
        super.onLayoutCompleted();

        showUserOption(false);
        setGuideAniMode(TextScriptView.MODE_SHOW_NOANI);
        setTextGuide("<font color='#595959'>응용탄탄학습:" + mWordList.get(0).episodeTitle + "</font>");
        showGuideNoAni();

        gData.updateStudySlot(Define.STUDY_SENTENCE);
    }

    @Override
    protected void onOrganizeControlPanel(View v) {
        mBtnNext = (CustomButtonView) v.findViewById(R.id.btnNext);
        mBtnNext.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnNext);

        mBtnPrev = (CustomButtonView) v.findViewById(R.id.btnPrev);
        mBtnPrev.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnPrev);
    }

    @Override
    protected void onCreateSteps() {
        try {
            makeStep(STEP_STUDY1, "학습1");
            makeStep(STEP_STUDY2, "학습2");
            makeStep(STEP_STUDY3, "학습3");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onOrganizeFlow() {
        if (!IS_JUMP_STUTY1) {
            mFlowStudy1 = pushFlow(STEP_STUDY1);
        }
        if (!IS_JUMP_STUTY2) {
            pushFlow(STEP_STUDY2);
        }
        if (!IS_JUMP_STUTY3) {
            pushFlow(STEP_STUDY3);
        }
    }

    @Override
    protected void initStep(int nFlow, int stepID, Step step) {
        if (stepID == STEP_STUDY1) {
            turnOnKeepScreen();

            setViewVisible(mBtnPrev, false);
            setViewVisible(mBtnNext, true);
            hideControlPanelIFSticky();
            hidePanelHideImgDelay();
            hideScratchPad();

            mImgStudyStepCheck1.setVisibility(View.INVISIBLE);
            mImgStudyStepCheck2.setVisibility(View.INVISIBLE);
            mImgStudyStepCheck3.setVisibility(View.INVISIBLE);
            mBtnStudyStep1.setVisibility(View.VISIBLE);
            mBtnStudyStep2.setVisibility(View.VISIBLE);
            mBtnStudyStep3.setVisibility(View.VISIBLE);
            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1_1_touch, R.drawable.bt_step_1_1_touch);
            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2_touch, R.drawable.bt_step_2_touch);
            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_touch, R.drawable.bt_step_3_touch);
            mBtnStudyStep1.setEnabled(false);
            mBtnStudyStep2.setEnabled(false);
            mBtnStudyStep3.setEnabled(false);
            mSentenceStudy2View.setVisibility(View.INVISIBLE);
            mSentenceStudy3View.setVisibility(View.INVISIBLE);

            for (int i = 0; i < mImgDifficulty.length; i++) {
                mImgDifficulty[i].setVisibility(View.INVISIBLE);
            }
            mTxtStudyQuestion.setVisibility(View.INVISIBLE);
            mTxtAnswerInstruction.setVisibility(View.INVISIBLE);
            mLyStudyAnswer.setVisibility(View.INVISIBLE);
            mImgStudyQuestion.setVisibility(View.INVISIBLE);

            if (mWordList.get(mWordNum).quizImg > 0) {
                mImgStudyAnswerBg.getLayoutParams().height = 360;
                Bitmap bmp = gRes.getBitmapByWordid(mWordList.get(mWordNum).quizImg);
                if (bmp != null) {
                    if (bmp.getWidth() < 300) {
                        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) mTxtStudyQuestion.getLayoutParams();
                        lp2.width = 560;
                        lp2.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                        lp2.topMargin = 14;
                        mTxtStudyQuestion.setLayoutParams(lp2);

                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mImgStudyQuestion.getLayoutParams();
                        lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                        lp.width = 272;
                        lp.height = 225;
                        lp.leftMargin = 640;
                        lp.topMargin = 10;
                        mImgStudyQuestion.setLayoutParams(lp);
                    } else {
                        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) mTxtStudyQuestion.getLayoutParams();
                        lp2.width = 900;
                        lp2.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                        lp2.topMargin = 0;
                        mTxtStudyQuestion.setLayoutParams(lp2);

                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mImgStudyQuestion.getLayoutParams();
                        lp.width = 784;
                        lp.height = 180;
                        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        lp.topMargin = 73; //56;
                        mImgStudyQuestion.setLayoutParams(lp);
                    }
                    mImgStudyQuestion.setImageBitmap(bmp);
                }

                ((RelativeLayout.LayoutParams) mImgDragFinger.getLayoutParams()).topMargin = 460;
            } else {
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) mTxtStudyQuestion.getLayoutParams();
                lp2.width = 900;
                lp2.topMargin = 14;
                mTxtStudyQuestion.setLayoutParams(lp2);

                mImgStudyAnswerBg.getLayoutParams().height = 476;

                ((RelativeLayout.LayoutParams) mImgDragFinger.getLayoutParams()).topMargin = 400;
            }

            mTxtDrag.setText("문제에서 찾아 여기로 드래그 해 오세요.");
            mTxtDragAnswer.setVisibility(View.INVISIBLE);

            step.clearState();
            step.pushState(getTickByMillis(1000))
                    .pushStateMillis(2000)
                    .pushStateMillis(2000)
                    .pushStateMillis(3000)
                    .pushState(Step.WAIT_EVENT)
                    .pushState(getTickByMillis(1500))
                    .pushState(getTickByMillis(1500));
        } else if (stepID == STEP_STUDY2) {
            turnOnKeepScreen();

            showScratchPad();

            mLyDrag.setVisibility(View.INVISIBLE);
            mImgDragFinger.setVisibility(View.INVISIBLE);
            mTxtAnswerInstruction.setVisibility(View.INVISIBLE);
            mSentenceStudy2View.setVisibility(View.INVISIBLE);
            mSentenceStudy3View.setVisibility(View.INVISIBLE);
            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1, R.drawable.bt_step_1_1_touch);
            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2_1_touch, R.drawable.bt_step_2_1_touch);
            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_touch, R.drawable.bt_step_3_touch);
            mBtnStudyStep1.setEnabled(true);
            mBtnStudyStep2.setEnabled(true);
            mBtnStudyStep3.setEnabled(false);

            step.clearState();
            step.pushState(getTickByMillis(2000))
                    .pushState(1)
                    .pushState(Step.WAIT_EVENT)
                    .pushState(getTickByMillis(1500))
                    .pushState(getTickByMillis(500));
        } else if (stepID == STEP_STUDY3) {
            turnOnKeepScreen();

            hideControlPanelIFSticky();
            hidePanelHideImgDelay();

            mSentenceStudy3View.setVisibility(View.INVISIBLE);
            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1, R.drawable.bt_step_1_1_touch);
            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2, R.drawable.bt_step_2_1_touch);
            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_1_touch, R.drawable.bt_step_3_1_touch);
            mBtnStudyStep1.setEnabled(true);
            mBtnStudyStep2.setEnabled(true);
            mBtnStudyStep3.setEnabled(true);

            step.clearState();
            step.pushState(getTickByMillis(2000))
                    .pushState(1)
                    .pushState(Step.WAIT_EVENT)
                    .pushState(getTickByMillis(1500))
                    .pushState(Step.WAIT_EVENT);
        }

        super.initStep(nFlow, stepID, step);
    }

    @Override
    protected void initState(final int stepID, final int stateID) {
        super.initState(stepID, stateID);

        if (stepID == STEP_STUDY1) {
            if (stateID == STATE_STUDY1_SHOW1) {
                mLyStudy.setVisibility(View.VISIBLE);
                popQuestionIn1();
            } else if (stateID == STATE_STUDY1_SHOW2) {
                popQuestionIn2();
            } else if (stateID == STATE_STUDY1_WAIT) {
                CustomizeTextView textView = mTxtStudyQuestion.getHighLightTextView();
                mTxtStudyQuestion.getHighLightTextView().setOnTouchListener(mDragTouchListener);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ((ViewGroup) mLyStudy.getParent()).addView(textView, lp);

                mLyDrag.setVisibility(View.VISIBLE);

                mTxtDrag.setAniMode(TextScriptView.MODE_SHOW_SCRIPTING);
                mTxtDrag.setTotalDuration(600);
                mTxtDrag.setString("문제에서 찾아 여기로 드래그 해 오세요");
                mTxtDrag.setVisibility(View.VISIBLE);
                mTxtDrag.start();

                mImgBg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TranslateAnimation animation = new TranslateAnimation(-140, 0, -390, 0);
                        animation.setDuration(1800);
                        mImgDragFinger.startAnimation(animation);
                        mImgDragFinger.setVisibility(View.VISIBLE);
                        mImgDragFinger.bringToFront();
                    }
                }, 1000);
            } else if (stateID == STATE_STUDY1_DRAG) {
                mIsDragEnd = false;
                mImgBg.postDelayed(dragState, 8000);
            } else if (stateID == STATE_STUDY1_RESULT) {
                mIsDragEnd = true;
                mImgBg.removeCallbacks(dragState);
                mTxtStudyQuestion.getOriginalHighLightTextView().setDrawHighLightBackGround(true);
                mTxtStudyQuestion.getOriginalHighLightTextView().invalidate();

                if (mIsAnswer) {
                    int[] loc = new int[2];
                    mTxtDrag.getLocationOnScreen(loc);
                    mAniMarkingView.setImageType(AniMarkingView.AniType.SMALL_CORRECT);
                    mAniMarkingView.setPosition(loc[0] + mTxtDrag.getWidth() / 2, loc[1]);
                    mAniMarkingView.start();
                    mIsMarking = true;
                    gRes.playEffect(ResManager.SE_MARK_SUCCESS);
                } else {
                    gRes.playEffect(ResManager.SE_MARK_WRONG);
                }
            } else if (stateID == STATE_STUDY1_FINISH) {
                ((ViewGroup) mLyStudy.getParent()).removeView(mTxtStudyQuestion.getHighLightTextView());

                mAniMarkingView.dispose();

                int[] loc = new int[2];
                int[] loc2 = new int[2];
                mTxtDrag.getLocationOnScreen(loc);
                mTxtDragAnswer.getLocationOnScreen(loc2);
                TranslateAnimation animation = new TranslateAnimation(0, loc2[0] - loc[0], 0, loc2[1] - loc[1]);
                animation.setDuration(800);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mTxtDragAnswer.setVisibility(View.VISIBLE);
                        mTxtDragAnswer.setText(mTxtDrag.getText());
                        mAnimation.fadeInAnim(mTxtDragAnswer, 300);

                        mLyDrag.setVisibility(View.INVISIBLE);
                        mImgDragFinger.setVisibility(View.INVISIBLE);
                        mAnimation.fadeOutAnim(mLyDrag, 300);
                        mAnimation.start();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mTxtDrag.startAnimation(animation);
            }
        } else if (stepID == STEP_STUDY2) {
            if (stateID == STATE_STUDY2_GUIDE_START) {
                Animation ggambak = AnimationUtils.loadAnimation(StudySentenceActivity.this, R.anim.ggambbak3);
                ggambak.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mImgStudyStepCheck1.setVisibility(View.INVISIBLE);
                        mImgStudyStepCheck2.setVisibility(View.VISIBLE);
                        mImgStudyStepCheck3.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mBtnStudyStep2.startAnimation(ggambak);
            } else if (stateID == STATE_STUDY2_SHOW) {
                mSentenceStudy2View.setData("", mWordList.get(mWordNum).quizData2);
                mSentenceStudy2View.setVisibility(View.VISIBLE);

                mLySentenceStudyScroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLySentenceStudyScroll.smoothScrollTo(0, (int) mSentenceStudy2View.getY() - 10);
                    }
                }, 100);
            } else if (stateID == STATE_STUDY2_WAIT) {

            } else if (stateID == STATE_STUDY2_RESULT) {
            } else if (stateID == STATE_STUDY2_FINISH) {

            }
        } else if (stepID == STEP_STUDY3) {
            if (stateID == STATE_STUDY3_GUIDE_START) {
                Animation ggambak = AnimationUtils.loadAnimation(StudySentenceActivity.this, R.anim.ggambbak3);
                ggambak.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mImgStudyStepCheck1.setVisibility(View.INVISIBLE);
                        mImgStudyStepCheck2.setVisibility(View.INVISIBLE);
                        mImgStudyStepCheck3.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mBtnStudyStep3.startAnimation(ggambak);
            } else if (stateID == STATE_STUDY3_SHOW) {
                mSentenceStudy3View.setData("", mWordList.get(mWordNum).quizData3);
                mSentenceStudy3View.setVisibility(View.VISIBLE);

                mLySentenceStudyScroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLySentenceStudyScroll.smoothScrollTo(0, (int) mSentenceStudy3View.getY() - 10);
                    }
                }, 100);
            } else if (stateID == STATE_STUDY3_WAIT) {

            } else if (stateID == STATE_STUDY3_RESULT) {
            } else if (stateID == STATE_STUDY3_FINISH) {
                showControlPanel(true);
            }
        }
    }

    private Runnable dragState = new Runnable() {
        @Override
        public void run() {
            int stepID = getCurStepID();
            int stateID = getCurStateID();
            if (stepID == STEP_STUDY1 && stateID == STATE_STUDY1_DRAG) {
                if (mDragState == 0) {
                    Animation ani = AnimationUtils.loadAnimation(StudySentenceActivity.this, R.anim.shake2);
                    mTxtStudyQuestion.getOriginalHighLightTextView().startAnimation(ani);
                    mDragState = 1;
                    mImgBg.postDelayed(dragState, 3000);
                } else {
                    mIsAnswer = false;
                    int[] dst = new int[2];
                    mTxtDrag.getLocationOnScreen(dst);

                    mTxtStudyQuestion.getOriginalHighLightTextView().setDrawHighLightBackGround(false);
                    View v = mTxtStudyQuestion.getOriginalHighLightTextView();
                    View v2 = mTxtStudyQuestion.getHighLightTextView();
                    int[] src = new int[2];
                    v.getLocationOnScreen(src);
                    Vector<Rect> highLightRectList = mTxtStudyQuestion.getHighLightRectList();
                    if (highLightRectList != null) {
                        src[0] = src[0] + highLightRectList.get(0).left;
                        src[1] = src[1] + highLightRectList.get(0).top;
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v2.getLayoutParams();
                        lp.leftMargin = src[0];
                        lp.topMargin = src[1];
                        v2.setLayoutParams(lp);
                        mAnimation.moveXAnim(v2, 1000, 0, 0, dst[0] - src[0] + mTxtDrag.getWidth() / 2 - v2.getWidth() / 2);
                        mAnimation.moveYAnim(v2, 1000, 0, 0, dst[1] - src[1]);
                        mAnimation.fadeAnim(v2, 1000, 0, 1.0f, 0.1f);
                        mAnimation.setOnFinishListener(new WMAnimation.OnFinishListener() {
                            @Override
                            public void onFinished() {
                                CustomizeTextView textView = mTxtStudyQuestion.getHighLightTextView();
                                mTxtDrag.setText(textView.getText().toString());
                                ((ViewGroup) mLyStudy.getParent()).removeView(textView);
                                mImgBg.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!mIsDragEnd) {
                                            mIsDragEnd = true;
                                            nextState();
                                        }
                                    }
                                }, 1000);
                                mDragState = 0;
                            }
                        });
                        if (!mIsDragEnd) {
                            mAnimation.start();
                        } else {
                            mAnimation.clear();
                        }
                    }
                }
            }
        }
    };

    private void popQuestionIn1() {
        // for all children, scale in one at a time
        TimeInterpolator overshooter = new OvershootInterpolator();
        int childCount = Math.min(mWordList.get(mWordNum).quizDifficulty, ID_IMAGE_DIFFICULTY.length);
        if (childCount < 4) {
            mLyDifficultySub1.setVisibility(View.VISIBLE);
            mLyDifficultySub2.setVisibility(View.GONE);
        } else {
            mLyDifficultySub1.setVisibility(View.VISIBLE);
            mLyDifficultySub2.setVisibility(View.VISIBLE);
        }
        ObjectAnimator[] childAnims = new ObjectAnimator[childCount];

        for (int i = 0; i < childCount; ++i) {
            View child = mImgDifficulty[i];
            child.setVisibility(View.VISIBLE);
            child.setScaleX(0);
            child.setScaleY(0);
            PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
            PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(child, pvhSX, pvhSY);
            anim.setDuration(150);
            anim.setInterpolator(overshooter);
            childAnims[i] = anim;
        }

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(childAnims);
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (mWordList.get(mWordNum).quizTitle.contains("\\f{")) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTxtStudyQuestion.getLayoutParams();
                    lp.topMargin = lp.topMargin - 10;
                    mTxtStudyQuestion.setLayoutParams(lp);
                    mTxtStudyQuestion.setLineSpacing(0, 1);
                } else {
                    mTxtStudyQuestion.setLineSpacing(15, 1);
                }
                mTxtStudyQuestion.setData(mWordList.get(mWordNum).quizTitle, mWordList.get(mWordNum).quizData1);
                mTxtStudyQuestion.setVisibility(View.VISIBLE);
                mAnimation.fadeInAnim(mTxtStudyQuestion, 400, 500);
                if (mWordList.get(mWordNum).quizImg > 0) {
                    mAnimation.fadeInAnim(mImgStudyQuestion, 400, 500);
                }
                mAnimation.setOnFinishListener(new WMAnimation.OnFinishListener() {
                    @Override
                    public void onFinished() {
                        mTxtStudyQuestion.setRenderPriority(ExtendString.RENDER_PRIORITY_TEXT);
                        mTxtStudyQuestion.getOriginalHighLightTextView().setVisibility(View.VISIBLE);
                        mTxtStudyQuestion.invalidate();
                    }
                });
                mAnimation.start();
            }
        });
    }

    private void popQuestionIn2() {
        Animation moveFromBottom = AnimationUtils.loadAnimation(StudySentenceActivity.this, R.anim.move_from_bottom);
        moveFromBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBtnStudyStep1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation ggambak = AnimationUtils.loadAnimation(StudySentenceActivity.this, R.anim.ggambbak3);
                        ggambak.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mImgStudyStepCheck1.setVisibility(View.VISIBLE);
                                mImgStudyStepCheck2.setVisibility(View.INVISIBLE);
                                mImgStudyStepCheck3.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        mBtnStudyStep1.startAnimation(ggambak);
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLyStudyAnswer.setVisibility(View.VISIBLE);
        mLyStudyAnswer.startAnimation(moveFromBottom);
    }

    protected void shrinkToFit(TextView tv) {
        shrinkToFit(tv, tv.getWidth());
    }

    protected void shrinkToFit(TextView tv, int w) {
        if (tv != null) {
            Paint p = tv.getPaint();
            float width = p.measureText(tv.getText().toString());
            float size = tv.getTextSize();
            if (width > w) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size - 1.0f);
                shrinkToFit(tv, w);
            }
        }
    }

    @Override
    protected void onDestroy() {
        long elapsedSec = getElapsedTimeSec();
        if (elapsedSec >= Define.MIN_ELPASED_STUDY_TIME) {
            gData.updateStudyTimeSec(Define.STUDY_SENTENCE, elapsedSec);
        }

        mImgBg.removeCallbacks(dragState);

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Define.DIALOG_FLOW) {
            if (gData.getShowTutorial() == 1) {
                gData.tutorialIncrement(TutorialData.TYPE_START_GRAMMAR);
                if (gData.checkTutorialShow(TutorialData.TYPE_START_GRAMMAR)) {
                    showTutorialForResultWithParam(TutorialPopupActivity.class, Define.REQID_POPUP_TUTORIAL, TutorialData.TYPE_START_GRAMMAR);
                } else {
                    nextFlow();
                }
            } else {
                nextFlow();
            }
        } else if (requestCode == Define.REQID_POPUP_TUTORIAL) {
            nextFlow();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onClickButton(View v) {
        if (!mIsMarking) {
            switch (v.getId()) {
                case R.id.btnPrev:
                    onBtnPrevStep();
                    break;
                case R.id.btnNext:
                    onBtnNextStep();
                    break;
                case R.id.imgStep1: {
                    int stepID = getCurStepID();
                    if (stepID == STEP_STUDY1 || stepID == STEP_STUDY2 || stepID == STEP_STUDY3) {
                        mImgStudyStepCheck1.setVisibility(View.VISIBLE);
                        mImgStudyStepCheck2.setVisibility(View.INVISIBLE);
                        mImgStudyStepCheck3.setVisibility(View.INVISIBLE);

                        if (stepID == STEP_STUDY1) {
                            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1_1_touch, R.drawable.bt_step_1_1_touch);
                            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2_touch, R.drawable.bt_step_2_touch);
                            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_touch, R.drawable.bt_step_3_touch);
                        } else if (stepID == STEP_STUDY2) {
                            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1_1_touch, R.drawable.bt_step_1_1_touch);
                            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2, R.drawable.bt_step_2_1_touch);
                            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_touch, R.drawable.bt_step_3_touch);
                        } else if (stepID == STEP_STUDY3) {
                            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1_1_touch, R.drawable.bt_step_1_1_touch);
                            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2, R.drawable.bt_step_2_1_touch);
                            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3, R.drawable.bt_step_3_1_touch);
                        }

                        mLySentenceStudyScroll.smoothScrollTo(0, 0);
                    }
                    break;
                }
                case R.id.imgStep2: {
                    int stepID = getCurStepID();
                    if (stepID == STEP_STUDY2 || stepID == STEP_STUDY3) {
                        mImgStudyStepCheck1.setVisibility(View.INVISIBLE);
                        mImgStudyStepCheck2.setVisibility(View.VISIBLE);
                        mImgStudyStepCheck3.setVisibility(View.INVISIBLE);

                        if (stepID == STEP_STUDY2) {
                            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1, R.drawable.bt_step_1_1_touch);
                            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2_1_touch, R.drawable.bt_step_2_1_touch);
                            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_touch, R.drawable.bt_step_3_touch);
                        } else if (stepID == STEP_STUDY3) {
                            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1, R.drawable.bt_step_1_1_touch);
                            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2_1_touch, R.drawable.bt_step_2_1_touch);
                            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3, R.drawable.bt_step_3_1_touch);
                        }

                        mLySentenceStudyScroll.smoothScrollTo(0, (int) mSentenceStudy2View.getY() - 10);
                    }
                    break;
                }
                case R.id.imgStep3: {
                    int stepID = getCurStepID();
                    int stateID = getCurStateID();
                    if (stepID == STEP_STUDY3) {
                        mImgStudyStepCheck1.setVisibility(View.INVISIBLE);
                        mImgStudyStepCheck2.setVisibility(View.INVISIBLE);
                        mImgStudyStepCheck3.setVisibility(View.VISIBLE);

                        if (stepID == STEP_STUDY3) {
                            setImageButton(mBtnStudyStep1, R.drawable.bt_step_1, R.drawable.bt_step_1_1_touch);
                            setImageButton(mBtnStudyStep2, R.drawable.bt_step_2, R.drawable.bt_step_2_1_touch);
                            setImageButton(mBtnStudyStep3, R.drawable.bt_step_3_1_touch, R.drawable.bt_step_3_1_touch);
                        }

                        mLySentenceStudyScroll.smoothScrollTo(0, (int) mSentenceStudy3View.getY() - 10);
                    }
                    break;
                }
            }
        }
        super.onClickButton(v);
    }

    @Override
    protected void onTouchUp(View v, MotionEvent event) {
        super.onTouchUp(v, event);
        if (v == mImgBg) {
            if (!mIsMarking) {
                int stepID = getCurStepID();
                if (stepID == STEP_STUDY2) {
                    mSentenceStudy2View.setQuestionInput("");
                } else if (stepID == STEP_STUDY3) {
                    mSentenceStudy3View.setQuestionInput("");
                }
            }
        }
    }

    @Override
    protected boolean onDragRightToLeft() {
        return true; // for consume event. don't return false
    }

    @Override
    protected boolean onDragLeftToRight() {
        return true; // for consume event. don't return false
    }

    @Override
    protected void setFlowJump() {
        setFlowJump(false);
    }

    protected void setFlowJump(boolean prev) {
        if (isFlowJumping()) {
            return;
        }

        int stepID = getCurStepID();

        if (prev) {

        } else {
            if (stepID == STEP_STUDY3) {
                mWordNum++;
                if (mWordNum < mWordList.size()) {
                    moveActivityWithParam(StepPopupActivity.class, Define.NONE, true, Define.STUDY_SENTENCE, mWordNum);
                } else {
                    moveActivityWithParam(StudyResultActivity.class, Define.NONE, true, Define.STUDY_SENTENCE);
                }
            }
        }
    }

    protected void onBtnPrevStep() {
        if (isFlowJumping()) {
            return;
        }

        setFlowJump(true);
    }

    protected void onBtnNextStep() {
        if (isFlowJumping()) {
            return;
        }

        setFlowJump();
    }

    @Override
    protected void setWriteResult(String text) {
        super.setWriteResult(text);

        int stepID = getCurStepID();
        if (stepID == STEP_STUDY2) {
            mSentenceStudy2View.setQuestionInputConfirm(text);
        } else if (stepID == STEP_STUDY3) {
            mSentenceStudy3View.setQuestionInputConfirm(text);
        }
    }

    @Override
    protected void updateWriteText(String text) {
        super.updateWriteText(text);

        if (!mIsMarking) {
            int stepID = getCurStepID();
            if (stepID == STEP_STUDY2) {
                mSentenceStudy2View.setQuestionInput(text);
            } else if (stepID == STEP_STUDY3) {
                mSentenceStudy3View.setQuestionInput(text);
            }
        }
    }

    @Override
    protected void jumpToNextStepDebug() {
        super.jumpToNextStepDebug();

        mWordNum++;
        if (mWordNum < mWordList.size()) {
            moveActivityWithParam(StepPopupActivity.class, Define.NONE, true, Define.STUDY_SENTENCE, mWordNum);
        } else {
            moveActivityWithParam(StudyResultActivity.class, Define.NONE, true, Define.STUDY_SENTENCE);
        }
    }

    @Override
    protected void onScratchPadTouch() {
        super.onScratchPadTouch();

        if (mTxtAnswerInstruction != null) {
            mTxtAnswerInstruction.setVisibility(View.INVISIBLE);
        }
    }

    private QuizBaseView.OnStudyListener mOnStudyListener = new QuizBaseView.OnStudyListener() {
        @Override
        public void onTouch(Rect r, MotionEvent event) {

        }

        @Override
        public void finishStudy(boolean isMarking, boolean isAnswer, int retryCount, int hintCount, int x, int y) {
            nextState();
        }

        @Override
        public void onEmptyTouchZone() {
            setWriteMode(false);
        }

        @Override
        public void onBlankStart(String answer, int inputType) { // inputType 은 SentenceStudyView enum INPUT_TYPE
            if (inputType == SentenceStudyView.INPUT_TYPE.NUMBER.value()) {
                setWriteLanguage(Language.TYPE_NUMERIC);
            } else if (inputType == SentenceStudyView.INPUT_TYPE.NUMBER_SYMBOL.value()) {
                setWriteLanguage(Language.TYPE_NUMERIC, Language.TYPE_SYMBOL);
            } else if (inputType == SentenceStudyView.INPUT_TYPE.CHAR.value()) {
                setWriteLanguage(Language.TYPE_KOREAN);
            } else if (inputType == SentenceStudyView.INPUT_TYPE.CHAR_SYMBOL.value()) {
                setWriteLanguage(Language.TYPE_NUMERIC, Language.TYPE_SYMBOL);
            } else {
                setWriteLanguage(Language.TYPE_NUMERIC, Language.TYPE_SYMBOL);
            }
        }

        @Override
        public void blankChangeComplete() {
            mIsMarking = false;
            mAniMarkingView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void playCorrectSound(boolean isMarking, boolean isCorrect, int retryCount, int hintCount, int x, int y) {
            int posX = Math.max(0, x - 15);
            int posY = Math.max(0, y - 15);

            gRes.playEffect(isCorrect ? ResManager.SE_MARK_SUCCESS : ResManager.SE_MARK_WRONG);

            if (isCorrect) {
                setWriteMode(false);

                mAniMarkingView.setImageType(AniMarkingView.AniType.SMALL_CORRECT);
                mAniMarkingView.setPosition(posX, posY);
                mAniMarkingView.start();
                mIsMarking = true;
            } else {
                if (retryCount >= SentenceStudyView.MAX_RETRY) {
                    setWriteMode(false);
                }
                mAniMarkingView.setImageType(AniMarkingView.AniType.SMALL_WRONG);
                mAniMarkingView.setPosition(posX, posY);
                mAniMarkingView.start();
                mIsMarking = true;
            }
        }

        @Override
        public void onBlankTouch(Rect rcBox, int answerCount) {
            if (!isWriteMode()) {
                if (isSetWriteLanuage(Language.TYPE_KOREAN)) {
                    setWriteMode(rcBox, true, answerCount < 2 ? WriteMode.KOREAN_S : WriteMode.KOREAN_L);
                } else {
                    setWriteMode(rcBox, true, isSetWriteLanuage(Language.TYPE_SYMBOL) ? WriteMode.SYMBOL : WriteMode.NO_SYMBOL);
                }
            } else {
                setWriteMode(false);
            }
        }
    };

    private final class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mDragState == 1) {
                        return true;
                    }

                    view.setBackgroundColor(Color.parseColor("#9fe2e7"));
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            mImgBg.removeCallbacks(dragState);
                            playSoundEffect(SoundEffectConstants.CLICK);
                            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                            ClipData data = new ClipData("", mimeTypes, item);
                            MyShadowBuilder shadowBuilder = new MyShadowBuilder(view, event.getX(), event.getY());
                            view.startDrag(data, // data to be dragged
                                    shadowBuilder, // drag shadow
                                    view, // local data about the drag and drop operation
                                    0 // no needed flags
                            );
                        }
                    });
                    return true;
                default:
                    break;
            }
            return false;
        }
    }

    public class MyShadowBuilder extends View.DragShadowBuilder {
        private float x;
        private float y;

        public MyShadowBuilder(View v, float x, float y) {
            super(v);

            this.x = x;
            this.y = y;
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            View v = getView();
            shadowSize.x = v.getWidth();
            shadowSize.y = v.getHeight();

            //터치 포인트를 기준으로 이미지 위치 지정
            int[] loc = new int[2];
            v.getLocationOnScreen(loc);
            shadowTouchPoint.x = (int) x - loc[0];
            shadowTouchPoint.y = (int) y - loc[1];
        }
    }

    private final class DragListener implements View.OnDragListener {
        ScaleAnimation targetAnimation = null;
        ScaleAnimation normalAnimation = null;

        public DragListener() {
            targetAnimation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            targetAnimation.setDuration(300);
            targetAnimation.setFillAfter(true);
            targetAnimation.setInterpolator(new BounceInterpolator());

            normalAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            normalAnimation.setDuration(300);
            normalAnimation.setFillBefore(true);
            normalAnimation.setInterpolator(new BounceInterpolator());
        }

        @SuppressLint("NewApi")
        @Override
        public boolean onDrag(View v, DragEvent event) {

            // Handles each of the expected events
            switch (event.getAction()) {
                // signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED: {
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.INVISIBLE);
                    mTxtStudyQuestion.getOriginalHighLightTextView().setDrawHighLightBackGround(false);
                    mTxtStudyQuestion.getOriginalHighLightTextView().invalidate();
                    mImgDragFinger.clearAnimation();
//                    mTxtDrag.setBackgroundResource(0);

                    // do nothing
                    break;
                }
                // the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (!v.equals(mLyStudyAnswer)) {
                        v.startAnimation(targetAnimation);
                    }
                    break;

                // the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    if (!v.equals(mLyStudyAnswer)) {
                        v.startAnimation(normalAnimation);
                    }
                    break;

                // drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP: {
                    mTxtDrag.setText(mTxtStudyQuestion.getHighLightTextView().getText());

                    if (!v.equals(mLyStudyAnswer)) {
                        v.startAnimation(normalAnimation);
                    }

                    mIsAnswer = true;
                    setState(STATE_STUDY1_RESULT);
                    break;
                }
                // the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED: {
                    mTxtStudyQuestion.getOriginalHighLightTextView().setDrawHighLightBackGround(mIsAnswer ? true : false);
                    mTxtStudyQuestion.getOriginalHighLightTextView().invalidate();
                    break;
                }
                default:
                    break;
            }
            return true;
        }
    }
}
