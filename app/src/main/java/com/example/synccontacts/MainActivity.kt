package com.example.synccontacts

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

import android.provider.ContactsContract

import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.WRITE_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    android.Manifest.permission.WRITE_CONTACTS
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(android.Manifest.permission.WRITE_CONTACTS),1)
            }else {
                Toast.makeText(this@MainActivity,"Permission Denied: Please Allow contact permissions from settings", Toast.LENGTH_LONG).show()
            }
        }
        val button:Button = findViewById(R.id.button)
        button.setOnClickListener {
            val retService = RetrofitInstance.getRetrofitInstance().create(ApiInterface::class.java)
            val responseLiveData: LiveData<ContactList> = liveData {

                var contactList: ContactList = ContactList()
                contactList.add(Contact(123, "ankit"))
                var response: ContactList = contactList
                try {
                    response = retService.getContacts()//contactList
                } catch (e: Exception) {
                    Log.i("My Tag", " error message " + e)
                }
                emit(response)
            }

            responseLiveData.observe(this, Observer {
                val contactList = it?.listIterator()
                if (contactList != null) {
                    while (contactList.hasNext()) {
                        val contact = contactList.next()
                        Log.i("My Tag", contact.name + " " + contact.phone);
                        addContact(contact.name, contact.phone.toString())
                    }
                }
            })

        }
    }

    private fun addContact(name: String, phone: String) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.WRITE_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    android.Manifest.permission.WRITE_CONTACTS
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(android.Manifest.permission.WRITE_CONTACTS), 1
                )
            } else {
                Toast.makeText(this@MainActivity,"Permission Denied", Toast.LENGTH_LONG).show()
            }
        } else {
            val contactId = getRawContactId()
            insertContactDisplayName(ContactsContract.Data.CONTENT_URI,contactId , name)

            insertContactPhoneNumber(
                ContactsContract.Data.CONTENT_URI,contactId, phone,
                "mobile"
            )
            Toast.makeText(this@MainActivity,"Contacts added", Toast.LENGTH_LONG).show()
        }
    }
    private fun getRawContactId(): Long {
        // Inser an empty contact.
        val contentValues = ContentValues()
        val rawContactUri =
            contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)
        // Get the newly created contact raw id.
        return ContentUris.parseId(rawContactUri!!)
    }


    // Insert newly created contact display name.
    private fun insertContactDisplayName(
        addContactsUri: Uri,
        rawContactId: Long,
        displayName: String
    ) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
        )

        // Put contact display name value.
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName)
        contentResolver.insert(addContactsUri, contentValues)
    }

    private fun insertContactPhoneNumber(
        addContactsUri: Uri,
        rawContactId: Long,
        phoneNumber: String,
        phoneTypeStr: String
    ) {
        // Create a ContentValues object.
        val contentValues = ContentValues()

        // Each contact must has an id to avoid java.lang.IllegalArgumentException: raw_contact_id is required error.
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )

        // Put phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)

        // Calculate phone type by user selection.
        var phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME
        if ("home".equals(phoneTypeStr, ignoreCase = true)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME
        } else if ("mobile".equals(phoneTypeStr, ignoreCase = true)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
        } else if ("work".equals(phoneTypeStr, ignoreCase = true)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK
        }
        // Put phone type value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, phoneContactType)

        // Insert new contact data into phone contact list.
        contentResolver.insert(addContactsUri, contentValues)
    }


}