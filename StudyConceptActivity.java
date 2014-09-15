package com.weaversmind.jrm;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;dddddddddddddddddddddddddd
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.diotek.dhwr.madeleine.Language;
import com.weaversmind.data.Define;
import com.weaversmind.data.JRMMathUnit;
import com.weaversmind.data.JRMQuizManager;
import com.weaversmind.data.JRMQuizUnit;
import com.weaversmind.data.ResManager;
import com.weaversmind.data.TutorialData;
import com.weaversmind.data.Util;
import com.weaversmind.data.WMAnimation;
import com.weaversmind.data.WMLog;
import com.weaversmind.widget.AniAccGraphView;
import com.weaversmind.widget.AniMarkingView;
import com.weaversmind.widget.AniPointView;
import com.weaversmind.widget.CustomButtonView;
import com.weaversmind.widget.CustomViewPager;
import com.weaversmind.widget.MoviePlayer;
import com.weaversmind.widget.QuizAreaView;
import com.weaversmind.widget.QuizBaseView;
import com.weaversmind.widget.SentenceStudyView;
import com.weaversmind.widget.TextScriptView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class StudyConceptActivity extends StudyBaseActivity {
    private static final String TAG = StudyConceptActivity.class.getSimpleName();

    // /////////////////////// Define ///////////////////////////////////////////////
    public static final int STEP_CARTOON = 1;
    public static final int STATE_CARTOON_SHOW = 0;
    public static final int STATE_CARTOON_IMAGE = 1;
    public static final int STATE_CARTOON_SOUND = 2;
    public static final int STATE_CARTOON_VERBAL = 3;
    public static final int STATE_CARTOON_LAST = 4;

    public static final int STEP_LECTURE_GUIDE = 2;

    public static final int STEP_LECTURE = 3;
    public static final int STATE_TITLE = 0;
    public static final int STATE_GUIDE_START = 1;
    public static final int STATE_LECTURE = 2;
    public static final int STATE_LECTURE_END = 3;

    public static final int STEP_SUMMARY = 4;
    public static final int STATE_SUMMARY_GUIDE = 0;
    public static final int STATE_SUMMARY_IMAGE = 1;
    public static final int STATE_SUMMARY_END = 2;

    public static final int STEP_PATTERN = 5;
    public static final int STATE_PATTERN_PREPARE = 0;
    public static final int STATE_PATTERN_SHOW_T = 1;
    public static final int STATE_PATTERN_SHOW_Q = 2;
    public static final int STATE_PATTERN_START = 3;
    public static final int STATE_PATTERN_WAIT = 4;
    public static final int STATE_PATTERN_FINISH = 5;

    private CustomButtonView mBtnNext = null;
    private CustomButtonView mBtnPrev = null;
    private CustomButtonView mBtnReplay = null;

    //// 만화 관련
    private static final int[] ID_CARTOON_PROGRESS = {R.id.imgCartoonProgress1,
            R.id.imgCartoonProgress2, R.id.imgCartoonProgress3, R.id.imgCartoonProgress4, R.id.imgCartoonProgress5};
    private static final int[] ID_CARTOON_VERBAL = {R.id.imgCartoonVerbal1, R.id.imgCartoonVerbal2,
            R.id.imgCartoonVerbal3};
    private RelativeLayout mLyCartoon = null;
    private ImageView mImgCartoonBg = null;
    private CustomViewPager mCartoonViewPager = null;
    private CartoonPagerAdapter mCartoonPagerAdapter = null;
    private RelativeLayout mLyCartoonProgress = null;
    private AniAccGraphView mAniCartoonProgressBar = null;
    private ImageView[] mImgCartoonProgress = new ImageView[ID_CARTOON_PROGRESS.length];
    private ImageView[] mCartoonVerbal = new ImageView[ID_CARTOON_VERBAL.length];
    private int mCartoonCurPage;
    private int mCartoonMaxPage;
    private int mCartonnVerbalImgIdx;
    private int mCartoonVerbalIdx;
    private int mCartoonVerbalCount;
    int[] vImg1 = {1, 1, 2, 1, 1};
    int[] vImg2 = {1, 1, 2, 1, 1};
    int[] vImg3 = {0, 1, 2, 3, 2};
    ////

    //// 동영상 관련
    private RelativeLayout mLyLecture = null;
    private MoviePlayer mLecturePlayer = null;
    private ImageView mLectureDummyImg = null;
    private ImageView mLectureFrameImg = null;
    private RelativeLayout mlyLectureController = null;
    private Handler mLectureControlHandler = new Handler();
    private SeekBar mTimerSeekBar = null;
    private TextView mLectureStartTime = null;
    private TextView mLectureEndTime = null;
    private CustomButtonView mBtnLecturePlay = null;
    private String mMaxDurationInfo = "";
    private String mCurDurationInfo = "";
    private boolean mMaxCheck = false;
    private boolean mIsClickedPauseButton = false;
    private int mMovieMaxPosition = 0;
    private int mMovieCurPosition = 0;
    // 동영상 재생 중 background로 전환시 처리용 변수
    private boolean mIsBackground = false;
    // 동영상 seek process
    private boolean mIsSeeking = false;
    private boolean mIsSeekStart = false;
    // 동영상 끝 지점 정지
    private boolean mIsLectureEnd = false;
    // seek 이동 후 제대로 이동안 한 경우의 count
    private int mSeekWrongCount = 0;
    private boolean mIsLectureReady = false;
    private boolean mIsLectureTouch = false;
    ////

    //// 강의 정리 관련
    private RelativeLayout mLySummary = null;
    private ScrollView mContentLayout = null;
    private ImageView mImgContent = null;
    private CustomButtonView mBtnSummaryOk = null;
    private ImageView mImgSummaryTip = null;
    ////

    //// 학습 관련
    private RelativeLayout mLyStudy = null;
    private RelativeLayout mLyPatternFrame = null;
    private TextView mTxtQuizInstruction = null;
    private TextScriptView mTxtQuizTitle = null;
    private QuizAreaView mImgQuizTitle = null;
    private CustomButtonView mBtnQuizSubmit = null;

    private AniPointView mAniPointView = null;
    private SparseIntArray mScoreList = new SparseIntArray();

    private JRMQuizManager mQuizManager = null;

    //사용자 유형 정보
    private ArrayList<JRMQuizUnit> mQuizCorrectList = new ArrayList<JRMQuizUnit>();
    private ArrayList<Integer> mPatternList = new ArrayList<Integer>();
    private int mPatternNum;
    private int mCurQuizType;

    private boolean mIsLectureBack = false;

    private int mFlowLec1;
    private int mFlowLec2;
    private int mFlowLec3;
    private int mFlowStudy3;
    private int mFlowIDSummary;

    private int mLevel;
    private int mEpisode;
    private int mChapter;

    private long mLecturePlaySec = 0;
    private long mCurrentTimeInMillis = 0;
    private boolean mIsFirstLecSkip = true;
    private boolean mIsAnimating = false;

    private int mFirstLecID = 2576;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_concept);

        mLevel = gData.getStudyLevel();
        mEpisode = gData.getStudyEpisode();
        mChapter = gData.getStudyChapter();

        //// 만화 관련
        mLyCartoon = (RelativeLayout) findViewById(R.id.layoutCartoon);
        mLyCartoon.setVisibility(View.INVISIBLE);
        mImgCartoonBg = (ImageView) mLyCartoon.findViewById(R.id.imgCartoonBg);
        mCartoonViewPager = (CustomViewPager) mLyCartoon.findViewById(R.id.pagerCartoon);
        mCartoonViewPager.setVisibility(View.INVISIBLE);
        mCartoonViewPager.setScrollDurationFactor(8); // make the animation twice as slow
//        mCartoonViewPager.setPageMargin(-320);
        mCartoonViewPager.beginFakeDrag(); // view page 드래그 막음
        mCartoonViewPager.setOnTouchListener(this);
        mCartoonViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int page) {
                mCartoonCurPage = page;
                updateCartoon();
            }

            @Override
            public void onPageScrolled(int page, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mLyCartoonProgress = (RelativeLayout) mLyCartoon.findViewById(R.id.layoutCartoonProgress);
        mAniCartoonProgressBar = (AniAccGraphView) mLyCartoonProgress.findViewById(R.id.aniGraphCartoonProgress);
        mAniCartoonProgressBar.setImage(R.drawable.flow_stillcut_line_red2);
        mAniCartoonProgressBar.setMaxSizeX(590);
        mAniCartoonProgressBar.setFrameMax(10);
        mAniCartoonProgressBar.setPoint(0);
        mAniCartoonProgressBar.setOnAniGraphListener(new AniAccGraphView.OnAniGraphListener() {
            @Override
            public void onAniUpdateListener(int curFrame, int maxFrame) {

            }

            @Override
            public void onAniCompleteListener() {
                if (mCartoonCurPage > 1) {
                    int progressID2 = getResources().getIdentifier("flow_stillcut_red_" + mCartoonCurPage, "drawable", getPackageName());
                    mImgCartoonProgress[mCartoonCurPage - 1].setImageResource(progressID2);
                    mAnimation.scaleAnim(mImgCartoonProgress[mCartoonCurPage - 1], 150, 0, 0, 1);
                    mAnimation.start();
                }
            }
        });
        for (int i = 0; i < mImgCartoonProgress.length; i++) {
            mImgCartoonProgress[i] = (ImageView) mLyCartoonProgress.findViewById(ID_CARTOON_PROGRESS[i]);
        }
        for (int i = 0; i < mCartoonVerbal.length; i++) {
            mCartoonVerbal[i] = (ImageView) mLyCartoon.findViewById(ID_CARTOON_VERBAL[i]);
        }
        ////

        //// 강의 관련
        mLyLecture = (RelativeLayout) findViewById(R.id.layoutLecture);
        mLyLecture.setVisibility(View.INVISIBLE);
        mLecturePlayer = (MoviePlayer) mLyLecture.findViewById(R.id.mvLecture);
        mLecturePlayer.setLoadingImage(R.drawable.flow_vod_loading_1);
        mLecturePlayer.setDummyImgColor(Color.parseColor("#ffffff"));
        mLecturePlayer.setPauseAtEnd(true);
        mLecturePlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mIsLectureTouch = true;

                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mlyLectureController.clearAnimation();

                    if (mlyLectureController.getVisibility() == View.VISIBLE) {
                        mlyLectureController.setVisibility(View.INVISIBLE);
                        mAnimation.fadeOutAnim(mlyLectureController, 400);
                        mAnimation.start();
                    } else {
                        mlyLectureController.setVisibility(View.VISIBLE);
                        mAnimation.fadeInAnim(mlyLectureController, 400);
                        mAnimation.start();
                        mLectureControlHandler.removeCallbacks(mLectureControlHideTask);
                        mLectureControlHandler.postDelayed(mLectureControlHideTask, 2000);
                    }
                }

                return true;
            }
        });

        mLecturePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mIsLectureEnd = true;
                setFlowJump();

                mLecturePlaySec += (System.currentTimeMillis() - mCurrentTimeInMillis) / 1000;
            }
        });
        mLecturePlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                mIsSeeking = false;
                mTimerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                mCurDurationInfo = getTime(mediaPlayer.getCurrentPosition());
                mLectureStartTime.setText(Html.fromHtml(mCurDurationInfo));
                mLectureEndTime.setText(Html.fromHtml(mMaxDurationInfo));
            }
        });
        mLecturePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMaxCheck = true;
                mMovieMaxPosition = mLecturePlayer.getDuration() - 1000;
                mTimerSeekBar.setMax(mMovieMaxPosition);
                mMaxDurationInfo = " / " + getTime(mMovieMaxPosition);
                mCurrentTimeInMillis = System.currentTimeMillis();
            }
        });

        //
        mLectureDummyImg = (ImageView) mLyLecture.findViewById(R.id.imgLectureDummy);
        mLectureDummyImg.setVisibility(View.INVISIBLE);
        mLectureFrameImg = (ImageView) mLyLecture.findViewById(R.id.imgLectureFrame);
        mlyLectureController = (RelativeLayout) mLyLecture.findViewById(R.id.layoutLectureController);
        mTimerSeekBar = (SeekBar) mlyLectureController.findViewById(R.id.timeSeekBar);
        mTimerSeekBar.setOnSeekBarChangeListener(mOnSeek);
        mLectureStartTime = (TextView) mlyLectureController.findViewById(R.id.txtLectureStartTime);
        mLectureEndTime = (TextView) mlyLectureController.findViewById(R.id.txtLectureEndTime);
        mBtnLecturePlay = (CustomButtonView) mlyLectureController.findViewById(R.id.btnLecturePlay);
        mBtnLecturePlay.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnLecturePlay);
        ////

        //// 강의 정리 관련
        mLySummary = (RelativeLayout) findViewById(R.id.layoutSummary);
        mLySummary.setVisibility(View.INVISIBLE);
        mContentLayout = (ScrollView) mLySummary.findViewById(R.id.lytContent);
        mContentLayout.setScrollbarFadingEnabled(false);
        mContentLayout.setVerticalFadingEdgeEnabled(true);
        mImgContent = (ImageView) mContentLayout.findViewById(R.id.imgContent);
        mImgContent.setOnTouchListener(this);
        mBtnSummaryOk = (CustomButtonView) mContentLayout.findViewById(R.id.btnSummaryOk);
        setCustomButton(mBtnSummaryOk);
        mImgSummaryTip = (ImageView) mLySummary.findViewById(R.id.imgSummaryTip);
        ////

        //// 학습 관련
        mLyStudy = (RelativeLayout) findViewById(R.id.layoutStudy);
        mLyStudy.setVisibility(View.INVISIBLE);
        mLyPatternFrame = (RelativeLayout) mLyStudy.findViewById(R.id.layoutPatternFrame);
        mTxtQuizInstruction = (TextView) mLyStudy.findViewById(R.id.txtQuizInstruction);
        mTxtQuizInstruction.setTypeface(getFont(FontType.CREGOTHIC_B));
        mTxtQuizInstruction.setVisibility(View.INVISIBLE);
        mTxtQuizTitle = (TextScriptView) mLyStudy.findViewById(R.id.txtQuizTitle);
        mTxtQuizTitle.setTypeface(getFont(FontType.CREGOTHIC_M));
        mTxtQuizTitle.setText("");
        mImgQuizTitle = (QuizAreaView) mLyStudy.findViewById(R.id.imgQuizMain);
        mBtnQuizSubmit = (CustomButtonView) mLyStudy.findViewById(R.id.btnQuizSubmit);
        mBtnQuizSubmit.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        mBtnQuizSubmit.setVisibility(View.INVISIBLE);
        setCustomButton(mBtnQuizSubmit);

        mAniPointView = (AniPointView) findViewById(R.id.aniPointView);
        mAniPointView.setVisibility(View.INVISIBLE);

        createControlPanel(R.layout.panel_control_concept);
        setWordList(Define.WLT_CONCEPT);

        //유형 별 onCreate
        mQuizManager = new JRMQuizManager(this, Define.STUDY_CONCEPT);
        mQuizManager.setOnStudyListener(onStudyListener);
        for (int i = 0; i < mWordList.size(); i++) {
            int quizType = mWordList.get(i).quizType;
            if (!mPatternList.contains(quizType)) {
                mPatternList.add(quizType);
            }
            onCreatePattern(quizType);
        }
    }

    private void onCreatePattern(int quizType) {
        int layoutID = getResources().getIdentifier("pattern_integrated_" + quizType, "layout", getPackageName());
        if (layoutID > 0) {
            RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(layoutID, null);
            mLyPatternFrame.addView(layout);
            layout.setVisibility(View.GONE);
            View v = layout.findViewById(R.id.patternView);
            mQuizManager.registerView(quizType, v);
        } else {
            WMLog.e(TAG, "onCreatePattern type : " + quizType + " error : " + "pattern_integrated_" + quizType + " is not exist");
        }
    }

    @Override
    protected void onLayoutCompleted() {
        super.onLayoutCompleted();

        showUserOption(false);
        setGuideAniMode(TextScriptView.MODE_SHOW_NOANI);
        setTextGuide("<font color='#595959'>개념콕콕학습:" + mWordList.get(0).episodeTitle + "</font>");
        showGuideNoAni();

        gData.updateStudySlot(Define.STUDY_CONCEPT);
    }

    @Override
    protected void onOrganizeControlPanel(View v) {
        mBtnNext = (CustomButtonView) v.findViewById(R.id.btnNext);
        mBtnNext.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnNext);

        mBtnPrev = (CustomButtonView) v.findViewById(R.id.btnPrev);
        mBtnPrev.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnPrev);

        mBtnReplay = (CustomButtonView) v.findViewById(R.id.btnReplay);
        mBtnReplay.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
        setCustomButton(mBtnReplay);
    }

    @Override
    protected void onCreateSteps() {
        try {
            makeStep(STEP_CARTOON, "4컷만화 - 보기");
            makeStep(STEP_LECTURE_GUIDE, "문법 강의 가이드");
            makeStep(STEP_LECTURE, "문법 강의");
            makeStep(STEP_SUMMARY, "문법 강의 정리");

            makeStep(STEP_PATTERN, "학습");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onOrganizeFlow() {
        pushFlow(STEP_CARTOON);
        pushFlow(STEP_LECTURE_GUIDE);
        mFlowLec1 = pushFlow(STEP_LECTURE);
        if (mLevel != 11) {
            pushFlow(STEP_PATTERN);
            mFlowLec2 = pushFlow(STEP_LECTURE);
            pushFlow(STEP_PATTERN);
            mFlowLec3 = pushFlow(STEP_LECTURE);
        }
        mFlowIDSummary = pushFlow(STEP_SUMMARY);
    }

    @Override
    protected void initStep(int nFlow, int stepID, Step step) {
        if (stepID == STEP_CARTOON) {
            turnOnKeepScreen();

            hideControlPanelIFSticky();
            hidePanelHideImgDelay();
            setViewVisible(mBtnNext, true);
            setViewVisible(mBtnPrev, false);
            setViewVisible(mBtnReplay, false);

            mCartoonVerbalIdx = 0;
            if (mCartoonCurPage == 0) {
                mImgBg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gRes.playBGM(R.raw.mainbgm_02, StudyConceptActivity.this);
                    }
                }, 400);

                mCartoonMaxPage = 6;
            }

            for (int i = 0; i < mCartoonVerbal.length; i++) {
                mCartoonVerbal[i].setVisibility(View.INVISIBLE);
            }

            int duration = 500;
            if (mCartoonCurPage == 0) {
                duration += gRes.getSoundDurationByWordID(mWordList.get(0).cartoonSndID) + 1000;
            } else {
                duration += gRes.getSoundDurationByWordID(mWordList.get(0).cartoonSndID + mCartoonCurPage);
            }

            step.clearState();
            step.pushStateMillis(1000);
            step.pushStateMillis(mCartoonCurPage == 0 ? 2000 : 2300);
            step.pushStateMillis(duration);
            step.pushState(Step.WAIT_EVENT);
            step.pushState(Step.WAIT_EVENT);
        } else if (stepID == STEP_LECTURE_GUIDE) {
            turnOnKeepScreen();

            gRes.stopSound();
            mLyCartoon.setVisibility(View.GONE);

            // 강의 정리 화면에서 다시보기 시 초기화
            mLyStudy.setVisibility(View.INVISIBLE);
            mLySummary.setVisibility(View.INVISIBLE);
            mLyLecture.setVisibility(View.INVISIBLE);
            setViewVisible(mBtnNext, true);
            setViewVisible(mBtnPrev, false);
            setViewVisible(mBtnReplay, false);
            resetControlPanel();
            showPanelHideImg();

//            step.clearState().pushStateMillis(gRes.getSoundDurationByWordID(8) + 500);
            step.clearState().pushStateMillis(500);
        } else if (stepID == STEP_LECTURE) {
            turnOnKeepScreen();

            gRes.stopSound();
            // 강의 정리 화면에서 다시보기 시 초기화
            mLyStudy.setVisibility(View.INVISIBLE);
            mLySummary.setVisibility(View.INVISIBLE);

            setViewVisible(mBtnNext, true);
            setViewVisible(mBtnReplay, false);
            setViewVisible(mBtnPrev, getCurFlow() != mFlowLec1);
            resetControlPanel();

            mMaxCheck = false;
            int lecID = mFirstLecID;
            if (mLevel == 11) { //TODO 추후제거
                lecID = 8061;
            } else {
                if (getCurFlow() == mFlowLec1) {
                    mFirstLecID = mWordList.get(mWordNum).lecID;
                    lecID = mFirstLecID;
                } else if (getCurFlow() == mFlowLec2) {
                    lecID = mFirstLecID + 10;
                } else if (getCurFlow() == mFlowLec3) {
                    lecID = mFirstLecID + 20;
                }
            }

            gRes.setMediaByWordid(mLecturePlayer, lecID);

            step.clearState().pushState(getTickByMillis(2500))
                    .pushState(getTickByMillis(800))
                    .pushState(Step.WAIT_EVENT)
                    .pushState(getTickByMillis(500));
        } else if (stepID == STEP_SUMMARY) {
            turnOffKeepScreen();

            hideControlPanelIFSticky();
            hidePanelHideImgDelay();

            mImgSummaryTip.setVisibility(View.INVISIBLE);

            step.clearState().pushStateMillis(500)
                    .pushState(getTickByMillis(1000))
                    .pushState(getTickByMillis(100))
                    .pushState(Step.WAIT_EVENT);
        } else if (stepID == STEP_PATTERN) {
            turnOffKeepScreen();

            mLySummary.setVisibility(View.INVISIBLE);
            setViewVisible(mLecturePlayer, false);

            hideControlPanelIFSticky();
            hidePanelHideImgDelay();

            step.clearState();
            step.pushStateMillis(100)
                    .pushState(Step.WAIT_EVENT)
                    .pushStateMillis(100)
                    .pushStateMillis(100)
                    .pushStateMillis(1500)
                    .pushState(Step.WAIT_EVENT);
        }

        super.initStep(nFlow, stepID, step);
    }

    @Override
    protected void initState(final int stepID, final int stateID) {
        super.initState(stepID, stateID);

        if (stepID == STEP_CARTOON) {
            if (stateID == STATE_CARTOON_SHOW) {
                if (mCartoonCurPage == 0) {
                    if (mLyCartoon.getVisibility() == View.INVISIBLE) {
                        mAnimation.fadeInAnim(mLyCartoon, 500);
                        mAnimation.start();
                    }
                    mLyCartoonProgress.setVisibility(View.INVISIBLE);
                } else {
                    if (mLyCartoonProgress.getVisibility() == View.INVISIBLE) {
                        mAnimation.fadeInAnim(mLyCartoonProgress, 300);
                        mAnimation.start();
                    }
                    mLyCartoonProgress.setVisibility(View.VISIBLE);
                    mAniCartoonProgressBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAniCartoonProgressBar.setPoint(25 * (mCartoonCurPage - 1) - 3);
                            mAniCartoonProgressBar.start();
                        }
                    }, 100);
                }
                updateCartoon();
            } else if (stateID == STATE_CARTOON_IMAGE) {
            } else if (stateID == STATE_CARTOON_SOUND) {
                gRes.playSoundByWordID(mWordList.get(0).cartoonSndID + mCartoonCurPage);

                if (mCartoonCurPage > 0) {
                    int[] verCount = null;
                    if (mLevel == 31 && mEpisode == 1) {
                        verCount = vImg1;
                    } else if (mLevel == 31 && mEpisode == 6) {
                        verCount = vImg2;
                    } else if (mLevel == 41) {
                        verCount = vImg3;
                    }
                    mCartoonVerbalIdx = 0;
                    mCartoonVerbalCount = verCount[mCartoonCurPage - 1];
                    mCartonnVerbalImgIdx = 0;
                    for (int i = 0; i < mCartoonCurPage - 1; i++) {
                        mCartonnVerbalImgIdx += verCount[i];
                    }
                }
            } else if (stateID == STATE_CARTOON_VERBAL) {
                if (mCartoonCurPage == 0) {
                    nextState();
                } else {
                    if (mCartoonVerbalIdx < mCartoonVerbalCount) {
                        Util.recycleImageView(mCartoonVerbal[mCartoonVerbalIdx]);
                        int cartoonID = mWordList.get(0).cartoonImgID + 500 + mCartonnVerbalImgIdx + mCartoonVerbalIdx;
                        Bitmap content = gRes.getBitmapByWordid(cartoonID);
                        mCartoonVerbal[mCartoonVerbalIdx].setImageBitmap(content);
                        mCartoonVerbal[mCartoonVerbalIdx].setVisibility(View.VISIBLE);
                        mAnimation.fadeInAnim(mCartoonVerbal[mCartoonVerbalIdx], 150);
                        mAnimation.start();
                        mCartoonVerbal[mCartoonVerbalIdx].postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCartoonVerbalIdx++;
                                setState(STATE_CARTOON_VERBAL);
                            }
                        }, 2500);
                    } else {
                        int delay = mCartoonVerbalCount == 0 ? 3000 : 500;
                        mImgBg.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                nextState();
                            }
                        }, delay);
                    }
                }
            } else if (stateID == STATE_CARTOON_LAST) {
                mCartoonCurPage++;
                if (mCartoonCurPage == mCartoonMaxPage) {
                    gRes.stopBGM();
                    showControlPanel(true);
                } else {
                    resetFlow();
                }
            }
        } else if (stepID == STEP_LECTURE) {
            if (stateID == STATE_TITLE) {
                int curFlow = getCurFlow();
                if (curFlow == mFlowLec1) {
                    if (!mIsLectureBack) {
                        mLecturePlayer.setLoadingImage(R.drawable.flow_vod_loading_1);
                        mAnimation.moveFromRightAnim(mLyLecture, 600);
                        mAnimation.start();
                    }
                    setViewVisible(mLecturePlayer, true);
                } else {
                    if (!mIsLectureBack) {
                        mLectureDummyImg.clearAnimation();
                        mLectureDummyImg.setImageResource(R.drawable.flow_vod_loading_5);
                        mLectureDummyImg.setVisibility(View.VISIBLE);
                        mAnimation.fadeOutAnim(mLectureDummyImg, 300, 500);
                        mAnimation.start();
                        mLecturePlayer.setLoadingImage(R.drawable.flow_vod_loading_5, false);
                        mAnimation.moveFromLeftAnim(mLyLecture, 600);
                        mAnimation.start();
                    }
                    setViewVisible(mLecturePlayer, true);
                    mLecturePlayer.start();
                }
                mIsLectureBack = false;
                mLyLecture.setVisibility(View.VISIBLE);
                mTimerSeekBar.setProgress(0);
                mLectureStartTime.setText(Html.fromHtml("00:00"));
                mLectureEndTime.setText(Html.fromHtml(" / 00:00"));
                setImageButton(mBtnLecturePlay, R.drawable.vod_bt_stop, R.drawable.vod_bt_stop_touch);
            } else if (stateID == STATE_GUIDE_START) {
                resetControlPanel();
            } else if (stateID == STATE_LECTURE) {
                if (mlyLectureController.getVisibility() == View.VISIBLE) {
                    mlyLectureController.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mlyLectureController.getVisibility() == View.VISIBLE) {
                                mlyLectureController.setVisibility(View.INVISIBLE);
                                mAnimation.fadeOutAnim(mlyLectureController, 400);
                                mAnimation.start();
                            }
                        }
                    }, 2000);
                }
            }
        } else if (stepID == STEP_SUMMARY) {
            if (stateID == STATE_SUMMARY_GUIDE) {
            } else if (stateID == STATE_SUMMARY_IMAGE) {
                mLySummary.setVisibility(View.VISIBLE);
                Util.recycleImageView(mImgContent);
                mImgContent.setImageBitmap(gRes.getBitmapByWordid(mWordList.get(0).lecImgID));
                mAnimation.fadeInAnim(mImgContent, 400);
                mAnimation.start();
                mImgContent.setVisibility(View.VISIBLE);
                mContentLayout.scrollTo(0, 0);
            } else if (stateID == STATE_SUMMARY_END) {
                Animation ani = AnimationUtils.loadAnimation(this, R.anim.move_from_bottom);
                mImgSummaryTip.startAnimation(ani);
                mImgSummaryTip.setVisibility(View.VISIBLE);
            }
        } else if (stepID == STEP_PATTERN) {
            if (stateID == STATE_PATTERN_SHOW_T) {
                mCurQuizType = mWordList.get(mWordNum).quizType;

                mAnimation.moveFromRightAnim(mLyStudy, 600);
                mAnimation.start();
                mLyStudy.setVisibility(View.VISIBLE);
                mTxtQuizInstruction.setVisibility(View.VISIBLE);
                mTxtQuizInstruction.setText(mWordList.get(mWordNum).quizInstruction);

                mTxtQuizTitle.setAniMode(TextScriptView.MODE_SHOW_NOANI);
                mTxtQuizTitle.setString("<big>" + (mWordNum * 2 + 2) + ".</big> " + mWordList.get(mWordNum).quizTitle);
                mTxtQuizTitle.setVisibility(View.VISIBLE);

                JRMMathUnit unit = mWordList.get(mWordNum);
                if (unit.quizImg > 0) {
                    //문제 이미지에 따른 가로 세로 배치 전환 (기본값 : 가로)
                    LinearLayout lly = (LinearLayout) mImgQuizTitle.getParent();
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mImgQuizTitle.getLayoutParams();
                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) mLyPatternFrame.getLayoutParams();
                    if (unit.quizImgAlign == 2) { //세로
                        lly.setOrientation(LinearLayout.VERTICAL);
                        lp.gravity = Gravity.CENTER_HORIZONTAL;
                        lp2.gravity = Gravity.CENTER_HORIZONTAL;
                        lp2.topMargin = 20;
                    } else  {   //그 외는 가로처리
                        lly.setOrientation(LinearLayout.HORIZONTAL);
                        lp.gravity = Gravity.CENTER_VERTICAL;
                        lp.leftMargin = 20;
                        lp2.gravity = Gravity.CENTER_VERTICAL;
                        lp2.leftMargin = 30;
                    }

                    mImgQuizTitle.setVisibility(View.VISIBLE);
                    int drawableID = getResources().getIdentifier("d" + unit.quizImg, "drawable", getPackageName());   //TODO d지워야함
                    mImgQuizTitle.setImageResource(drawableID);
                } else if (unit.quizText != null && unit.quizText.length() > 0) {
                    mImgQuizTitle.setData(unit.quizText);
                    mImgQuizTitle.setVisibility(View.VISIBLE);
                } else {
                    mImgQuizTitle.setVisibility(View.GONE);
                }
                mQuizManager.setData(mWordNum, unit);
                // 유형 하나하나가 끝날때마다 layout에서 제거하기 때문에 무조건 첫번째 것이 현재 할 학습이다.
                mLyPatternFrame.getChildAt(mWordNum).setVisibility(View.VISIBLE);
            } else if (stateID == STATE_PATTERN_SHOW_Q) {

            } else if (stateID == STATE_PATTERN_START) {

            } else if (stateID == STATE_PATTERN_WAIT) {
            } else if (stateID == STATE_PATTERN_FINISH) {
                hideMarking();
                setWriteMode(false);
                mWordNum++;
                mAnimation.moveToRightAnim(mLyStudy, 600);
                mAnimation.setOnFinishListener(mOnMoveToRightFinishListener);
                mAnimation.start();
                mIsAnimating = true;
                mImgBg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextState();
                    }
                }, 500);
            }
        }
    }

    @Override
    protected void updateState(int nTick, int nFlow, int stepID, int stateID, int nDuration) {
        super.updateState(nTick, nFlow, stepID, stateID, nDuration);

        if (stepID == STEP_LECTURE) {
            if (stateID == STATE_TITLE) {
                int curFlow = getCurFlow();
                if (curFlow == mFlowLec1) {
                    if (nTick == getTickByMillis(500)) {
                        setViewVisible(mLecturePlayer, true);
                    } else if (nTick == getTickByMillis(2000)) {
                        mLecturePlayer.start();
                        if (mLecturePlayer.isInPlaybackState()) {
                            nextState();
                        }
                    }
                }
            }

            if (mLecturePlayer.isPlaying() && !mIsSeeking && !mIsSeekStart) {
                mMovieCurPosition = mLecturePlayer.getCurrentPosition();
                // SEEK BAR 움직일시 튀는 것 무시 , SEEK 해도 그 위치로 안가고 다시 돌아가는 경우 존재 (3번이상시 다시 seek)
                if (mMovieCurPosition < mTimerSeekBar.getProgress() - 5000 || mMovieCurPosition > mTimerSeekBar.getProgress() + 5000) {
                    mSeekWrongCount++;
                    if (mSeekWrongCount == 3) {
                        mSeekWrongCount = 0;
                        mLecturePlayer.seekTo(mTimerSeekBar.getProgress());
                    }
                    return;
                }
                mTimerSeekBar.setProgress(mMovieCurPosition);
                mCurDurationInfo = getTime(mMovieCurPosition);
                mLectureStartTime.setText(Html.fromHtml(mCurDurationInfo));
                mLectureEndTime.setText(Html.fromHtml(mMaxDurationInfo));
            }
        } else if (stepID == STEP_SUMMARY) {
            if (nTick > 20 && nTick % 20 == 0) {
                if (mBtnSummaryOk.getVisibility() == View.VISIBLE) {
                    Animation ani = AnimationUtils.loadAnimation(this, R.anim.button_jelly_consist);
                    mBtnSummaryOk.startAnimation(ani);
                }
            }
        }
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
    protected void onResumeStudy() {
        super.onResumeStudy();

        int stepID = getCurStepID();
        if (stepID == STEP_LECTURE || stepID == STEP_LECTURE_GUIDE) {
            if (!mIsBackground) {
                resumeLecture();
            } else {
                restartLecture();
                mIsBackground = false;
            }
        } else if (stepID == STEP_CARTOON) {
            gRes.resumeBGM();
        }
    }

    @Override
    protected void onPauseStudy() {
        super.onPauseStudy();

        int stepID = getCurStepID();
        if (stepID == STEP_LECTURE || stepID == STEP_LECTURE_GUIDE) {
            pauseLecture();
        } else if (stepID == STEP_CARTOON) {
            gRes.pauseBGM();
        }
    }

    @Override
    public void finish() {
        if (mLecturePlayer.getVisibility() == View.VISIBLE) {
            mLecturePlayer.setVisibility(View.INVISIBLE);
        }
        mLecturePlayer.cleanLoadingView(); // 로딩시 스킵할때 처리
        super.finish();
    }

    @Override
    protected void onDestroy() {
        long elapsedSec = getElapsedTimeSec();
        if (elapsedSec >= Define.MIN_ELPASED_STUDY_TIME) {
            gData.updateStudyTimeSec(Define.STUDY_CONCEPT, elapsedSec);
        }

        mLecturePlayer.stopPlayback();
        if (mCartoonPagerAdapter != null) {
            mCartoonPagerAdapter.recycle();
        }

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
        } else if (requestCode == Define.DIALOG_LECTURE_SKIP) {
            if (data != null) {
                int sel = data.getIntExtra("sel", Define.NONE);
                if (sel == 0) {
                    pauseStudy();
                    mIsLectureReady = true;
                    mLecturePlayer.cleanLoadingView(); // 로딩시 스킵할때 처리
                    if (getCurFlow() == mFlowLec3) {
                        mLectureDummyImg.setImageResource(R.drawable.flow_vod_loading_5);
                    } else {
                        if (mLevel == 11) { //TODO 추후 제거
                            mLectureDummyImg.setImageResource(R.drawable.flow_vod_loading_5);
                        } else {
                            mLectureDummyImg.setImageResource(R.drawable.flow_vod_ready);
                        }
                    }
                    mLecturePlayer.pause(false);
                    mLectureDummyImg.setVisibility(View.VISIBLE);
                    mLectureDummyImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
                    mImgBg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAnimation.moveToLeftAnim(mLyLecture, 600);
                            mAnimation.setOnFinishListener(mOnMoveToLeftFinishListener);
                            mAnimation.start();
                            mImgBg.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mIsLectureReady = false;
                                    nextFlow();
                                }
                            }, 200);
                        }
                    }, 1500);
                    if (mlyLectureController.getVisibility() == View.VISIBLE) {
                        mlyLectureController.setVisibility(View.INVISIBLE);
                        mAnimation.fadeOutAnim(mlyLectureController, 400);
                        mAnimation.start();
                    }
                } else {
                    resetControlPanel();
                }
            }
        } else if (requestCode == Define.REQID_POPUP_TUTORIAL) {
            nextFlow();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onClickButton(View v) {
        if (!mIsAnimating) {
            switch (v.getId()) {
                case R.id.btnPrev:
                    onBtnPrevStep();
                    break;
                case R.id.btnReplay:
                    onBtnPrevStep();
                    break;
                case R.id.btnNext:
                    onBtnNextStep();
                    break;
                case R.id.btnSummaryOk:
                    moveActivityWithParam(StudyConceptQuizActivity.class, Define.NONE, true);
                    overridePendingTransition(R.anim.move_from_right, R.anim.move_to_left);
                    break;
                case R.id.btnLecturePlay:
                    onBtnPause();
                    break;
                case R.id.btnQuizSubmit:
                    mQuizManager.submit(mWordNum); //todo
                    break;
            }
        }
        super.onClickButton(v);
    }

    @Override
    protected void onScreenLockRelease() {
        super.onScreenLockRelease();

        mLecturePlayer.cleanLoadingView();
        mLecturePlayer.cleanDummyView();
        if (hasFocused() && isStudyPaused()) {
            restartLecture();
        }
    }

    private void resumeLecture() {
        if (mLecturePlayer != null && mLecturePlayer.isPaused()) {
            mLecturePlayer.start();

            mCurrentTimeInMillis = System.currentTimeMillis();
        }
        setImageButton(mBtnLecturePlay, R.drawable.vod_bt_stop, R.drawable.vod_bt_stop_touch);
        mIsClickedPauseButton = false;
    }

    private void pauseLecture() {
        if (mLecturePlayer != null) {
            mLecturePlayer.pause(false);
            mLecturePlaySec += (System.currentTimeMillis() - mCurrentTimeInMillis) / 1000;
        }
        setImageButton(mBtnLecturePlay, R.drawable.vod_bt_play, R.drawable.vod_bt_play_touch);
        mIsClickedPauseButton = true;
    }

    private void onBtnPause() {
        if (mIsClickedPauseButton) {
            mIsClickedPauseButton = false;
            mLectureControlHandler.postDelayed(mLectureControlHideTask, 2000);
            if (mIsLectureEnd) { // 끝 도달 중 재생 시 다시 처음부터 재생
                mIsLectureEnd = false;
                mLecturePlayer.seekTo(1000);
            }
            resumeStudy();
            gData.updateUserPatternClick("그래머", "재생", false);
        } else {
            mIsClickedPauseButton = true;
            mLectureControlHandler.removeCallbacks(mLectureControlHideTask);
            pauseStudy();
            gData.updateUserPatternClick("그래머", "일시정지", false);
        }
    }

    @Override
    protected void onTouchUp(View v, MotionEvent event) {
        super.onTouchUp(v, event);

        if (v == mImgContent) {
            onTouchPanel(event);
        } else if (v == mCartoonViewPager) {
            onTouchPanel(event);
        }
    }

    @Override
    protected boolean onDragRightToLeft() {
        int stepID = getCurStepID();
        if (stepID == STEP_CARTOON || stepID == STEP_LECTURE_GUIDE ||
                stepID == STEP_SUMMARY || stepID == STEP_PATTERN || isWriteMode()) {
            return false;
        }

        if (stepID == STEP_LECTURE && mIsLectureTouch) {
            mIsLectureTouch = false;
            return false;
        }

        onBtnNextStep();
        return true; // for consume event. don't return false
    }

    @Override
    protected boolean onDragLeftToRight() {
        int stepID = getCurStepID();
        if (stepID == STEP_CARTOON || stepID == STEP_LECTURE_GUIDE ||
                stepID == STEP_SUMMARY || stepID == STEP_PATTERN || isWriteMode()) {
            return false;
        }

        if (stepID == STEP_LECTURE && mIsLectureTouch) {
            mIsLectureTouch = false;
            return false;
        }

        onBtnPrevStep();
        return true; // for consume event. don't return false
    }

    @Override
    protected void setFlowJump() {
        setFlowJump(false);
    }

    protected void setFlowJump(boolean prev) {
        if (isFlowJumping() || mIsLectureReady) {
            return;
        }

        int stepID = getCurStepID();
        if (prev) {
            if (stepID == STEP_SUMMARY) {
                setViewVisible(mLectureFrameImg, true);
                setViewVisible(mTimerSeekBar, true);
                setViewVisible(mLectureStartTime, true);
                setViewVisible(mLectureEndTime, true);
                setFlow(mFlowStudy3);
            } else if (stepID == STEP_LECTURE) {
                if (getCurFlow() != mFlowLec1 && !mIsLectureReady && !mIsLectureBack) {
                    mLecturePlayer.cleanLoadingView(); // 로딩시 스킵할때 처리
                    mLecturePlayer.pause(false);
                    mLectureDummyImg.setImageResource(R.drawable.flow_vod_loading_5);
//                    mLectureDummyImg.setVisibility(View.INVISIBLE);
                    mLectureDummyImg.setVisibility(View.VISIBLE);
                    mLectureDummyImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
                    mImgBg.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAnimation.moveToRightAnim(mLyLecture, 600);
                            mAnimation.setOnFinishListener(mOnMoveToRightFinishListener);
                            mAnimation.start();
                            mIsAnimating = true;
                            mIsLectureBack = true;
                        }
                    }, 500);
                    if (mlyLectureController.getVisibility() == View.VISIBLE) {
                        mlyLectureController.setVisibility(View.INVISIBLE);
                        mAnimation.fadeOutAnim(mlyLectureController, 400);
                        mAnimation.start();
                    }
                }
            }
        } else {
            if (stepID == STEP_LECTURE) {
                if (!mIsLectureBack && !mIsLectureReady) {
                    if (mMovieCurPosition > mMovieMaxPosition || !mIsFirstLecSkip) {
                        mIsLectureReady = true;
                        mLecturePlayer.cleanLoadingView(); // 로딩시 스킵할때 처리
                        int delay = 1500;
                        if (getCurFlow() == mFlowLec3) {
                            delay = 500;
                            mLectureDummyImg.setImageResource(R.drawable.flow_vod_loading_5);
                        } else {
                            if (mLevel == 11) { //TODO 추후 제거
                                mLectureDummyImg.setImageResource(R.drawable.flow_vod_loading_5);
                            } else {
                                mLectureDummyImg.setImageResource(R.drawable.flow_vod_ready);
                            }
                        }
                        mLectureDummyImg.clearAnimation();
                        mAnimation.fadeInAnim(mLectureDummyImg, 300);
                        mAnimation.start();
                        mLecturePlayer.pause(false);
                        mImgBg.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAnimation.moveToLeftAnim(mLyLecture, 600);
                                mAnimation.setOnFinishListener(mOnMoveToLeftFinishListener);
                                mAnimation.start();
                                mIsAnimating = true;
                                mImgBg.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIsLectureReady = false;
                                        nextFlow();
                                    }
                                }, 200);
                            }
                        }, delay);
                        if (mlyLectureController.getVisibility() == View.VISIBLE) {
                            mlyLectureController.setVisibility(View.INVISIBLE);
                            mAnimation.fadeOutAnim(mlyLectureController, 400);
                            mAnimation.start();
                        }
                    } else {
                        mIsFirstLecSkip = false;
                        showControlPanel(true);
                        showDialog(Define.DIALOG_LECTURE_SKIP, Define.DIALOG_LECTURE_SKIP);
                    }
                }
            } else if (stepID == STEP_SUMMARY) {
                int score = 0;
                for (int i = 0; i < mScoreList.size(); i++) {
                    score += mScoreList.get(mScoreList.keyAt(i));
                }
                WMLog.d(TAG, "SCORE " + score + " " + mScoreList.size() + " total " + 180 + " result " + ((score * 100) / 180));
                score = (score * 100) / 180;

                //moveActivityWithParam(StudyResultActivity.class, Define.NONE, true, Define.STUDY_SENTENCE, score);
//                overridePendingTransition(0, R.anim.delay);
                finish();
            } else if (stepID == STEP_CARTOON) {
                mAnimation.moveToLeftAnim(mLyCartoon, 600);
                mAnimation.setOnFinishListener(new WMAnimation.OnFinishListener() {
                    @Override
                    public void onFinished() {
                        nextFlow();
                        mIsAnimating = false;
                    }
                });
                mAnimation.start();
                mIsAnimating = true;
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

    private WMAnimation.OnFinishListener mOnMoveToLeftFinishListener = new WMAnimation.OnFinishListener() {
        @Override
        public void onFinished() {
            if (isPausedTick()) {
                clearTick();
            }
            mLectureDummyImg.setVisibility(View.INVISIBLE);
            mLyLecture.setVisibility(View.INVISIBLE);
            mLyCartoon.setVisibility(View.INVISIBLE);
            mIsAnimating = false;
        }
    };

    private WMAnimation.OnFinishListener mOnMoveToRightFinishListener = new WMAnimation.OnFinishListener() {
        @Override
        public void onFinished() {
            int curStepID = getCurStepID();
            if (curStepID == STEP_LECTURE) {
                mImgQuizTitle.setVisibility(View.INVISIBLE);
                mBtnQuizSubmit.setVisibility(View.INVISIBLE);
                mLyPatternFrame.getChildAt(mWordNum - 1).setVisibility(View.GONE);

                if (mIsLectureBack) {
                    if (isPausedTick()) {
                        clearTick();
                    }
                    mLecturePlayer.cleanDummyView();
                    mLecturePlayer.cleanLoadingView();
                    if (getCurFlow() == mFlowLec3) {
                        mLecturePlayer.setLoadingImage(R.drawable.flow_vod_loading_5, false);
                    } else {
                        mLecturePlayer.setLoadingImage(R.drawable.flow_vod_loading_1);
                    }
                    mAnimation.moveFromLeftAnim(mLyLecture, 600);
                    mAnimation.start();
                    int curFlow = getCurFlow();
                    if (curFlow == mFlowLec2) {
                        setFlow(mFlowLec1);
                    } else if (curFlow == mFlowLec3) {
                        setFlow(mFlowLec2);
                    }
                }
            }
            mIsAnimating = false;
        }
    };

    @Override
    protected void onStudySoundCompletion() {
        int stepID = getCurStepID();
        if (stepID == STEP_CARTOON) {

        } else {
            if (mSoundPlayButtonView != null) {
                setImageButton((CustomButtonView) mSoundPlayButtonView, R.drawable.bt_play, R.drawable.bt_play_touch);
            }
        }

        super.onStudySoundCompletion();
    }

    @Override
    protected void jumpToNextStepDebug() {
        int stepID = getCurStepID();
        if (stepID == STEP_CARTOON) {
            gRes.stopBGM();
            onBtnNextStep();
        }
    }

    private void hideBtnAnswer() {
        if (mQuizManager.isNeedSubmit(mWordNum, mWordList.get(mWordNum))) {
            if (mBtnQuizSubmit.getVisibility() == View.VISIBLE) {
                mAnimation.moveToRightAnim(mBtnQuizSubmit, 300);
                mAnimation.start();
            }
        }
    }

    private void showBtnAnswer() {
        if (mQuizManager.isNeedSubmit(mWordNum, mWordList.get(mWordNum))) {
            if (mBtnQuizSubmit.getVisibility() != View.VISIBLE) {
                mAnimation.moveFromRightAnim(mBtnQuizSubmit, 300);
                mAnimation.start();
            }
        }
    }

    @Override
    protected void setWriteResult(String text) {
        mQuizManager.submit(mWordNum);
    }

    @Override
    protected void updateWriteText(String text) {
        mQuizManager.setInputValue(mWordNum, text);
    }

    private QuizBaseView.OnStudyListener onStudyListener = new QuizBaseView.OnStudyListener() {
        @Override
        public void onTouch(Rect r, MotionEvent event) {
            showBtnAnswer();
        }

        @Override
        public void finishStudy(boolean isMarking, boolean isAnswer, int retryCount, int hintCount, int x, int y) {
            if (isMarking) {
                gRes.playEffect(isAnswer ? ResManager.SE_MARK_SUCCESS : ResManager.SE_MARK_WRONG);
                startMarking(x, y, isAnswer);
            }
            setWriteMode(false);
            hideBtnAnswer();
            clearScratchPad();
            nextState();
        }

        @Override
        public void onEmptyTouchZone() {

        }

        @Override
        public void playCorrectSound(boolean isMarking, boolean isCorrect, int retryCount, int hintCount, int x, int y) {
            if (isMarking) {
                gRes.playEffect(isCorrect ? ResManager.SE_MARK_SUCCESS : ResManager.SE_MARK_WRONG);
                startMarking(x, y, isCorrect);
            }
        }

        @Override
        public void onBlankStart(String answer, int inputType) {
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
        public void onBlankTouch(Rect r, int answerCount) {
            if (!isWriteMode()) {
                if (isSetWriteLanuage(Language.TYPE_KOREAN)) {
                    setWriteMode(r, true, answerCount < 2 ? WriteMode.KOREAN_S : WriteMode.KOREAN_L);
                } else {
                    setWriteMode(r, true, isSetWriteLanuage(Language.TYPE_SYMBOL) ? WriteMode.SYMBOL : WriteMode.NO_SYMBOL);
                }
            } else {
                setWriteMode(false);
            }
        }

        @Override
        public void blankChangeComplete() {

        }
    };

    ////////////////////// 동영상 관련 ////////////////////////////////////////////
    @Override
    protected void onRestart() {
        // 사용자가 이 어플을 background로 돌려놓고 다시 돌아올시 동영상이 재생중이었다면 그 위치부터 다시 재생하도록
        if (hasFocused() && !isScreenOff()) {
            if (mIsBackground) {
                restartLecture();
            }
        }

        if (hasFocused()) {
            mIsBackground = false;
        }
        super.onRestart();
    }

    private void restartLecture() {
        if (mLecturePlayer != null) {
            if (mMovieCurPosition >= 0) {
                mLecturePlayer.seekTo(mMovieCurPosition);
                mLecturePlayer.post(new Runnable() {
                    @Override
                    public void run() {
                        mLecturePlayer.start();
                    }
                });

                if (isStudyPaused()) {
                    mLecturePlayer.setPauseWithStart(true);
                } else {
                    setImageButton(mBtnLecturePlay, R.drawable.vod_bt_stop, R.drawable.vod_bt_stop_touch);
                    mIsClickedPauseButton = false;
                }
            }
        }
    }

    @Override
    protected void onStart() {
        ((AudioManager) getSystemService(AUDIO_SERVICE))
                .requestAudioFocus(null, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (hasFocused() && !isScreenOff()) {
            if (getCurStepID() == STEP_LECTURE && mLecturePlayer != null) {
                mLecturePlayer.pause();
            }
        }

        ((AudioManager) getSystemService(AUDIO_SERVICE))
                .abandonAudioFocus(null);
        mIsBackground = true;
        super.onStop();
    }

    // -------------------------재생 위치 이동 처리--------------------------//
    private void onBtnForward() {
        if (mMaxCheck) {
            mIsSeeking = true;
            int curPos = mLecturePlayer.getCurrentPosition();
            final int nextPos = Math.min(curPos + 10000, mMovieMaxPosition - 2000);
            mLecturePlayer.seekTo(nextPos);
            mImgBg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLecturePlayer.seekTo(nextPos + 100);
                }
            }, 50);

            mMovieCurPosition = nextPos;
        }
    }

    private void onBtnBackward() {
        if (mMaxCheck) {
            mIsSeeking = true;
            int curPos = mLecturePlayer.getCurrentPosition();
            final int nextPos = Math.max(curPos - 10000, 1000);
            mLecturePlayer.seekTo(nextPos);
            mImgBg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLecturePlayer.seekTo(nextPos - 100);
                }
            }, 100);
            mMovieCurPosition = nextPos;
        }
    }

    SeekBar.OnSeekBarChangeListener mOnSeek = new SeekBar.OnSeekBarChangeListener() {
        int beforePos;

        public void onStopTrackingTouch(SeekBar seekBar) { // 터치 끝나면 호출
            mIsSeekStart = false;
            if (!isStudyPaused()) {
                mIsClickedPauseButton = false;
                mLecturePlayer.start();
            }
            // 동영상 끝 지점 멈춤시 다시 뒤로 seekbar 움직였을 시
            if (seekBar.getProgress() < seekBar.getMax() && mIsLectureEnd) {
                mIsLectureEnd = false;
            }
            mLecturePlayer.setVolumeMute(false);
            mLectureControlHandler.postDelayed(mLectureControlHideTask, 2000);
        }

        public void onStartTrackingTouch(SeekBar seekBar) { // 터치 시작시 호출
            mIsLectureTouch = true;
            mIsSeekStart = true;
            beforePos = seekBar.getProgress();
            if (!isStudyPaused()) {
                mIsClickedPauseButton = true;
                mLecturePlayer.pause(false);
            }
            mLecturePlayer.setVolumeMute(true);
            mLectureControlHandler.removeCallbacks(mLectureControlHideTask);
        }

        // 클릭하고 이동할때 호출
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && mMaxCheck) {
                mIsSeeking = true;
                mLecturePlayer.seekTo(progress);
                mMovieCurPosition = progress;
            }
        }
    };

    private String getTime(int n) {
        int minute, second;
        int time = n / 1000;
        int extra = n % 1000;

        minute = time / 60;
        second = time % 60;
        if (extra > 0) {
            second += 1;
            if (second >= 60) {
                second = 0;
                minute += 1;
            }
        }
        return String.format("%02d:%02d", minute, second);
    }

    private Runnable mLectureControlHideTask = new Runnable() {
        public void run() {
            if (mlyLectureController.getVisibility() == View.VISIBLE) {
                mlyLectureController.setVisibility(View.INVISIBLE);
                mAnimation.fadeOutAnim(mlyLectureController, 400);
                mAnimation.start();
            }
        }
    };

    /////////////////////////////////////////////////////////////////////////////////
    private void updateCartoon() {
        if (mCartoonPagerAdapter == null) {
            mCartoonViewPager.setOffscreenPageLimit(mCartoonMaxPage);
            mCartoonPagerAdapter = new CartoonPagerAdapter(this);
            mCartoonViewPager.setAdapter(mCartoonPagerAdapter);
            mAnimation.fadeInAnim(mCartoonViewPager, 400, 500);
            mAnimation.start();
        }
        mCartoonViewPager.setCurrentItem(mCartoonCurPage);
    }

    /**
     * 스케쥴 리스트를 관리하고 구성하는 클래스
     */
    private class CartoonPagerAdapter extends PagerAdapter {
        private List<WeakReference<ViewGroup>> mRecycleList = new ArrayList<WeakReference<ViewGroup>>();

        public CartoonPagerAdapter(Context c) {
            super();
        }

        @Override
        public int getCount() {
            return mCartoonMaxPage;
        }

        @Override
        public Object instantiateItem(ViewGroup pager, int position) {
            ImageView ivCartoon = new ImageView(StudyConceptActivity.this);
            ivCartoon.setLayoutParams(new ViewGroup.LayoutParams(890, 500));

            int cartoonID = mWordList.get(0).cartoonImgID + position;
            Bitmap content = gRes.getBitmapByWordid(cartoonID);
            ivCartoon.setImageBitmap(content);
            pager.addView(ivCartoon, 0);

            //메모리 해제할 View를 추가
            mRecycleList.add(new WeakReference<ViewGroup>(pager));
            return ivCartoon;
        }

        @Override
        public void destroyItem(ViewGroup pager, int position, Object view) {
            pager.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        //onDestory에서 쉽게 해제할 수 있도록 메소드 생성
        public void recycle() {
            for (WeakReference<ViewGroup> ref : mRecycleList) {
                Util.recursiveRecycle(ref.get());
            }
            mRecycleList.clear();
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup arg0) {
        }

        @Override
        public void finishUpdate(ViewGroup arg0) {
        }
    }
}