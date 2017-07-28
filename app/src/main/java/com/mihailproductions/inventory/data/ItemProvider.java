package com.mihailproductions.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.mihailproductions.inventory.data.ItemContract.ItemEntry;

public class ItemProvider extends ContentProvider {
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    private ItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {
        //Name check
        if (values.getAsString(ItemEntry.COLUMN_ITEM_NAME) == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        //Price check
        Float price = values.getAsFloat(ItemEntry.COLUMN_ITEM_PRICE);
        if(price == null || price<= 0)
        {
            throw new IllegalArgumentException("Item requires a valid price");
        }
        //Quantity check
        Integer quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
        if(quantity == null || quantity< 0)
        {
            throw new IllegalArgumentException("Item requires a valid quantity");
        }
        //Supplier name check
        if (values.getAsString(ItemEntry.COLUMN_SUPPLIER_NAME) == null) {
            throw new IllegalArgumentException("Item requires a dupplier name");
        }
        //Supplier email check
        if (values.getAsString(ItemEntry.COLUMN_SUPPLIER_EMAIL) == null) {
            throw new IllegalArgumentException("Item requires an email");
        }
        //Image check
        if (values.getAsString(ItemEntry.COLUMN_ITEM_IMAGE) == null) {
            throw new IllegalArgumentException("Item requires an image");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ItemEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //name check
        if (values.containsKey(ItemEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        //price check
        if(values.containsKey(ItemEntry.COLUMN_ITEM_PRICE)){
            Float price = values.getAsFloat(ItemEntry.COLUMN_ITEM_PRICE);
            if(price == null || price <= 0)
            {
                throw new IllegalArgumentException("Item requires a valid price");
            }
        }
        //quantity check
        if(values.containsKey(ItemEntry.COLUMN_ITEM_QUANTITY)){
            Integer quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
            if(quantity == null || quantity < 0)
            {
                throw new IllegalArgumentException("Item requires a valid quantity");
            }
        }
        //supplier name check
        if (values.containsKey(ItemEntry.COLUMN_SUPPLIER_NAME)) {
            if (values.getAsString(ItemEntry.COLUMN_SUPPLIER_NAME) == null) {
                throw new IllegalArgumentException("Item requires a supplier name");
            }
        }
        //supplier email check
        if (values.containsKey(ItemEntry.COLUMN_SUPPLIER_EMAIL)) {
            if (values.getAsString(ItemEntry.COLUMN_SUPPLIER_EMAIL) == null) {
                throw new IllegalArgumentException("Item requires a supplier email");
            }
        }
        //image check
        if (values.containsKey(ItemEntry.COLUMN_ITEM_IMAGE)) {
            if (values.getAsString(ItemEntry.COLUMN_ITEM_IMAGE) == null) {
                throw new IllegalArgumentException("Item requires an image");
            }
        }
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}