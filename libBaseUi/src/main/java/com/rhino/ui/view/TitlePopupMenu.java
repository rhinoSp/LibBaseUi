package com.rhino.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rhino.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The custom menu extends popup window.</p>
 *
 * @since Created by LuoLin on 2017/5/27.
 **/
public class TitlePopupMenu extends PopupWindow {

    /**
     * the popup window width
     **/
    private static final int DF_POPUP_WINDOW_WIDTH = 150;
    /**
     * the top or bottom margin of popup window
     **/
    private static final int DF_POPUP_WINDOW_MARGIN_TOP_OR_BOTTOM = 8;
    /**
     * the left or right margin of popup window
     **/
    private static final int DF_POPUP_WINDOW_MARGIN_RIGHT_OR_LEFT = 13;
    /**
     * the ListView item divider height
     **/
    private static final int DF_POPUP_WINDOW_DIVIDER_HEIGHT = 1;
    /**
     * the ListView item height
     **/
    private static final int DF_POPUP_WINDOW_ITEM_HEIGHT = 40;
    /**
     * the popup window background
     **/
    private static final int DF_POPUP_WINDOW_BACKGROUND_COLOR = 0xBB000000;

    /**
     * align this view bottom and align window left
     **/
    public static final int STYLE_ALIGN_THIS_BOTTOM_WINDOW_LEFT = 0x1;
    /**
     * align this view bottom and center
     **/
    public static final int STYLE_ALIGN_THIS_BOTTOM_CENTER = 0x2;
    /**
     * align this view bottom and align window right
     **/
    public static final int STYLE_ALIGN_THIS_BOTTOM_WINDOW_RIGHT = 0x3;
    /**
     * align this view top and align window left
     **/
    public static final int STYLE_ALIGN_THIS_TOP_WINDOW_LEFT = 0x4;
    /**
     * align this view top and align window center
     **/
    public static final int STYLE_ALIGN_THIS_TOP_CENTER = 0x5;
    /**
     * align this view top and align window right
     **/
    public static final int STYLE_ALIGN_THIS_TOP_WINDOW_RIGHT = 0x6;
    /**
     * the popup window style, STYLE_XXX
     **/
    private int mPopupStyle = STYLE_ALIGN_THIS_BOTTOM_WINDOW_RIGHT;

    /**
     * the screen width
     **/
    private int mScreenWidth;
    /**
     * the popup window margin top or bottom
     **/
    private int mMarginTopOrBottom;
    /**
     * the popup window margin right or left
     **/
    private int mMarginRightOrLeft;
    /**
     * the list divider height
     **/
    private int mListDividerHeight;
    /**
     * the popup window width
     **/
    private int mPopupWindowWidth;
    /**
     * the item height
     **/
    private int mItemHeight;

    /**
     * the list view
     **/
    private ListView mListView;
    /**
     * the list adapter
     **/
    private MenuListAdapter mMenuListAdapter;
    /**
     * the list of items
     **/
    private List<MenuItem> mMenuItems;
    /**
     * the max show item count
     **/
    private int mMaxShowItemCount = 10;
    /**
     * the flag for item update
     **/
    private boolean mIsNeedUpdateItem;
    /**
     * the click listener
     **/
    private OnItemOnClickListener mItemOnClickListener;

    public TitlePopupMenu(Context context) {
        this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public TitlePopupMenu(Context context, int width, int height) {
        setWidth(width);
        setHeight(height);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable());

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mPopupWindowWidth = (int) (DF_POPUP_WINDOW_WIDTH * dm.density + 0.5f);
        mMarginTopOrBottom = (int) (DF_POPUP_WINDOW_MARGIN_TOP_OR_BOTTOM * dm.density + 0.5f);
        mMarginRightOrLeft = (int) (DF_POPUP_WINDOW_MARGIN_RIGHT_OR_LEFT * dm.density + 0.5f);
        mListDividerHeight = (int) (DF_POPUP_WINDOW_DIVIDER_HEIGHT * dm.density + 0.5f);
        mItemHeight = (int) (DF_POPUP_WINDOW_ITEM_HEIGHT * dm.density + 0.5f);

        LinearLayout mLinearLayout = new LinearLayout(context);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setBackgroundColor(DF_POPUP_WINDOW_BACKGROUND_COLOR);

        mListView = new ListView(context);
        mListView.setDivider(new ColorDrawable(Color.WHITE));
        mListView.setDividerHeight(mListDividerHeight);
        mListView.setLayoutParams(new LinearLayout.LayoutParams(mPopupWindowWidth,
                LayoutParams.MATCH_PARENT));

        mMenuItems = new ArrayList<>();
        mMenuListAdapter = new MenuListAdapter(mMenuItems);
        mListView.setAdapter(mMenuListAdapter);

        mLinearLayout.addView(mListView);
        setContentView(mLinearLayout);
    }

    /**
     * show the menu
     *
     * @param view  view
     * @param style STYLE_XXX
     */
    public void show(View view, int style) {
        setPopupStyle(style);
        show(view);
    }

    /**
     * show the menu
     *
     * @param view view
     */
    public void show(View view) {
        if (isShowing()) {
            return;
        }
        if (mIsNeedUpdateItem) {
            mMenuListAdapter.refresh(mMenuItems);
            mIsNeedUpdateItem = false;
            if (mMaxShowItemCount < mMenuItems.size()) {
                mListView.getLayoutParams().height = mMaxShowItemCount * mItemHeight + (mMaxShowItemCount - 1) * mListDividerHeight;
            }
        }
        int[] location = getShowLocation(view);
        showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1]);
    }

    /**
     * get the popup window show location
     *
     * @param view view
     * @return int[]
     */
    private int[] getShowLocation(View view) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] mLocation = new int[2];
        mLocation[0] = mScreenWidth - mPopupWindowWidth - mMarginRightOrLeft;
        mLocation[1] = viewLocation[1] + view.getHeight() + mMarginTopOrBottom;
        if (STYLE_ALIGN_THIS_BOTTOM_WINDOW_LEFT == mPopupStyle) {
            mLocation[0] = mMarginRightOrLeft;
            mLocation[1] = viewLocation[1] + view.getHeight() + mMarginTopOrBottom;
        } else if (STYLE_ALIGN_THIS_BOTTOM_CENTER == mPopupStyle) {
            mLocation[0] = viewLocation[0] + view.getWidth() / 2 - mPopupWindowWidth / 2;
            mLocation[1] = viewLocation[1] + view.getHeight() + mMarginTopOrBottom;
        } else if (STYLE_ALIGN_THIS_BOTTOM_WINDOW_RIGHT == mPopupStyle) {
            mLocation[0] = mScreenWidth - mPopupWindowWidth - mMarginRightOrLeft;
            mLocation[1] = viewLocation[1] + view.getHeight() + mMarginTopOrBottom;
        } else if (STYLE_ALIGN_THIS_TOP_WINDOW_LEFT == mPopupStyle) {
            mLocation[0] = mMarginRightOrLeft;
            mLocation[1] = viewLocation[1] - mMarginTopOrBottom - getPopupWindowRealHeight();
        } else if (STYLE_ALIGN_THIS_TOP_CENTER == mPopupStyle) {
            mLocation[0] = viewLocation[0] + view.getWidth() / 2 - mPopupWindowWidth / 2;
            mLocation[1] = viewLocation[1] - mMarginTopOrBottom - getPopupWindowRealHeight();
        } else if (STYLE_ALIGN_THIS_TOP_WINDOW_RIGHT == mPopupStyle) {
            mLocation[0] = mScreenWidth - mPopupWindowWidth - mMarginRightOrLeft;
            mLocation[1] = viewLocation[1] - mMarginTopOrBottom - getPopupWindowRealHeight();
        }
        return mLocation;
    }

    /**
     * get the popup window real height
     *
     * @return height
     */
    public int getPopupWindowRealHeight() {
        if (null == mMenuItems) {
            mMenuItems = new ArrayList<>();
        }

        if (mMaxShowItemCount < mMenuItems.size()) {
            return mMaxShowItemCount * mItemHeight + (mMaxShowItemCount - 1) * mListDividerHeight;
        }
        return mMenuItems.size() * mItemHeight + (mMenuItems.size() - 1) * mListDividerHeight;
    }

    /**
     * set the popup style
     *
     * @param style STYLE_XXX
     */
    public void setPopupStyle(int style) {
        mPopupStyle = style;
    }

    /**
     * set the popup window width
     *
     * @param width width
     */
    public void setPopupWidth(int width) {
        mPopupWindowWidth = width;
        mListView.setLayoutParams(new LinearLayout.LayoutParams(mPopupWindowWidth,
                LayoutParams.WRAP_CONTENT));
    }

    /**
     * set menu item height
     *
     * @param height height
     */
    public void setItemHeight(int height) {
        mItemHeight = height;
    }

    /**
     * set ListView item divider height
     *
     * @param height height
     */
    public void setItemDividerHeight(int height) {
        mListDividerHeight = height;
        mListView.setDividerHeight(mListDividerHeight);
    }

    /**
     * set ListView item divider color
     *
     * @param color color
     */
    public void setDividerColor(int color) {
        mListView.setDivider(new ColorDrawable(color));
    }

    /**
     * set margin top or bottom
     *
     * @param margin margin
     */
    public void setMarginTopOrBottom(int margin) {
        mMarginTopOrBottom = margin;
    }

    /**
     * set margin right or left
     *
     * @param margin margin
     */
    public void setMarginRightOrLeft(int margin) {
        mMarginRightOrLeft = margin;
    }

    /**
     * add menu item
     *
     * @param itemImg  the item image drawable
     * @param itemDesc the item desc
     */
    public void addItem(Drawable itemImg, String itemDesc) {
        if (null == mMenuItems) {
            mMenuItems = new ArrayList<>();
        }
        mMenuItems.add(new MenuItem(itemImg, itemDesc));
        mIsNeedUpdateItem = true;
    }

    /**
     * clear all items
     */
    public void clearAllItems() {
        if (null == mMenuItems) {
            mMenuItems = new ArrayList<>();
        }
        mMenuItems.clear();
        mIsNeedUpdateItem = true;
    }

    /**
     * set the item click listener
     *
     * @param onItemOnClickListener listener
     */
    public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    /**
     * the item click listener
     **/
    public interface OnItemOnClickListener {
        void onItemClick(String desc);
    }

    public class MenuItem {
        public Drawable mDrawable;
        public String mDesc;

        public MenuItem(Drawable drawable, String desc) {
            this.mDrawable = drawable;
            this.mDesc = desc;
        }
    }

    private class ViewHolder {
        private View view;
        private ImageView mImageView;
        private TextView mTextView;

        public ViewHolder(ViewGroup parent) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_popup_menu, parent, false);
            mImageView = (ImageView) view.findViewById(R.id.title_popup_menu_img);
            mTextView = (TextView) view.findViewById(R.id.title_popup_menu_desc);
        }

        public void bindView(MenuItem item) {
            LayoutParams p = view.getLayoutParams();
            p.height = mItemHeight;

            mTextView.setText(item.mDesc);
            if (null != item.mDrawable) {
                mImageView.setImageDrawable(item.mDrawable);
                mImageView.setVisibility(View.VISIBLE);
            } else {
                mImageView.setVisibility(View.GONE);
            }
        }

        public void setOnItemClickListener() {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (mItemOnClickListener != null) {
                        mItemOnClickListener.onItemClick(mTextView.getText().toString());
                    }
                }
            });
        }
    }

    private class MenuListAdapter extends BaseAdapter {

        private List<MenuItem> mMenuItems;

        public MenuListAdapter(List<MenuItem> menuItems) {
            mMenuItems = menuItems;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null != convertView) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder(parent);
                convertView = holder.view;
                convertView.setTag(holder);
            }
            holder.bindView(mMenuItems.get(position));
            holder.setOnItemClickListener();
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return mMenuItems.get(position);
        }

        @Override
        public int getCount() {
            return mMenuItems.size();
        }

        public void refresh(List<MenuItem> menuItems) {
            mMenuItems = menuItems;
            notifyDataSetChanged();
        }
    }
}
