    public static final int STATE_TYPE1_WAIT = 0;
    public static final int STATE_TYPE1_FINISH = 1;

    public static final int STEP_TYPE2 = 2;
    public static final int STATE_TYPE2_WAIT = 0;
    public static final int STATE_TYPE2_SUB_SHOW = 1;
    public static final int STATE_TYPE2_SUB_START = 2;
    public static final int STATE_TYPE2_WAIT2 = 3;

    public static final int STEP_TYPE3 = 3;
    public static final int STATE_TYPE3_WAIT = 0;
    public static final int STATE_TYPE3_SUB_SHOW = 1;
    public static final int STATE_TYPE3_SUB_START = 2;
    public static final int STATE_TYPE3_WAIT2 = 3;

    public static final int STEP_TYPE4 = 4;
    public static final int STATE_TYPE4_WAIT = 0;
    public static final int STATE_TYPE4_SUB_SHOW = 1;
    public static final int STATE_TYPE4_SUB_SHOW2 = 2;
    public static final int STATE_TYPE4_SUB_START = 3;
    public static final int STATE_TYPE4_WAIT2 = 4;

    private RelativeLayout mLyTitle = null;
    private ImageView mImgTitle = null;
    private RelativeLayout mLyTxt = null;
    private TextView mTxtSubTitle = null;
    private TextView mTxtTip = null;

    private RelativeLayout mLySubStep = null;
    private CustomButtonView mBntSubStepStart = null;
    private RelativeLayout mLySubStepPattern = null;
    private ImageView mImgSubStepPatternTitle = null;
    private TextView mTxtSubStepPatternContent = null;
    private RelativeLayout mLySubStepSentence = null;
    private TextView mTxtSubStepSentenceTitle = null;
    private TextView mTxtSubStepSentenceSubTitle = null;
    private TextView mTxtSubStepSentenceContent = null;
    private int mSndID;

    private int mType = 0;
    private int mTxtType = 0;

    private int mLevel;
    private int mEpisode;
    private int mChapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.start_enter, R.anim.delay2);

        mLevel = gData.getStudyLevel();
        mEpisode = gData.getStudyEpisode();
        mChapter = gData.getStudyChapter();

        mType = getIntentParamAt(0, Define.STUDY_CONCEPT);
        switch (mType) {
            case Define.STUDY_CONCEPT:
            case Define.STUDY_PATTERN:
            case Define.STUDY_SENTENCE:
                setContentView(R.layout.popup_flow_type1);
                break;
            case Define.STUDY_SENTENCE_PRACTICE:
                setContentView(R.layout.popup_flow_type2);
                break;
		case Define.STUD : {

}
        }

        final RelativeLayout mLyStepPopupBg = (RelativeLayout) findViewById(R.id.layoutFlowPopup);
        if (mLyStepPopupBg != null) {
            TSCLIntroUnit iu = new TSCLIntroUnit();
            gDatabase.getMainDB().getChapterInfo(iu, mLevel, mEpisode, mChapter);

            mTxtType = getIntentParamAt(1, 0);

            mLyStepPopupBg.setBackgroundResource(IMG_BG[mType]);
            mLyTitle = (RelativeLayout) mLyStepPopupBg.findViewById(R.id.layoutStepPopupTitle);
            mImgTitle = (ImageView) mLyTitle.findViewById(R.id.imgStepPopup);
            mImgTitle.setImageResource(IMG_STEP[mType]);
            mLyTxt = (RelativeLayout) findViewById(R.id.layoutStepPopupBg);

            mTxtSubTitle = (TextView) findViewById(R.id.txtStepPopupTitle);
            if (mTxtSubTitle != null) {
                mTxtSubTitle.setTypeface(getFont(FontType.CREGOTHIC_B));
                mTxtSubTitle.setText(iu.chapTitle.replaceAll("/", "\n"));
                mTxtSubTitle.setBackgroundResource(mType == Define.STUDY_PATTERN ?
                        R.drawable.sub_intro_sub_textbox_2 : R.drawable.sub_intro_sub_textbox_1);
            }

            mTxtTip = (TextView) findViewById(R.id.txtStepPopup);
            mTxtTip.setTypeface(getFont(FontType.CREGOTHIC_M));
            mTxtTip.setShadowLayer(1, 0, 2, 0x22000000);
            mTxtTip.setText(TXT_STEP[mType][0]);

            if (mType == Define.STUDY_SENTENCE_PRACTICE) {
                ArrayList<JRMMathUnit> units = new ArrayList<JRMMathUnit>();
                gDatabase.getMainDB().getWordSentencePList(units, mLevel, mEpisode, mChapter);
                for (int i = 0; i < ID_PATTERN_CONTENT.length; i++) {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(ID_LAYOUT_PATTERN_TITLE[i]);
                    relativeLayout.setVisibility(View.VISIBLE);
                    TextView textView = (TextView) relativeLayout.findViewById(ID_PATTERN_CONTENT[i]);
                    textView.setTypeface(getFont(FontType.CREGOTHIC_B));
                    textView.setText(units.get(i * 2).quizInstruction);
                    relativeLayout.findViewById(ID_PATTERN_CHECK[i]).setVisibility(View.INVISIBLE);
                }
            }

            mLySubStep = (RelativeLayout) findViewById(R.id.layoutSubStep);
            if (mLySubStep != null) {
                if (mType == Define.STUDY_PATTERN || mType == Define.STUDY_SENTENCE) {
                    mLySubStep.setVisibility(View.VISIBLE);
                    mLySubStep.setTranslationY(-505);
                    mBntSubStepStart = (CustomButtonView) mLySubStep.findViewById(R.id.btnSubStepStart);
                    mBntSubStepStart.setClickAniMode(CustomButtonView.MODE_CLICK_ANI.NONE);
                    setCustomButton(mBntSubStepStart);
                    mLySubStepPattern = (RelativeLayout) mLySubStep.findViewById(R.id.layoutSubPattern);
                    mLySubStepSentence = (RelativeLayout) mLySubStep.findViewById(R.id.layoutSubSentence);
                    if (mType == Define.STUDY_PATTERN) {
                        mLySubStepPattern.setVisibility(View.VISIBLE);
                        mLySubStepSentence.setVisibility(View.INVISIBLE);

                        int drawableID = getResources().getIdentifier("sub2_intro_board_title_" + (mTxtType + 1), "drawable", getPackageName());
                        mImgSubStepPatternTitle = (ImageView) mLySubStepPattern.findViewById(R.id.imgSubPatternTitle);
                        mImgSubStepPatternTitle.setImageResource(drawableID);
                        mTxtSubStepPatternContent = (TextView) mLySubStepPattern.findViewById(R.id.txtSubPatternContent);
                        mTxtSubStepPatternContent.setTypeface(getFont(FontType.CREGOTHIC_M));
                        if (mTxtType == 0) {
                            ArrayList<JRMMathUnit> wordList = new ArrayList<JRMMathUnit>();
                            ArrayList<JRMQuizUnit> mQuizCorrectList = gData.getStudyQuizSet(mLevel, mEpisode, mChapter, Define.WLT_PATTERN);
                            if (true) {// mQuizCorrectList == null || mQuizCorrectList.size() < 1) { // 처음 실행시 필수 문제  TODO 나중에 맞춤 오답
                                if (mQuizCorrectList == null) {
                                    mQuizCorrectList = new ArrayList<JRMQuizUnit>();
                                }

                                gDatabase.getMainDB().getWordPatternList(wordList, mLevel, mEpisode, mChapter, 1);
                            } else {

                            }

                            mTxtSubStepPatternContent.setText(Html.fromHtml("<font color='#F74856'>" +
                                    wordList.size() + "문제</font>에 도전합니다."));
                        } else if (mTxtType == 1) {
                            mTxtSubStepPatternContent.setText(Html.fromHtml("선택한 " +
                                    "<font color='#F74856'>약점 유형</font>을 도움말과 함께<br>차근차근 배워봅시다."));
                        } else {
                            ArrayList<JRMMathUnit> wordList = new ArrayList<JRMMathUnit>();
                            ArrayList<JRMQuizUnit> mQuizCorrectList = gData.getStudyQuizSet(mLevel, mEpisode, mChapter, Define.WLT_PATTERN);
                            gDatabase.getMainDB().getWordPatternFinalList(wordList, mLevel, mEpisode, mChapter);
                            for (int i = 0; i < mQuizCorrectList.size(); i++) {
                                JRMQuizUnit unit = mQuizCorrectList.get(i);
                                int existType = Define.NONE;
                                boolean isExistWrong = false;
                                for (int k = 0; k < wordList.size(); k++) {
                                    JRMMathUnit temp = wordList.get(k);
                                    if (unit.quizSubType == temp.quizSubType) {
                                        existType = unit.quizSubType;
                                        break;
                                    }
                                }

                                if (existType == Define.NONE) {
                                    for (int k = 0; k < mQuizCorrectList.size(); k++) {
                                        JRMQuizUnit temp = mQuizCorrectList.get(k);
                                        if (unit.quizSubType == temp.quizSubType) {
                                            if (temp.isCorrect == 0) {
                                                isExistWrong = true;
                                                break;
                                            }
                                        }
                                    }

                                    if (isExistWrong) { // 다지기 필수 유형은 아니고 틀린 유형일 때
                                        ArrayList<JRMMathUnit> randomWordList = new ArrayList<JRMMathUnit>();
                                        gDatabase.getMainDB().getWordPatternList(randomWordList, mLevel, mEpisode, mChapter, 0, unit.quizSubType);
                                        wordList.add(0, new JRMMathUnit(randomWordList.get(0)));
                                    }
                                }
                            }

                            mTxtSubStepPatternContent.setText(Html.fromHtml("목표점수 : 90점<br><font color='#F74856'>" +
                                    wordList.size() + "문제</font>로 다시 도전합니다!"));
                        }
                    } else {
                        ArrayList<JRMMathUnit> wordList = new ArrayList<JRMMathUnit>();
                        gDatabase.getMainDB().getWordSentenceList(wordList, mLevel, mEpisode, mChapter);
                        JRMMathUnit unit = wordList.get(mTxtType);
                        mSndID = unit.wordID;
                        String[] title = unit.quizInstruction.split("/");

                        mLySubStepPattern.setVisibility(View.INVISIBLE);
                        mLySubStepSentence.setVisibility(View.VISIBLE);

                        mTxtSubStepSentenceTitle = (TextView) mLySubStepSentence.findViewById(R.id.txtSubSentenceTitle);
                        mTxtSubStepSentenceTitle.setTypeface(getFont(FontType.CREGOTHIC_B));
                        mTxtSubStepSentenceTitle.setShadowLayer(1, 0, 2, 0x22000000);
                        mTxtSubStepSentenceTitle.setText("문제해결 " + (mTxtType + 1));
                        mTxtSubStepSentenceSubTitle = (TextView) mLySubStepSentence.findViewById(R.id.txtSubSentenceSubTitle);
                        mTxtSubStepSentenceSubTitle.setTypeface(getFont(FontType.CREGOTHIC_M));
                        mTxtSubStepSentenceSubTitle.setText(Html.fromHtml(title[0]));
                        shrinkToFit(mTxtSubStepSentenceSubTitle, 360);
                        mTxtSubStepSentenceContent = (TextView) mLySubStepSentence.findViewById(R.id.txtSubSentenceContent);
                        mTxtSubStepSentenceContent.setTypeface(getFont(FontType.CREGOTHIC_M));
                        mTxtSubStepSentenceContent.setText(Html.fromHtml(title[1]));
                    }

                    mLySubStep.findViewById(R.id.imgTeacher).setVisibility(View.INVISIBLE);
                } else {
                    mLySubStep.setVisibility(View.INVISIBLE);
                }
            }
//                gRes.playEffect(ResManager.SE_FLOW);
            if (mType == Define.STUDY_PATTERN) {
                gRes.playEffect(ResManager.SE_TYPE_STEPPOPUP2);
            } else if (mType == Define.STUDY_SENTENCE) {
                gRes.playEffect(ResManager.SE_TYPE_STEPPOPUP3);
            } else if (mType == Define.STUDY_SENTENCE_PRACTICE) {
                gRes.playEffect(ResManager.SE_TYPE_STEPPOPUP4);
            } else {
                gRes.playEffect(ResManager.SE_TYPE_STEPPOPUP1);
            }
        }
    }

    @Override
    protected void onCreateSteps() {
        try {
            makeStep(STEP_TYPE1, "4초후 종료");
            makeStep(STEP_TYPE2, "서브타입 FLOW");
            makeStep(STEP_TYPE3, "서브타입 FLOW");
            makeStep(STEP_TYPE4, "서브타입 FLOW");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onOrganizeFlow() {
        if (mType == Define.STUDY_PATTERN) {
            pushFlow(STEP_TYPE2);
        } else if (mType == Define.STUDY_SENTENCE_PRACTICE) {
            pushFlow(STEP_TYPE3);
        } else if (mType == Define.STUDY_SENTENCE) {
            pushFlow(STEP_TYPE4);
        } else {
            pushFlow(STEP_TYPE1);
        }
    }

    @Override
    protected void initStep(int nFlow, int stepID, Step step) {
        if (stepID == STEP_TYPE1) {

            step.clearState();
            step.pushStateMillis(4000)
                    .pushState(Step.WAIT_EVENT);
        } else if (stepID == STEP_TYPE2) {

            step.clearState();
            step.pushStateMillis(4000)
                    .pushStateMillis(2000)
                    .pushStateMillis(1000)
                    .pushState(Step.WAIT_EVENT);
        } else if (stepID == STEP_TYPE4) {

            step.clearState();
            step.pushStateMillis(4000)
                    .pushStateMillis(2000)
                    .pushStateMillis(gRes.getSoundDurationByWordID(mSndID) + 1000)
                    .pushStateMillis(1000)
                    .pushState(Step.WAIT_EVENT);
        } else if (stepID == STEP_TYPE3) {

            step.clearState();
            step.pushStateMillis(3000)
                    .pushStateMillis(1000)
                    .pushStateMillis(2000)
                    .pushState(Step.WAIT_EVENT);
        }

        super.initStep(nFlow, stepID, step);
    }

    @Override
    protected void initState(final int stepID, final int stateID) {
        super.initState(stepID, stateID);

        if (stepID == STEP_TYPE1) {
            if (stateID == STATE_TYPE1_FINISH) {
                hideAni();
            }
        } else if (stepID == STEP_TYPE2) {
            if (stateID == STATE_TYPE2_SUB_SHOW) {
                mLySubStep.setVisibility(View.VISIBLE);
                mAnimation.moveYAnim(mLySubStep, 800, 0, null, -505, 0);
                mAnimation.fadeOutAnim(mTxtTip, 800);
                mAnimation.start();
            } else if (stateID == STATE_TYPE2_SUB_START) {
                Drawable d = getResources().getDrawable(R.drawable.sub2_intro_board_bt_start);
                setImageButton(mBntSubStepStart, R.drawable.sub2_intro_board_bt_start, R.drawable.sub2_intro_board_bt_start_touch);

                mBntSubStepStart.setPivotX(d.getIntrinsicWidth() / 2);
                mBntSubStepStart.setPivotY(0f);
                mBntSubStepStart.setScaleX(0.27f);
                mBntSubStepStart.setScaleY(0.30f);
                PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f);
                PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f);
                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mBntSubStepStart, pvhSX, pvhSY);
                anim.setDuration(350);
                anim.setInterpolator(new OvershootInterpolator());
                anim.start();
            }
        } else if (stepID == STEP_TYPE4) {
            if (stateID == STATE_TYPE4_SUB_SHOW) {
                mLySubStep.setVisibility(View.VISIBLE);
                mAnimation.moveYAnim(mLySubStep, 800, 0, null, -505, 0);
                mAnimation.fadeOutAnim(mTxtTip, 800);
                mAnimation.start();
            } else if (stateID == STATE_TYPE4_SUB_SHOW2) {
                mAnimation.moveFromRightAnim(mLySubStep.findViewById(R.id.imgTeacher), 300);
                mAnimation.start();

                gRes.playSoundByWordID(mSndID);
            } else if (stateID == STATE_TYPE4_SUB_START) {
                Drawable d = getResources().getDrawable(R.drawable.sub2_intro_board_bt_start);
                setImageButton(mBntSubStepStart, R.drawable.sub2_intro_board_bt_start, R.drawable.sub2_intro_board_bt_start_touch);

                mBntSubStepStart.setPivotX(d.getIntrinsicWidth() / 2);
                mBntSubStepStart.setPivotY(0f);
                mBntSubStepStart.setScaleX(0.27f);
                mBntSubStepStart.setScaleY(0.30f);
                PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f);
                PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f);
                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mBntSubStepStart, pvhSX, pvhSY);
                anim.setDuration(350);
                anim.setInterpolator(new OvershootInterpolator());
                anim.start();
            }
        } else if (stepID == STEP_TYPE3) {
            if (stateID == STATE_TYPE3_SUB_SHOW) {
                /*for (int i = 0; i < ID_PATTERN_CONTENT.length; i++) {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(ID_LAYOUT_PATTERN_TITLE[i]);
                    mAnimation.scaleInAnim(relativeLayout, 150, new OvershootInterpolator());
                }
                mAnimation.start(WMAnimation.MODE_PLAY.SEQUENTIALLY);*/
                for (int i = 0; i < ID_PATTERN_CONTENT.length; i++) {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(ID_LAYOUT_PATTERN_TITLE[i]);
                    if (i == mTxtType) {
                        mAnimation.scaleInAnim(relativeLayout.findViewById(ID_PATTERN_CHECK[i]), 150, new OvershootInterpolator());
                        mAnimation.start();
                        break;
                    }
                }
            } else if (stateID == STATE_TYPE3_SUB_START) {
                /*for (int i = 0; i < ID_PATTERN_CONTENT.length; i++) {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(ID_LAYOUT_PATTERN_TITLE[i]);
                    if (i == mTxtType) {
                        mAnimation.fadeInAnim(relativeLayout.findViewById(ID_PATTERN_CHECK[i]), 500);
                        mAnimation.start();
                        break;
                    }
                }*/
            } else if (stateID == STATE_TYPE3_WAIT2) {
                hideAni();
            }
        }
    }

    private void hideAni() {
        moveStudy();
        overridePendingTransition(R.anim.move_from_right, R.anim.move_to_left);
    }

    @Override
    protected void onClickButton(View v) {
        if (v == mBntSubStepStart) {
            hideAni();
        }

        super.onClickButton(v);
    }

    private void moveStudy() {
        switch (mType) {
            case Define.STUDY_CONCEPT:
                moveActivity(StudyConceptActivity.class, Define.NONE, true);
//                moveActivity(StudyConceptQuizActivity.class, Define.NONE, true); //TODO 제거
                break;
            case Define.STUDY_PATTERN:
                moveActivityWithParam(StudyPatternActivity.class, Define.NONE, true, mTxtType);
                break;
            case Define.STUDY_SENTENCE:
                moveActivityWithParam(StudySentenceActivity.class, Define.NONE, true, mTxtType);
                break;
            case Define.STUDY_SENTENCE_PRACTICE:
                moveActivityWithParam(StudySentencePActivity.class, Define.NONE, true, mTxtType);
                break;
        }
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
}
