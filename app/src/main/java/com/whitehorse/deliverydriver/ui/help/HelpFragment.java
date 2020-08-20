package com.whitehorse.deliverydriver.ui.help;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.whitehorse.deliverydriver.R;

public class HelpFragment extends Fragment {

    ImageView btn_gmail;
    ImageView btn_message;
    ImageView btn_whatsapp;
    ImageView btn_call;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_help, container, false);

        btn_gmail = view.findViewById(R.id.btn_gmail);
        btn_whatsapp = view.findViewById(R.id.btn_whatsapp);
        btn_message = view.findViewById(R.id.btn_message);
        btn_call = view.findViewById(R.id.btn_call);

        btn_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
                //i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                //i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = getActivity().getPackageManager();
                Uri uri = Uri.parse("smsto:" + "9559266951");
                try {
                    Intent waIntent = new Intent(Intent.ACTION_SENDTO,uri);

                    PackageInfo info=pm.getPackageInfo("com.whatsapp",
                            PackageManager.GET_META_DATA);
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, "Hello");
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getActivity());

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra("address", "123456");

                if (defaultSmsPackageName != null)
                {
                    sendIntent.setPackage(defaultSmsPackageName);
                }
                startActivity(sendIntent);

            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:9999999999"));
                startActivity(intent);

            }
        });
        return view;
    }
}
