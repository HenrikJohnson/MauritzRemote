package com.underscoreresearch.mauritzremote.rooms.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.underscoreresearch.mauritzremote.CovertArtCache;
import com.underscoreresearch.mauritzremote.R;
import com.underscoreresearch.mauritzremote.RemoteService;
import com.underscoreresearch.mauritzremote.config.Settings;
import com.underscoreresearch.mauritzremote.library.LibraryItem;
import com.underscoreresearch.mauritzremote.library.QueueItem;
import com.underscoreresearch.mauritzremote.view.BackAwareEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henri on 1/29/2017.
 */

public class MediaCenterLibraryFragment extends DeviceFragment {
    private static final String[] searchTypes = new String[] {
            "Entered",
            "Toplist",
            "Artist",
            "Title",
            "Last Played",
            "Album",
            "Backlog"
    };
    private View rootView;
    private RecyclerView queueRecyclerView;
    private LinearLayoutManager queueLayoutManager;
    private RecyclerViewTouchActionGuardManager queueRecyclerViewTouchActionGuardManager;
    private RecyclerViewDragDropManager queueRecyclerViewDragDropManager;
    private RecyclerViewSwipeManager queueRecyclerViewSwipeManager;
    private RecyclerView.Adapter wrappedAdapter;
    private String queue;
    private Gson gson = new Gson();
    private List<QueueItem> items = new ArrayList<>();
    private DraggableSwipeableAdapter adapter;
    private Spinner searchTypeSpinner;
    private BackAwareEditText searchCriteriaEdit;
    private RecyclerView libraryRecyclerView;
    private String lastSearchKey;

    private Handler handler = new Handler();

    private void setupButtons(View view) {
        setupButton(view, R.id.btn_rewind, R.string.cmd_Rewind_In_Media_Center);
        setupButton(view, R.id.btn_play, R.string.cmd_Play_In_Media_Center);
        setupButton(view, R.id.btn_forward, R.string.cmd_Fast_Forward_In_Media_Center);
        setupButton(view, R.id.btn_skip, R.string.cmd_Go_To_Next_In_Media_Center);
    }

    public void setEditEnabled(boolean enabled) {
        View parent = getView();
        if (parent != null) {
            View view = parent.findViewById(R.id.searchCriteria);
            if (view != null) {
                view.setEnabled(enabled);
                if (!enabled) {
                    view.clearFocus();
                }
            }
        }
    }

    static public class ViewUtils {
        public static boolean hitTest(View v, int x, int y) {
            final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
            final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
            final int left = v.getLeft() + tx;
            final int right = v.getRight() + tx;
            final int top = v.getTop() + ty;
            final int bottom = v.getBottom() + ty;

            return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
        }
    }

    public static class ViewHolder extends AbstractDraggableSwipeableItemViewHolder {
        public ConstraintLayout mContainer;
        public ImageView mDragHandle;
        public TextView mArtistView;
        public TextView mTitleView;
        public TextView mAlbumView;

        public ViewHolder(View v) {
            super(v);
            mContainer = (ConstraintLayout) v.findViewById(R.id.container);
            mDragHandle = (ImageView) v.findViewById(R.id.drag_handle);
            mArtistView = (TextView) v.findViewById(R.id.artist);
            mTitleView = (TextView) v.findViewById(R.id.title);
            mAlbumView = (TextView) v.findViewById(R.id.album);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }

        public void applyItem(LibraryItem item) {
            if (item != itemView.getTag()) {
                // set text
                mArtistView.setText(item.getArtist());
                String title = item.getTitle();
                if (title != null && title.startsWith("Episode "))
                    title = title.substring(7);
                mTitleView.setText(title);
                String album = item.getAlbum();
                if (album != null && album.startsWith("Season "))
                    album = "S" + album.substring(7);
                mAlbumView.setText(album);
                itemView.setTag(item);

                mDragHandle.setImageBitmap(null);
                if (item.coverUrl != null) {
                    CovertArtCache.loadBitmap(itemView.getContext(), item.coverUrl, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            mDragHandle.setImageBitmap(response);
                        }
                    });
                }
            }
        }
    }

    static class DraggableSwipeableAdapter
            extends RecyclerView.Adapter<ViewHolder>
            implements DraggableItemAdapter<ViewHolder>,
            SwipeableItemAdapter<ViewHolder> {
        private static final String TAG = "MyDSItemAdapter";

        // NOTE: Make accessible with short name
        private interface Draggable extends DraggableItemConstants {
        }
        private interface Swipeable extends SwipeableItemConstants {
        }

        private EventListener mEventListener;
        private View.OnClickListener mItemViewOnClickListener;
        private View.OnClickListener mSwipeableViewContainerOnClickListener;
        private List<QueueItem> items;

        public interface EventListener {
            void onItemRemoved(QueueItem item);
            void onItemMoved(QueueItem item, QueueItem afterItem);
            void onItemViewClicked(QueueItem item);
        }

        public DraggableSwipeableAdapter(List<QueueItem> items) {
            this.items = items;
            mItemViewOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemViewClick(v);
                }
            };
            mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSwipeableViewContainerClick(v);
                }
            };

            // DraggableItemAdapter and SwipeableItemAdapter require stable ID, and also
            // have to implement the getItemId() method appropriately.
            setHasStableIds(true);
        }

        private void onItemViewClick(View v) {
            if (mEventListener != null) {
                mEventListener.onItemViewClicked((QueueItem) v.getTag());
            }
        }

        private void onSwipeableViewContainerClick(View v) {
            onItemViewClick(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).getQueueId();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View v = inflater.inflate(R.layout.queue_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final QueueItem item = items.get(position);

            holder.applyItem(item);
            holder.itemView.setOnClickListener(mItemViewOnClickListener);
            holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);

            // set background resource (target view ID: container)
            final int dragState = holder.getDragStateFlags();
            final int swipeState = holder.getSwipeStateFlags();

            if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) ||
                    ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0)) {
                int bgResId;

                if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                    bgResId = R.drawable.bg_item_dragging_active_state;

                    // need to clear drawable state here to get correct appearance of the dragging item.
                    // holder.mContainer.getForeground().setState(new int[0]);
                } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                    bgResId = R.drawable.bg_item_dragging_state;
                } else if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
                    bgResId = R.drawable.bg_item_swiping_active_state;
                } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
                    bgResId = R.drawable.bg_item_swiping_state;
                } else {
                    bgResId = R.drawable.bg_item_normal_state;
                }

                holder.mContainer.setBackgroundResource(bgResId);
            }

            // set swiping properties
            holder.setSwipeItemHorizontalSlideAmount(0);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

            if (fromPosition == toPosition) {
                return;
            }

            QueueItem item = items.get(fromPosition);
            items.remove(fromPosition);
            QueueItem afterItem = items.get(toPosition - 1);
            items.add(toPosition, item);
            if (mEventListener != null) {
                mEventListener.onItemMoved(item, afterItem);
            }

            notifyItemMoved(fromPosition, toPosition);

//            item.setQueueId(items.get(toPosition + 1).getQueueId());
//            for (int i = toPosition + 1; i < items.size(); i++) {
//                items.get(i).setQueueId(items.get(i).getQueueId()+1);
//            }
        }

        @Override
        public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
            if (position == 0) {
                return false;
            }

            // x, y --- relative from the itemView's top-left
            final View containerView = holder.mContainer;
            final View dragHandleView = holder.mDragHandle;

            final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
            final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

            return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder, int position) {
            // no drag-sortable range specified
            return null;
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            if (dropPosition == 0) {
                return false;
            }
            return true;
        }

        @Override
        public void onItemDragStarted(int position) {

        }

        @Override
        public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {

        }

        @Override
        public int onGetSwipeReactionType(ViewHolder holder, int position, int x, int y) {
            if (onCheckCanStartDrag(holder, position, x, y)) {
                return Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
            } else {
                return Swipeable.REACTION_CAN_SWIPE_RIGHT;
            }
        }

        @Override
        public void onSwipeItemStarted(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public void onSetSwipeBackground(ViewHolder holder, int position, int type) {
            int bgRes = 0;
            switch (type) {
                case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                    bgRes = R.drawable.bg_swipe_item_neutral;
                    break;
                case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                    bgRes = R.drawable.bg_swipe_item_right;
                    break;
            }

            holder.itemView.setBackgroundResource(bgRes);
        }

        @Override
        public SwipeResultAction onSwipeItem(ViewHolder holder, final int position, int result) {
            Log.d(TAG, "onSwipeItem(position = " + position + ", result = " + result + ")");

            switch (result) {
                // swipe right
                case Swipeable.RESULT_SWIPED_RIGHT:
                    // not pinned --- remove
                    return new SwipeRightResultAction(this, position);
                // other --- do nothing
                case Swipeable.RESULT_CANCELED:
                default:
                    return null;
            }
        }

        public EventListener getEventListener() {
            return mEventListener;
        }

        public void setEventListener(EventListener eventListener) {
            mEventListener = eventListener;
        }

        private static class SwipeRightResultAction extends SwipeResultActionRemoveItem {
            private DraggableSwipeableAdapter mAdapter;
            private final int mPosition;

            SwipeRightResultAction(DraggableSwipeableAdapter adapter, int position) {
                mAdapter = adapter;
                mPosition = position;
            }

            @Override
            protected void onPerformAction() {
                super.onPerformAction();

                if (mAdapter.mEventListener != null) {
                    mAdapter.mEventListener.onItemRemoved(mAdapter.items.remove(mPosition));
                }
                mAdapter.notifyItemRemoved(mPosition);
            }

            @Override
            protected void onSlideAnimationEnd() {
                super.onSlideAnimationEnd();
            }

            @Override
            protected void onCleanUp() {
                super.onCleanUp();
                // clear the references
                mAdapter = null;
            }
        }
    }

    public class LibraryAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final View.OnClickListener mItemViewOnClickListener;
        private final View.OnClickListener mSwipeableViewContainerOnClickListener;
        private LibraryItem[] items;

        public LibraryAdapter(LibraryItem[] items) {
            this.items = items;
            mItemViewOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemViewClick(v);
                }
            };
            mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSwipeableViewContainerClick(v);
                }
            };
        }


        private void onItemViewClick(View v) {
            LibraryItem item = (LibraryItem) v.getTag();
            RemoteService.addQueueItem(getRoom(), queue, item.getItemId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    updateQueue(response);
                }
            });
        }

        private void onSwipeableViewContainerClick(View v) {
            onItemViewClick(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View v = inflater.inflate(R.layout.queue_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.applyItem(items[position]);
            holder.itemView.setOnClickListener(mItemViewOnClickListener);
            holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);
            holder.mContainer.setBackgroundResource(R.drawable.bg_item_normal_state);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        public void addItems(LibraryItem[] newItems) {
            LibraryItem[] totalItems = new LibraryItem[items.length + newItems.length];
            for(int i = 0; i < items.length ; i ++) {
                totalItems[i] = items[i];
            }
            for(int i = 0; i < newItems.length; i++) {
                totalItems[items.length + i] = newItems[i];
            }
            items = totalItems;

            notifyDataSetChanged();
        }
    }

    public void refresh() {
        lastSearchKey = null;

        RemoteService.getActiveQueue(getRoom(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                queue = response;

                String typeName = Settings.getSelectedSearchType(getContext());
                int ind = 0;
                for (int i = 0; i < searchTypes.length; i++) {
                    if (searchTypes[i].equals(typeName)) {
                        ind = i;
                        break;
                    }
                }
                searchTypeSpinner.setSelection(ind);

                updateSearch();

                RemoteService.getQueueContents(getRoom(), queue, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateQueue(response);
                    }
                });
            }
        });
    }

    private void updateQueue(String response) {
        items.clear();
        for (QueueItem item : gson.fromJson(response, QueueItem[].class)) {
            items.add(item);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.media_center_library, container, false);

        setupButtons(ret);

        if (ret instanceof ViewGroup) {
            attachButtonCallbacks((ViewGroup) ret);
        }

        return ret;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queueRecyclerView = (RecyclerView) view.findViewById(R.id.queueView);
        queueRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        queueRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        queueRecyclerViewTouchActionGuardManager.setEnabled(true);

        queueLayoutManager = new LinearLayoutManager(getContext());

        // drag & drop manager
        queueRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        queueRecyclerViewDragDropManager.setCheckCanDropEnabled(true);
        queueRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3));

        // swipe manager
        queueRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        final DraggableSwipeableAdapter myItemAdapter = new DraggableSwipeableAdapter(items);
        myItemAdapter.setEventListener(new DraggableSwipeableAdapter.EventListener() {
            @Override
            public void onItemRemoved(QueueItem item) {
                RemoteService.removeQueueItem(getRoom(), queue, item.getQueueId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateQueue(response);
                    }
                });
            }

            @Override
            public void onItemMoved(QueueItem item, QueueItem afterItem) {
                RemoteService.moveQueueItem(getRoom(), queue, item.getQueueId(), afterItem.getQueueId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateQueue(response);
                    }
                });
            }

            @Override
            public void onItemViewClicked(QueueItem item) {
                RemoteService.addQueueItem(getRoom(), queue, item.getItemId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateQueue(response);
                    }
                });
            }
        });

        adapter = myItemAdapter;

        wrappedAdapter = queueRecyclerViewDragDropManager.createWrappedAdapter(adapter);      // wrap for dragging
        wrappedAdapter = queueRecyclerViewSwipeManager.createWrappedAdapter(wrappedAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);

        queueRecyclerView.setLayoutManager(queueLayoutManager);
        queueRecyclerView.setAdapter(wrappedAdapter);
        queueRecyclerView.setItemAnimator(animator);

        queueRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        queueRecyclerViewTouchActionGuardManager.attachRecyclerView(queueRecyclerView);
        queueRecyclerViewSwipeManager.attachRecyclerView(queueRecyclerView);
        queueRecyclerViewDragDropManager.attachRecyclerView(queueRecyclerView);

        searchTypeSpinner = (Spinner) view.findViewById(R.id.searchType);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, searchTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(dataAdapter);
        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (lastSearchKey != null) {
                    Settings.setSelectedSearchType(getContext(), searchTypes[position]);
                    updateSearch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchCriteriaEdit = (BackAwareEditText) view.findViewById(R.id.searchCriteria);
        searchCriteriaEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSearch();
            }
        });
        searchCriteriaEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchCriteriaEdit.getWindowToken(), 0);
                    showControls(true);
                    return true;
                }
                return false;
            }
        });
        searchCriteriaEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showControls(false);
            }
        });
        searchCriteriaEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showControls(false);
                }
            }
        });
        searchCriteriaEdit.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                showControls(true);
            }
        });

        libraryRecyclerView = (RecyclerView) view.findViewById(R.id.libraryView);
        libraryRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));
        libraryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        View newButton = view.findViewById(R.id.btn_new);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libraryRecyclerView.clearOnScrollListeners();
                libraryRecyclerView.setAdapter(new LibraryAdapter(new LibraryItem[0]));

                lastSearchKey = null;
                searchTypeSpinner.setSelection(0);
                searchCriteriaEdit.setText("");
                updateSearch();
            }
        });
        newButton.setVisibility(View.VISIBLE);

        rootView = view;
    }

    private void showControls(boolean visible) {
        if (visible) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rootView.findViewById(R.id.controlAbove).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.controlButtons).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.controlBelow).setVisibility(View.VISIBLE);
                }
            }, 100);
        } else {
            rootView.findViewById(R.id.controlAbove).setVisibility(View.GONE);
            rootView.findViewById(R.id.controlButtons).setVisibility(View.GONE);
            rootView.findViewById(R.id.controlBelow).setVisibility(View.GONE);
        }
    }

    private void updateSearch() {
        String searchKey = queue + searchTypeSpinner.getSelectedItem() + searchCriteriaEdit.getText().toString();
        if (!searchKey.equals(lastSearchKey)) {
            lastSearchKey = searchKey;
            RemoteService.query(queue, (String) searchTypeSpinner.getSelectedItem(), searchCriteriaEdit.getText().toString(), 0, 50, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (libraryRecyclerView != null) {
                        LibraryItem[] items = gson.fromJson(response, LibraryItem[].class);

                        libraryRecyclerView.clearOnScrollListeners();
                        libraryRecyclerView.setAdapter(new LibraryAdapter(items));
                        if (items.length == 50) {
                            libraryRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) libraryRecyclerView.getLayoutManager()) {
                                @Override
                                public void onLoadMore(int current_page) {
                                    RemoteService.query(queue, (String) searchTypeSpinner.getSelectedItem(), searchCriteriaEdit.getText().toString(), current_page * 50, 50, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            LibraryAdapter adapter = (LibraryAdapter) libraryRecyclerView.getAdapter();
                                            LibraryItem[] items = gson.fromJson(response, LibraryItem[].class);
                                            adapter.addItems(items);
                                            if (items.length != 50) {
                                                libraryRecyclerView.clearOnScrollListeners();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public MediaCenterLibraryFragment() {
        super();
    }

    @Override
    public void onPause() {
        queueRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void lostFocus() {
        if (queueRecyclerViewDragDropManager != null) {
            queueRecyclerViewDragDropManager.cancelDrag();
        }
        if (searchCriteriaEdit != null) {
            searchCriteriaEdit.clearFocus();
        }
    }

    @Override
    public void onDestroyView() {
        if (queueRecyclerViewDragDropManager != null) {
            queueRecyclerViewDragDropManager.release();
            queueRecyclerViewDragDropManager = null;
        }

        if (queueRecyclerViewSwipeManager != null) {
            queueRecyclerViewSwipeManager.release();
            queueRecyclerViewSwipeManager = null;
        }

        if (queueRecyclerViewTouchActionGuardManager != null) {
            queueRecyclerViewTouchActionGuardManager.release();
            queueRecyclerViewTouchActionGuardManager = null;
        }

        if (queueRecyclerView != null) {
            queueRecyclerView.setItemAnimator(null);
            queueRecyclerView.setAdapter(null);
            queueRecyclerView = null;
        }

        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        adapter = null;
        queueLayoutManager = null;

        if (libraryRecyclerView != null) {
            libraryRecyclerView.setAdapter(null);
            libraryRecyclerView = null;
        }

        searchCriteriaEdit = null;

        super.onDestroyView();
    }
}
