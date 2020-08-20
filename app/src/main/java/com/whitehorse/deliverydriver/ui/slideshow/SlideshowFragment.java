package com.whitehorse.deliverydriver.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.whitehorse.deliverydriver.R;

public class SlideshowFragment extends Fragment {

    LinearLayout change;
    NavController navc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        change= root.findViewById(R.id.change_password);
        navc= NavHostFragment.findNavController(this);
//        change.setOnClickListener(v -> navc.navigate(R.id.action_nav_slideshow_to_changePassword));
        return root;
    }
}
