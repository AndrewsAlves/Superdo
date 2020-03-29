package com.andysapps.superdo.todo.fragments.bucket;


import android.animation.Animator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.adapters.taskrecyclers.BucketsRecyclerAdapter;
import com.andysapps.superdo.todo.enums.BucketUpdateType;
import com.andysapps.superdo.todo.enums.MoonButtonType;
import com.andysapps.superdo.todo.events.ClickBucketEvent;
import com.andysapps.superdo.todo.events.DeleteBucketEvent;
import com.andysapps.superdo.todo.events.ExitBucketTaskFragment;
import com.andysapps.superdo.todo.events.OpenAddBucketFragmentEvent;
import com.andysapps.superdo.todo.events.UpdateMoonButtonType;
import com.andysapps.superdo.todo.events.firestore.BucketUpdatedEvent;
import com.andysapps.superdo.todo.events.firestore.FetchBucketEvent;
import com.andysapps.superdo.todo.manager.AnimationManager;
import com.andysapps.superdo.todo.manager.TaskOrganiser;
import com.andysapps.superdo.todo.model.Bucket;
import com.andysapps.superdo.todo.views.UiUtils;
import com.github.florent37.viewanimator.ViewAnimator;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

/**
 * A simple {@link Fragment} subclass.
 */

public class BucketFragment extends Fragment{

    public static String TAG = "BucketFragment";

    @BindView(R.id.recyclerView_bucket_list)
    RecyclerView recyclerView;

    @BindView(R.id.root_view_bucket_fragment)
    RelativeLayout rootLayout;

    @BindView(R.id.ib_close_buckets)
    ImageButton btnClose;

    BucketsRecyclerAdapter adapter;

    List<Bucket> bucketList;

    @BindView(R.id.ll_noBuckets)
    LinearLayout llNoTasks;

    public boolean animationEnded;

    public BucketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bucket, container, false);
        ButterKnife.bind(this, v);

        enterCircularReveal();
        //AnimationManager.getInstance().scaleUp(btnClose);
        initUi();

        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new UpdateMoonButtonType(MoonButtonType.ADD_BUCKET));

        bucketList = new ArrayList<>();
        bucketList.addAll(TaskOrganiser.getInstance().bucketList);
        if (bucketList.isEmpty()) {
            llNoTasks.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BucketsRecyclerAdapter(getContext(), bucketList);
        recyclerView.setAdapter(adapter);

        Log.e( getClass().getName(), "onCreateView: bucket fragment" );

        return v;
    }

    public void initUi() {
        PushDownAnim.setPushDownAnimTo(btnClose)
                .setScale(MODE_SCALE, 0.90f  )
                .setOnClickListener(view -> exitCircularReveal());
    }
    
    @Override
    public void onDestroyView() {
        //exitCircularReveal();
        //EventBus.getDefault().unregister(this);
        //EventBus.getDefault().post(new UpdateMoonButtonType(MoonButtonType.ADD_TASK));
        super.onDestroyView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FetchBucketEvent event) {
        if (event.isSuccess()) {
            adapter.updateList(TaskOrganiser.getInstance().bucketList);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BucketUpdatedEvent event) {
       if (event.getDocumentChange() == BucketUpdateType.Added) {
           adapter.notifyBucketAdded(TaskOrganiser.getInstance().bucketList);
       } else if (event.getDocumentChange() == BucketUpdateType.Deleted){
           adapter.notifyBucketRemoved(event.getBucket());
       } else {
           adapter.updateList(TaskOrganiser.getInstance().bucketList);
       }
    }

    @OnClick(R.id.ib_close_buckets)
    public void clickClose() {
        exitCircularReveal();
       // getActivity().getSupportFragmentManager().popBackStack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeleteBucketEvent event) {
        TaskOrganiser.getInstance().deleteBucket(event.getBucket());
        onMessageEvent(new BucketUpdatedEvent(BucketUpdateType.Deleted, event.getBucket()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenAddBucketFragmentEvent event) {
        new AddBucketFragment().show(getFragmentManager(), "add bucket");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ExitBucketTaskFragment event) {
        clickClose();
    }

    /////////////
    /// ANIMATIONS
    /////////////

    public void enterCircularReveal() {
        rootLayout.setVisibility(View.INVISIBLE);

        ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    circularRevealActivity();
                    rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    public void exitCircularReveal() {
        int cx = rootLayout.getRight() - UiUtils.getDips(getContext(), 60);
        int cy = rootLayout.getTop() + UiUtils.getDips(getContext(), 60);

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, finalRadius, 0);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                rootLayout.setVisibility(View.INVISIBLE);
                animationEnded = true;
                EventBus.getDefault().post(new UpdateMoonButtonType(MoonButtonType.ADD_TASK));
                getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

            circularReveal.setDuration(400);
            circularReveal.start();
            animateColor(R.color.white, R.color.lightOrange_whitish);
    }

    private void circularRevealActivity() {

        int cx = rootLayout.getRight() - UiUtils.getDips(getContext(), 60);
        int cy = rootLayout.getTop() + UiUtils.getDips(getContext(), 60);

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(400);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
        animateColor(R.color.lightOrange_whitish, R.color.white);
    }

    public void animateColor(int color, int color2) {
        ViewAnimator
                .animate(rootLayout)
                .backgroundColor(getResources().getColor(color),getResources().getColor(color2))
                .duration(400)
                .accelerate()
                .start();
    }
}


