package com.project.scuevents.ui.registeredevent;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.project.scuevents.R;
import com.project.scuevents.adapter.RegisteredChildSelectionAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisteredEventsFragment extends Fragment {
    View root;
    ViewPager viewPager;
    TabLayout tabLayout;
    public RegisteredEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_registered_events, container, false);

        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RegisteredChildSelectionAdapter adapter= new RegisteredChildSelectionAdapter(getChildFragmentManager());
        adapter.addFragment(new UpcomingEventFragment(),"Upcoming Events");
        adapter.addFragment(new PastEventFragment(),"Past Events");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
