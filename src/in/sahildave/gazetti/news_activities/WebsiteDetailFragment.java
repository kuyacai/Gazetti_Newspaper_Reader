package in.sahildave.gazetti.news_activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.news_activities.adapter.CustomAdapter;
import in.sahildave.gazetti.news_activities.fetch.firstPost;
import in.sahildave.gazetti.news_activities.fetch.hindu;
import in.sahildave.gazetti.news_activities.fetch.indianExpress;
import in.sahildave.gazetti.news_activities.fetch.toi;

public class WebsiteDetailFragment extends Fragment {
    private static final String TAG = "DFRAGMENT";
    private static final String TAG_ASYNC = "ASYNC";

    public static final String HEADLINE_CLICKED = "headlineClicked";

    private String mImageURL;
    private String mArticleURL;
    private String mArticlePubDate;
    private String headlineClicked = "";
    String[] articleContent;

    private TaskCallbacks mCallbacks;

    private LinearLayout mScrollToReadLayout;

    private GestureDetectorCompat mDetector;
    private Animation slide_down;

    private String npNameString;
    private MyAsyncTask mTask;

    static interface TaskCallbacks {
        void onPreExecute(View rootView);

        void setHeaderStub(View headerStub);

        void onPostExecute(String[] result);
    }

    public WebsiteDetailFragment() {
//        Log.d(TAG, "DetailFragment constructor");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof TaskCallbacks)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }
//        Log.d(TAG, "DetailFragment onAttach");

        mCallbacks = (TaskCallbacks) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        npNameString = getArguments().getString("npName");

        if (getArguments().containsKey(HEADLINE_CLICKED)) {
            headlineClicked = getArguments().getString(HEADLINE_CLICKED);
            mArticleURL = CustomAdapter.linkMap.get(headlineClicked);
            mArticlePubDate = CustomAdapter.pubDateMap.get(headlineClicked);
        }

        mDetector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.animator.slide_down);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "DetailFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_website_detail, container, false);

        ImageButton mNewspaperTile = (ImageButton) rootView.findViewById(R.id.newspaperTile);
        TextView mViewInBrowser = (TextView) rootView.findViewById(R.id.viewInBrowser);
        // TODO: category in subtitle

        if (npNameString.equals("The Hindu")) {
            mNewspaperTile.setImageResource(R.drawable.ic_hindu);
        } else if (npNameString.equals("The Times of India")) {
            mNewspaperTile.setImageResource(R.drawable.ic_toi);
        }

        mNewspaperTile.setOnClickListener(webViewCalled);
        mViewInBrowser.setOnClickListener(webViewCalled);

        mScrollToReadLayout = (LinearLayout) rootView.findViewById(R.id.scrollToReadLayout);
        mScrollToReadLayout.setVisibility(View.INVISIBLE);

        Log.d(TAG, "Async called");
        mTask = new MyAsyncTask(rootView);
        mTask.execute();

        ScrollView mScrollView = (ScrollView) rootView.findViewById(R.id.scroller);

        mScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "ScrollView ACTION_UP");
                    if (mScrollToReadLayout.getVisibility() == View.VISIBLE) {
                        mScrollToReadLayout.startAnimation(slide_down);
                        mScrollToReadLayout.setVisibility(View.INVISIBLE);
                    }
                    return mDetector.onTouchEvent(event);
                }

                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "DetailFragment onDetach");
        super.onDetach();
        mCallbacks = null;
    }

    private OnClickListener webViewCalled = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mArticleURL)));

        }
    };

    public class MyAsyncTask extends AsyncTask<Void, String, String[]> {

        View rootView;

        public MyAsyncTask(View rootView) {
            Log.d(TAG_ASYNC, "Async Constructor");
            this.rootView = rootView;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG_ASYNC, "Async onPreExecute");

            if (mCallbacks != null) {
                mCallbacks.onPreExecute(rootView);
            }

        }

        @Override
        protected String[] doInBackground(Void... params) {

            if (npNameString.equalsIgnoreCase("The Hindu")) {
                hindu hinduObject = new hindu(mArticleURL);
                articleContent = hinduObject.getHinduArticleContent();
                mImageURL = articleContent[1];
            } else if (npNameString.equalsIgnoreCase("The Times of India")) {
                toi toiObject = new toi(mArticleURL);
                articleContent = toiObject.getToiArticleContent();
                mImageURL = articleContent[1];
            } else if (npNameString.equalsIgnoreCase("First Post")) {
                firstPost fpObject = new firstPost(mArticleURL);
                articleContent = fpObject.getFirstPostArticleContent();
                mImageURL = articleContent[1];
            } else if (npNameString.equalsIgnoreCase("The Indian Express")) {
                indianExpress tieObject = new indianExpress(mArticleURL);
                articleContent = tieObject.getTIEArticleContent();
                mImageURL = articleContent[1];
            }

            if (articleContent[0] == null || articleContent[0].length() == 0) {
                articleContent[0] = headlineClicked;
            }

            if (articleContent[2] == null || articleContent[2].length() == 0) {
                articleContent[2] = "Sorry, this story is not supported for mobile view. Try to read in the browser";
            }

            return articleContent;
        }

        @Override
        protected void onPostExecute(String[] result) {
            Log.d(TAG_ASYNC, "Async onPostExecute");

            if (mCallbacks != null) {
                View headerStub;
                if (mImageURL == null) {
                    headerStub = ((ViewStub) rootView.findViewById(R.id.article_title_stub_import)).inflate();
                } else {
                    headerStub = ((ViewStub) rootView.findViewById(R.id.article_header_stub_import)).inflate();
                }
                mCallbacks.setHeaderStub(headerStub);
                mCallbacks.onPostExecute(result);
            }
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            // Log.d(TAG, "ONDOWN");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            // Log.d(TAG, "ONFLING");
            return super.onFling(event1, event2, velocityX, velocityY);
        }
    }
}