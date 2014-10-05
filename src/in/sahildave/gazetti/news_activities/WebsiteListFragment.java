package in.sahildave.gazetti.news_activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.news_activities.adapter.CustomAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebsiteListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener,
        ListView.OnScrollListener {

    // Tags
    private static final String TAG = "MasterDetail";
    private ItemSelectedCallback mItemSelectedCallback = sDummyItemSelectedCallback;
    private static final String PREFS_NAME = "QueryPrefs";
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    // Retained objects
    private static int mActivatedPosition = 1;

    // For HeadlinesList
    public String dbToSearch;
    private SwipeRefreshLayout mListViewContainer;
    private CustomAdapter customAdapter;
    private ListView mListView;
    private View headerOnList;
    private View footerOnList;
    private List<ParseObject> retainedList = new ArrayList<ParseObject>();

    // Header on Listview
    private String dateLastUpdatedString;

    // From Bundle
    private boolean mTwoPane;
    private String npIdString;
    private String npNameString;
    private String catIdString;
    private int listViewHeaderColor;

    // Booleans
    private boolean firstRun = false;
    private boolean flag_loading_old_data = false;

    // Network Info
    private NetworkInfo networkInfo;
    private Context context;

    public interface ItemSelectedCallback {
        public void onItemSelected(String headlineText, CustomAdapter customAdapter);
    }

    private static ItemSelectedCallback sDummyItemSelectedCallback = new ItemSelectedCallback() {
        @Override
        public void onItemSelected(String headlineText, CustomAdapter customAdapter) {
        }
    };

    public WebsiteListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Log.d(TAG, "ListFragment in onAttach ");

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof ItemSelectedCallback)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mTwoPane = getArguments().getBoolean("mTwoPane");
        npIdString = getArguments().getString("npId");
        npNameString = getArguments().getString("npName");
        catIdString = getArguments().getString("catId");
        listViewHeaderColor = getArguments().getInt("color");
        mItemSelectedCallback = (ItemSelectedCallback) activity;

        if (npIdString.equalsIgnoreCase("1")) {
            dbToSearch = "hindu_data";
        } else if (npIdString.equalsIgnoreCase("2")) {
            dbToSearch = "toi_data";
        } else if (npIdString.equalsIgnoreCase("3")) {
            dbToSearch = "fp_data";
        } else if (npIdString.equalsIgnoreCase("4")) {
            dbToSearch = "tie_data";
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.d(TAG, "ListFragment in onCreate ");
        firstRun = true;

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Log.d(TAG, "ListFragment in onCreateView ");
        View rootView = inflater.inflate(R.layout.fragment_website_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Log.d(TAG, "ListFragment in onViewCreated ");

        mListViewContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);

        mListViewContainer.setOnRefreshListener(this);
        mListViewContainer.setColorScheme(R.color.holo_blue_bright, R.color.holo_orange_light,
                R.color.holo_green_light, R.color.holo_red_light);

        mListView = getListView();
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnScrollListener(this);

        headerOnList = getActivity().getLayoutInflater().inflate(R.layout.header_view, null);
        headerOnList.setBackgroundColor(listViewHeaderColor);
        footerOnList = getActivity().getLayoutInflater().inflate(R.layout.footer_view, null);
        mListView.addHeaderView(headerOnList);

        customAdapter = new CustomAdapter(getActivity(), retainedList);
        SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(customAdapter);
        ScaleInAnimationAdapter animAdapterMultiple = new ScaleInAnimationAdapter(animAdapter);
        animAdapterMultiple.setAbsListView(mListView);

        mListView.setAdapter(animAdapterMultiple);

        mListView.addFooterView(footerOnList);
        mListView.removeFooterView(footerOnList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "ListFragment in onActivityCreated ");

        context = getActivity();

        if (mTwoPane && !firstRun) {
            // Log.d(TAG, "Reseting activatedPostion");
            setActivatedPosition(mActivatedPosition);
        }
        if (!firstRun) {
            // Log.d(TAG, "Reseting date");
            ((TextView) headerOnList).setText(dateLastUpdatedString);
        }

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (firstRun) {
                getNewListItems();
            }
        } else {
            ((TextView) headerOnList).setText("No Internet Connection");
        }

    }

    private void getNewListItems() {

        mListViewContainer.setRefreshing(true);
        ParseQuery<ParseObject> queryGetNewItems = ParseQuery.getQuery(dbToSearch);
        queryGetNewItems.whereEqualTo("newspaper_id", npIdString);
        queryGetNewItems.whereEqualTo("cat_id", catIdString);
        queryGetNewItems.orderByDescending("createdAt");

        if (retainedList.size() > 0) {
            ParseObject topObject = retainedList.get(0);
            Date topObjectCreatedAt = topObject.getCreatedAt();
            queryGetNewItems.whereGreaterThan("createdAt", topObjectCreatedAt);
        }

        if (firstRun) {
            queryGetNewItems.setLimit(15);
        }
        //TODO: Try-catch with message
        queryGetNewItems.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> articleObjectList, ParseException arg1) {

                if(arg1 == null){
                    retainedList.addAll(0, articleObjectList);

                    customAdapter.notifyDataSetChanged();

                    dateLastUpdatedString = "Last Updated: "
                            + (DateUtils.formatDateTime(context, System.currentTimeMillis(), //
                            DateUtils.FORMAT_SHOW_TIME //
                                    | DateUtils.FORMAT_SHOW_WEEKDAY //
                                    | DateUtils.FORMAT_SHOW_DATE //
                                    | DateUtils.FORMAT_ABBREV_WEEKDAY //
                                    | DateUtils.FORMAT_NO_NOON //
                                    | DateUtils.FORMAT_NO_MIDNIGHT)); //
                    ((TextView) headerOnList).setText(dateLastUpdatedString);
                    mListViewContainer.setRefreshing(false);

                    if (mTwoPane) {
                        mListView.performItemClick(customAdapter.getView(mActivatedPosition - 1, null, null),
                                mActivatedPosition, mActivatedPosition);

                    }
                    firstRun = false;
                } else {
                    Log.e(TAG, "Wrong while fetching - "+arg1.getMessage());
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void getOldListItems(Date lastObjectCreatedAtDate) {

        ParseQuery<ParseObject> queryGetOldItems = ParseQuery.getQuery(dbToSearch);
        queryGetOldItems.whereEqualTo("newspaper_id", npIdString);
        queryGetOldItems.whereEqualTo("cat_id", catIdString);
        queryGetOldItems.whereLessThan("createdAt", lastObjectCreatedAtDate);
        queryGetOldItems.setLimit(20);
        queryGetOldItems.orderByDescending("createdAt");

        queryGetOldItems.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> articleObjectList, ParseException arg1) {
                // Log.d(TAG, "Old Items articleObjectList " +
                // articleObjectList.size());

                retainedList.addAll(articleObjectList);
                customAdapter.notifyDataSetChanged();

                // Log.d(TAG, "articleObjectList Retained " +
                // retainedList.size());

                mListView.removeFooterView(footerOnList);
                mListViewContainer.setRefreshing(false);
                flag_loading_old_data = false;
            }

        });

    }

    @Override
    public void onRefresh() {
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(context, "Getting New Headlines", Toast.LENGTH_SHORT).show();
            getNewListItems();
        } else {
            Toast.makeText(context, "Cannot Refresh. No Connection", Toast.LENGTH_SHORT).show();
            mListViewContainer.setRefreshing(false);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Log.d(TAG, "onScrollStateChanged");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition = (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView
                .getChildAt(0).getTop();
        mListViewContainer.setEnabled(topRowVerticalPosition >= 0);
        // Log.d(TAG, firstVisibleItem + ", " + visibleItemCount + ", " +
        // totalItemCount);

        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            // Log.d(TAG, visibleItemCount + ", " + totalItemCount);

            // So that short lists dont load
            if (totalItemCount > visibleItemCount && flag_loading_old_data == false) {
                mListView.addFooterView(footerOnList);
                mListViewContainer.setRefreshing(true);
                flag_loading_old_data = true;
                // Log.d(TAG, "Last Position - " +
                // retainedList.get(totalItemCount - 2).getString("title"));

                Date lastObjectCreatedAtDate = retainedList.get(totalItemCount - 2).getCreatedAt();
                getOldListItems(lastObjectCreatedAtDate);
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Fragment in onDetach");

        // Reset the active callbacks interface to the dummy implementation.
        mItemSelectedCallback = sDummyItemSelectedCallback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment in onDestroy");
        mActivatedPosition = 1;
        dateLastUpdatedString = null;

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Log.d(TAG, "onListItemClick position, id +" + position + ", " + id);

        TextView headlineTextView = (TextView) view.findViewById(R.id.headline);
        String headlineText = (String) headlineTextView.getText();
        // Log.d(TAG, "onListItemClick view - " + headlineText);
        // view.setActivated(true);
        setActivatedPosition(position);
        mItemSelectedCallback.onItemSelected(headlineText, customAdapter);
    }

    private void setActivatedPosition(int position) {
        // Log.d(TAG, "ListFragment  SETACTIVATED");
        if (position == ListView.INVALID_POSITION) {
            mListView.setItemChecked(mActivatedPosition, false);
        } else {
            mListView.setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Log.d(TAG, "ListFragment onSaveInstanceState ");
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Log.d(TAG, "LISTFRAGMENT SAVING position " + mActivatedPosition);
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        outState.putLong("time", System.currentTimeMillis());
        super.onSaveInstanceState(outState);
    }
}
