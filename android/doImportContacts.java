public void doImport(final String fileName, final boolean replace) {
    try {

        File vcfFile = new File(fileName);

        final BufferedReader vcfBuffer = new BufferedReader(new FileReader(fileName),1048576);

        final long maxlen = vcfFile.length();


        long importStatus = 0;

                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                SQLiteStatement querySyncId = db.compileStatement("SELECT " + SYNCID + " FROM " + SYNCDATA_TABLE_NAME + " WHERE " + PERSONID + "=?");
                SQLiteStatement queryPersonId = db.compileStatement("SELECT " + PERSONID + " FROM " + SYNCDATA_TABLE_NAME + " WHERE " + SYNCID + "=?");
                SQLiteStatement insertSyncId = db.compileStatement("INSERT INTO  " + SYNCDATA_TABLE_NAME + " (" + PERSONID + "," + SYNCID + ") VALUES (?,?)");
                db.close();
                Contact parseContact = new Contact(querySyncId, queryPersonId, insertSyncId);
                String popa="";
                popa=parseContact.getContent();
                try {
                    long ret = 0;
                    do  {

                        ret = parseContact.parseVCard(vcfBuffer);

                        /* GOOGLE CODE IS JUST THIS ON LINE WHICH AIN'T WORKING!!
                        parseContact.addContact(CO, 0, true); */

                        if (ret >= 0) {
                            String DisplayName = parseContact.displayName;


                            List<RowData> MobileNumbers=parseContact.phones;
                            List <RowData> Addresses = parseContact.addrs;
                            List <RowData> IMs = parseContact.ims;
                            List <OrgData> Orgs = parseContact.orgs;
                            String Notes = parseContact.notes;
                            byte[] dp = parseContact.photo;
                            String BirthDay = parseContact.birthday;


                            ContentResolver cr = CO.getContentResolver();

                            List<RowData> mails=parseContact.emails;

                            try 
                                {


                                // ADDING NAME
                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                            int rawContactInsertIndex = ops.size();

                            ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(RawContacts.ACCOUNT_NAME, null).build());
                            ops.add(ContentProviderOperation
                                    .newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(StructuredName.DISPLAY_NAME, DisplayName) // Name of the person
                                    .build());

                            //ADDING PHONES
                           for(RowData l : MobileNumbers)
                            {

                               {
                            ops.add(ContentProviderOperation
                                    .newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(
                                            ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)

                                    .withValue(Phone.NUMBER,l.data) 
                                    .withValue(Phone.TYPE,l.type).build());
                               }

                            }
                           //ADDING MAILS
                           for(RowData a :mails)
                           {

                               {
                                   ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(
                                                ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)

                                        .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)

                                        .withValue(Email.DATA,a.data)
                                        .withValue(Email.TYPE, a.type).build()); 
                               }


                           }

                           //ADDING ADDRESSES

                           for(RowData add :Addresses)
                           {
                              ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(
                                                ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)


                                        .withValue(ContactsContract.Data.MIMETYPE,StructuredPostal.CONTENT_ITEM_TYPE)

                                        .withValue(StructuredPostal.DATA,add.data) 
                                        .withValue(StructuredPostal.TYPE, add.type).build()); 
                           }

                           //ADDING ORGANISATIONS


                           for(OrgData org :Orgs)
                           {
                              ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(
                                                ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)


                                        .withValue(ContactsContract.Data.MIMETYPE,Organization.CONTENT_ITEM_TYPE)

                                        .withValue(Organization.DATA,org.company) 
                                        .withValue(Organization.TYPE, org.type)
                                        .withValue(Organization.TITLE, org.title)
                                        .withValue(Organization.LABEL, org.customLabel)

                                        .build()); 
                           }

                           //ADDING IMs

                           for(RowData IM :IMs)
                           {
                              ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(
                                                ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)

                                        .withValue(ContactsContract.Data.MIMETYPE,Im.CONTENT_ITEM_TYPE)

                                        .withValue(Im.DATA,IM.data) 
                                        .withValue(Im.TYPE, IM.type).build()); 
                           }

                           //ADDING NOTES

                           if(Notes!=null && !Notes.equals(""))
                           {
                              ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(
                                                ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)


                                        .withValue(ContactsContract.Data.MIMETYPE,Note.CONTENT_ITEM_TYPE)

                                        .withValue(Note.NOTE,Notes).build()); 

                           }

                           // ADDING PHOTO

                           if(dp!=null)
                           {
                              ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(
                                                ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)


                                        .withValue(ContactsContract.Data.MIMETYPE,Photo.CONTENT_ITEM_TYPE)

                                        .withValue(Photo.PHOTO,dp).build());

                           }

                           //ADDING BIRTHDAY
                           if(BirthDay!=null && !BirthDay.equals(""))
                           {
                              ops.add(ContentProviderOperation
                                        .newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(
                                                ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)


                                        .withValue(ContactsContract.Data.MIMETYPE,CommonDataKinds.Event.CONTENT_ITEM_TYPE)

                                        .withValue(CommonDataKinds.Event.START_DATE,BirthDay)
                                        .withValue(CommonDataKinds.Event.TYPE,CommonDataKinds.Event.TYPE_BIRTHDAY).build()); // Number of the person

                           }


                                                    cr.applyBatch(ContactsContract.AUTHORITY, ops);

                                                } 
                                                catch (Exception e) 
                                                {               
                                                    e.printStackTrace();
                                                    Toast.makeText(CO, "Exception: "+e.toString()+"Eebolra:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }


                            importStatus += parseContact.getParseLen();


                        }
                        db.close();
                        } while (ret > 0);

                    db.close();

                } catch (Exception e) {

                     Toast.makeText(CO,"NO "+e.getMessage()+"-"+e.getLocalizedMessage()+"-"+e.toString(), Toast.LENGTH_SHORT).show();

                }



    } catch (FileNotFoundException e) {

    }
}