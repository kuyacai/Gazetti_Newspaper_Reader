package in.sahildave.gazetti.welcomescreen;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.util.UserSelectionUtil;

import java.util.HashMap;
import java.util.List;

public class WelcomeScreenExpListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "FEED";

    private Context mContext;
    private List<String> mListDataHeader;
    private HashMap<String, List<String>> mListDataChild;
    public static HashMap<Integer, boolean[]> mChildCheckStates;

    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    private String groupText;
    private String childText;
    private int lastExpandedGroupPosition = -1;

    public static ExpandableListView expandableList;
    TypedArray explist_np_images;

    public WelcomeScreenExpListAdapter(Context context, List<String> listDataHeader,
                                       HashMap<String, List<String>> listChildData) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;

        mChildCheckStates = UserSelectionUtil.getUserFeedSelection();

        explist_np_images = mContext.getResources().obtainTypedArray(R.array.explist_newspaper_images);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        groupText = getGroup(groupPosition).toString();

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.welcome_feed_select_explist_group, null);

            // Initialize the GroupViewHolder defined at the bottom of this
            // document
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mGroupText = (TextView) convertView
                    .findViewById(R.id.welcome_feed_select_explist_group_textview);
            convertView.setTag(groupViewHolder);
        } else {

            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setText(groupText);
        groupViewHolder.mGroupText.setTypeface(null, Typeface.BOLD);

        int imageResId = explist_np_images.getResourceId(groupPosition, -1);
        Drawable image = mContext.getResources().getDrawable(imageResId);
        image.setBounds(0, 0, image.getMinimumWidth(), image.getMinimumHeight());
        groupViewHolder.mGroupText.setCompoundDrawables(null, null, image, null);
        System.out.println(imageResId + ", " + image.hashCode());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {

        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.welcome_feed_select_explist_rowdetail, null);

            childViewHolder = new ChildViewHolder();

            childViewHolder.mChildText = (TextView) convertView
                    .findViewById(R.id.welcome_feed_select_explist_childTextView);

            childViewHolder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.welcome_feed_select_explist_childCheckBox);

            convertView.setTag(R.layout.welcome_feed_select_explist_rowdetail, childViewHolder);
        } else {

            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.welcome_feed_select_explist_rowdetail);
        }

        childViewHolder.mChildText.setText(childText);

        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
            childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);

        } else {
            boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];
            mChildCheckStates.put(mGroupPosition, getChecked);
            childViewHolder.mCheckBox.setChecked(false);
        }

        childViewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                getChecked[mChildPosition] = isChecked;
                mChildCheckStates.put(mGroupPosition, getChecked);
            }
        });

        return convertView;
    }

    public void setExpList(ExpandableListView explist) {
        expandableList = explist;
    }

    public HashMap<Integer, boolean[]> getClickedStates() {
        return mChildCheckStates;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return mListDataChild.get(getGroup(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListDataChild.get(getGroup(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

        if (groupPosition != lastExpandedGroupPosition) {
            expandableList.collapseGroup(lastExpandedGroupPosition);
        }
        expandableList.smoothScrollToPosition(groupPosition);
        lastExpandedGroupPosition = groupPosition;
        super.onGroupExpanded(groupPosition);

    }

    ;

    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {

        TextView mChildText;
        CheckBox mCheckBox;
    }
}