package io.github.wulkanowy.materialchipsinput.sample;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.materialchipsinput.MaterialChipsInput;

public class ContactListActivity extends AppCompatActivity {

    private static final String TAG = ContactListActivity.class.toString();

    MaterialChipsInput mMaterialChipsInput;

    Button mValidateButton;

    TextView mChipListText;
    private List<Chip> mContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        // butter knife
        mContactList = new ArrayList<>();

        // get contact list
        new RxPermissions(this)
                .request(Manifest.permission.READ_CONTACTS)
                .subscribe(granted -> {
                    if (granted && mContactList.size() == 0)
                        getContactList();

                }, err -> {
                    Log.e(TAG, err.getMessage());
                    Toast.makeText(ContactListActivity.this, "Error get contacts, see logs", Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Get the contacts of the user and add each contact in the mContactList
     * And finally pass the mContactList to the mChipsInput
     */
    private void getContactList() {
        Cursor phones = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // loop over all contacts
        if (phones != null) {
            while (phones.moveToNext()) {
                // get contact info
                String phoneNumber = null;
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String avatarUriString = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                Uri avatarUri = null;
                if (avatarUriString != null)
                    avatarUri = Uri.parse(avatarUriString);

                // get phone number
                if (Integer.parseInt(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (pCur != null && pCur.moveToNext()) {
                        phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    pCur.close();

                }
                Chip chip = new Chip(this);
                chip.setText(name);

                mContactList.add(chip);
            }
            phones.close();
        }

        mMaterialChipsInput.setItemList(mContactList);
    }
}
