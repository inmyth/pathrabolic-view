package com.pathrabolic;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class PathrabolicView extends FrameLayout {
	private static final int DURATION_ENTRY_BUTTON_ANIM_MS = 400;
	private static final int DURATION_TEXT_FADE_OPEN_MS = 500;
	private boolean isExpanded;

	 private PathrabolicListener pathrabolicListener;
	 private List<ViewHolder> vHolders = new ArrayList<ViewHolder>();
	 private ImageView entryTop, entryBottom;
	 private View overlay;
	 private FrameLayout entry;
	 private RelativeLayout container;
	 

	public PathrabolicView(Context paramContext) {
		super(paramContext);
	}

	public PathrabolicView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}
	
	
	/**
	 * 
	 * @param arcMap index to title map
	 */
	public void init(LinkedHashMap<Integer, String> arcMap) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_arc, this);
		
		entryTop = (ImageView)findViewById(R.id.arc_entry_top);
		entryBottom = (ImageView)findViewById(R.id.arc_entry_bottom);
		overlay = findViewById(R.id.arc_overlay);
		container = (RelativeLayout)findViewById(R.id.arc_container);
		
		for (Integer i : arcMap.keySet()){
			vHolders.add(new ViewHolder(inflater, i, arcMap.get(i)));			
		}
		rearrange();
		entry = (FrameLayout) findViewById(R.id.arc_entry);
		entry.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openClose();
			}
		});
	}

	private static Animation createItemDisapperAnimation(long duration, boolean isClicked) {
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 2.0f: 0.0f, 1.0f, isClicked ? 2.0f : 0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f));
		animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
		animationSet.setDuration(duration);
		animationSet.setInterpolator(new DecelerateInterpolator());
		animationSet.setFillAfter(true);
		return animationSet;
	}

	private Animation initEntryBottomAnim(boolean isOpen) {

		AnimationSet animationSet = new AnimationSet(true);
		if (isOpen) {
			animationSet.addAnimation(new RotateAnimation(90.0F, 0.0F,Animation.RELATIVE_TO_SELF, 0.5F,Animation.RELATIVE_TO_SELF, 0.5F));
			animationSet.addAnimation(new AlphaAnimation(0.0F, 1.0F));
		} else {
			animationSet.addAnimation(new RotateAnimation(0.0F, 90.0F,Animation.RELATIVE_TO_SELF, 0.5F,	Animation.RELATIVE_TO_SELF, 0.5F));
			animationSet.addAnimation(new AlphaAnimation(1.0F, 0.0F));
		}
		animationSet.setDuration(400L);
		return animationSet;
	}

	private AlphaAnimation initOpenAnimation(long offset) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
		alphaAnimation.setDuration(500L);
		alphaAnimation.setStartOffset(offset);
		return alphaAnimation;
	}

	private Animation initEntryTopAnim(final boolean isOpen) {
		AnimationSet animationSet = new AnimationSet(true);
		if (isOpen) {
			animationSet.addAnimation(new RotateAnimation(0.0F, -90.0F, Animation.RELATIVE_TO_SELF,0.5F, Animation.RELATIVE_TO_SELF, 0.5F));
			animationSet.addAnimation(new AlphaAnimation(1.0F, 0.0F));
		}else{		
			animationSet.addAnimation(new RotateAnimation(-90.0F, 0.0F, Animation.RELATIVE_TO_SELF,0.5F, Animation.RELATIVE_TO_SELF, 0.5F));
			animationSet.addAnimation(new AlphaAnimation(0.0F, 1.0F));			
		}
		animationSet.setDuration(400L);
		animationSet.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				entryTop.setVisibility(View.VISIBLE);
				entryBottom.setVisibility(View.VISIBLE);			
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				postDelayed(new Runnable() {
					public void run() {
						if (isOpen){
							entryTop.setVisibility(View.GONE);
							entryBottom.setVisibility(View.VISIBLE);	
						}else{
							entryTop.setVisibility(View.VISIBLE);
							entryBottom.setVisibility(View.GONE);	
						}
						
					}
				}, 0L);

			}
		});		
		return animationSet;
	}
	

	
	private void openClose() {
		isExpanded = !isExpanded;		
		if (isExpanded){
			overlay.setVisibility(View.VISIBLE);
			entryTop.startAnimation(initEntryTopAnim(true));
			entryBottom.startAnimation(initEntryBottomAnim(true));
			for (ViewHolder vh :vHolders){
				vh.item.setVisibility(View.VISIBLE);
				vh.tName.setVisibility(View.VISIBLE);
				vh.tName.startAnimation(vh.openAlphaAnim);
			}			
		}else{
			overlay.setVisibility(View.GONE);
			entryTop.startAnimation(initEntryTopAnim(false));
			entryBottom.startAnimation(initEntryBottomAnim(false));
			for (ViewHolder vh :vHolders){
				vh.item.setVisibility(View.GONE);
				vh.tName.setVisibility(View.GONE);
				vh.icon.clearAnimation();
			}	
			
		}
	}
	
	private void rearrange() {
		for (int i = 0; i < vHolders.size(); i++){
			vHolders.get(i).index = i;
			vHolders.get(i).item.setId(i + 25423);
			vHolders.get(i).openAlphaAnim = initOpenAnimation(i * 200);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70.0F, getResources().getDisplayMetrics()));
			layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						10 + 10 * (i * i), getResources().getDisplayMetrics()),
						0, 0, 0);
			if (i == 0){
				layoutParams.addRule(RelativeLayout.ABOVE, R.id.arc_entry);				
			}else{
				layoutParams.addRule(RelativeLayout.ABOVE, vHolders.get(i - 1).item.getId());
			}
			vHolders.get(i).item.setLayoutParams(layoutParams);
			container.addView(vHolders.get(i).item);
		}
	}

	public void forceOpenClose(boolean isExpanded) {		
		this.isExpanded = isExpanded;
		if (isExpanded){
			overlay.setVisibility(View.VISIBLE);
			entryTop.setVisibility(View.GONE);
			entryTop.clearAnimation();
			entryBottom.setVisibility(View.VISIBLE);
			entryBottom.clearAnimation();
			for (ViewHolder vh : vHolders){
				vh.item.setVisibility(View.VISIBLE);
				vh.tName.setVisibility(View.VISIBLE);
			}
		}else{
			overlay.setVisibility(View.GONE);
			entryTop.setVisibility(View.VISIBLE);
			entryTop.clearAnimation();
			entryBottom.setVisibility(View.GONE);
			entryBottom.clearAnimation();
			for (ViewHolder vh : vHolders){
				vh.item.setVisibility(View.GONE);
				vh.tName.setVisibility(View.GONE);
			}
		}
	}
	


	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		return false;
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		return this.isExpanded;
	}

	public void setOnArcClickedListener(PathrabolicListener pathrabolicListener) {
		this.pathrabolicListener = pathrabolicListener;
	}

	public static abstract interface PathrabolicListener {
		public abstract void onItemClicked(int which);
	}

	class ViewHolder {
		private ImageView icon;
		private int index;
		private LinearLayout item;
		private AlphaAnimation openAlphaAnim;
		private TextView tName;

		public ViewHolder(LayoutInflater inflater, int imgRes, String title) {
			item = (LinearLayout) inflater.inflate(R.layout.item_arc, null);
			item.setVisibility(View.GONE);
			icon = (ImageView) this.item.findViewById(R.id.arc_icon);
			tName = (TextView) this.item.findViewById(R.id.arc_title);
			icon.setImageResource(imgRes);
			tName.setText(title);
			icon.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					
					for (int i = 0; i < vHolders.size(); i++){
						if (i == index){
							Animation menuAnim = createItemDisapperAnimation(400L, true);
							menuAnim.setAnimationListener(new AnimationListener() {
								
								@Override
								public void onAnimationStart(Animation animation) {}
								
								@Override
								public void onAnimationRepeat(Animation animation) {}
								
								@Override
								public void onAnimationEnd(Animation animation) {
									postDelayed(new Runnable() {
										public void run() {
											openClose();
											if (pathrabolicListener != null)
												pathrabolicListener.onItemClicked(index);											
										}
									}, 0L);								
								}
							});
							vHolders.get(i).icon.startAnimation(menuAnim);
						}else{
							vHolders.get(i).icon.startAnimation(createItemDisapperAnimation(300L, false));
							vHolders.get(i).tName.setVisibility(View.INVISIBLE);
						}
					}
				}
			});
		}
	}
}