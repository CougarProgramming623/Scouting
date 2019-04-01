package com.jt.scoutingapp;

import android.content.Intent;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.jt.scoutcore.AssignerEntry;
import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;

import java.io.File;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class MultiPageScoutingActivity extends AbstractScoutingActivity {

    private MatchSubmission currentSubmission;
    private SparseArray<Object> valueCache = new SparseArray<Object>();//Maps view id's to their previous value
    private ScoutingPage[] pages;
    private int index = 0;

    public MultiPageScoutingActivity(ScoutingPage... pages) {
        this.pages = pages;
    }

    public static abstract class ScoutingPage {
        //Called once each time the layout switches - for each page, for each match
        public abstract void showImpl(MultiPageScoutingActivity parent);

        private void show(MultiPageScoutingActivity parent) {
            showImpl(parent);//Let the user set their layout so we can pull the requisite buttons from it
            parent.pageShown(this);//Re-load any saved values
            View submitButton = setListener(parent, R.string.submit_tag, (v) -> parent.nextPage());
            View nextButton = setListener(parent, R.string.next_tag, (v) -> parent.nextPage());
            View previousButton = setListener(parent, R.string.previous_tag, (v) -> parent.previousPage());

            parent.setTeamAndMatch();
            if(submitButton == null && nextButton == null && previousButton == null) {
                throw new RuntimeException("Scouting Activity must have at least one navigation element, either submit, next or previous!");
            }
        }

        private View setListener(MultiPageScoutingActivity parent, int tag, View.OnClickListener listener) {
            View view = parent.findViewByTag(tag);
            if(view != null)
                view.setOnClickListener(listener);
            return view;
        }

        //Called when the user goes onto the next page
        public abstract void onSubmit(MultiPageScoutingActivity parent, MatchSubmission m);
    }

    public void nextPage() {
        if(index < pages.length) {
            pages[index].onSubmit(this, currentSubmission);//Save the info from the current page
            pageHidden(pages[index]);
            index++;//Go onto the next page
        }
        resetPage();//Display the new page
    }

    public void previousPage() {
        if(index < pages.length) {
            pages[index].onSubmit(this, currentSubmission);//Save the info from the current page
            pageHidden(pages[index]);
        }
        if(index >= 1) {
            index--;
            resetPage();
        }
    }

    //Called once on app startup to grab objects from the xml
    public void init() {

    }

    private void resetPage() {
        if(index >= pages.length) {
            //Go to the next match
            Intent intent = new Intent(MultiPageScoutingActivity.this, Popup.class);
            startActivityForResult(intent, 42);//Request a submit
        } else {
            pages[index].show(this);
        }
    }

    public abstract void finalizeSubmission(MatchSubmission m);

    private interface ViewConsumer {
        public void accept(View view);
    }

    public void recursiveLoopForStore(ViewConsumer consumer) {
        recursiveLoopForStore(this.findViewById(android.R.id.content), consumer);
    }

    private static boolean isTrue(Object obj) {
        if(obj == null) return false;
        if(obj instanceof Boolean) {
            return ((Boolean) obj);
        } else if(obj instanceof String) {
            String s = (String) obj;
            return s.equals("1") || s.equalsIgnoreCase("true");
        }
        return false;
    }

    public void recursiveLoopForStore(ViewGroup parent, ViewConsumer consumer) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);
            if(child == null) continue;
            if (isTrue(child.getTag(R.id.store))) {//Make sure we specified to store it
                consumer.accept(child);
            } else if (child instanceof ViewGroup) {
                recursiveLoopForStore((ViewGroup) child, consumer);
            }
        }

    }

    private void pageHidden(ScoutingPage page) {
        recursiveLoopForStore((view) -> {
            int id = view.getId();
            if (view instanceof RadioGroup) {
                valueCache.put(id, ((RadioGroup) view).getCheckedRadioButtonId());

            } else if (view instanceof CompoundButton) {//Superclass of radio button, switch, checkbox etc.
                valueCache.put(id, ((CompoundButton) view).isChecked());

            } else if (view instanceof RatingBar) {
                valueCache.put(id, ((RatingBar) view).getRating());

            } else if(view instanceof TextView) {
                valueCache.put(id, ((TextView) view).getText());

            } else {
                throw new Error("Unhandled type " + view.getClass());
            }
        });
    }

    private void pageShown(ScoutingPage page) {
        recursiveLoopForStore((view) -> {
            int id = view.getId();
            Object value = valueCache.get(id);
            if (value == null) return;
            if (view instanceof RadioGroup) {
                if(id > 0)//Make sure de dont try to select an invalid radio button
                    ((RadioGroup) view).check((Integer) value);
            } else if (view instanceof CompoundButton) {//Superclass of radio button, switch, checkbox etc.
                ((CompoundButton) view).setChecked((Boolean) value);
            } else if (view instanceof RatingBar){
                ((RatingBar) view).setRating((Float) value);
            } else if(view instanceof TextView) {
                ((TextView) view).setText((CharSequence) value);
            } else {
                throw new Error("Unhandled type " + view.getClass());
            }
        });
    }

    //Called each time when a new match needs to be displayed
    public void resetImpl() {
        AssignerEntry current = list.getCurrent();
        if(current == null) {
            setContentView(R.layout.end_of_assignments);
            Log.e("Scout", "Reset impl no assignments ");
        } else {
            Log.e("Scout", "Good match " + current);
            index = 0;
            currentSubmission = new MatchSubmission(current.team, current.match, current.red ? TeamColor.RED : TeamColor.BLUE);
            valueCache.clear();//We are moving to a new page so we don't care about the values from last time
            resetPage();//Show the first page
        }
    }

    @Override
    protected void handleSubmit() {
        File file = ClientUtils.getMatchFile(list.getCurrent().match, list.getCurrent().team);
        ClientUtils.ANDROID_MATCHES_DIR.mkdirs();
        finalizeSubmission(currentSubmission);
        ScoutingUtils.write(currentSubmission, file);
        Log.w("Scout", "Handle submit calling next()");
    }

    public int sumCheckBoxes(int... ids) {
        CheckBox[] array = new CheckBox[ids.length];
        for(int i = 0; i < ids.length; i++) {
            View view = findViewById(ids[i]);
            if(view instanceof CheckBox) {
                array[i] = (CheckBox) view;
            } else {
                array[i] = null;
            }
        }
        return sumCheckBoxes(array);
    }

    public int sumCheckBoxes(CheckBox... ids) {
        int total = 0;
        for(int i = 0; i < ids.length; i++) {
            if(ids[i] != null)
                total += ids[i].isChecked() ? 1 : 0;
        }
        return total;
    }
}
