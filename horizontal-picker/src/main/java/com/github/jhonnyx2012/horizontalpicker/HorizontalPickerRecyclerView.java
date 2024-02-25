package com.github.jhonnyx2012.horizontalpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DecimalFormat;

/**
 * Created by Jhonny Barrios on 22/02/2017.
 */

public class HorizontalPickerRecyclerView extends RecyclerView implements OnItemClickedListener, View.OnClickListener {

    private HorizontalPickerAdapter adapter;
    private int lastPosition;
    private LinearLayoutManager layoutManager;
    private float itemWidth;
    private HorizontalPickerListener listener;
    private int offset;

    public HorizontalPickerRecyclerView(Context context) {
        super(context);
    }

    public HorizontalPickerRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalPickerRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Context context, final int daysToPlus, final int initialOffset, final int mBackgroundColor, final int mDateSelectedColor, final int mDateSelectedTextColor, final int mTodayDateTextColor, final int mTodayDateBackgroundColor, final int mDayOfWeekTextColor, final int mUnselectedDayTextColor) {
        this.offset = initialOffset;
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);
        post(new Runnable() {
            @Override
            public void run() {
                itemWidth = getMeasuredWidth() / 7;
                adapter = new HorizontalPickerAdapter((int) itemWidth, HorizontalPickerRecyclerView.this, getContext(), daysToPlus, initialOffset, mBackgroundColor, mDateSelectedColor, mDateSelectedTextColor, mTodayDateTextColor,
                        mTodayDateBackgroundColor,
                        mDayOfWeekTextColor,
                        mUnselectedDayTextColor);
                setAdapter(adapter);
                LinearSnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(HorizontalPickerRecyclerView.this);
                removeOnScrollListener(onScrollListener);
                addOnScrollListener(onScrollListener);
            }
        });
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    listener.onStopDraggingPicker();
                    int position = (int) ((computeHorizontalScrollOffset() / itemWidth) + 3.5);
                    if (position != -1 && position != lastPosition) {
                        selectItem(true, position);
                        selectItem(false, lastPosition);
                        lastPosition = position;
                    }
                    break;
                case SCROLL_STATE_DRAGGING:
                    listener.onDraggingPicker();
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    private void selectItem(boolean isSelected, int position) {
        adapter.getItem(position).setSelected(isSelected);
        adapter.notifyItemChanged(position);
        if (isSelected) {
            listener.onDateSelected(adapter.getItem(position));
        }
    }

    public void setListener(HorizontalPickerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClickView(View v, int adapterPosition) {
        /*if (adapterPosition != lastPosition) {
            selectItem(true, adapterPosition);
            selectItem(false, lastPosition);
            lastPosition = adapterPosition;
        }*/
        if (adapterPosition != lastPosition) {
            selectItem(true, adapterPosition);
            selectItem(false, lastPosition);
            if (adapterPosition < lastPosition)
                scrollToPosition(adapterPosition - 3);
            else
                scrollToPosition(adapterPosition + 3);
            lastPosition = adapterPosition;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvToday) {
            setDate(new DateTime());
        } else if (id == R.id.imgNextMonth) {
           /* try {
                DateTime selectedDay = adapter.getSelectedItem().getDate();
                DateTime currentDate = new DateTime();

                if (selectedDay.toDate().before(currentDate.toDate())) {
                    setDate(selectedDay.plusMonths(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            try {
                DateTime selectedDay = adapter.getSelectedItem().getDate();
                DateTime currentDate = new DateTime();

                if (selectedDay.toDate().before(currentDate.toDate())) {
                    DateTime newDate = selectedDay.plusMonths(1);
                    DecimalFormat df = new DecimalFormat("00");
                    if (newDate.toString("MM").equals(currentDate.toString("MM")) && Integer.parseInt(newDate.toString("dd")) > Integer.parseInt(currentDate.toString("dd"))) {
                        setDate(currentDate);
                    } else {
                        setDate(newDate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id == R.id.imgPrevMonth) {
            /*try {
                DateTime selectedDay = adapter.getSelectedItem().getDate();
                DateTime currentDate = new DateTime();
                if (selectedDay.toDate().after(currentDate.minusYears(1).toDate())) {
                    setDate(selectedDay.minusMonths(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            try {
                DateTime selectedDay = adapter.getSelectedItem().getDate();
                DateTime currentDate = new DateTime();
                Log.e("TAG", "onClick::::Setdate:::  " + selectedDay + "  " + currentDate + "  " + selectedDay.toDate().after(currentDate.minusYears(1).toDate()));
                if (selectedDay.toDate().after(currentDate.minusYears(1).toDate())) {
                    setDate(selectedDay.minusMonths(1));
                    Log.e("TAG", "onClick:Iff::: Next ");
                } else {
                    Log.e("TAG", "onClick:ElsE::: Next ");
                }
            } catch (Exception e) {
                Log.e("TAG", "onClick::::eeee " + e.toString());
                e.printStackTrace();
            }
        }

    }

    @Override
    public void smoothScrollToPosition(int position) {
        final SmoothScroller smoothScroller = new CenterSmoothScroller(getContext());
        smoothScroller.setTargetPosition(position);
        post(new Runnable() {
            @Override
            public void run() {
                layoutManager.startSmoothScroll(smoothScroller);
            }
        });
    }

/*    public void setDate(DateTime date) {
        try{
            DateTime today = new DateTime().withTime(0, 0, 0, 0);
            int difference = Days.daysBetween(date, today).getDays() * (date.getYear() < today.getMillis() ? -1 : 1);
            if ((offset + difference) > 0)
                scrollToPosition(offset + difference);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    public void setDate(DateTime date) {
        try {
            DateTime today = new DateTime().withTime(0, 0, 0, 0);
            int difference = Days.daysBetween(date, today).getDays() * (date.getYear() < today.getMillis() ? -1 : 1);
            if ((offset + difference) > 0) {
                if ((offset + difference) < lastPosition)
                    scrollToPosition(offset + difference - 3);
                else
                    scrollToPosition(offset + difference + 3);
                //scrollToPosition();
                selectItem(true, offset + difference);
                selectItem(false, lastPosition);
                lastPosition = offset + difference;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static class CenterSmoothScroller extends LinearSmoothScroller {

        CenterSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }
    }
}
